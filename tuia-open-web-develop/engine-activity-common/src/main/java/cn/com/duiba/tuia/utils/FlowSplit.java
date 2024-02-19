package cn.com.duiba.tuia.utils;

/**
 * @author xuyenan
 * @createTime 2017/1/23 流量切分参数类
 */
public class FlowSplit {

    /** 策略值 */
    private int    caseValue;
    /** 所占百分比 */
    private double percent;

    /**
     * constructor
     * 
     * @param caseValue 策略值
     * @param percent 百分比
     */
    public FlowSplit(int caseValue, double percent) {
        this.caseValue = caseValue;
        this.percent = percent;
    }

    public int getCaseValue() {
        return caseValue;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
