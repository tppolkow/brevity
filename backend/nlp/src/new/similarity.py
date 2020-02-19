from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize

import nltk

# nltk.download('punkt')
# nltk.download('stopwords')


class Similarity:
    @staticmethod
    def calculate_similarity_score(x, y):
        # x_list = word_tokenize(x)
        # print(x_list)
        # y_list = word_tokenize(y)
        # print(y_list)

        x_list = x.split()
        # print(x_list)
        y_list = y.split()
        # print(y_list)

        sw = stopwords.words('english')
        l1 = []
        l2 = []

        x_set = {w for w in x_list if not w in sw}
        y_set = {w for w in y_list if not w in sw}

        rvector = x_set.union(y_set)
        for w in rvector:
            if w in x_set:
                l1.append(1)  # create a vector
            else:
                l1.append(0)
            if w in y_set:
                l2.append(1)
            else:
                l2.append(0)
        c = 0

        # cosine formula
        for i in range(len(rvector)):
            c += l1[i] * l2[i]
        cosine = c / float((sum(l1) * sum(l2)) ** 0.5)
        return cosine
