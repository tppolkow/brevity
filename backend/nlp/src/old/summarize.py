import nltk
import numpy as np
import re
from nltk.corpus import stopwords, brown
from nltk.cluster.util import cosine_distance
from operator import itemgetter

nltk.download('stopwords')
nltk.download('brown')
# np.seterr(divide='ignore', invalid='ignore')

# parameters
MIN_WORD_LEN = 2
MIN_WORDS_IN_SENT = 4


def page_rank(sim_matrix, eps=0.0001, d=0.85):
    R = np.ones(len(sim_matrix))

    while True:
        r = np.ones(len(sim_matrix)) * (1 - d) + d * sim_matrix.T.dot(R)
        if abs(r - R).sum() <= eps:
            return r
        R = r

    # ones_matrix = np.ones(len(sim_matrix))
    # P = ones_matrix / len(sim_matrix)
    # # print(P)
    #
    # while True:
    #     new_P = np.ones(len(sim_matrix)) * (1 - d) / len(sim_matrix) + d * sim_matrix.T.dot(P)
    #     delta = abs(new_P - P).sum()
    #     if delta <= eps:
    #         return new_P
    #     P = new_P


def sentence_similarity(sentence_1, sentence_2, stop_words):
    if stop_words is None:
        stop_words = []

    sent1a = []
    for word in sentence_1:
        sent1a.append(word.lower())

    sent2a = []
    for word in sentence_2:
        sent2a.append(word.lower())

    all_words = list(set(sent1a + sent2a))

    vector1 = [0] * len(all_words)
    vector2 = [0] * len(all_words)

    for word in sentence_1:
        if word in stop_words:
            continue
        vector1[all_words.index(word.lower())] += 1

    for word in sentence_2:
        if word in stop_words:
            continue
        vector2[all_words.index(word.lower())] += 1

    cos_dist = cosine_distance(vector1, vector2)

    val = 1.0 - cos_dist

    return val


def build_similarity_matrix(sentences, stop_words):
    num_sentences = len(sentences)
    s = np.zeros((num_sentences, num_sentences))

    for index1 in range(num_sentences):
        for index2 in range(num_sentences):
            if index1 == index2:
                continue
            sentence_sim = sentence_similarity(sentences[index1], sentences[index2], stop_words)
            s[index1][index2] = sentence_sim

    for index3 in range(len(s)):
        if s[index3].sum() == 0:
            continue
        s[index3] /= s[index3].sum()

    print(s)

    return s


def main():
    stop_words = stopwords.words('english')

    with open("in1.txt", "r") as input_file:
        text = input_file.readlines()

    sentences0 = nltk.sent_tokenize(text[0])

    sentences = []
    for sent in sentences0:
        # Removing Square Brackets and Extra Spaces
        sent = sent.lower()
        sent = re.sub(r'\[[0-9]*\]', ' ', sent)
        sent = re.sub(r'\s+', ' ', sent)

        # Removing special characters and digits
        formatted_sent = re.sub('[^a-zA-Z]', ' ', sent)
        formatted_sent = re.sub(r'\s+', ' ', formatted_sent)

        words = nltk.word_tokenize(formatted_sent)

        # Removing single letter words
        for word in words:
            if len(word) < MIN_WORD_LEN:
                words.remove(word)

        if len(words) > MIN_WORDS_IN_SENT:
            sentences.append(words)

    s = build_similarity_matrix(sentences, stop_words)

    sentence_ranks = page_rank(s)

    # print(sentence_ranks)

    # Sort the sentence ranks
    for item in sorted(enumerate(sentence_ranks), key=lambda item: -item[1]):
        ranked_sentence_indexes = item[0]

    selected_sentences = sorted(ranked_sentence_indexes[:7])

    summary = itemgetter(*selected_sentences)(sentences)

    # for sentence in summary:
    #     print(' '.join(sentence))


main()
