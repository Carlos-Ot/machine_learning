package bagging;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;

import java.util.ArrayList;
import java.util.List;

public class BaggingKNN extends Bagging {

    private int k = 1;

    @Override
    public List<Classifier> generatePool(Dataset dataset, int nClassifiers) {
        List<Classifier> pool = new ArrayList<>();

        for (int i = 0; i < nClassifiers; i++) {
            Dataset sample = generateDataset(dataset);
            Classifier knn = new KNearestNeighbors(k);
            knn.buildClassifier(sample);

            pool.add(knn);
        }
        return pool;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}
