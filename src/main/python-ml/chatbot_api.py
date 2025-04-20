from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer, util
import pymysql
import pandas as pd
import torch
import pickle
import json
import re
import os
from flask_cors import CORS

app = Flask(__name__)
CORS(app, resources={r"/chat": {"origins": "http://localhost:4200"}})

# ✅ Handle relative paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# ✅ Load ML model
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)

# ✅ Load general Q&A
with open(os.path.join(BASE_DIR, "general_qa.json"), "r") as f:
    general_qa = json.load(f)

general_questions = [q["question"] for q in general_qa]
general_embeddings = model.encode(general_questions, convert_to_tensor=True)

# ✅ Live packs from MySQL DB
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

# ✅ Extract budget from user message
def extract_budget(text):
    match = re.search(r"(\d{2,6})\s*(TND|dt|dinar|d|d\.)?", text.lower())
    if match:
        return float(match.group(1))
    return None

# ✅ Chatbot logic
@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_input = request.json.get("message")
        if not user_input or len(user_input.strip()) < 3:
            return jsonify({"type": "none", "response": "Please enter a question about our travel packs."})

        user_embedding = model.encode(user_input, convert_to_tensor=True)

        # 🔹 General Q&A
        sim_gen = util.cos_sim(user_embedding, general_embeddings)
        if sim_gen.max().item() >= 0.6:
            best_idx = sim_gen.argmax().item()
            return jsonify({"type": "general", "response": general_qa[best_idx]["answer"]})

        # 🔹 Fetch packs
        df_packs = get_live_packs()
        if df_packs.empty:
            return jsonify({"type": "none", "response": "Sorry, no packs are available right now."})

        # 🔹 Budget Match
        budget = extract_budget(user_input)
        if budget:
            filtered = df_packs[df_packs['prix'] <= budget]
            if not filtered.empty:
                top = filtered.sort_values("prix").iloc[0]
                return jsonify({
                    "type": "budget",
                    "response": f"💸 Based on your budget of {budget} TND, I recommend: {top['nom']}\n📝 {top['description']} (Price: {top['prix']} TND)"
                })
            else:
                return jsonify({"type": "budget", "response": f"Sorry, we have no packs under {budget} TND."})

        # 🔹 Cheapest pack (no number but asked cheap)
        if "cheap" in user_input.lower() or "cheapest" in user_input.lower():
            cheapest = df_packs.loc[df_packs['prix'].idxmin()]
            return jsonify({
                "type": "price",
                "response": f"💸 Our cheapest pack is {cheapest['nom']}\n📝 {cheapest['description']} (Price: {cheapest['prix']} TND)"
            })

        # 🔹 Semantic match with pack descriptions
        pack_embeddings = model.encode(df_packs["description"].tolist(), convert_to_tensor=True)
        sim_scores = util.cos_sim(user_embedding, pack_embeddings)
        best_score = sim_scores.max().item()
        best_idx = sim_scores.argmax().item()

        if best_score >= 0.6:
            pack = df_packs.iloc[best_idx]
            return jsonify({
                "type": "recommendation",
                "response": f"🌿 I recommend: {pack['nom']}\n📝 {pack['description']} (Price: {pack['prix']} TND)"
            })
        elif best_score >= 0.4:
            pack = df_packs.iloc[best_idx]
            return jsonify({
                "type": "soft-match",
                "response": f"🤔 I didn’t find an exact match, but this one might interest you:\n🌿 {pack['nom']}\n📝 {pack['description']} (Price: {pack['prix']} TND)"
            })

        return jsonify({
            "type": "none",
            "response": "I can only help with questions about NatureLink travel packs. Please try asking about a destination, activity, or budget."
        })

    except Exception as e:
        print("❌ ERROR IN /chat:", e)
        return jsonify({"type": "error", "response": str(e)}), 500

# ✅ Run Flask server on port 5002 (disable reloader for Spring compatibility)
if __name__ == "__main__":
    app.run(port=5002, debug=False, use_reloader=False)
