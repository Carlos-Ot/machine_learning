package utils;

import bagging.Bagging;
import bagging.BaggingKNN;
import bagging.BaggingNaiveBayes;
import bagging.BaggingRandomTree;
import be.abeel.util.Pair;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;
import random_subspace.RandomSubspace;
import random_subspace.RndSubPool;

import java.io.IOException;
import java.util.*;

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

    public static Map<String, Map<String, Double>> executeRandomSubspace(Dataset dataset, double samplingRate) throws Exception {
        Map<String, Map<String, Double>> results = new HashMap<>();
        //Divide dataset into training and validation sets

        double[] samplings = {0.3, 0.4, 0.5};
        for (double sampling: samplings) {
            System.out.println("Rate: " + sampling);
            List<RndSubPool> pool;
            for (int i = 0; i <= MAXIMUM_CLASSIFIERS; i += CLASSIFIERS_STEP) {
                Pair<Dataset, Dataset> data = DatasetUtils.samplingData(dataset, samplingRate);
                List<Double> rates = new ArrayList<>();
                System.out.println("Tests progress: nClassifiers = " + i);
                System.out.print("[");
                for (int n = 0; n < MAXIMUM_TESTS; n++) {
                    RandomSubspace classifier = new RandomSubspace();
                    Dataset trainingData = data.x().copy();
                    //Training pool with trainingData data.x()
                    if (i == 0) {
                        pool = classifier.generatePool(trainingData, sampling, i + ONE_CONSTANT);
                    } else {
                        pool = classifier.generatePool(trainingData, sampling, i);
                    }

                    //Validate Pool
                    Dataset validatingData = data.y().copy();
                    double accuracyRate = validateRandomPool(validatingData, pool);
                    rates.add(accuracyRate);
                    System.out.print(" . . .");
                }
                System.out.println(" ]\n");
                String classifierName = "RandomSubspace" + i + "Rate - " + (sampling*100);

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

    /**
     * @param validationData
     * @param pool
     * @return
     */
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

    private static double validateRandomPool(Dataset validationData, List<RndSubPool> pool) {
        double correct = 0.0;
        double wrong = 0.0;
        Object[] classes = validationData.classes().toArray();

        for (Instance instance: validationData) {
            Instance newInstance = instance.copy();
            Object predictedValue = randomClassify(classes, newInstance, pool);
            Object realValue = newInstance.classValue();

            if (predictedValue.equals(realValue)) {
                correct++;
            } else {
                wrong++;
            }
        }

        return ((correct)/(correct + wrong));
    }

    private static Object randomClassify(Object[] classArray, Instance instance, List<RndSubPool> pool){
        List<VotingClasses> classes = new ArrayList<>();

        for (Object classInstance : classArray) {
            VotingClasses votingClass = new VotingClasses(classInstance);
            classes.add(votingClass);
        }

        for (RndSubPool classifier : pool) {
            Instance newInstance = instance.copy();
            newInstance.removeAttributes(classifier.getMap());
            Object predicted = classifier.getClassifier().classify(newInstance);

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

    private static Dataset getSubValidationData(Dataset dataset, Set<Integer> map) {
        Dataset subDataset = new DefaultDataset();

        for (Instance instance: dataset) {
            instance.removeAttributes(map);
            subDataset.add(instance);
        }

        return subDataset;
    }

    public static void executeExercise1() throws Exception {
//        Dataset dataset = DatasetUtils.readDataset("./datasets/balance-scale.data", 0);
        Dataset dataset = DatasetUtils.readDataset("./datasets/wdbc.data", 1);

        Map<String, Map<String, Double>> results = TaskUtils.executeBaggingClassifier(dataset, 0.6);

        List<XYSeries> series = new ArrayList<>();
        List<Double> average;
        List<String> keys;

        //Get Values for BaggingNaiveBayes
        System.out.println("Values for BaggingNaiveBayes");
        keys = getClassifierKeys(new BaggingNaiveBayes());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingNaiveBayes"));

        keys.clear();
        average.clear();

        //GetValues for BagginhRandomTree
        System.out.println("Values for BaggingRandomTree");
        keys = getClassifierKeys(new BaggingRandomTree());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingRandomTree"));

        keys.clear();
        average.clear();

        //Get Values for BaggingKNN
        System.out.println("Values for BaggingKNN");
        keys = getClassifierKeys(new BaggingKNN());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingKNN"));

        keys.clear();
        average.clear();

        final PlotChart test = new PlotChart("Bagging", series);
        test.pack();
        RefineryUtilities.centerFrameOnScreen(test);
        test.setVisible(true);
    }

    public static void executeExercise2() throws Exception {

//        List<Object[]> lines = DatasetUtils.readData("./datasets/transfusion.data", 4);
//        List<Object[]> newLines = RandomSubspace.expandAttributes(lines, 4, 7);
//        DatasetUtils.writeFile("./datasets/e_transfusion.data", newLines);
//
//        Dataset dataset = DatasetUtils.readDataset("./datasets/e_transfusion.data", 4);

        List<Object[]> lines = DatasetUtils.readData("./datasets/balance-scale.data", 0);
        List<Object[]> newLines = RandomSubspace.expandAttributes(lines, 0, 7);
        DatasetUtils.writeFile("./datasets/e_balance-scale.data", newLines);

        Dataset dataset = DatasetUtils.readDataset("./datasets/e_balance-scale.data", 0);

        Map<String, Map<String, Double>> results =  executeRandomSubspace(dataset, 0.6);

        List<XYSeries> series = new ArrayList<>();
        List<Double> average;
        List<String> keys;

        System.out.println("Values for RandomSubspace - 0.3");
        keys = getRandomKeys(0.3);
        average = getResults(results, keys);
        series.add(getSeries(average, "RandomSubspace - 0.3"));

        keys.clear();
        average.clear();

        System.out.println("Values for RandomSubspace - 0.4");
        keys = getRandomKeys(0.4);
        average = getResults(results, keys);
        series.add(getSeries(average, "RandomSubspace - 0.4"));

        keys.clear();
        average.clear();

        System.out.println("Values for RandomSubspace - 0.5");
        keys = getRandomKeys(0.5);
        average = getResults(results, keys);
        series.add(getSeries(average, "RandomSubspace - 0.5"));

        keys.clear();
        average.clear();

        final PlotChart test = new PlotChart("RandomSubspace", series);
        test.pack();
        RefineryUtilities.centerFrameOnScreen(test);
        test.setVisible(true);
    }


    private static List<Double> getResults(Map<String, Map<String, Double>> results, List<String> keys) {
        List<Double> average = new ArrayList<>();
        for (String key : keys) {
            Map<String, Double> result = results.get(key);
            average.add(result.get("mean"));
            System.out.println(key);
            System.out.print("mean: ");
            System.out.print(result.get("mean") + ", variance: ");
            System.out.print(result.get("variance") + ", stdDeviation: ");
            System.out.println(result.get("stdDeviation"));
        }
        return average;
    }

    private static XYSeries getSeries(List<Double> results, String classifier) {
        XYSeries series = new XYSeries(classifier);
        int key = 0;
        for (double value : results) {
            if (key == 0) {
                series.add(1, value);
            } else {
                series.add(key, value);
            }
            key += 10;
        }

        return series;
    }

    private static List<String> getClassifierKeys(Bagging classifier) {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i <= 100; i += 10) {
            String key = classifier.getClass().getSimpleName() + i;
            keys.add(key);
        }

        return keys;
    }

    private static List<String> getRandomKeys(double sampling) {
        List<String> keys = new ArrayList<>();

        for (int i = 0 ; i < 100 ; i+=10) {
            String key = "RandomSubspace" + i + "Rate - " + (sampling*100);
            keys.add(key);
        }

        return keys;
    }

}
