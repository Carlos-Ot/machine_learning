package utils;

import bagging.Bagging;
import bagging.BaggingKNN;
import bagging.BaggingNaiveBayes;
import bagging.BaggingRandomTree;
import be.abeel.util.Pair;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskUtils {

    public static final int MAXIMUM_CLASSIFIERS = 100;
    public static final int CLASSIFIERS_STEP = 10;
    public static final int MAXIMUM_TESTS = 30;
    public static final int ONE_CONSTANT = 1;

    public static Map<String, Map<String, Double>> executeBaggingClassifier(Dataset dataset, double samplingData) throws Exception {
        Map<String, Map<String, Double>> results = new HashMap<>();
        List<Bagging> classifiers = new ArrayList<>();

        System.out.println("Excuting Bagging Classifier Task...");

        BaggingKNN baggingKNN = new BaggingKNN(5);
        classifiers.add(baggingKNN);

        BaggingRandomTree baggingRandomTree = new BaggingRandomTree();
        classifiers.add(baggingRandomTree);

        BaggingNaiveBayes baggingNaiveBayes = new BaggingNaiveBayes();
        classifiers.add(baggingNaiveBayes);

        Pair<Dataset, Dataset> data = DatasetUtils.samplingData(dataset, samplingData);

        for (Bagging classifier : classifiers) {
            List<Classifier> pool;
            System.out.println("Classifier: " + classifier.getClass().getSimpleName());
            for (int i = 0; i <= MAXIMUM_CLASSIFIERS; i += CLASSIFIERS_STEP) {
                List<Double> rates = new ArrayList<>();
                System.out.println("Tests progress: nClassifiers = " + i);
                System.out.print("[");
                for (int n = 0; n < MAXIMUM_TESTS; n++) {
                    //Training pool with trainingData data.x()
                    if (i == 0) {
                        pool = classifier.generatePool(data.x(), i + ONE_CONSTANT);
                    } else {
                        pool = classifier.generatePool(data.x(), i);
                    }
                    //Validating pool
                    double accuracyRate = validatePool(data.y(), pool);
                    rates.add(accuracyRate);
                    System.out.print(" . . .");
                }
                System.out.println(" ]\n");
                String classifierName = classifier.getName() + i;

                System.out.println("getRates for" + classifierName +  " nClassifiers = " + i);
                results.put(classifierName, getRates(rates));
            }
        }
        return results;
    }

    private static Map<String, Double> getRates(List<Double> rates) {
        Map<String, Double> statisticsRates = new HashMap<>();
        Statistics statistics = new Statistics(rates);

        double mean = statistics.getMean();
        double var = statistics.getVariance();
        double stdDev = statistics.getStdDev(var);

        statisticsRates.put("mean", mean);
        statisticsRates.put("variance", var);
        statisticsRates.put("stdDeviation", stdDev);

        System.out.println("Mean: " + mean + " Variance: " + var + " stdDeviation: " + stdDev);

        return statisticsRates;
    }

    private static double validatePool(Dataset validationData, List<Classifier> pool) {
        double correct = 0.0;
        double wrong = 0.0;
        //Validating pool with validationData validationData)
        for (Instance instance : validationData) {
            Object predictedValue = classify(validationData.classes().toArray(), instance, pool);
            Object realValue = instance.classValue();

            if (predictedValue.equals(realValue)) {
                correct++;
            } else {
                wrong++;
            }
        }
        return ((correct)/(correct + wrong));
    }

    private static Object classify(Object[] classArray, Instance instance, List<Classifier> pool) {
        List<VotingClasses> classes = new ArrayList<>();

        for (Object classInstance : classArray) {
            VotingClasses votingClass = new VotingClasses(classInstance);
            classes.add(votingClass);
        }

        for (Classifier classifier : pool) {
            Object predicted = classifier.classify(instance);

            for (VotingClasses votingClass : classes) {
                if (predicted.equals(votingClass.getClassName())) {
                    votingClass.vote();
                }
            }
        }

        Object predictedClass = null;
        int maxVotes = 0;
        for (VotingClasses voted : classes) {
            if (voted.getVotes() > maxVotes) {
                predictedClass = voted.getClassName();
                maxVotes = voted.getVotes();
            }
        }

        return predictedClass;
    }

}
