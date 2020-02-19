import re


class Cleaner:
    @staticmethod
    def clean(text):
        cleaned_sentences = []
        pattern = re.compile('([^\s\w]|_)+')

        sentences = text.split('.')

        # Take out all non-alphanumeric chars
        for sent in sentences:
            # print(sent)
            sent = pattern.sub('', sent)
            cleaned_sentences.append(sent)

        # Remove all empty sentences
        while '' in cleaned_sentences:
            cleaned_sentences.remove('')

        # Remove all words that are not certain length
        min_word_len = 2
        for sent in cleaned_sentences:
            index = cleaned_sentences.index(sent)
            # print('{} {}'.format(index, sent))
            new_sent = ''
            for word in sent.split():
                if word == 'n':
                    # print('Unacceptable word {}'.format(word))
                    pass
                else:
                    new_sent += '{} '.format(word)
            # print(new_sent)
            cleaned_sentences[index] = new_sent

        # Fix up words that are like 'nID' and 'n4A'
        new_cleaned_sentences = []
        for sent in cleaned_sentences:
            new_sent = ''
            for word in sent.split():
                if len(word) < 4:
                    pass
                else:
                    if word[0] == 'n':
                        word = word[1:]
                new_sent += '{} '.format(word)
            new_cleaned_sentences.append(new_sent)

        text = ''
        for cs in new_cleaned_sentences:
            text += '{}.'.format(cs)

        text = text.rstrip('.')

        return text

    def clean_file(self, input_file):
        with open(input_file) as file:
            text = file.read()
            return self.clean(text)


# c = Cleaner()
# # c.clean_file('../../data/in4.txt')
# print(c.clean_file('../../data/in4.txt'))
