package cn.com.duiba.nezha.compute.core.util;

public class MathUtil {


    // sigmoid function: 1 / (1 + exp(-z))
    public static double sigmoid(double value) {
        return 1 / (1 + Math.exp(-1 * value));
    }

    public static double stdwithBoundary(double value, double lowB, double upB) {

        return value < lowB ? lowB : (value > upB ? upB : value);


    }

}
