import re

class Cleaner:
    @staticmethod
    def clean(text):
        # Takes care of words like 'psych- ology' with weird spacing
        text = text.replace('-\n', '')

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
            if len(sentence.split()) < 3:
                index = sentences.index(sentence)
                previous_sentence = sentences[index - 1]
                if index < len(sentences) - 1:
                    next_sentence = sentences[index + 1]
                    if not previous_sentence.isdigit() and not sentence.isdigit() \
                            and not next_sentence.isdigit():
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

        # Replace any sentence that have greater than 20 words with empty
        # (should take care of those pesky headers)
        max_number_of_words = 22.5
        for sentence in sentences:
            if len(sentence.split()) > max_number_of_words:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Replace all sentences with copyright with empty
        for sentence in sentences:
            is_match = re.search(u'(\N{COPYRIGHT SIGN}|\N{TRADE MARK SIGN}|'
                                 u'\N{REGISTERED SIGN})', sentence)
            if is_match:
                index = sentences.index(sentence)
                sentences[index] = ''

        undesirable = ['figure', 'table', 'fig', 'chapter', 'publisher',
                       'publishers', 'ch', '>', '<', '+', '_' 'eg',
                       'references', 'key terms']
        # Remove any sentence with figure/table/Fig/chapter/page type keywords
        for sent in sentences:
            index = sentences.index(sent)
            sent = sent.lower()
            for u in undesirable:
                if u in sent:
                    sentences[index] = ''
                    break

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

        # Replace all sentences that start with a number with empty
        for sentence in sentences:
            is_match = re.match(r'^([A-Z])', sentence)
            if not is_match:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Replace all sentences that have a time
        for sentence in sentences:
            is_match = re.match(r'([0-9]+)(:|-)[0-5][0-9]', sentence)
            if is_match:
                index = sentences.index(sentence)
                sentences[index] = ''

        # Replace all sentences that have any words in all CAPS
        for sentence in sentences:
            sentence_index = sentences.index(sentence)
            words = sentence.split()
            for word in words:
                if word.isupper():
                    sentences[sentence_index] = ''
                    break
        
        # Drop any empty sentences
        sentences = list(filter(None, sentences))
        
        try:
            del text
            del words
            del sentence_index
        except:
            pass

        return sentences
    
    def clean_file(self, input_file):
        with open(input_file) as file:
            text = file.read()
            return self.clean(text)
