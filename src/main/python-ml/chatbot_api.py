from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer, util
import pymysql
import pandas as pd
import pickle
import json
import re
import os
import unicodedata
import torch

app = Flask(__name__)
CORS(app, resources={r"/chat": {"origins": "*"}})

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load model and general Q&A
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)
with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
    general_qa = json.load(f)
general_embeddings = model.encode([q["question"] for q in general_qa], convert_to_tensor=True)

# Normalize helper
def normalize(text):
    text = text.lower()
    return unicodedata.normalize('NFKD', text).encode('ASCII', 'ignore').decode('utf-8')

# DB connection
def get_live_packs():
    conn = pymysql.connect(host="localhost", user="root", password="0000", database="naturelink")
    df = pd.read_sql("SELECT id, nom, description, prix FROM pack", conn)
    conn.close()
    df["full_text"] = (df["nom"] + " " + df["description"]).apply(normalize)
    return df

# Budget matcher
def extract_budget(text):
    match = re.search(r"(\d{2,5})\s*(tnd|dt|dinar)?", text)
    return float(match.group(1)) if match else None

# Entity extraction
def extract_entities(text):
    text = normalize(text)
    locations = ["hammamet", "ain draham", "djerba", "kef", "zaghouan", "tunis"]
    cuisines = ["french", "tunisian", "seafood"]
    activities = ["hiking", "photography", "relax", "romantic", "beach", "culture"]

    ent = {"location": None, "cuisine": None, "activity": None}
    for l in locations:
        if l in text: ent["location"] = l
    for c in cuisines:
        if c in text: ent["cuisine"] = c
    for a in activities:
        if a in text: ent["activity"] = a
    return ent

@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_msg = request.json.get("message", "").strip()
        if not user_msg:
            return jsonify({"response": "Please enter a message."})

        df = get_live_packs()
        user_norm = normalize(user_msg)
        budget = extract_budget(user_msg)
        entities = extract_entities(user_msg)

        # 1. General Q&A detection
        user_embed = model.encode(user_norm, convert_to_tensor=True)
        sim_gen = util.cos_sim(user_embed, general_embeddings)
        if sim_gen.max().item() > 0.65:
            best = general_qa[sim_gen.argmax().item()]["answer"]
            return jsonify({"response": best})

        # 2. Budget
        if budget:
            df_budget = df[df["prix"] <= budget]
            if not df_budget.empty:
                top = df_budget.sort_values("prix").iloc[0]
                return jsonify({
                    "response": f"🌿 Under {budget} TND: {top['nom']}\n📜 {top['description']}\n💵 {top['prix']} TND"
                })
            return jsonify({"response": f"Sorry, no packs under {budget} TND found."})

        # 3. Cheapest
        if any(x in user_norm for x in ["cheap", "cheapest", "low price"]):
            cheapest = df.loc[df["prix"].idxmin()]
            return jsonify({
                "response": f"🌿 Cheapest pack: {cheapest['nom']}\n📜 {cheapest['description']}\n💵 {cheapest['prix']} TND"
            })

        # 4. Entity filter
        mask = pd.Series(True, index=df.index)
        if entities["location"]:
            mask &= df["full_text"].str.contains(entities["location"])
        if entities["cuisine"]:
            mask &= df["full_text"].str.contains(entities["cuisine"])
        if entities["activity"]:
            mask &= df["full_text"].str.contains(entities["activity"])

        df_entity = df[mask]
        if not df_entity.empty:
            results = df_entity.sort_values("prix").head(3)
            reply = "\n\n".join([
                f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                for _, row in results.iterrows()
            ])
            return jsonify({"response": reply})

        # 5. Semantic fallback
        pack_embeds = model.encode(df["full_text"].tolist(), convert_to_tensor=True)
        sim_scores = util.cos_sim(user_embed, pack_embeds)[0]
        top_indices = torch.topk(sim_scores, k=3).indices.tolist()
        reply = "\n\n".join([
            f"🌿 {df.iloc[i]['nom']}\n📜 {df.iloc[i]['description']}\n💵 {df.iloc[i]['prix']} TND"
            for i in top_indices
        ])
        return jsonify({"response": reply})

    except Exception as e:
        return jsonify({"response": f"Error: {str(e)}"})

if __name__ == "__main__":
    app.run(port=5002, debug=True, use_reloader=False)
