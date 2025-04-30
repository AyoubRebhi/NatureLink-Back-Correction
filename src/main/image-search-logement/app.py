from flask import Flask, request, jsonify
import os
from utils import build_embeddings, find_similar_images
from flask_cors import CORS  # Import CORS

app = Flask(__name__)
UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
CORS(app, origins="http://localhost:4200")  # or origins=["http://localhost:4200"]

@app.route("/build", methods=["POST"])
def build():
    build_embeddings()
    return jsonify({"message": "Embeddings built!"})

@app.route("/search", methods=["POST"])
def search():
    if "image" not in request.files:
        return jsonify({"error": "No image uploaded"}), 400

    file = request.files["image"]
    path = os.path.join(UPLOAD_FOLDER, file.filename)
    file.save(path)

    # Get similar images using the full path
    similar_images = find_similar_images(path)

    return jsonify({"matches": similar_images})

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)