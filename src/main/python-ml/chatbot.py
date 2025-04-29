from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer, util
import json
import os

app = Flask(__name__)
CORS(app)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Load data
with open(os.path.join(BASE_DIR, "data.json"), "r", encoding="utf-8") as f:
    data = json.load(f)

questions = [item['question'] for item in data]
answers = [item['answer'] for item in data]

# Load sentence transformer model
model = SentenceTransformer('all-MiniLM-L6-v2')
question_embeddings = model.encode(questions)

def get_answer(user_input):
    user_embedding = model.encode(user_input)
    scores = util.cos_sim(user_embedding, question_embeddings)[0]
    best_idx = scores.argmax()
    best_score = scores[best_idx].item()

    if best_score > 0.4:
        return answers[best_idx]
    else:
        return "Sorry, I didn't understand your question. Can you rephrase?"

@app.route("/chat", methods=["POST"])
def chat():
    try:
        data = request.get_json()
        user_message = data.get("message", "").strip()
        if not user_message:
            return jsonify({"response": "Please enter a message."}), 400
        response = get_answer(user_message)
        return jsonify({"response": response})

    except Exception as e:
        return jsonify({"response": f"Error: {str(e)}"})

if __name__ == "__main__":
    app.run(port=5020, debug=True, use_reloader=False)
