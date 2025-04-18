from flask import Flask, request, jsonify
from recommender import ActivityRecommender
from flask_cors import CORS
CORS(app)

app = Flask(__name__)
recommender = ActivityRecommender()  # No longer needs CSV path since we'll receive activities

@app.route("/recommend", methods=["POST"])
def recommend():
    try:
        data = request.get_json()
        mood_input = data.get("mood_input", "")
        activity_data = data.get("activities", [])

        # Call the recommender
        recommendations = recommender.recommend_from_list(mood_input, activity_data)
        return jsonify(recommendations)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5005)