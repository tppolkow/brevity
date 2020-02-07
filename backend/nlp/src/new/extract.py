from new.sentence import Sentence
from new.paragraph import Paragraph

import numpy as np


class Extract:
    sents = list()
    paras = list()
    summary = list()
    sent_count = 0
    para_count = 0
    sim_mat = np.zeros((0, 0))

    def tokenize_text(self):
        pass

    def get_comm_word_count(self, sentence1, sentence2):
        sent1_words = sentence1.split()
        sent2_words = sentence2.split()
        return len(set(sent1_words).intersection(set(sent2_words)))

    def create_sim_mat(self):
        self.sim_mat = np.zeros((self.sent_count,
                                 self.sent_count))
        for i in range(0, self.sent_count):
            for j in range(0, self.sent_count):
                if i <= j:
                    sent1 = self.sents[i]
                    sent2 = self.sents[j]
                    self.sim_mat[i][j] = \
                        self.get_comm_word_count(sent1, sent2) / (
                                (sent1.word_count + sent2.word_count) / 2)
                else:
                    self.sim_mat[i][j] = self.sim_mat[j][i]

    def print_summary(self):
        pass
