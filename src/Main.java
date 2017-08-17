import bagging.BaggingKNN;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Dataset data = FileHandler.loadDataset(new File("./datasets/wdbc.data"), 1, ",");

        BaggingKNN baggingKNN = new BaggingKNN();

        List<Classifier> pool = baggingKNN.generatePool(data, 100);

        Dataset dataToClassify = FileHandler.loadDataset(new File("./datasets/wdbc.data"), 1, ",");

        int correct = 0;
        int wrong = 0;
        for (Instance instance: dataToClassify) {
            Object predictedValue = classify(dataToClassify.classes().toArray(), instance, pool);
            Object realValue = instance.classValue();

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