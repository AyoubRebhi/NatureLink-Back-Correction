from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer, util
import pymysql
import pandas as pd
import torch
import pickle
import json
import re
import os

app = Flask(__name__)
CORS(app, resources={r"/chat": {"origins": "http://localhost:4200"}})

# Memory for follow-up questions
last_recommended_pack = {"name": None}

# Handle relative paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load ML model
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)

# Load general Q&A with error handling
try:
    with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
        general_qa = json.load(f)
    print(f"Loaded {len(general_qa)} Q&A pairs from general_qa.json")  # Removed emoji
except UnicodeDecodeError as e:
    print(f"Error decoding JSON: {e}")
    raise Exception("Failed to decode general_qa.json. Ensure it’s saved with UTF-8 encoding.")
except json.JSONDecodeError as e:
    print(f"Invalid JSON format: {e}")
    raise Exception("general_qa.json is not valid JSON. Check for syntax errors.")
except FileNotFoundError:
    print(f"File not found: general_qa.json")
    raise Exception("general_qa.json not found in the script directory.")

# Encode general questions
general_questions = [q["question"] for q in general_qa]
general_embeddings = model.encode(general_questions, convert_to_tensor=True)

# Get packs from MySQL
def get_live_packs():
    conn = pymysql.connect(
        host='localhost',
        user='root',
        password='0000',
        database='naturelink'
    )
    df = pd.read_sql("SELECT id, nom, description, prix FROM pack", conn)
    conn.close()
    return df

# Extract budget from message
def extract_budget(text):
    match = re.search(r"(\d{2,6})\s*(TND|dt|dinar|d|d\.)?", text.lower())
    if match:
        return float(match.group(1))
    return None

# Chat route
@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_input = request.json.get("message")
        if not user_input or len(user_input.strip()) < 2:
            return jsonify({"type": "none", "response": "Please enter a question about our travel packs."})

        user_embedding = model.encode(user_input, convert_to_tensor=True)

        # General Q&A matching
        sim_gen = util.cos_sim(user_embedding, general_embeddings)
        best_score = sim_gen.max().item()
        best_idx = sim_gen.argmax().item()

        if best_score >= 0.6 or (len(user_input.split()) <= 3 and best_score >= 0.4):
            return jsonify({"type": "general", "response": general_qa[best_idx]["answer"]})

        # Load packs
        df_packs = get_live_packs()
        if df_packs.empty:
            return jsonify({"type": "none", "response": "Sorry, no packs are available right now."})

        # Budget filter
        budget = extract_budget(user_input)
        if budget:
            filtered = df_packs[df_packs['prix'] <= budget]
            if not filtered.empty:
                top = filtered.sort_values("prix").iloc[0]
                last_recommended_pack["name"] = top["nom"]
                return jsonify({
                    "type": "budget",
                    "response": f"Based on your budget of {budget} TND, I recommend: {top['nom']}\nDescription: {top['description']} (Price: {top['prix']} TND)"
                })
            else:
                return jsonify({"type": "budget", "response": f"Sorry, we have no packs under {budget} TND."})

        # Cheapest pack
        if "cheap" in user_input.lower() or "cheapest" in user_input.lower():
            cheapest = df_packs.loc[df_packs['prix'].idxmin()]
            last_recommended_pack["name"] = cheapest["nom"]
            return jsonify({
                "type": "price",
                "response": f"Our cheapest pack is {cheapest['nom']}\nDescription: {cheapest['description']} (Price: {cheapest['prix']} TND)"
            })

        # Semantic match
        pack_embeddings = model.encode(df_packs["description"].tolist(), convert_to_tensor=True)
        sim_scores = util.cos_sim(user_embedding, pack_embeddings)
        best_score = sim_scores.max().item()
        best_idx = sim_scores.argmax().item()

        if best_score >= 0.6:
            pack = df_packs.iloc[best_idx]
            last_recommended_pack["name"] = pack["nom"]
            return jsonify({
                "type": "recommendation",
                "response": f"I recommend: {pack['nom']}\nDescription: {pack['description']} (Price: {pack['prix']} TND)"
            })
        elif best_score >= 0.4:
            pack = df_packs.iloc[best_idx]
            last_recommended_pack["name"] = pack["nom"]
            return jsonify({
                "type": "soft-match",
                "response": f"I think you might like this one:\n{pack['nom']}\nDescription: {pack['description']} (Price: {pack['prix']} TND)"
            })

        # Follow-up intent
        if any(phrase in user_input.lower() for phrase in ["is it good", "is he good", "is it the best", "do you recommend it"]):
            if last_recommended_pack["name"]:
                return jsonify({
                    "type": "followup",
                    "response": f"Based on your preferences, yes — '{last_recommended_pack['name']}' is a great choice! Let me know if you'd like other suggestions."
                })

        return jsonify({
            "type": "none",
            "response": "I can only help with questions about NatureLink travel packs. Please try asking about a destination, activity, or budget."
        })

    except Exception as e:
        print("ERROR IN /chat:", e)
        return jsonify({"type": "error", "response": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5002, debug=False, use_reloader=False)