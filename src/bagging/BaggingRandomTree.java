package bagging;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;

import java.util.ArrayList;
import java.util.List;

public class BaggingRandomTree extends Bagging {
    @Override
    public List<Classifier> generatePool(Dataset dataset, int nClassifiers) {
        List<Classifier> pool = new ArrayList<>();

        for (int i = 0; i < nClassifiers; i++) {
            Dataset sample = generateDataset(dataset);
            Classifier classifier = new RandomForest(10);
            classifier.buildClassifier(sample);

            pool.add(classifier);
        }
        return pool;
    }
}
