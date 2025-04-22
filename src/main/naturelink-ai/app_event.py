# app_event.py

from flask import Flask, request, jsonify
from event_recommender import EventRecommender

app = Flask(__name__)
recommender = EventRecommender()

@app.route('/recommend/event', methods=['POST'])
def recommend_events():
    data = request.json
    user_input = data.get('user_input', '')
    events = data.get('events', [])
    top_n = data.get('top_n', 3)

    if not user_input or not events:
        return jsonify({'error': 'Missing user input or events'}), 400

    recommendations = recommender.recommend_from_list(user_input, events, top_n)
    return jsonify(recommendations)


if __name__ == '__main__':
    app.run(debug=True)
