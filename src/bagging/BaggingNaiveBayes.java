package bagging;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.MeanFeatureVotingClassifier;
import net.sf.javaml.classification.SOM;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.classification.tree.RandomTree;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;

import java.util.ArrayList;
import java.util.List;

import net.sf.javaml.tools.weka.WekaClassifier;
import weka.classifiers.bayes.NaiveBayes;

public class BaggingNaiveBayes extends Bagging {
    @Override
    public List<Classifier> generatePool(Dataset dataset, int nClassifiers) {

        List<Classifier> pool = new ArrayList<>();

        for (int i = 0; i < nClassifiers; i++) {
            Dataset sample = generateDataset(dataset);

            NaiveBayes naiveBayes = new NaiveBayes();
            Classifier jmlTree = new WekaClassifier(naiveBayes);
            jmlTree.buildClassifier(sample);

            pool.add(jmlTree);
        }
        return pool;
    }
}
