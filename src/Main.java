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
import utils.DatasetUtils;
import utils.TaskUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, Exception {

//        Dataset dataset = DatasetUtils.readDataset("./datasets/balance-scale.data", 0);
        Dataset dataset = DatasetUtils.readDataset("./datasets/wdbc.data", 1);

        Map<String, Map<String, Double>> results = TaskUtils.executeBaggingClassifier(dataset, 0.6);

        List<XYSeries> series = new ArrayList<>();
        List<Double> average;
        List<String> keys;

        //Get Values for BaggingNaiveBayes
        System.out.println("Values for BaggingNaiveBayes");
        keys = getClassifierKeys(new BaggingNaiveBayes());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingNaiveBayes"));

        keys.clear();
        average.clear();

        //GetValues for BagginhRandomTree
        System.out.println("Values for BaggingRandomTree");
        keys = getClassifierKeys(new BaggingRandomTree());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingRandomTree"));

        keys.clear();
        average.clear();

        //Get Values for BaggingKNN
        System.out.println("Values for BaggingKNN");
        keys = getClassifierKeys(new BaggingKNN());
        average = getResults(results, keys);
        series.add(getSeries(average, "BaggingKNN"));

        keys.clear();
        average.clear();

        final PlotChart test = new PlotChart("Bagging", series);
        test.pack();
        RefineryUtilities.centerFrameOnScreen(test);
        test.setVisible(true);


    }

    private static List<Double> getResults(Map<String, Map<String, Double>> results, List<String> keys) {
        List<Double> average = new ArrayList<>();
        for (String key : keys) {
            Map<String, Double> result = results.get(key);
            average.add(result.get("mean"));
            System.out.println(key);
            System.out.print("mean: ");
            System.out.print(result.get("mean") + ", variance: ");
            System.out.print(result.get("variance") + ", stdDeviation: ");
            System.out.println(result.get("stdDeviation"));
        }
        return average;
    }

    private static XYSeries getSeries(List<Double> results, String classifier) {
        XYSeries series = new XYSeries(classifier);
        int key = 0;
        for (double value : results) {
            if (key == 0) {
                series.add(1, value);
            } else {
                series.add(key, value);
            }
            key += 10;
        }

        return series;
    }

    private static List<String> getClassifierKeys(Bagging classifier) {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i <= 100; i += 10) {
            String key = classifier.getClass().getSimpleName() + i;
            keys.add(key);
        }

        return keys;
    }

    public static class PlotChart extends ApplicationFrame {

        PlotChart(final String title, List<XYSeries> seriesList) throws IOException {
            super(title);

            final XYSeriesCollection data = new XYSeriesCollection();
            //add Series to data
            for (XYSeries serie: seriesList) {
                data.addSeries(serie);
            }
            final JFreeChart chart = ChartFactory.createXYLineChart(
                    "Bagging\n" + "Breast Cancer Wisconsin",
                    "Nº de Classificadores",
                    "Taxa de Acerto",
                    data,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            int width = 640;   /* Width of the image */
            int height = 480;  /* Height of the image */
            File XYChart = new File( "bagging_wdbc.jpeg" );
            chart.getXYPlot().setRenderer(new XYSplineRenderer());
            ChartUtilities.saveChartAsJPEG( XYChart, chart, width, height);

            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(640, 480));
            setContentPane(chartPanel);
        }

    }
}