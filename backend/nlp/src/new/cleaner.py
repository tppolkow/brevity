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

        # Replace any sentence that have less than 5 words with empty
        min_number_of_words = 5
        for sentence in sentences:
            if len(sentence.split()) < min_number_of_words:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Replace all sentences with copyright with empty
        for sentence in sentences:
            is_match = re.search(u'(\N{COPYRIGHT SIGN}|\N{TRADE MARK SIGN}|'
                                 u'\N{REGISTERED SIGN})', sentence)
            if is_match:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Replace all sentences that have random spacing with empty
        # Example: Psychology 43 c o n c e p t c h e c k 2.
        for sentence in sentences:
            sentence_index = sentences.index(sentence)
            words = sentence.split()
            for word in words:
                word_index = words.index(word)
                if word_index < len(words) - 1:
                    next_word = words[word_index + 1]
                    if len(word) == 1 and len(next_word) == 1:
                        sentences[sentence_index] = ''

        # Drop any empty sentences
        sentences = list(filter(None, sentences))

        return sentences

    def clean_file(self, input_file):
        with open(input_file) as file:
            text = file.read()
            return self.clean(text)
