package utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PlotChart extends ApplicationFrame {

    PlotChart(final String title, List<XYSeries> seriesList) throws IOException {
        super(title);

        final XYSeriesCollection data = new XYSeriesCollection();
        //add Series to data
        for (XYSeries serie: seriesList) {
            data.addSeries(serie);
        }
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Random Subspace\n" + "Balance Scale",
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
        File XYChart = new File( "random_balance.jpeg" );
        chart.getXYPlot().setRenderer(new XYSplineRenderer());
        ChartUtilities.saveChartAsJPEG( XYChart, chart, width, height);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(640, 480));
        setContentPane(chartPanel);
    }

}
