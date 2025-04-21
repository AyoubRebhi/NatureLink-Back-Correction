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

# Context memory
last_recommended_pack = {"name": None}

# Paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load ML model
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)

# Load general Q&A
with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
    general_qa = json.load(f)
general_questions = [q["question"] for q in general_qa]
general_embeddings = model.encode(general_questions, convert_to_tensor=True)

# Keyword intentions
keywords = {
    "romantic": ["romantic", "love", "honeymoon", "couple"],
    "mountain": ["mountain", "hiking", "trail", "altitude"],
    "relax": ["relax", "peace", "calm", "retreat", "wellness"],
    "beach": ["beach", "sea", "ocean", "swim", "coast"],
    "adventure": ["adventure", "explore", "wild", "nature", "discover"]
}

# Fetch packs from DB
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

# Budget extraction
def extract_budget(text):
    match = re.search(r"(\d{2,6})\s*(TND|dt|dinar|d|d\.)?", text.lower())
    return float(match.group(1)) if match else None

@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_input = request.json.get("message", "").strip()
        if not user_input or len(user_input) < 2:
            return jsonify({"type": "none", "response": "Please enter a question about our travel packs."})

        # General Q&A semantic match
        user_embedding = model.encode(user_input, convert_to_tensor=True)
        sim_gen = util.cos_sim(user_embedding, general_embeddings)
        best_score = sim_gen.max().item()
        best_idx = sim_gen.argmax().item()

        if best_score >= 0.6 or (len(user_input.split()) <= 3 and best_score >= 0.4):
            return jsonify({"type": "general", "response": general_qa[best_idx]["answer"]})

        # Load live packs
        df_packs = get_live_packs()
        if df_packs.empty:
            return jsonify({"type": "none", "response": "Sorry, no packs are available right now."})

        df_packs["full_text"] = df_packs["nom"] + " " + df_packs["description"]

        # Budget-based suggestion
        budget = extract_budget(user_input)
        if budget:
            filtered = df_packs[df_packs['prix'] <= budget]
            if not filtered.empty:
                top = filtered.sort_values("prix").iloc[0]
                last_recommended_pack["name"] = top["nom"]
                return jsonify({
                    "type": "budget",
                    "response": f"Based on your budget of {budget} TND, I recommend: 🌿 {top['nom']}\n📜 {top['description']} (Price: {top['prix']} TND)"
                })
            else:
                return jsonify({"type": "budget", "response": f"Sorry, we have no packs under {budget} TND."})

        # Cheapest pack
        if any(x in user_input.lower() for x in ["cheap", "cheapest", "low price"]):
            cheapest = df_packs.loc[df_packs['prix'].idxmin()]
            last_recommended_pack["name"] = cheapest["nom"]
            return jsonify({
                "type": "price",
                "response": f"Our cheapest pack is 🌿 {cheapest['nom']}\n📜 {cheapest['description']} (Price: {cheapest['prix']} TND)"
            })

        # Follow-up intent
        if any(x in user_input.lower() for x in ["what did you recommend", "which one", "what was it", "remind me"]):
            if last_recommended_pack["name"]:
                return jsonify({
                    "type": "followup",
                    "response": f"Previously, I suggested: 🌿 '{last_recommended_pack['name']}'. Want to hear about another?"
                })

        # Semantic pack match
        pack_embeddings = model.encode(df_packs["full_text"].tolist(), convert_to_tensor=True)
        sim_scores = util.cos_sim(user_embedding, pack_embeddings)
        best_score = sim_scores.max().item()
        best_idx = sim_scores.argmax().item()

        if best_score >= 0.6:
            pack = df_packs.iloc[best_idx]
            last_recommended_pack["name"] = pack["nom"]
            return jsonify({
                "type": "recommendation",
                "response": f"🌿 I recommend: {pack['nom']}\n📜 {pack['description']} (Price: {pack['prix']} TND)"
            })

        # Keyword fallback
        user_lower = user_input.lower()
        for tag, words in keywords.items():
            if any(w in user_lower for w in words):
                for _, row in df_packs.iterrows():
                    if any(w in row["full_text"].lower() for w in words):
                        last_recommended_pack["name"] = row["nom"]
                        return jsonify({
                            "type": "keyword-match",
                            "response": f"🌿 You might enjoy: {row['nom']}\n📜 {row['description']} (Price: {row['prix']} TND)"
                        })

        return jsonify({
            "type": "none",
            "response": "I'm not sure I understood. Could you rephrase or mention a place, activity, or budget?"
        })

    except Exception as e:
        print("ERROR IN /chat:", e)
        return jsonify({"type": "error", "response": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5002, debug=False, use_reloader=False)
