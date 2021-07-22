# Replication package for the paper "A Comparison of Machine Learning-Based Text Classifiers for Mapping Source Code to Architectural Modules" by Alexander Florean, Laoa Jalal, Zipani Sinkala, and Sebastian Herold.

The replication package requires Python 3.8.5.

To start working in the environment, install required packages by running:   
`pip install -r requirements.txt`  

The main entry point for is a Jupyter Notbook located at ./impl/jupyter/paper_experiments.ipynb - although the code of the notebook can easily executed stand-alone, the use of Jupyter Notebook is highly encouraged.

---

#### Preprocessing settings
The experiments of the paper test several thousands of preprocessing settings.  If you want to experiment with only a couple of settings yourself, you can define them in the `config.yaml`. Additionally, modify the notebook as described in its introduction

Each setting in the configuration file consists of a list of lists. The first item in each list describes what to extract from the raw source  file, then the following strings is the parsing of the said extraction.    

E.g. `[['lib', 'tow', 'jk', 'scw','lc', 'stem'],['pac', 'tow', 'jk']]`  
First list, extracts the imports, tokenizes the words, removes java keywords,   
separate composite words, lower case, then stemmize the words.   
Second list, extracts packages, tokenizes them, remove java keywords.   

All the settings are described down below.   

```
settings = [
        ['extraction', 'parsing' ... ,'parsing],
        ...,
        ['extraction', 'parsing']
    ]
```
#### Extraction options
* Raw data (code files as they are): 'raw'
* Class declarations: 'c'
* Public method declarations: 'pm'
* Public variable declarations: 'pv'
* Import statements: 'lib'
* Package declarations: 'pac'
* Comments: 'com'
* Clear chosen settings from list: 'clear'

#### Parsing options
* Lower case: 'lc'
* Remove single characters:  'sc'
* Remove stop words: 'sw'
* Remove java keywords: 'jk'
* Remove numbers: 'nu'
* Separate compound words: 'scw'
* Stem words: 'stem'
* Clear chosen settings from list: 'clear'

___
