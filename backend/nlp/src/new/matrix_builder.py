import numpy as np

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