package bagging;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.weka.WekaClassifier;
import weka.classifiers.functions.SMO;

import java.util.ArrayList;
import java.util.List;

public class BaggingSMO extends Bagging {
    @Override
    public List<Classifier> generatePool(Dataset dataset, int nClassifiers) {
        List<Classifier> pool = new ArrayList<>();

        for (int i = 0; i < nClassifiers; i++) {
            Dataset sample = generateDataset(dataset);
            SMO smo = new SMO();
            Classifier javamlsmo = new WekaClassifier(smo);
            javamlsmo.buildClassifier(sample);

            pool.add(javamlsmo);
        }
        return pool;
    }
}
