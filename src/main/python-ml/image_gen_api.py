from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import requests
import os
from uuid import uuid4

app = Flask(__name__)
CORS(app, resources={r"/generate-image": {"origins": "http://localhost:4200"}})

# 🔐 Hugging Face API Configuration
HF_TOKEN = "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"  # Make sure this token has WRITE permission
HF_MODEL = "stabilityai/stable-diffusion-xl-base-1.0"
HF_API_URL = f"https://api-inference.huggingface.co/models/{HF_MODEL}"

HEADERS = {
    "Authorization": f"Bearer {HF_TOKEN}"
}

# 📁 Ensure static directory exists
os.makedirs("static", exist_ok=True)

# 🖼️ Generate Image API
@app.route("/generate-image", methods=["POST"])
def generate_image():
    data = request.json
    prompt = data.get("prompt")

    if not prompt:
        return jsonify({"error": "Prompt is required"}), 400

    try:
        # Request image from Hugging Face
        response = requests.post(
            HF_API_URL,
            headers=HEADERS,
            json={"inputs": prompt}
        )

        if response.status_code == 200:
            # Save with a unique filename
            unique_filename = f"generated_{uuid4().hex[:8]}.png"
            image_path = os.path.join("static", unique_filename)

            with open(image_path, "wb") as f:
                f.write(response.content)

            return jsonify({
                "image_url": f"http://localhost:5003/static/{unique_filename}"
            })
        else:
            return jsonify({
                "error": "Failed to generate image",
                "details": response.json().get("error", "Unknown error")
            }), 500

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# 🧾 Serve static images
@app.route("/static/<path:filename>")
def serve_static(filename):
    return send_from_directory("static", filename)

# 🚀 Run on port 5003
if __name__ == "__main__":
    app.run(port=5003, debug=False, use_reloader=False)
