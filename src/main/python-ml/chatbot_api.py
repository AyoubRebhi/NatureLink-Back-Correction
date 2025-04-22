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
import unicodedata

app = Flask(__name__)
CORS(app, resources={r"/chat": {"origins": "http://localhost:4200"}})

# Context memory
last_recommended_pack = {"name": None}
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load model
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)

# Load general Q&A
with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
    general_qa = json.load(f)
general_questions = [q["question"] for q in general_qa]
general_embeddings = model.encode(general_questions, convert_to_tensor=True)

# Normalize helper
def normalize(text):
    text = text.lower()
    return unicodedata.normalize('NFKD', text).encode('ASCII', 'ignore').decode('utf-8')

# Connect to DB
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

# Extract budget
def extract_budget(text):
    match = re.search(r"(\d{2,6})\s*(TND|dt|dinar|d|d\.)?", text.lower())
    return float(match.group(1)) if match else None

@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_input = request.json.get("message", "").strip()
        if not user_input or len(user_input) < 2:
            return jsonify({"type": "none", "response": "Please enter a question about our travel packs."})

        user_input_norm = normalize(user_input)
        user_embedding = model.encode(user_input_norm, convert_to_tensor=True)

        # 1️⃣ General Q&A
        sim_gen = util.cos_sim(user_embedding, general_embeddings)
        if sim_gen.max().item() >= 0.6 or (len(user_input.split()) <= 3 and sim_gen.max().item() >= 0.4):
            return jsonify({
                "type": "general",
                "response": general_qa[sim_gen.argmax().item()]["answer"]
            })

        # 2️⃣ Load and normalize
        df_packs = get_live_packs()
        if df_packs.empty:
            return jsonify({"type": "none", "response": "Sorry, no packs are available right now."})
        df_packs["full_text"] = (df_packs["nom"] + " " + df_packs["description"]).apply(normalize)

        # 3️⃣ Budget
        budget = extract_budget(user_input)
        if budget:
            filtered = df_packs[df_packs['prix'] <= budget]
            if not filtered.empty:
                top = filtered.sort_values("prix").iloc[0]
                last_recommended_pack["name"] = top["nom"]
                return jsonify({
                    "type": "budget",
                    "response": f"Based on your budget of {budget} TND, I recommend:\n\n🌿 {top['nom']}\n📜 {top['description']} (Price: {top['prix']} TND)"
                })
            else:
                return jsonify({"type": "budget", "response": f"Sorry, we have no packs under {budget} TND."})

        # 4️⃣ Cheapest
        if any(x in user_input_norm for x in ["cheapest", "cheap", "lowest", "low price"]):
            cheapest = df_packs.loc[df_packs['prix'].idxmin()]
            last_recommended_pack["name"] = cheapest["nom"]
            return jsonify({
                "type": "price",
                "response": f"Our cheapest pack is:\n\n🌿 {cheapest['nom']}\n📜 {cheapest['description']} (Price: {cheapest['prix']} TND)"
            })

        # 5️⃣ Follow-up
        if any(x in user_input_norm for x in ["what did you recommend", "which one", "what was it", "remind me"]):
            if last_recommended_pack["name"]:
                return jsonify({
                    "type": "followup",
                    "response": f"Previously, I suggested:\n\n🌿 {last_recommended_pack['name']}. Want another recommendation?"
                })

        # 6️⃣ Stricter keyword fallback
        keyword_map = {
            "romantic": ["romantic", "honeymoon", "love", "couple"],
            "french": ["french", "gourmet", "cuisine"],
            "beach": ["beach", "coast", "sea", "ocean"],
            "hiking": ["hike", "trail", "mountain"],
            "relax": ["retreat", "relax", "calm", "wellness"],
            "nature": ["nature", "wild", "green", "forest"],
            "kef": ["kef"],
            "ain drahem": ["ain drahem", "draham"],
            "zaghouan": ["zaghouan"]
        }

        matches_by_tag = []
        for tag, keywords in keyword_map.items():
            if any(kw in user_input_norm for kw in keywords):
                for _, row in df_packs.iterrows():
                    if any(kw in row["full_text"] for kw in keywords):
                        matches_by_tag.append(row)

        if matches_by_tag:
            rows = pd.DataFrame(matches_by_tag).drop_duplicates().head(3)
            responses = [
                f"🌿 {row['nom']}\n📜 {row['description']} (Price: {row['prix']} TND)"
                for _, row in rows.iterrows()
            ]
            last_recommended_pack["name"] = rows.iloc[0]["nom"]
            return jsonify({
                "type": "keyword-match",
                "response": "Here’s what I found based on your request:\n\n" + "\n\n".join(responses)
            })

        # 7️⃣ Semantic fallback
        pack_embeddings = model.encode(df_packs["full_text"].tolist(), convert_to_tensor=True)
        sim_scores = util.cos_sim(user_embedding, pack_embeddings)[0]
        matches = [(i, score.item()) for i, score in enumerate(sim_scores) if score.item() >= 0.55]
        matches.sort(key=lambda x: x[1], reverse=True)

        if matches:
            if len(matches) == 1 or matches[0][1] >= 0.75:
                pack = df_packs.iloc[matches[0][0]]
                last_recommended_pack["name"] = pack["nom"]
                return jsonify({
                    "type": "semantic-match",
                    "response": f"🌿 I recommend: {pack['nom']}\n📜 {pack['description']} (Price: {pack['prix']} TND)"
                })
            else:
                responses = []
                for i, score in matches[:5]:
                    pack = df_packs.iloc[i]
                    responses.append(f"🌿 {pack['nom']}\n📜 {pack['description']} (Price: {pack['prix']} TND)")
                last_recommended_pack["name"] = df_packs.iloc[matches[0][0]]["nom"]
                return jsonify({
                    "type": "multi-match",
                    "response": "Here are some options you might like:\n\n" + "\n\n".join(responses)
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
