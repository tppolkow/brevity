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
        print(pageranks)

        result = nlargest(summary_length, pageranks, key=pageranks.get)
        result.sort()
        print(result)

        for key in result:
            print(m.sentences[key])

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
    for sentence in text_array:
        text += sentence

    summary = ext.extract(raw_txt=text, summary_length=50)

    producer.send('brevity_responses', str.encode(summary), key=key.encode())
