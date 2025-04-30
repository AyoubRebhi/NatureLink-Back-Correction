from flask import Flask, request, jsonify
import mysql.connector
import re
from flask_cors import CORS
app = Flask(__name__)
CORS(app)  # Enable CORS for all routes
# Database configuration
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '0000',
    'database': 'naturelink'
}

def determine_search_mode(query):
    """Detect search mode dynamically"""
    query = query.lower()

    avoid_phrases = ['allergic to', 'can\'t eat', 'avoid', 'no', 'without', 'excluding', 'don\'t want']
    want_phrases = ['want', 'looking for', 'craving', 'would like', 'give me', 'show me', 'recommend me']

    avoid_count = sum(1 for phrase in avoid_phrases if phrase in query)
    want_count = sum(1 for phrase in want_phrases if phrase in query)

    if avoid_count > want_count:
        return 'avoid'
    elif want_count > avoid_count:
        return 'want'
    return 'neutral'

def extract_food_terms(query):
    """Extract potential food terms from query"""
    query = query.lower()

    # Remove common non-food words
    stop_words = {'me', 'all', 'items', 'dishes', 'options', 'menu', 'food', 'give', 'show', 'recommend'}
    food_terms = []

    # Look for quoted phrases first
    quoted = re.findall(r'"([^"]*)"|\'([^\']*)\'', query)
    for group in quoted:
        for term in group:
            if term:
                food_terms.extend(term.split())

    # Extract other words
    words = re.findall(r'\b[a-z]{3,}\b', query)
    for word in words:
        if word not in stop_words and word not in food_terms:
            food_terms.append(word)

    return food_terms

@app.route('/api/recommendations', methods=['POST'])
def get_recommendations():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No data provided"}), 400

        query = data.get('query', '').strip()
        restaurant_id = data.get('restaurant_id')

        if not all([query, restaurant_id]):
            return jsonify({"error": "Missing required parameters"}), 400

        mode = determine_search_mode(query)
        food_terms = extract_food_terms(query)

        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor(dictionary=True)

        base_query = """
        SELECT id, plats AS name, prix_moyen AS price,
               image, ingredients_details AS description
        FROM menu
        WHERE restaurant_id = %s
        """
        params = [restaurant_id]

        if mode == 'avoid' and food_terms:
            conditions = []
            for term in food_terms:
                conditions.append("(LOWER(plats) NOT LIKE %s AND LOWER(ingredients_details) NOT LIKE %s)")
                params.extend([f"%{term}%", f"%{term}%"])
            base_query += " AND " + " AND ".join(conditions)

        elif mode == 'want' and food_terms:
            conditions = []
            for term in food_terms:
                conditions.append("(LOWER(plats) LIKE %s OR LOWER(ingredients_details) LIKE %s)")
                params.extend([f"%{term}%", f"%{term}%"])
            base_query += " AND (" + " OR ".join(conditions) + ")"

        # For neutral mode or no terms, return all items for the restaurant

        cursor.execute(base_query, params)
        results = cursor.fetchall()

        return jsonify({
            "status": "success",
            "count": len(results),
            "mode": mode,
            "search_terms": food_terms,
            "recommendations": results
        })

    except mysql.connector.Error as e:
        return jsonify({"error": f"Database error: {e}"}), 500
    except Exception as e:
        return jsonify({"error": f"Server error: {e}"}), 500
    finally:
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == '__main__':
    app.run(port=5007, debug=True, use_reloader=False)