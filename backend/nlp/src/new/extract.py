from new.sentence import Sentence
from new.paragraph import Paragraph

import numpy as np


class Extract:
    text = ''
    sents = list()
    paras = list()
    summary = list()
    sent_count = 0
    para_count = 0
    sim_mat = np.zeros((0, 0))

    def read_text(self, path_to_text):
        with open(path_to_text, 'r') as file:
            self.text = file.read().replace('\n', '')

    def extract_sents(self):
        raw_paras = self.text.split('\n')
        print(raw_paras)
        print(len(raw_paras))
        for para in raw_paras:
            self.para_count += 1
            sentences = para.split('.')
            for sent in sentences:
                sent_instance = Sentence(self.sent_count, sent, len(sent),
                                         self.para_count)
                self.sents.append(sent_instance)
                self.sent_count += 1

    def group_sents_into_paras(self):
        para_num = 0
        para = Paragraph(0)
        for i in range(0, self.sent_count):
            if self.sents[i].paragraph_number == para_num:
                pass
            else:
                self.paras.append(para)
                para_num += 1
                para = Paragraph(para_num)
            para.sentences.append(self.sents[i])
        self.paras.append(para)

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

    def print_sentences(self):
        for sent in self.sents:
            print('Sentence #: {}'.format(sent.number))
            print('Value: {}'.format(sent.value))
            print('Length: {}'.format(sent.length))
            print('Word Count: {}'.format(sent.word_count))
            print('Paragraph #: {}'.format(sent.paragraph_number))
            print('\n')

    def print_summary(self):
        pass


extract = Extract()
extract.read_text('/home/syedmoiz/Documents/brevity/backend/nlp/data/in2.txt')
extract.extract_sents()
extract.print_sentences()
