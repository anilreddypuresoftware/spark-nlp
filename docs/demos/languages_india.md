---
layout: demopage
title: Spark NLP in Action
full_width: true
permalink: /languages_india
key: demo
license: false
show_edit_on_github: false
show_date: false
data:
  sections:  
    - title: Spark NLP - World Languages 
      excerpt: Languages of India 
      secheader: yes
      secheader:
        - title: Spark NLP - World Languages
          subtitle: Languages of India 
          activemenu: languages_india
      source: yes
      source: 
        - title: NER Model for Hindi+English
          id: ner_model_hindi_english
          image: 
              src: /assets/images/NER_Model_for_Hindi_English.svg
          image2: 
              src: /assets/images/NER_Model_for_Hindi_English_f.svg
          excerpt: This model shows how Places an Organizations can be detected in mixed Hindi+English texts.
          actions:
          - text: Live Demo
            type: normal
            url:  https://demo.johnsnowlabs.com/public/NER_HINDI_ENGLISH/
          - text: Colab Netbook
            type: blue_btn
            url: https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/NER_HINDI_ENGLISH.ipynb                
        - title: Recognize Entities in Bengali
          id: recognize_entities_in_bengali
          image: 
              src: /assets/images/Flag_of_Bangladesh.png
          image2: 
              src: /assets/images/Flag_of_Bangladesh.png
          excerpt: Recognize Persons, Locations, Organizations and Misc entities using an out of the box pretrained Deep Learning model and GloVe word embeddings (glove_840b_300d).
          actions:
          - text: Live Demo
            type: normal
            url:  https://demo.johnsnowlabs.com/public/NER_BN
          - text: Colab Netbook
            type: blue_btn
            url:  https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/NER.ipynb
        - title: Translate text from Marathi to English
          id: translate_text_from_marathi_to_english
          image: 
              src: /assets/images/Translate_text_from_Marathi_to_English.svg
          image2: 
              src: /assets/images/Translate_text_from_Marathi_to_English_f.svg
          excerpt: Translate text from Marathi to English using pre-trained Deep Learning pipeline
          actions:
          - text: Live Demo
            type: normal
            url:  https://demo.johnsnowlabs.com/public/INDIAN_TRANSLATION_MARATHI/
          - text: Colab Netbook
            type: blue_btn
            url: https://githubtocolab.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/TRANSLATION_PIPELINES_MODELS.ipynb  
        - title: Translate text from Punjabi to English
          id: translate_text_from_punjabi_to_english
          image: 
              src: /assets/images/Translate_text_from_Punjabi_to_English.svg
          image2: 
              src: /assets/images/Translate_text_from_Punjabi_to_English_f.svg
          excerpt: Translate text from Punjabi to English using pretrained Deep Learning pipeline
          actions:
          - text: Live Demo
            type: normal
            url:  https://demo.johnsnowlabs.com/public/INDIAN_TRANSLATION_PUNJABI/
          - text: Colab Netbook
            type: blue_btn
            url: https://githubtocolab.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/TRANSLATION_PIPELINES_MODELS.ipynb
        - title: Urdu news classifier 
          id: urdu_news_classifier  
          image: 
              src: /assets/images/Urdu_news_classifier.svg
          image2: 
              src: /assets/images/Urdu_news_classifier_f.svg
          excerpt: This demo shows how to classify Urdu news into different categories, such as Science, Entertainment, etc.
          actions:
          - text: Live Demo
            type: normal
            url: https://demo.johnsnowlabs.com/public/CLASSIFICATION_UR_NEWS/
          - text: Colab Netbook
            type: blue_btn
            url: https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/CLASSIFICATION_UR_NEWS.ipynb
        - title: Urdu fake news classifier
          id: urdu_fake_news_classifier
          image: 
              src: /assets/images/Urdu_fake_news.svg
          image2: 
              src: /assets/images/Urdu_fake_news_f.svg
          excerpt: This demo shows how to detect fake Urdu news.
          actions:
          - text: Live Demo
            type: normal
            url: https://demo.johnsnowlabs.com/public/CLASSIFICATION_UR_NEWS/
          - text: Colab Netbook
            type: blue_btn
            url: https://colab.research.google.com/github/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/streamlit_notebooks/CLASSIFICATION_UR_NEWS.ipynb       
---
