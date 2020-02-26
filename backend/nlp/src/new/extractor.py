import os
import kafka_helper

from heapq import nlargest
from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder
from kafka import KafkaConsumer
from kafka import KafkaProducer

class Extractor:
    @staticmethod
    def extract(raw_txt):
        c = Cleaner()
        cleaned_text_list = c.clean(raw_txt)

        print(len(cleaned_text_list))
        print('Done cleaning')
        # print(cleaned_text_list)

        m = MatrixBuilder()
        matrix = m.build_sim_matrix(cleaned_text_list)
        # print(matrix)
        # print('Dimensions: {}'.format(matrix.shape))

        print('Done building sim matrix')

        g = Grapher()
        pageranks = g.graph(matrix)
        # print(m.sentences)
        # print(pageranks)

        print('Generated graph and got pageranks')

        summary_length = int(0.2 * len(cleaned_text_list))
        top_ranked = nlargest(summary_length, pageranks, key=pageranks.get)
        top_ranked.sort()
        # print(result)

        result = ''
        for key in top_ranked:
            top_ranked_sentence = m.sentences[key]
            # print('.{}.'.format(top_ranked_sentence))
            result += '{}. '.format(top_ranked_sentence)

        return result

prefix = os.getenv('KAFKA_PREFIX', '')
servers = kafka_helper.get_kafka_brokers()
certs = kafka_helper.get_kafka_ssl_context()
producer = KafkaProducer(
    bootstrap_servers = servers,
    security_protocol = 'SSL', 
    ssl_context = certs
)
consumer = KafkaConsumer(
    prefix + 'brevity_request', 
    bootstrap_servers = servers, 
    group_id = prefix + 'brevity_consumer',
    security_protocol = 'SSL', 
    ssl_context = certs
)

ext = Extractor()

for message in consumer:
    key = str(message.key)
    text_array = message.value.decode('utf-8')
    text = ''
    for character in text_array:
        text += character

    summary = ext.extract(raw_txt=text)

    print(summary)

    producer.send(prefix + 'brevity_responses', str.encode(summary), key=key.encode())
