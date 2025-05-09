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
from difflib import get_close_matches

app = Flask(__name__)
CORS(app)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load model and general Q&A
with open(os.path.join(BASE_DIR, "chat_model.pkl"), "rb") as f:
    model = pickle.load(f)
with open(os.path.join(BASE_DIR, "general_qa.json"), "r", encoding="utf-8") as f:
    general_qa = json.load(f)
general_questions = [q["question"] for q in general_qa]
general_embeddings = model.encode(general_questions, convert_to_tensor=True)

# Tunisian knowledge base
TUNISIA_KNOWLEDGE = {
    "locations": [
        "bizerte", "mateur", "menzel bourguiba", "tabarka", "ain draham", "beja", "jendouba", "kef", "siliana",
        "el kef", "sakiet sidi youssef", "hammamet", "nabeul", "kelibia", "sousse", "monastir", "moknine",
        "mahdia", "sfax", "kerkennah", "djerba", "houmt souk", "zarzis", "tataouine", "medenine", "ben guerdane",
        "tozeur", "nefta", "douz", "kebili", "el hamma", "gabes", "matmata", "tunis", "ariana", "la marsa",
        "sidi bou said", "carthage", "ben arous", "ezzahra", "radès", "manouba", "gafsa", "zaghouan"
    ],
    "activities": [
        "hiking", "trekking", "climbing", "quad", "camel", "desert", "off-road", "sandboarding", "spa", "massage",
        "yoga", "wellness", "relax", "photography", "romantic", "adventure", "nature", "historic", "culture",
        "beach", "snorkeling", "scuba", "swimming"
    ],
    "cuisines": [
        "french", "tunisian", "seafood", "couscous", "brik", "tajine", "pizza", "pasta", "burger", "vegetarian", "organic"
    ]
}

# Normalize text
def normalize(text):
    text = text.lower()
    return unicodedata.normalize('NFKD', text).encode('ASCII', 'ignore').decode('utf-8')

# Fuzzy matching
def fuzzy_match(word, vocab):
    matches = get_close_matches(word, vocab, n=1, cutoff=0.75)
    return matches[0] if matches else None

# Detect entities dynamically
def fuzzy_entity_detection(text):
    text = normalize(text)
    entities = {key[:-1]: [] for key in TUNISIA_KNOWLEDGE}
    for category, vocab in TUNISIA_KNOWLEDGE.items():
        for word in text.split():
            match = fuzzy_match(word, vocab)
            if match and match not in entities[category[:-1]]:
                entities[category[:-1]].append(match)
    return entities

# Budget extraction
def extract_budget(text):
    match = re.search(r"(\d{2,5})\s*(tnd|dt|dinar)?", text)
    return float(match.group(1)) if match else None

# Fetch live pack data
def get_live_packs():
    conn = pymysql.connect(host="localhost", user="root", password="1234", database="naturelink")
    df = pd.read_sql("SELECT id, nom, description, prix FROM pack", conn)
    conn.close()
    df["full_text"] = (df["nom"] + " " + df["description"]).apply(normalize)
    return df

@app.route("/chat", methods=["POST"])
def chat():
    try:
        user_msg = request.json.get("message", "").strip()
        if not user_msg:
            return jsonify({"response": "Please enter a message."})

        user_norm = normalize(user_msg)
        df = get_live_packs()
        budget = extract_budget(user_msg)
        entities = fuzzy_entity_detection(user_msg)

        # General question detection
        user_embed = model.encode(user_norm, convert_to_tensor=True)
        sim_gen = util.cos_sim(user_embed, general_embeddings)
        if sim_gen.max().item() > 0.6 and not (budget or any(entities.values())):
            return jsonify({"response": general_qa[sim_gen.argmax().item()]["answer"]})

        # Budget-based filter
        if budget:
            filtered = df[df["prix"] <= budget].sort_values("prix")
            if not filtered.empty:
                reply = "\n\n".join([
                    f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                    for _, row in filtered.iterrows()
                ])
                return jsonify({"response": reply})
            return jsonify({"response": f"Sorry, no packs under {budget} TND."})

        # Cheapest pack logic
        if any(word in user_norm for word in ["cheapest", "cheap", "lowest", "low price"]):
            cheapest = df[df["prix"] == df["prix"].min()]
            reply = "\n\n".join([
                f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                for _, row in cheapest.iterrows()
            ])
            return jsonify({"response": reply})

        # Entity filter
        if any(entities.values()):
            mask = pd.Series(True, index=df.index)
            for cat, values in entities.items():
                if values:
                    mask &= df["full_text"].apply(lambda x: any(v in x for v in values))
            results = df[mask].sort_values("prix").head(4)
            if not results.empty:
                reply = "\n\n".join([
                    f"🌿 {row['nom']}\n📜 {row['description']}\n💵 {row['prix']} TND"
                    for _, row in results.iterrows()
                ])
                return jsonify({"response": reply})
            return jsonify({"response": "Sorry, no packs match your request."})

        return jsonify({"response": "Sorry, I couldn't understand your request. Please try again using real destinations, activities, or prices."})

    except Exception as e:
        return jsonify({"response": f"Error: {str(e)}"})

if __name__ == "__main__":
    app.run(port=5002, debug=True, use_reloader=False)