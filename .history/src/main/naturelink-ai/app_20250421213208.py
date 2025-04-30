<<<<<<< HEAD
# app.py
from flask import Flask, request, jsonify
from recommender import ActivityRecommender

import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


app = Flask(__name__)
recommender = ActivityRecommender("activities.csv")

@app.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json()
    mood_input = data.get("mood_input", "")
    activity_data = data.get("activities", [])

    df = pd.DataFrame(activity_data)
    df.fillna('', inplace=True)

    def weighted_combine(row):
        mood = ' '.join([f"{m} " * 3 for m in row['mood'].split(',')]) if isinstance(row['mood'], str) else ''
        tags = ' '.join([f"{t} " * 2 for t in row['tags'].split(',')]) if isinstance(row['tags'], str) else ''
        return f"{row['type']} {mood} {tags} {row['description']}"

    df['combined'] = df.apply(weighted_combine, axis=1)
    vectorizer = TfidfVectorizer()
    vectors = vectorizer.fit_transform(df['combined'])
    input_vec = vectorizer.transform([mood_input])
    similarities = cosine_similarity(input_vec, vectors).flatten()

    df['similarity'] = similarities
    top_df = df.sort_values(by='similarity', ascending=False).head(5)
    return jsonify(top_df.to_dict(orient="records"))


if __name__ == '__main__':
    app.run(port=5005)
=======
from flask import Flask, request, jsonify
from recommender import ActivityRecommender
from flask_cors import CORS
import logging

app = Flask(__name__)
CORS(app)

# Initialize logger
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize recommender (this will load the BERT model)
logger.info("Loading BERT model...")
recommender = ActivityRecommender()
logger.info("BERT model loaded successfully")

@app.route("/recommend", methods=["POST"])
def recommend():
    try:
        data = request.get_json()
        mood_input = data.get("mood_input", "").strip()
        activity_data = data.get("activities", [])

        if not mood_input:
            return jsonify({"error": "Mood input is required"}), 400
        if not activity_data:
            return jsonify({"error": "Activity list is empty"}), 400

        logger.info(f"Received recommendation request for mood: {mood_input}")

        recommendations = recommender.recommend_from_list(mood_input, activity_data)
        return jsonify({
            "recommendations": recommendations,
            "status": "success"
        })
    except Exception as e:
        logger.error(f"Error in recommendation: {str(e)}", exc_info=True)
        return jsonify({
            "error": "An error occurred during recommendation",
            "details": str(e)
        }), 500

if __name__ == '__main__':
    app.run(port=5005, debug=True)
>>>>>>> origin/ayoub
