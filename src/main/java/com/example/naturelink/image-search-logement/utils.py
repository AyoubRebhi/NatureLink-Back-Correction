import os
import json
import numpy as np
from model import get_image_embedding
from sklearn.metrics.pairwise import cosine_similarity

EMBEDDING_PATH = "embeddings.json"
IMAGE_DIR = "uploads"
BASE_URL = "http://localhost:5000"  # Your base URL for image access

def build_embeddings():
    embeddings = []
    for filename in os.listdir(IMAGE_DIR):
        path = os.path.join(IMAGE_DIR, filename)
        if path.lower().endswith((".jpg", ".png", ".jpeg")):
            emb = get_image_embedding(path).tolist()
            embeddings.append({"path": path, "embedding": emb})  # Full path is saved here
    with open(EMBEDDING_PATH, "w") as f:
        json.dump(embeddings, f)

def find_similar_images(uploaded_image_path, top_k=3):
    with open(EMBEDDING_PATH, "r") as f:
        embeddings = json.load(f)

    if not embeddings:
        return []

    uploaded_emb = get_image_embedding(uploaded_image_path).reshape(1, -1)
    all_embs = np.array([e["embedding"] for e in embeddings])
    similarities = cosine_similarity(uploaded_emb, all_embs)[0]
    top_indices = similarities.argsort()[-top_k:][::-1]

    return [
        {
            "image_name": os.path.basename(embeddings[i]["path"]),  # Full image name (including the unique identifier)
            "score": round(float(similarities[i]), 4),
            "url": f"{BASE_URL}/{embeddings[i]['path']}"  # Full URL for image access
        }
        for i in top_indices
    ]
