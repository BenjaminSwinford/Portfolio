Concept:
A simple artificial intelligence designed to determine if a review of a product is positive or negative based off of language used.


Implimentation:
The program "learns" words most commonly used in positive and negative reviews by providing it a sample with the rating visible. Accurate estimations of reviews may then
be determined through the usage of Bayes theorem in conjunction with the learned information.


How To Run:
Make sure that bayes.py, trainingSet.txt, and testSet.txt are in the same directory.

Run the bayes program with this line:

python bayes.py trainingSet.txt testSet.txt preprocessed_train.txt preprocessed_test.txt results.txt
