import bagging.BaggingNaiveBayes;
import be.abeel.util.Pair;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Dataset data = FileHandler.loadDataset(new File("./datasets/wdbc.data"), 1, ",");

        Sampling sampling = Sampling.SubSampling;
        Pair<Dataset, Dataset> dataset = sampling.sample(data, (int)(data.size()*0.5));

        Dataset trainingData = dataset.x();
        Dataset validationData = dataset.y();

        System.out.println("Training: " + trainingData.size() + " Validation: " + validationData.size() + " Total: " + data.size());

       /* BaggingKNN baggingKNN = new BaggingKNN();
        baggingKNN.setK(5);
        List<Classifier> pool = baggingKNN.generatePool(trainingData, 10);*/

        BaggingNaiveBayes baggingNaiveBayes = new BaggingNaiveBayes();
        List<Classifier> pool = baggingNaiveBayes.generatePool(trainingData, 10);

      /*  BaggingSMO baggingSMO = new BaggingSMO();
        List<Classifier> pool = baggingSMO.generatePool(trainingData, 10);*/

        int correct = 0;
        int wrong = 0;
        for (Instance instance: validationData) {
            Object predictedValue = classify(validationData.classes().toArray(), instance, pool);
            Object realValue = instance.classValue();
           // System.out.println("Predicted Value: " + predictedValue.toString() + " Real Value: " + realValue.toString());

            if (predictedValue.equals(realValue)) {
                correct++;
            }
            else {
                wrong++;
            }
        }

        System.out.println("Correct: " + correct + "\nWrong: " + wrong);


    }

    private static Object classify(Object[] classArray, Instance instance, List<Classifier> pool) {
        List<VotingClasses> classes  = new ArrayList<>();

        for (Object classInstance : classArray) {
            VotingClasses votingClass = new VotingClasses(classInstance);
            classes.add(votingClass);
        }

        for (Classifier classifier : pool) {
            Object predicted = classifier.classify(instance);

            for (VotingClasses votingClass : classes) {
//                System.out.println("Predicted: " + predicted.toString() + " == VotingClass: " + votingClass.getClassName().toString());
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