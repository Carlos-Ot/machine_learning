package random_subspace;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

import javax.xml.crypto.Data;
import java.util.*;

public class RandomSubspace {

    public List<RndSubPool> generatePool(Dataset dataset, double k, int nClassifiers) {
        List<RndSubPool> pool = new ArrayList<>();

        for (int i = 0 ; i < nClassifiers ; i++) {
            RandomDataset randomDataset = selectRandomSubspace(dataset, k);
            Classifier classifier = new NaiveBayesClassifier(true, false, true);
            classifier.buildClassifier(randomDataset.getDataset());
            RndSubPool rndSubPool = new RndSubPool(randomDataset.getAttributesMap(), classifier);
            pool.add(rndSubPool);
        }

        return pool;
    }

    private static RandomDataset selectRandomSubspace(Dataset data, double k) {

        int classIndex = data.classIndex(data.instance(1).classValue());
        Set<Integer> attributesToRemove = getAttributesIndexes(data.noAttributes(), k, classIndex);

        Dataset newDataset = new DefaultDataset();
        for (Instance instance: data) {
            Instance newInstance = instance.copy();
            newInstance.removeAttributes(attributesToRemove);
            newDataset.add(newInstance);
        }

        return new RandomDataset(attributesToRemove, newDataset);
    }

    private static Set<Integer> getAttributesIndexes(int noAttributes, double sampling, int classIndex) {
        Set<Integer> attributes = new HashSet<>();

        //this is needed once we will remove the attributes
        int samplingAttributes = (int) Math.round(noAttributes * sampling);
        Random random = new Random();
        while (attributes.size() != samplingAttributes) {
            int index;
            do {
               index =  random.nextInt(noAttributes - 1);
            } while (index == classIndex);
            attributes.add(index);
        }
        return attributes;
    }

    public static List<Object[]> expandAttributes(List<Object[]> data, int classIndex, int expansionSize) {
        List<Object[]> instances = new ArrayList<>();
        for (Object[] line: data) {
            Object[] newLine = line;
            for (int i = 0; i < line.length ; i++) {
                if (i != classIndex) {
                    Object[] power = powerSeries(line[i], expansionSize);
                    newLine = concat(newLine, power);

                    Object[] trigonometric = trigonometricSeries(line[i], expansionSize);
                    newLine = concat(newLine, trigonometric);
                }
            }
            instances.add(newLine);
        }

        return instances;
    }

    private static Object[] powerSeries(Object x, int expansionSize) {
        Object[] array = new Object[expansionSize];
        for (int i = 0; i < expansionSize; i++) {
            array[i] = Math.pow((Double) x, i);
        }
        return array;
    }

    private static Object[] trigonometricSeries(Object x, int expansionSize) {
        Object[] array = new Object[expansionSize];

        int count = 1;
        for (int i = 0; i < expansionSize ; i++) {
            if (i == 0) {
                array[i] = x;
            }
            else {
                if ((i + 1) % 2 == 0) {
                    array[i] = Math.cos(count*Math.PI*(Double) x);
                }
                else {
                    array[i] = Math.sin(count*Math.PI*(Double) x);
                    count++;
                }
            }
        }

        return array;
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
