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
            result += m.sentences[key]

        print(result)
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

    print(sent_count)
    summary = ext.extract(raw_txt=text, summary_length=int(sent_count / 5))
    print(summary)

    producer.send('brevity_responses', str.encode(summary), key=key.encode())
