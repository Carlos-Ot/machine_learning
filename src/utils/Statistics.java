package utils;

import java.util.Arrays;
import java.util.List;

public class Statistics {
    public static final int DECIMAL = 4;
    private static Double[] data;
    private static int size;

    public Statistics(List<Double> data) {
        this.data = data.toArray(new Double[data.size()]);
        this.size = data.size();
    }

    public double getMean() {
        double sum = 0.0;
        for (double x: data) {
            sum += x;
        }
        return sum/size;
    }

    public double getVariance() {
        double mean = getMean();
        double temp = 0;
        for (double x: data) {
            temp += (x-mean)*(x-mean);
        }
        return temp/size;
    }

    public double getStdDev(double variance) {
        return Math.sqrt(variance);
    }
}
