# -*- coding: utf-8 -*-

import math
from sklearn.feature_extraction.text import TfidfVectorizer

documents = ["Euler is the father of graph theory",
             "Graph theory studies the properties of graphs",
             "Bioinformatics studies the application of efficient algorithms in biological problems",
             "DNA sequences are very complex structures",
             "Genes are parts of a DNA sequence",
             "Run to the hills, run for your lives",
             "The lonenliness of the long distance runner",
             "Heaven can wait til another day",
             "Road runner and coyote is my favorite cartoon",
             "Heaven can can Heaven can graph"] # the last document is our query

def my_cosine_similarity(vector1, vector2):
    ## TODO: complete the lines as given in the description

tfidf_vectorizer = TfidfVectorizer()

## TODO: get the tabular form of the tf-idf matrix

# define that the query is the last document in the collection
query_vector = tfidf_matrix[9,:]
print(query_vector)

print("Similarity among the query and the documents: ")
for x in range(0,9):
    ## TODO: use the my_cosine_similarity