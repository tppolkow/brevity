from heapq import nlargest

from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder
from kafka import KafkaConsumer
from kafka import KafkaProducer


class Extractor:
    @staticmethod
    def extract(raw_txt, summary_length):
        c = Cleaner()
        text = c.clean(raw_txt)

        m = MatrixBuilder()
        matrix = m.build_sim_matrix(text)
        # print(matrix)
        # print('Dimensions: {}'.format(matrix.shape))

        g = Grapher()
        pageranks = g.graph(matrix)
        # print(m.sentences)
        # print(pageranks)

        top_ranked = nlargest(summary_length, pageranks, key=pageranks.get)
        top_ranked.sort()
        # print(result)

        result = ''
        for key in top_ranked:
            top_ranked_sentence = m.sentences[key].strip()
            # print('.{}.'.format(top_ranked_sentence))
            result += '{}. '.format(top_ranked_sentence)

        # print(result)
        return result


producer = KafkaProducer(bootstrap_servers='localhost:9092')
consumer = KafkaConsumer('brevity_requests',
                         bootstrap_servers=['localhost:9092'])

ext = Extractor()

for message in consumer:
    print("got kafka msg")
    key = str(message.key)
    print(key)
    text_array = str(message.value)
    text = ""
    sent_count = 0
    for sentence in text_array:
        text += sentence
        sent_count += 1

    summary_len = int(sent_count / 5)
    if summary_len > 20:
        summary_len = 20

    # print('{} {}'.format(sent_count, summary_len))
    summary = ext.extract(raw_txt=text, summary_length=summary_len)
    # print(summary)

    producer.send('brevity_responses', str.encode(summary), key=key.encode())
