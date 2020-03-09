from heapq import nlargest
from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder
from cluster import Cluster
from kafka import KafkaConsumer
from kafka import KafkaProducer

import threading
import logging
import struct


class Extractor:

    @staticmethod
    def extract(raw_txt, logger):

        c = Cleaner()
        cleaned_text_list = c.clean(raw_txt)

        logger.info('Done cleaning')
        logger.debug(len(cleaned_text_list))
        logger.debug(cleaned_text_list)

        matrix_builder = MatrixBuilder()
        matrix = matrix_builder.build_sim_matrix(cleaned_text_list, logger)

        logger.info('Done building sim matrix')
        logger.debug('Dimensions: {}'.format(matrix.shape))
        logger.debug(matrix)

        g = Grapher()
        pageranks = g.graph(matrix)

        logger.info('Generated graph and got pageranks')
        logger.debug(pageranks)

        total_doc_size = len(cleaned_text_list)
        if total_doc_size in range(0, 300):
            summary_length = int(0.4 * total_doc_size)
        elif total_doc_size in range(301, 800):
            summary_length = int(0.2 * total_doc_size)
        elif total_doc_size in range(801, 1500):
            summary_length = int(0.1 * total_doc_size)
        else:
            summary_length = int(0.05 * total_doc_size)

        top_ranked = nlargest(summary_length, pageranks, key=pageranks.get)
        top_ranked.sort()

        cl = Cluster()
        top_ranked = cl.splitIntoParagraph(top_ranked, 10)

        logger.debug(top_ranked)
        result = ''
        for paragraph in top_ranked:
            for key in paragraph:
                top_ranked_sentence = cleaned_text_list[key]
                result += '{}. '.format(top_ranked_sentence)
            result += '\n\n'

        return result


producer = KafkaProducer(bootstrap_servers='localhost:9092')


class ConsumerThread(threading.Thread):
    def __init__(self, logger):
        super(ConsumerThread, self).__init__()
        self.logger = logger

    def run(self):
        consumer = KafkaConsumer('brevity_requests', group_id='nlp-consumers',
                                 bootstrap_servers=['localhost:9092'])

        ext = Extractor()

        for message in consumer:
            # unpack the summary id, set > for big endian, Q for unsigned long
            (key,) = struct.unpack('>Q', message.key)

            text_array = message.value.decode('utf-8')
            text = ''
            for character in text_array:
                text += character

            summary = ext.extract(raw_txt=text, logger=self.logger)

            self.logger.info('Summary: \n{}'.format(summary))

            producer.send('brevity_responses', str.encode(summary),
                          struct.pack('>Q', key))


for i in range(8):
    fname = '../log/nlp' + str(i) + '.log'
    handler = logging.FileHandler(fname)
    handler.setFormatter(logging.Formatter('%(asctime)s - %(message)s'))
    logger = logging.getLogger("NLP" + str(i))
    logger.setLevel(logging.INFO)
    logger.addHandler(handler)

    ConsumerThread(logger).start()
