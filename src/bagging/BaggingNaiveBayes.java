package bagging;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.core.Dataset;

import java.util.ArrayList;
import java.util.List;

public class BaggingNaiveBayes extends Bagging {
    @Override
    public List<Classifier> generatePool(Dataset dataset, int nClassifiers) {

        List<Classifier> pool = new ArrayList<>();

        for (int i = 0; i < nClassifiers; i++) {
            Dataset sample = generateDataset(dataset);

            Classifier classifier = new NaiveBayesClassifier(true, false, true);
            classifier.buildClassifier(sample);
            pool.add(classifier);
        }
        return pool;
    }
}
