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

        # Convert the activities to the format your recommender expects
        formatted_activities = []
        for activity in activity_data:
            formatted = {
                "id": activity.get("id"),
                "name": activity.get("name", ""),
                "description": activity.get("description", ""),
                "type": activity.get("type", ""),
                "mood": activity.get("mood", []),
                "tags": activity.get("tags", []),
                "imageUrls": activity.get("imageUrls", []),
                "requiredEquipment": activity.get("requiredEquipment", [])
            }
            formatted_activities.append(formatted)

        recommendations = recommender.recommend_from_list(mood_input, formatted_activities)
        return jsonify({
            "recommendations": recommendations,
            "status": "success"
        })
    except Exception as e:
        logger.error(f"Error in recommendation: {str(e)}", exc_info=True)
        return jsonify({
            "error": "Failed to process recommendation",
            "details": str(e)
        }), 500
if __name__ == '__main__':
    app.run(port=5005, debug=True)