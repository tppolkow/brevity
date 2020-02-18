import re


class Cleaner:
    @staticmethod
    def clean(input_file):
        cleaned_sentences = []
        pattern = re.compile('([^\s\w]|_)+')

        with open(input_file) as file:
            text = file.read()
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


c = Cleaner()
print(c.clean('../../data/in3.txt'))
