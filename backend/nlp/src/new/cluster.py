class Cluster:
    def splitIntoParagraph(self, numlist, interval):
        x, y = 0,0
        paragraph = []
        while y < len(numlist) - 1:
            if numlist[y + 1] - numlist[y] > interval:
                paragraph.append(numlist[x:y+1])
                x = y + 1

            y = y + 1
        paragraph.append(numlist[x:y+1])
        return paragraph
