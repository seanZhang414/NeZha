package cn.com.duiba.nezha.compute.common.util;


/**
 * Created by pc on 2016/12/16.
 */
public class OneHotUtil {
    public static int[] getOneHotInt(int idx, int size) {

        if (size < 1 || idx > size) {
            return null;
        }
        int[] array = new int[size + 1];

        if (idx < 0 || idx > size) {
            array[0] = 1;
        } else {
            array[idx+1] = 1;
        }
        return array;
    }

    public static double[] getOneHotDouble(int idx, int size) {

        if (size < 1 || idx > size) {
            return null;
        }
        double[] array = new double[size + 1];

        if (idx < 0 || idx > size) {
            array[0] = 1.0;
        } else {
            array[idx+1] = 1.0;
        }
        return array;
    }
}
