import bagging.Bagging;
import bagging.BaggingKNN;
import net.sf.javaml.core.Dataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import utils.DatasetUtils;
import utils.TaskUtils;

import org.jfree.ui.ApplicationFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, Exception {

//        Dataset dataset = DatasetUtils.readDataset("./datasets/balance-scale.data", 0);
        Dataset dataset = DatasetUtils.readDataset("./datasets/wdbc.data", 1);

       Map<String, Map<String, Double>> results = TaskUtils.executeBaggingClassifier(dataset, 0.6);


       List<String> keys = getClassifierKeys(new BaggingKNN());

       List<Double> average = new ArrayList<>();
       for (String key: keys) {
           Map<String, Double> result = results.get(key);
           average.add(result.get("mean"));
       }

        final PlotChart test = new PlotChart("Gráfico", average, "BaggingKNN");
        test.pack();
        RefineryUtilities.centerFrameOnScreen(test);
        test.setVisible(true);


    }

    private static List<String> getClassifierKeys(Bagging classifier) {
        List<String> keys = new ArrayList<>();

        for (int i = 0; i < 50 ; i+= 10) {
            String key = classifier.getClass().getSimpleName() + i;
            keys.add(key);
        }

        return keys;
    }

    public static class PlotChart extends ApplicationFrame {

        PlotChart(final String title, List<Double> results, String classifier) {
            super(title);

            final XYSeries series = new XYSeries(classifier);
            int key = 0;
            for (double value: results) {
                if (key == 0) {
                    series.add(1, value);
                } else {
                    series.add(key, value);
                }
                key += 10;
            }
            series.add(0, 0);
            series.add(50, 100);
            final XYSeriesCollection data = new XYSeriesCollection(series);
            final JFreeChart chart = ChartFactory.createXYLineChart(
                    classifier,
                    "Número de Classificadores",
                    "taxa de Acerto",
                    data,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(chartPanel);
        }

    }
}