from heapq import nlargest

from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder


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


ext = Extractor()
ext.extract(raw_txt='../../data/in5.txt', summary_length=50)
