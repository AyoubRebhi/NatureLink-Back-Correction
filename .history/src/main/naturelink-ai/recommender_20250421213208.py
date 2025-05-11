<<<<<<< HEAD
# recommender.py
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class ActivityRecommender:
    def __init__(self, csv_path):
        self.df = pd.read_csv(csv_path)
        self.df.fillna('', inplace=True)
        self.vectorizer = TfidfVectorizer(stop_words='english')
        self.df['combined'] = (
                self.df['type'] + ' ' + self.df['mood'] + ' ' + self.df['tags'] + ' ' + self.df['description']
        ).str.lower()
        self.activity_vectors = self.vectorizer.fit_transform(self.df['combined'])

    def recommend(self, user_input, top_n=5):
        input_vector = self.vectorizer.transform([user_input.lower()])
        similarities = cosine_similarity(input_vector, self.activity_vectors).flatten()
        self.df['similarity'] = similarities
        recommendations = self.df.sort_values(by='similarity', ascending=False).head(top_n)
        return recommendations.to_dict(orient='records')
=======
import pandas as pd
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import torch

class ActivityRecommender:
    def __init__(self):
        # Load a pre-trained BERT model (using SentenceTransformers for simplicity)
        self.model = SentenceTransformer('all-MiniLM-L6-v2')  # Lightweight but effective model
        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        self.model = self.model.to(self.device)

    def recommend_from_list(self, user_input, activity_list, top_n=3):
        # Convert to DataFrame
        df = pd.DataFrame(activity_list)
        df.fillna('', inplace=True)

        # Create enhanced text representations
        def create_bert_text(row):
            # Weighted components - mood gets 3x, tags 2x, others 1x
            components = [
                str(row.get('type', '')),
                ' '.join([str(row.get('mood', ''))] * 3),
                ' '.join([str(row.get('tags', ''))] * 2),
                str(row.get('description', ''))
            ]
            return ' '.join(components)

        df['bert_text'] = df.apply(create_bert_text, axis=1)

        # Generate embeddings
        with torch.no_grad():
            # Embed activities
            activity_embeddings = self.model.encode(
                df['bert_text'].tolist(),
                convert_to_tensor=True,
                device=self.device
            )

            # Embed user input
            user_embedding = self.model.encode(
                [user_input.lower()],
                convert_to_tensor=True,
                device=self.device
            )

        # Calculate similarities
        similarities = cosine_similarity(
            user_embedding.cpu().numpy(),
            activity_embeddings.cpu().numpy()
        ).flatten()

        df['similarity'] = similarities
        recommendations = df.sort_values(by='similarity', ascending=False).head(top_n)

        # Ensure imageUrls exists
        if 'imageUrls' not in recommendations.columns:
            recommendations['imageUrls'] = [[] for _ in range(len(recommendations))]

        print("🔥 Recommended with BERT embeddings:")
        print(recommendations[['name', 'similarity', 'imageUrls']])

        result = []
        for _, row in recommendations.iterrows():
            activity = {
                'id': row.get('id'),
                'name': row.get('name'),
                'description': row.get('description'),
                'location': row.get('location'),
                'duration': row.get('duration'),
                'maxParticipants': row.get('maxParticipants'),
                'price': row.get('price'),
                'difficultyLevel': row.get('difficultyLevel'),
                'type': row.get('type'),
                'mood': row.get('mood'),
                'tags': row.get('tags'),
                'requiredEquipment': row.get('requiredEquipment'),
                'imageUrls': row.get('imageUrls', []),
                'similarity': row.get('similarity')
            }
        result.append(activity)
        return result
>>>>>>> origin/ayoub
