package bagging;

import be.abeel.util.Pair;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.sampling.Sampling;

import java.util.List;
import java.util.Random;

public abstract class Bagging {

    /**
     * Generate a pool of classifiers according to nClassifiers passed
     * @param dataset the dataset to be trained
     * @param nClassifiers the number of classifiers
     * @return the pool of classifier
     */
    public abstract List<Classifier> generatePool(Dataset dataset, int nClassifiers);

    /**
     * Generate a sub Dataset
     * @param data the base Dataset
     * @return a sub Dataset
     */
    static Dataset generateDataset (Dataset data) {
        Sampling sampling = Sampling.SubSampling;

        double start = 0.4;
        double end = 0.9;
        double random = new Random().nextDouble();
        double result = start + (random * (end - start));

        Pair<Dataset, Dataset> dataset = sampling.sample(data, (int)(data.size()*result));

        return dataset.x();
    }

    /**
     * Generate a sub Dataset according to the passed SampleSize
     * @param data the Dataset
     * @param sampleSize the Size of sub Dataset
     * @return a sub Dataset
     */
    protected static Dataset generateDataset (Dataset data, double sampleSize) {
        Sampling sampling = Sampling.SubSampling;

        //Check if the sample size is in double format or percent format
        if (sampleSize > 1) {
            //The sample size is in percent format
            //Convert sampleSize to double format
            sampleSize = sampleSize / 100;
        }

        Pair<Dataset, Dataset> dataset = sampling.sample(data, (int)(data.size()*sampleSize));

        return dataset.x();
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}
