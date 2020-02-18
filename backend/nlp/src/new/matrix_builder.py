import numpy as np
import operator
from heapq import nlargest
from cleaner import Cleaner
from grapher import Grapher
from similarity import Similarity


class MatrixBuilder:
    sentences = []

    def build_sim_matrix(self, input_text):
        sim = Similarity()
        self.sentences = input_text.split('.')
        sim_matrix = np.empty([len(self.sentences), len(self.sentences)])

        # print(sentences)

        for i in range(0, len(self.sentences)):
            for j in range(i + 1, len(self.sentences)):
                s1 = self.sentences[i]
                s2 = self.sentences[j]
                try:
                    score = sim.calculate_similarity_score(s1, s2)
                except ZeroDivisionError:
                    # print('Problematic s1 {}'.format(s1))
                    # print('Problematic s2 {}\n'.format(s2))
                    pass

                # print('{} | {} | {},{} | {}'.format(s1, s2, i, j, score))

                sim_matrix[i][j] = round(score, 2)
                sim_matrix[j][i] = sim_matrix[i][j]

            sim_matrix[i][i] = 1.00

        return sim_matrix


c = Cleaner()
text = c.clean('../../data/in3.txt')

m = MatrixBuilder()
matrix = m.build_sim_matrix(text)
# print(matrix)
# print('Dimensions: {}'.format(matrix.shape))

g = Grapher()
pageranks = g.graph(matrix)
# print(m.sentences)
print(pageranks)

result = nlargest(5, pageranks, key=pageranks.get)
result.sort()
print(result)

for key in result:
    print(m.sentences[key])
