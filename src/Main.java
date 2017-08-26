import bagging.Bagging;
import bagging.BaggingKNN;
import bagging.BaggingNaiveBayes;
import bagging.BaggingRandomTree;
import net.sf.javaml.core.Dataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import random_subspace.RandomDataset;
import random_subspace.RandomSubspace;
import utils.DatasetUtils;
import utils.TaskUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, Exception {

//        Dataset dataset = DatasetUtils.readDataset("./datasets/transfusion.data", 4);

//        List<Object[]> lines = DatasetUtils.readDataset("./datasets/balance-scale.data");
//        List<Object[]> lines = DatasetUtils.readData("./datasets/transfusion.data", 4);

//        List<Object[]> newLines = RandomSubspace.expandAttributes(lines, 4, 7);
//        for (Object[] line: newLines) {
//            System.out.println(Arrays.toString(line));
//        }
//
//        DatasetUtils.writeFile("./datasets/e_transfusion.data", newLines);
//
//        Dataset datasetOld = DatasetUtils.readDataset("./datasets/transfusion.data", 4);

        TaskUtils.executeExercise2();

    }
}