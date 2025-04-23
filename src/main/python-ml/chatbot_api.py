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
CORS(app)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load model and general Q&A
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)
with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
    general_qa = json.load(f)
general_embeddings = model.encode([q["question"] for q in general_qa], convert_to_tensor=True)

# Tunisian knowledge base
TUNISIA_KNOWLEDGE = {
    "locations": {
        "all": [
            "bizerte", "mateur", "menzel bourguiba", "tabarka", "ain draham", "beja", "jendouba", "kef", "siliana",
            "el kef", "sakiet sidi youssef", "hammamet", "nabeul", "kelibia", "sousse", "monastir", "moknine",
            "mahdia", "sfax", "kerkennah", "djerba", "houmt souk", "zarzis", "tataouine", "medenine", "ben guerdane",
            "tozeur", "nefta", "douz", "kebili", "el hamma", "gabes", "matmata", "tunis", "ariana", "la marsa",
            "sidi bou said", "carthage", "ben arous", "ezzahra", "radès", "manouba", "gafsa", "zaghouan"
        ]
    },
    "activities": {
        "all": [
            "hiking", "trekking", "climbing", "quad", "camel", "desert", "off-road", "sandboarding", "spa", "massage",
            "yoga", "wellness", "relax", "photography", "romantic", "adventure", "nature", "historic", "culture",
            "beach", "snorkeling", "scuba", "swimming"
        ]
    },
    "cuisines": {
        "all": [
            "french", "tunisian", "seafood", "couscous", "brik", "tajine", "pizza", "pasta", "burger", "vegetarian"
        ]
    }
}

def normalize(text):
    text = text.lower()
    return unicodedata.normalize('NFKD', text).encode('ASCII', 'ignore').decode('utf-8')

def get_live_packs():
    conn = pymysql.connect(host="localhost", user="root", password="0000", database="naturelink")
    df = pd.read_sql("SELECT id, nom, description, prix FROM pack", conn)
    conn.close()
    df["full_text"] = (df["nom"] + " " + df["description"]).apply(normalize)
    return df

def extract_budget(text):
    match = re.search(r"(\d{2,5})\s*(tnd|dt|dinar)?", text)
    return float(match.group(1)) if match else None

def extract_entities(text):
    text = normalize(text)
    entities = {"location": [], "activity": [], "cuisine": []}
    for loc in TUNISIA_KNOWLEDGE["locations"]["all"]:
        if loc in text: entities["location"].append(loc)
    for act in TUNISIA_KNOWLEDGE["activities"]["all"]:
        if act in text: entities["activity"].append(act)
    for cui in TUNISIA_KNOWLEDGE["cuisines"]["all"]:
        if cui in text: entities["cuisine"].append(cui)
    return entities

@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_msg = request.json.get("message", "").strip()
        if not user_msg:
            return jsonify({"response": "Please enter a message."})

        user_norm = normalize(user_msg)
        df = get_live_packs()
        budget = extract_budget(user_msg)
        entities = extract_entities(user_msg)

        user_embed = model.encode(user_norm, convert_to_tensor=True)
        sim_gen = util.cos_sim(user_embed, general_embeddings)

        # ✅ General Q&A if it's purely general
        if sim_gen.max().item() > 0.7 and not (budget or any(entities.values())):
            best = general_qa[sim_gen.argmax().item()]["answer"]
            return jsonify({"response": best})

        # ✅ Budget-based filter
        if budget:
            df_bud = df[df["prix"] <= budget]
            if not df_bud.empty:
                reply = "\n\n".join([
                    f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                    for _, row in df_bud.sort_values("prix").iterrows()
                ])
                return jsonify({"response": reply})
            return jsonify({"response": f"Sorry, no packs under {budget} TND."})

        # ✅ Cheapest
        if any(word in user_norm for word in ["cheapest", "cheap", "lowest", "low price"]):
            min_price = df["prix"].min()
            df_min = df[df["prix"] == min_price]
            reply = "\n\n".join([
                f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                for _, row in df_min.iterrows()
            ])
            return jsonify({"response": reply})

        # ✅ Entity-based filtering
        has_entities = any(entities.values())
        if has_entities:
            mask = pd.Series(True, index=df.index)
            if entities["location"]:
                mask &= df["full_text"].apply(lambda x: any(loc in x for loc in entities["location"]))
            if entities["activity"]:
                mask &= df["full_text"].apply(lambda x: any(act in x for act in entities["activity"]))
            if entities["cuisine"]:
                mask &= df["full_text"].apply(lambda x: any(cui in x for cui in entities["cuisine"]))
            df_filtered = df[mask]
            if not df_filtered.empty:
                reply = "\n\n".join([
                    f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                    for _, row in df_filtered.sort_values("prix").head(4).iterrows()
                ])
                return jsonify({"response": reply})
            return jsonify({"response": "Sorry, no packs match your request."})

        # ❌ No budget or valid entities or general Q&A: deny
        return jsonify({"response": "Sorry, I couldn't understand your request. Please try again using real destinations, activities, or prices."})

    except Exception as e:
        return jsonify({"response": f"Error: {str(e)}"})

if __name__ == "__main__":
    app.run(port=5002, debug=True, use_reloader=False)
