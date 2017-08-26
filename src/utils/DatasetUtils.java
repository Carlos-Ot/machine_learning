package utils;

import be.abeel.util.Pair;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.sampling.Sampling;
import net.sf.javaml.tools.data.FileHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static List<Object[]> readData(String path, int classIndex) throws IOException {
        List<Object[]> lines = new ArrayList<>();
        for (String line: Files.readAllLines(Paths.get(path))) {
            String[] parts = line.split("\\s+,|,\\s+|,");
            Object[] lineArray = new Object[parts.length];
            for (int i = 0 ; i < parts.length ; i++) {
                if (i != classIndex) {
                    lineArray[i] = Double.parseDouble(parts[i]);
                } else {
                    lineArray[i] = parts[i];
                }
            }
            lines.add(lineArray);
        }

        return lines;
    }

    public static void writeFile(String filename, List<Object[]> lines) throws IOException{
        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter(filename));
        for (Object[] line: lines) {
            for (int i = 0; i < line.length; i++) {
                outputWriter.write(line[i]+"");
                if (i < line.length - 1)
                    outputWriter.write(",");
            }
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }
}
