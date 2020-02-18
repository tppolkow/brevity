from heapq import nlargest

from cleaner import Cleaner
from grapher import Grapher
from matrix_builder import MatrixBuilder


class Extractor:
    @staticmethod
    def extract():
        c = Cleaner()
        text = c.clean('../../data/in5.txt')

        m = MatrixBuilder()
        matrix = m.build_sim_matrix(text)
        # print(matrix)
        # print('Dimensions: {}'.format(matrix.shape))

        g = Grapher()
        pageranks = g.graph(matrix)
        # print(m.sentences)
        print(pageranks)

        result = nlargest(50, pageranks, key=pageranks.get)
        result.sort()
        print(result)

        for key in result:
            print(m.sentences[key])


ext = Extractor()
ext.extract()
