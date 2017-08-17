import bagging.BaggingKNN;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Dataset data = FileHandler.loadDataset(new File("./datasets/wdbc.data"), 1, ",");

        BaggingKNN baggingKNN = new BaggingKNN();

        List<Classifier> pool = baggingKNN.generatePool(data, 10);

    }
}