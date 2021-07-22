from functools import partial

import nltk
import pandas as pd
import regex
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from nltk.tokenize import RegexpTokenizer
import logging
import threading

nltk.download('stopwords')

STOP_WORDS = set(stopwords.words("english"))
JAVA_KEYWORDS = {
    "abstract",
    "assert",
    "boolean",
    "break",
    "byte",
    "case",
    "catch",
    "char",
    "class",
    "continue",
    "const",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "exports",
    "extends",
    "false",
    "final",
    "finally",
    "float",
    "for",
    "goto",
    "if",
    "implements",
    "import",
    "instanceof",
    "int",
    "Integer",
    "interface",
    "java",
    "long",
    "module",
    "native",
    "new",
    "non-sealed",
    "null",
    "package",
    "private",
    "protected",
    "public",
    "requires",
    "return",
    "short",
    "static",
    "strictfp",
    "string",
    "String",
    "super",
    "switch",
    "synchronized",
    "this",
    "throw",
    "throws",
    "transient",
    "try",
    "true",
    "void",
    "volatile",
    "while",
    "permits",
    "record",
    "sealed",
    "var",
    "yield",
}
JAVA_ANNOTATIONS = {
    "@Override",
    "@Deprecated",
    "@SuppressWarnings",
    "@Retention",
    "@Documented",
    "@Target",
    "@Inherited",
    "@SafeVarags",
    "@FunctionalInterface",
    "@Repeatable",
}
ACCESS_MODIFIERS = {"public", "private", "protected"}


class Preprocess:
    """
    Uses 2 clases to extract, parse then save the parsed data into a csv file
    """

    def __init__(
        self, settings, csv_raw_data_filepath: str, csv_processed_filepath: str
    ):

        self.dataframe = pd.read_csv(csv_raw_data_filepath)
        self.csv_processed_filepath = csv_processed_filepath

        self.settings = settings
        self.extraction_list = []
        self.parsing_list = []
        self.extractor = DataExtractor()
        self.parser = Parser()

    def preprocess_data(self):
        """
        For every fow in 'Filecontent', extract and parse according to
        the given settings and replace the raw data with the parsed data.
        """
        for index, row in self.dataframe.iterrows():
            raw_data = row["FileContent"]
            processed_data = self.process_according_settings(raw_data)
            self.dataframe._set_value(index, "FileContent", processed_data)
        self.save_data_to_csv_file()
        return self.dataframe

    def process_according_settings(self, raw_data):
        processed_data = []

        for item in self.settings:
            extraction_choice = str(item[0])
            parsing_choices = list(item[1:])

            self.update_extraction_list(extraction_choice)
            self.update_parsing_list(parsing_choices)

            extracted_data = self.extract_data_from_string(raw_data)
            processed_data.extend(self.parse_list_of_strings(extracted_data))

            self.update_extraction_list("clear")
            self.update_parsing_list(["clear"])
        return processed_data

    def extract_data_from_string(self, raw_data):
        self.extractor.set_raw_data(raw_data)
        self.extractor.execute_functions(self.extraction_list)
        extracted_data = self.extractor.get_extracted_data()
        self.extractor.clear_data()
        return extracted_data

    def parse_list_of_strings(self, data):
        self.parser.set_data(data)
        self.parser.execute_functions(self.parsing_list)
        processed_data = self.parser.get_parsed_data()
        return processed_data

    def save_data_to_csv_file(self):
        self.dataframe.sort_values(by='FileName').to_csv(self.csv_processed_filepath, index=False)

    # returns the tokens converted to a comma sepperated string
    def convert_tokens_to_csv_string(self):
        string = ""
        if self.tokens.len() != 0:
            string = ",".join(self.tokens)
        return string

    def update_parsing_list(self, function_choices):
        for choice in function_choices:
            if choice == "lc":
                self.parsing_list.append(partial(self.parser.lower_case_parsed_data))
            elif choice == "sc":
                self.parsing_list.append(
                    partial(self.parser.remove_single_characters_from_parsed_data)
                )
            elif choice == "jk":
                self.parsing_list.append(
                    partial(self.parser.remove_java_keywords_from_parsed_data)
                )
            elif choice == "ja":
                self.parsing_list.append(
                    partial(self.parser.remove_java_annotations_from_parsed_data)
                )
            elif choice == "js":
                self.parsing_list.append(
                    partial(self.parser.remove_java_syntax_from_parsed_data)
                )
            elif choice == "sw":
                self.parsing_list.append(
                    partial(self.parser.remove_stopwords_from_parsed_data)
                )
            elif choice == "nu":
                self.parsing_list.append(
                    partial(self.parser.remove_numeric_items_from_parsed_data)
                )
            elif choice == "scw":
                self.parsing_list.append(
                    partial(self.parser.separate_compound_words_from_parsed_data)
                )
            elif choice == "tow":
                self.parsing_list.append(
                    partial(self.parser.tokenize_only_words_from_parsed_data)
                )
            elif choice == "stem":
                self.parsing_list.append(
                    partial(self.parser.stem_words_from_parsed_data)
                )
            elif choice == "clear":
                self.parsing_list = []
            else:
                raise ValueError('Parser: "' + choice + '" is not a option.')

    def update_extraction_list(self, choice: str):
        if choice == "raw":
            self.extraction_list.append(partial(self.extractor.use_raw_data))
        elif choice == "c":
            self.extraction_list.append(partial(self.extractor.extract_classes))
        elif choice == "pm":
            self.extraction_list.append(
                partial(self.extractor.extract_public_functions)
            )
        elif choice == "pv":
            self.extraction_list.append(
                partial(self.extractor.extract_public_variables)
            )
        elif choice == "am":
            self.extraction_list.append(partial(self.extractor.extract_all_functions))
        elif choice == "av":
            self.extraction_list.append(partial(self.extractor.extract_all_variables))
        elif choice == "com":
            self.extraction_list.append(partial(self.extractor.extract_comments))
        elif choice == "lib":
            self.extraction_list.append(partial(self.extractor.extract_library_rows))
        elif choice == "pac":
            self.extraction_list.append(partial(self.extractor.extract_package_rows))
        elif choice == "clear":
            self.extraction_list = []
        else:
            raise ValueError('Extraction: "' + choice + '" is not a option.')


class Parser:
    """
    Takes in a list of strings, returns the parsed list of strings according
    to chosen methods. The input data needs to be tokenized.
    """

    def __init__(self, raw_data_list=[]):
        self.parsed_data = raw_data_list

    def execute_functions(self, function_list):
        for function in function_list:
            function()

    def set_data(self, data):
        self.parsed_data = data

    def get_parsed_data(self):
        return self.parsed_data

    """
    Following functions manipulate the parsed_data list
    """

    # TODO:Removes all syntax in java, including special characters from the
    # parsed_data list, the list needs to be tokenized
    def remove_java_syntax_from_parsed_data(self):
        self.remove_java_annotations_from_parsed_data()
        self.remove_java_keywords_from_parsed_data()

    def remove_java_annotations_from_parsed_data(self):
        result = list(filter(lambda n: n not in JAVA_ANNOTATIONS, self.parsed_data))
        self.parsed_data = result

    # Pre: needs the parsed data to be tokenized
    def remove_java_keywords_from_parsed_data(self):
        result = list(filter(lambda n: n not in JAVA_KEYWORDS, self.parsed_data))
        self.parsed_data = result

    def remove_single_characters_from_parsed_data(self):
        result = list(filter(lambda n: len(n) > 1, self.parsed_data))
        self.parsed_data = result

    def remove_numeric_items_from_parsed_data(self):
        result = list(filter(lambda n: not n.isnumeric(), self.parsed_data))
        self.parsed_data = result

    def remove_stopwords_from_parsed_data(self):
        result = list(filter(lambda n: n not in STOP_WORDS, self.parsed_data))
        self.parsed_data = result

    def separate_compound_words_from_parsed_data(self):
        result = []
        for item in self.parsed_data:
            result.extend(self.separate_compound_string(item))
        self.parsed_data = result

    def lower_case_parsed_data(self):
        new_list = [item.lower() for item in self.parsed_data]
        self.parsed_data = new_list

    def tokenize_everything_from_parsed_data(self):
        result = list(self.parsed_data)
        self.parsed_data = []
        [self.tokenize_words_and_characters(item) for item in result]

    def tokenize_only_words_from_parsed_data(self):
        tmp_list = list(self.parsed_data)
        self.parsed_data = []
        [self.tokenize_only_words(item) for item in tmp_list]

    def stem_words_from_parsed_data(self):
        tmp_list = list(self.parsed_data)
        ps = PorterStemmer()
        self.parsed_data = [ps.stem(word) for word in tmp_list]

    """
    Following functions inputs a string, and appends result to self.parsed_data
    """

    def tokenize_words_and_characters(self, item):
        self.parsed_data.extend(nltk.wordpunct_tokenize(item))

    def tokenize_only_words(self, item):
        # Removes all special characters (incl. puncuation) and appends the
        # result to the parsed_data list
        tokenizer = RegexpTokenizer(r"\w+")
        result = tokenizer.tokenize(item)
        self.parsed_data.extend(result)

    def separate_compound_string(self, item):
        # Tokenizes only words and compound words, characters and numbers gets
        # removed automatically
        r = RegexpTokenizer(r"([A-Z]*[a-z]*)")
        return list(filter(None, r.tokenize(item)))


class DataExtractor:
    """
    Extracts text from the raw data string with the chosen methods and returns
    the results as list of strings
    """

    def __init__(self):
        self.raw_data = ""
        self.extracted_data = []

    def execute_functions(self, function_list):
        for function in function_list:
            function()

    def set_raw_data(self, data):
        self.raw_data = data

    def clear_data(self):
        self.extracted_data = []

    def use_raw_data(self):
        self.extracted_data.extend([self.raw_data])

    def get_extracted_data(self):
        return self.extracted_data

    def extract_library_rows(self):
        # Adds the rows to the self.extracted_data list, without ';'
        rule = r"(?=import\s)(\w*.*)(?=;)"
        result = regex.findall(rule, self.raw_data)
        self.extracted_data.extend(result)
        return result

    def extract_package_rows(self):
        # Adds the rows to the self.extracted_data list, without ';'
        rule = r"(?=package\s)(\w*.*)(?=;)"
        result = regex.findall(rule, self.raw_data)
        self.extracted_data.extend(result)
        return result

    # TODO: check if valid extraction?
    def extract_classes(self):
        result = []
        rule_class_declaration = r"(?<=class\s).*?(?=[\s]*{)"
        rule_class_objects = r"(?<=public\s)\w+(?=.*\()"
        rule_new_object_calls = r"(?<=new\s)\w+(?=\()"
        rule_private_class_objects = r"(?<=private\s).*(?=\s\w+;|\s\w+\s=)"
        # private declarations e.g. privat final static Panel panel

        tmp = regex.findall(rule_class_declaration, self.raw_data)
        result.extend(tmp)
        tmp = regex.findall(rule_class_objects, self.raw_data)
        result.extend(tmp)
        tmp = regex.findall(rule_new_object_calls, self.raw_data)
        result.extend(tmp)
        tmp = regex.findall(rule_private_class_objects, self.raw_data)
        result.extend(tmp)

        self.extracted_data.extend(result)
        return result

    # TODO: check if valid extraction?
    def extract_public_functions(self):
        result = []
        rule_1 = r"(?<=public\s.*)\w+(?=\()"
        rule_2 = r"(?<=\.).*?(?=\()"

        tmp = regex.findall(rule_1, self.raw_data)
        result.extend(tmp)
        tmp = regex.findall(rule_2, self.raw_data)
        result.extend(tmp)
        self.extracted_data.extend(result)
        return result

    # TODO: check if valid extraction?
    def extract_public_variables(self):
        rule = r"(?<=public\s)(\w.*)(?=\s=)"
        # to add and create test
        result = regex.findall(rule, self.raw_data)
        self.extracted_data.extend(result)

    def extract_comments(self):
        result = []
        regex_line_comments = r"((?<=\/\/\s*).*)"
        regex_row_comments = r"((?<=\/\*)[\s\S]+?(?=\*\/))"
        tmp = regex.findall(regex_line_comments, self.raw_data)
        result.extend(tmp)
        tmp = regex.findall(regex_row_comments, self.raw_data)
        result.extend(tmp)

        self.extracted_data.extend(result)
        return result

save_files_dict = {}
lock = threading.Lock()

def flatten_lists(s : pd.Series):
    lists = [eval(item) for item in s]
    return ["\'" + str(item) + "\'" for list in lists for item in list]

def clear_preprocessing_cache():
    save_files_dict.clear()
    
def preprocess_settings(settings, raw_data_file, save_file):
    """
    sets the given settings for the preprocess class, then run the
    preprocessing.
    Returns a dataframe containing the result of the preprocessing.
    """
    logging.getLogger().info("Starting preprocessing setting %s", settings)
    df = Preprocess(settings, raw_data_file, save_file).preprocess_data()
    return df

def lazy_preprocess_settings(settings, raw_data_file, save_file):
    if len(settings) == 1:
        df = preprocess_settings(settings, raw_data_file, save_file)
        lock.acquire()
        save_files_dict[str(settings[0])] = save_file
        lock.release()
        return df
    else:
        lock.acquire() 
        relevant_savefiles = [save_files_dict[str(setting)] for setting in settings if str(setting) in save_files_dict]
        lock.release()
        if len(settings) == len(relevant_savefiles):
            logging.getLogger().info("Starting lazy preprocessing setting %s", settings)
            df = pd.DataFrame(columns=['FileName', 'Label', 'FileContent'])
            try:
                df[['FileName', 'Label']] = pd.read_csv(relevant_savefiles[0])[['FileName', 'Label']]
                df_files = [pd.read_csv(file)['FileContent'] for file in relevant_savefiles]
                df['FileContent'] = pd.concat(df_files, axis=1).apply(flatten_lists, axis=1)
            except Exception as e:
                print("Error: {0}".format(e))
            df.sort_values(by='FileName').to_csv(save_file, index=False)
            return df
        else:
            return preprocess_settings(settings, raw_data_file, save_file)
       