# event_model.py

import pandas as pd
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
import torch

class EventRecommender:
    def __init__(self):
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        self.model = self.model.to(self.device)

    def recommend_from_list(self, user_input, event_list, top_n=3):
        df = pd.DataFrame(event_list)
        df.fillna('', inplace=True)

        # Texte enrichi pour chaque événement
        def create_bert_text(row):
            components = [
                str(row.get('title', '')),
                str(row.get('description', '')),
                str(row.get('location', '')),
                str(row.get('founder', ''))
            ]
            return ' '.join(components)

        df['bert_text'] = df.apply(create_bert_text, axis=1)

        # Embeddings
        with torch.no_grad():
            event_embeddings = self.model.encode(
                df['bert_text'].tolist(),
                convert_to_tensor=True,
                device=self.device
            )
            user_embedding = self.model.encode(
                [user_input.lower()],
                convert_to_tensor=True,
                device=self.device
            )

        similarities = cosine_similarity(
            user_embedding.cpu().numpy(),
            event_embeddings.cpu().numpy()
        ).flatten()

        df['similarity'] = similarities
        recommendations = df.sort_values(by='similarity', ascending=False).head(top_n)

        result = []
        for _, row in recommendations.iterrows():
            event = {
                'id': row.get('id'),
                'title': row.get('title'),
                'description': row.get('description'),
                'location': row.get('location'),
                'date': row.get('date'),
                'founder': row.get('founder'),
                'image': row.get('image'),
                'similarity': row.get('similarity')
            }
            result.append(event)

        return result
