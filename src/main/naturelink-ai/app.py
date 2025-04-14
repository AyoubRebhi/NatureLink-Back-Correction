# app.py
from flask import Flask, request, jsonify
from recommender import ActivityRecommender

app = Flask(__name__)
recommender = ActivityRecommender("activities.csv")

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json()
    user_input = data.get('query', '')
    top_n = data.get('top_n', 5)
    results = recommender.recommend(user_input, top_n)
    return jsonify(results)

if __name__ == '__main__':
    app.run(port=5005)
