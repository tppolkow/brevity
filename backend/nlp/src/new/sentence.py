class Sentence:
    number = 0
    paragraph_number = 0
    length = 0
    word_count = 0
    score = 0.0
    value = ''

    def __init__(self, number, value, length, paragraph_number):
        self.number = number
        self.value = value
        self.length = length
        self.word_count = len(value.split())
        self.paragraph_number = paragraph_number
