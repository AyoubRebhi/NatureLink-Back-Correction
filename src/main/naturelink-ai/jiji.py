from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import os
import json
from dotenv import load_dotenv
import re

# Chargement des variables d’environnement
load_dotenv()

app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "http://localhost:4200"}})

# Clé API Gemini et URL
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "AIzaSyDyNz4ohakiDI8lZQnQPyHHI38jKtAUN7E")
GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

def clean_gemini_response(text):
    text = re.sub(r"^```json|^```|```$", "", text.strip(), flags=re.IGNORECASE)
    return text.strip()

def generate_travel_itinerary(destination, days, comfort_level, travel_style):
    prompt = f"""
    Crée un itinéraire de voyage détaillé pour {destination} sur {days} jours.
    Niveau de confort: {comfort_level}
    Style de voyage: {travel_style}

    L'itinéraire doit inclure:
    - Pour chaque jour: activités matin, après-midi et soirée
    - Lieux à visiter avec brève description
    - Conseils pratiques (transports, réservations)
    - Options de restauration
    - Budget estimé

    IMPORTANT: Retourne uniquement du JSON sans backticks ni préfixe.
    Format :
    {{
        "destination": "...",
        "days": ...,
        "comfort_level": "...",
        "travel_style": "...",
        "itinerary": [...],
        "estimated_budget": "...",
        "additional_tips": "..."
    }}
    """

    try:
        response = requests.post(
            f"{GEMINI_API_URL}?key={GEMINI_API_KEY}",
            headers={'Content-Type': 'application/json'},
            json={"contents": [{"parts": [{"text": prompt}]}]},
            timeout=30
        )

        if response.status_code == 200:
            text = response.json()['candidates'][0]['content']['parts'][0]['text']
            print(" Réponse brute de Gemini:", text)

            clean_text = clean_gemini_response(text)
            print(" Après nettoyage:", clean_text)

            if not clean_text:
                return {"status": "error", "message": "Empty response from Gemini"}

            try:
                itinerary = json.loads(clean_text)
                return {"status": "success", "data": itinerary}
            except Exception as e:
                print(" JSON parsing error:", str(e))
                return {"status": "error", "message": f"JSON error: {str(e)}", "raw": clean_text}

        else:
            return {
                "status": "error",
                "message": f"Gemini API error: {response.text}",
                "code": response.status_code
            }

    except Exception as e:
        return {"status": "error", "message": str(e)}

@app.route('/api/generate-itinerary', methods=['POST'])
def api_generate_itinerary():
    data = request.get_json()
    print("Données reçues:", data)

    required_fields = ['destination', 'days', 'comfort_level', 'travel_style']
    if not all(f in data for f in required_fields):
        return jsonify({"status": "error", "message": "Missing fields"}), 400

    try:
        if not isinstance(data['days'], int) or data['days'] <= 0:
            return jsonify({"status": "error", "message": "Invalid days"}), 400

        result = generate_travel_itinerary(
            data['destination'],
            data['days'],
            data['comfort_level'],
            data['travel_style']
        )

        return jsonify(result), 200 if result["status"] == "success" else 500

    except Exception as e:
        print("Erreur API:", str(e))
        return jsonify({"status": "error", "message": str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health():
    return jsonify({"status": "healthy", "service": "travel_itinerary", "version": "1.0"})

if __name__ == "__main__":
    app.run(debug=True, port=5010, use_reloader=False)
