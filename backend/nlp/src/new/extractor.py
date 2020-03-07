from heapq import nlargest
from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder
from cluster import Cluster
from kafka import KafkaConsumer
from kafka import KafkaProducer

import logging


class Extractor:
    logging.basicConfig(format='%(asctime)s - %(message)s',
                        datefmt='%d-%b-%y %H:%M:%S', level=logging.INFO)

    @staticmethod
    def extract(raw_txt):
        with open('nlp/src/new/out.txt', 'w') as file:
            file.write(raw_txt)

        c = Cleaner()
        cleaned_text_list = c.clean(raw_txt)

        logging.info('Done cleaning')
        logging.debug(len(cleaned_text_list))
        logging.debug(cleaned_text_list)

        matrix_builder = MatrixBuilder()
        matrix = matrix_builder.build_sim_matrix(cleaned_text_list)

        logging.info('Done building sim matrix')
        logging.debug('Dimensions: {}'.format(matrix.shape))
        logging.debug(matrix)

        g = Grapher()
        pageranks = g.graph(matrix)

        logging.info('Generated graph and got pageranks')
        logging.debug(pageranks)

        summary_length = int(0.2 * len(cleaned_text_list))
        top_ranked = nlargest(summary_length, pageranks, key=pageranks.get)
        top_ranked.sort()
        cl = Cluster()

        top_ranked = cl.splitIntoParagraph(top_ranked, 25)


        logging.debug(top_ranked)
        result = ''
        for paragraph in top_ranked:
            for key in paragraph:
                top_ranked_sentence = cleaned_text_list[key]
                result += '{}. '.format(top_ranked_sentence)
            result += '\n\n'

        return result


producer = KafkaProducer(bootstrap_servers='localhost:9092')
consumer = KafkaConsumer('brevity_requests',
                         bootstrap_servers=['localhost:9092'])

ext = Extractor()

for message in consumer:
    key = str(message.key)
    text_array = message.value.decode('utf-8')
    text = ''
    for character in text_array:
        text += character

    summary = ext.extract(raw_txt=text)

    logging.info('Summary: \n{}'.format(summary))

    producer.send('brevity_responses', str.encode(summary), key=key.encode())
