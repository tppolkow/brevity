import heapq
import re

import nltk
from kafka import KafkaConsumer
from kafka import KafkaProducer

summary_len = 20

producer = KafkaProducer(bootstrap_servers='localhost:9092')
consumer = KafkaConsumer('brevity_requests',
                         bootstrap_servers=['localhost:9092'])

for message in consumer:
    key = str(message.key)
    article_text_array = str(message.value)
    article_text = ""
    for sentence in article_text_array:
        article_text += sentence

    # Removing Square Brackets and Extra Spaces
    article_text = re.sub(r'\[[0-9]*\]', ' ', article_text)
    article_text = re.sub(r'\s+', ' ', article_text)

    # Removing special characters and digits
    formatted_article_text = re.sub('[^a-zA-Z]', ' ', article_text)
    formatted_article_text = re.sub(r'\s+', ' ', formatted_article_text)

    # Converting text to sentences
    sentence_list = nltk.sent_tokenize(article_text)

    # Finding weighted frequency of occurence
    stopwords = nltk.corpus.stopwords.words('english')

    word_frequencies = {}
    for word in nltk.word_tokenize(formatted_article_text):
        if word not in stopwords:
            if word not in word_frequencies.keys():
                word_frequencies[word] = 1
            else:
                word_frequencies[word] += 1

    maximum_frequncy = max(word_frequencies.values())

    for word in word_frequencies.keys():
        word_frequencies[word] = (word_frequencies[word] / maximum_frequncy)

    # Calculating sentence scores
    sentence_scores = {}
    for sent in sentence_list:
        for word in nltk.word_tokenize(sent.lower()):
            if word in word_frequencies.keys():
                if len(sent.split(' ')) < 30:
                    if sent not in sentence_scores.keys():
                        sentence_scores[sent] = word_frequencies[word]
                    else:
                        sentence_scores[sent] += word_frequencies[word]

    # Getting the summary
    summary_sentences = heapq.nlargest(summary_len, sentence_scores,
                                       key=sentence_scores.get)

    summary = ' '.join(summary_sentences)
    # TODO: clean this replacement up
    summary = summary.replace('-\\n', '')
    summary = summary.replace('\n', ' ')
    summary = summary.replace('\\n', ' ')
    summary = summary.replace('\\xe2', ' ')
    summary = summary.replace('\\x80', ' ')
    summary = summary.replace('\\x99', ' ')
    summary = summary.replace('\\x9c', ' ')
    summary = summary.replace('\\x9d', ' ')
    print(summary)

    producer.send('brevity_responses', str.encode(summary), key=key.encode())
