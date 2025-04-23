from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import os
from uuid import uuid4

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "http://localhost:4200"}})

HF_TOKEN = "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"
HF_MODEL = "stabilityai/stable-diffusion-xl-base-1.0"
HF_API_URL = f"https://api-inference.huggingface.co/models/{HF_MODEL}"

HEADERS = {
    "Authorization": f"Bearer {HF_TOKEN}"
}

# Correct path to Spring project's static folder
SPRING_STATIC_PATH = r"C:\Users\Aziz\Desktop\NatureLink-Back-Aziz\static"
os.makedirs(SPRING_STATIC_PATH, exist_ok=True)

@app.route("/generate-image", methods=["POST"])
def generate_image():
    data = request.json
    prompt = data.get("prompt")

    if not prompt:
        return jsonify({"error": "Prompt is required"}), 400

    try:
        response = requests.post(
            HF_API_URL,
            headers=HEADERS,
            json={"inputs": prompt}
        )

        if response.status_code == 200:
            unique_filename = f"generated_{uuid4().hex[:8]}.png"
            image_path = os.path.join(SPRING_STATIC_PATH, unique_filename)
            with open(image_path, "wb") as f:
                f.write(response.content)
            print(f"Image saved: {image_path}")
            return jsonify({
                "image_url": unique_filename
            })
        else:
            print(f"Hugging Face API error: {response.status_code} - {response.json()}")
            return jsonify({
                "error": "Failed to generate image",
                "details": response.json().get("error", "Unknown error")
            }), response.status_code

    except Exception as e:
        print(f"Error in generate_image: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5003, debug=True, use_reloader=False)