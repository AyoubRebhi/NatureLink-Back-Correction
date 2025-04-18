import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class ActivityRecommender:
    def __init__(self):
        self.vectorizer = TfidfVectorizer(stop_words='english')

    def recommend_from_list(self, user_input, activity_list, top_n=3):
        df = pd.DataFrame(activity_list)
    df.fillna('', inplace=True)

    def weighted_combine(row):
        mood = ' '.join([f"{m} " * 3 for m in str(row.get('mood', '')).split(',')])
        tags = ' '.join([f"{t} " * 2 for t in str(row.get('tags', '')).split(',')])
        return f"{row.get('type', '')} {mood} {tags} {row.get('description', '')}"

        df['combined'] = df.apply(weighted_combine, axis=1)

        vectors = self.vectorizer.fit_transform(df['combined'])
        input_vec = self.vectorizer.transform([user_input.lower()])
        similarities = cosine_similarity(input_vec, vectors).flatten()

        df['similarity'] = similarities
        recommendations = df.sort_values(by='similarity', ascending=False).head(top_n)

    # ✅ Make sure imageUrls is included
        if 'imageUrls' not in recommendations.columns:
            recommendations['imageUrls'] = [[] for _ in range(len(recommendations))]
        print("🔥 Recommended with imageUrls:")
        print(recommendations[['name', 'imageUrls']])

        return recommendations.to_dict(orient='records')
