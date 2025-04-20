from flask import Flask, request, jsonify
import json
import re
import os

app = Flask(__name__)

# ✅ Show working directory
cwd = os.getcwd()
print(f"[INFO] Current Working Directory: {cwd}")

# ✅ Load validation rules
try:
    with open("src/main/python-ml/validation_rules.json", "r", encoding="utf-8") as f:
        rules = json.load(f)
except FileNotFoundError:
    print("[ERROR] 'validation_rules.json' file not found. Make sure it's in the same folder.")
    exit(1)

# ✅ Load curse words (skip very short ones)
curse_words = []
for lang, words in rules["curse_words"].items():
    for word in words:
        clean_word = word.strip().lower()
        if len(clean_word) > 3:
            curse_words.append(clean_word)

# Add edge case manually
curse_words.append("nigga")

print(f"[INFO] Total curse words loaded: {len(curse_words)}")

# ✅ Compile safe regex patterns
too_strict = ["{5,", "[^A-Za-zÀ-ÿ]+", "[a-zA-Z]{10", "^(.)\\1{4,}"]
safe_patterns = [p for p in rules["weird_name_patterns"] if not any(x in p for x in too_strict)]
compiled_patterns = [re.compile(p, re.IGNORECASE) for p in safe_patterns]

# ✅ Preprocess name
def preprocess(name):
    return re.sub(r"\s+", " ", name.strip().lower())

# ✅ Validate logic
def validate_name(name):
    name = preprocess(name)

    # Check for curse words
    for curse in curse_words:
        if curse in name:
            print(f"[BLOCKED] Curse word detected: '{curse}' in name: '{name}'")
            return "INVALID"

    # Check weird patterns
    for pattern in compiled_patterns:
        if pattern.search(name):
            print(f"[BLOCKED] Pattern matched: '{pattern.pattern}' in name: '{name}'")
            return "INVALID"

    # Check valid format
    if re.match(r"^[A-Za-zÀ-ÿ'\- ]{2,50}$", name):
        return "VALID"

    return "INVALID"

# ✅ API route
@app.route("/validate", methods=["POST"])
def validate():
    data = request.get_json()
    names = data.get("name", [])

    if isinstance(names, list):
        return jsonify([{"name": n, "result": validate_name(n)} for n in names])
    else:
        return jsonify({"name": names, "result": validate_name(names)})

if __name__ == "__main__":
    app.run(port=5001, debug=False, use_reloader=False)
