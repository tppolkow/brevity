from heapq import nlargest

from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder
from kafka import KafkaConsumer
from kafka import KafkaProducer
import struct

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


producer = KafkaProducer(bootstrap_servers='localhost:9092')
consumer = KafkaConsumer('brevity_requests',
                         bootstrap_servers=['localhost:9092'])

ext = Extractor()

for message in consumer:
    # unpack the summary id, set > for big endian, Q for unsigned long
    (key,) = struct.unpack('>Q', message.key)
    print("Processing summary id :",key)

    text_array = message.value.decode('utf-8')
    text = ''
    for character in text_array:
        text += character

    summary = ext.extract(raw_txt=text)

    print(summary)

    producer.send('brevity_responses', str.encode(summary), struct.pack('>Q', key))

