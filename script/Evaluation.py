import concurrent.futures
import Testing
from Classifier import Algorithm, Classifier


class Evaluation:

    # fold_quantity splits the dataFrame into 10 folders, 9 of these are used as test data while
    # 1 is used for training. This will be executed in a cross-validator.

    def __init__(
        self,
        dataFrame=None,
        feature_vector=None,
        test_size=None,
        fold_quantity=10,
        numberOfFiles=None,
    ):
        self.dataFrame = dataFrame
        self.f_vector = feature_vector
        self.fold_quantity = fold_quantity
        self.number_of_files_to_test = numberOfFiles
        self.test_size = test_size

    def evaluate_Naive_Bayes(self, type="standard"):
        classifier_metrics = Testing.evaluate_classifier(
            self.dataFrame,
            classifier=Classifier(Algorithm.NAIVE_BAYES, "Naive Bayes"),
            feature_representation=self.f_vector,
            fold_quantity=self.fold_quantity,
            test_size=self.test_size,
            type=type,
            number_of_files_for_training=self.number_of_files_to_test,
        )
        return classifier_metrics

    def evaluate_MaxEnt(self, type="standard"):
        classifier_metrics = Testing.evaluate_classifier(
            self.dataFrame,
            classifier=Classifier(Algorithm.MAX_ENT, "Logistic Regression"),
            feature_representation=self.f_vector,
            fold_quantity=self.fold_quantity,
            test_size=self.test_size,
            type=type,
            number_of_files_for_training=self.number_of_files_to_test,
        )
        return classifier_metrics

    def evaluate_SVM(self, type="standard"):
        classifier_metrics = Testing.evaluate_classifier(
            self.dataFrame,
            classifier=Classifier(Algorithm.SVM, "Support Vector Machines"),
            feature_representation=self.f_vector,
            fold_quantity=self.fold_quantity,
            test_size=self.test_size,
            type=type,
            number_of_files_for_training=self.number_of_files_to_test,
        )
        return classifier_metrics

    def evaluate_all(self, type="standard"):
        with concurrent.futures.ThreadPoolExecutor() as executor:
            futures = [
                executor.submit(self.evaluate_Naive_Bayes, type),
                executor.submit(self.evaluate_MaxEnt, type),
                executor.submit(self.evaluate_SVM, type)
            ]
            results = [f.result() for f in concurrent.futures.as_completed(futures)]
            return results		