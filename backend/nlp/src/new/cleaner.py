import re


class Cleaner:
    @staticmethod
    def clean(text):
        # Remove all \n \r
        text = text.replace('\n', ' ')
        text = text.replace('\r', ' ')

        # Remove anywhere there is more than 2 spaces together
        text = ' '.join(text.split())

        # Split text into sentences
        sentences = text.split('.')

        # Remove any space at the beginning or end of sentence
        for sentence in sentences:
            index = sentences.index(sentence)
            new_sentence = sentence.strip()
            sentences[index] = new_sentence

        # Join any 1 word sentence with previous sentence
        for sentence in sentences:
            if len(sentence.split()) == 1:
                index = sentences.index(sentence)
                previous_sentence = sentences[index - 1]
                if index < len(sentences) - 1:
                    next_sentence = sentences[index + 1]
                sentences[index - 1] = '{}.{}.{}'.format(previous_sentence,
                                                         sentence,
                                                         next_sentence)
                sentences[index] = ''
                if index < len(sentences) - 1:
                    sentences[index + 1] = ''

        # Replace any sentences that have less than 5 words with empty
        for sentence in sentences:
            if len(sentence.split()) < 5:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Drop any empty sentences
        sentences = list(filter(None, sentences))

        return sentences

    def clean_file(self, input_file):
        with open(input_file) as file:
            text = file.read()
            return self.clean(text)
