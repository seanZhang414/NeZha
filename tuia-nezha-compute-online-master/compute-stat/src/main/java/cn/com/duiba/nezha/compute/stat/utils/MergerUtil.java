package cn.com.duiba.nezha.compute.stat.utils;

/**
 * Created by pc on 2017/9/12.
 */
public class MergerUtil {




    /**
     * 24小时权重动态生成
     *
     * @param lastHourNum 时间参数,权重因子参数
     * @return
     */
    public static long hourMergeWeight(long lastHourNum) {
        long ret = 0L;
        if (lastHourNum <= 1) {
            ret = 100 - lastHourNum * 10;
        } else if (lastHourNum <= 18) {
            ret = 90 - lastHourNum * 4;
        } else if (lastHourNum < 24) {
            ret = 80 - (24 - lastHourNum) * 5;
        }
        return ret;
    }

    /**
     * 日权重动态生成
     *
     * @param lastDayNum 日粒度参数,权重因子参数
     * @return
     */
    public static long dayMergeWeight(long lastDayNum) {
        long ret = 0L;

        if (lastDayNum <= 1) {
            ret = 100 - lastDayNum * 10;
        } else if (lastDayNum <= 3) {
            ret = 50L;
        }
        return ret;
    }


    /**
     * @param v1Cnt
     * @param v2Cnt
     * @param newScala
     * @return
     */
    public static Double getCtrWithBias(long v1Cnt, long v2Cnt, long threshold, Double bias,long biasThreshold, int newScala) {
        Double ret = null;

        if (v2Cnt >= threshold) {
            return MathUtil.division(v1Cnt+1, v2Cnt+1, newScala);
        } else if (bias != null) {
            ret = MathUtil.division((v1Cnt + bias * biasThreshold), (v2Cnt + biasThreshold), newScala);
        }
        return ret;
    }

    /**
     * @param v1Cnt
     * @param v2Cnt
     * @param newScala
     * @return
     */
    public static Double getCtrWithBias(double v1Cnt, long v2Cnt, long threshold, Double bias,long biasThreshold,int newScala) {
        Double ret = null;
        if (v2Cnt >= threshold) {
            ret = MathUtil.division(v1Cnt, v2Cnt, newScala);
        } else if (bias != null) {
            ret = MathUtil.division((v1Cnt + bias * biasThreshold), (v2Cnt + biasThreshold), newScala);
        }

        return ret;
    }


}
