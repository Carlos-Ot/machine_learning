package utils;

import be.abeel.util.Pair;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;

public class DatasetUtils {

    public static Dataset readDataset(String path, int classIndex) throws IOException {
        return FileHandler.loadDataset(new File(path), classIndex, ",");
    }

    /**
     * Sample Dataset spliting it into two Datasets: trainingDataset | validationDataset
     * @param dataset the dataset to be sampled
     * @param samplingRate the sampling rate
     * @return Pair where trainingDataset is in .x() and validationDataset is in .y()
     * @throws Exception Error if Pair<Dataset, Dataset> is null while generating data
     */
    public static Pair<Dataset, Dataset> samplingData(Dataset dataset, double samplingRate) throws Exception{
        Sampling sampling = Sampling.SubSampling;
        Object[] classes = dataset.classes().toArray();
        Dataset trainingDataset = new DefaultDataset();
        Dataset validationDataset = new DefaultDataset();

        System.out.println("Sampling Data...");

        for (Object clazz: classes) {
            Dataset tempDataset = new DefaultDataset();
            Pair<Dataset, Dataset> classDataset = null;

            for (Instance instance: dataset) {
                if (instance.classValue().equals(clazz)) {
                    tempDataset.add(instance);
                }
                classDataset = sampling.sample(tempDataset, (int) (tempDataset.size()*samplingRate));
            }

            if (classDataset != null) {
                trainingDataset.addAll(classDataset.x());
                validationDataset.addAll(classDataset.y());
            } else {
                throw new Exception("Error while sampling data!");
            }
        }

        Collections.shuffle(trainingDataset);
        Collections.shuffle(validationDataset);

        return new Pair<>(trainingDataset, validationDataset);
    }
}
