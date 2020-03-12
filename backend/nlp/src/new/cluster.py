import numpy as np
from numpy import array, linspace
from sklearn.neighbors import KernelDensity
from scipy.signal import argrelextrema


class Cluster:
    def splitIntoParagraph(self, numlist, interval):
        # x, y = 0,0
        # paragraph = []
        # while y < len(numlist) - 1:
        #     if numlist[y + 1] - numlist[y] > interval:
        #         paragraph.append(numlist[x:y+1])
        #         x = y + 1

        #     y = y + 1
        # paragraph.append(numlist[x:y+1])
        # return paragraph

        a = array(numlist).reshape(-1, 1)
        kde = KernelDensity(kernel='gaussian', bandwidth=3).fit(a)
        s = linspace(0,50)
        e = kde.score_samples(s.reshape(-1,1))

        mi, ma = argrelextrema(e, np.less)[0], argrelextrema(e, np.greater)[0]

        print("Minima: ",  s[mi])
        paragraph = []
        for x in s[mi]:
            paragraphList = []
            for i in numlist:
                if i < x:
                    paragraphList.append(i)
                    numlist = list(filter(lambda xx : xx > x , numlist))
            paragraph.append(paragraphList)
        paragraph.append(numlist)
        print(paragraph)
        return paragraph
