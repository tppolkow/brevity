import re


class Cleaner:
    @staticmethod
    def clean(text):
        cleaned_sentences = []
        pattern = re.compile('([^\s\w]|_)+')

        sentences = text.split('.')

        for sent in sentences:
            sent = pattern.sub('', sent)
            cleaned_sentences.append(sent)

        while '' in cleaned_sentences:
            cleaned_sentences.remove('')

        text = ''
        for cs in cleaned_sentences:
                text += '{}.'.format(cs)

        text = text.rstrip('.')

        return text

    @staticmethod
    def clean_file(input_file):
        with open(input_file) as file:
            text = file.read()
            return clean(text)

# c = Cleaner()
# print(c.clean('../../data/in3.txt'))
