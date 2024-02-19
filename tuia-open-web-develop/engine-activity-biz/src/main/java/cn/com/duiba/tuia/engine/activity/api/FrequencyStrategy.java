package cn.com.duiba.tuia.engine.activity.api;

/**
 * 频次策略
 */
public final class FrequencyStrategy {

    private final int seconds;

    private final int maxCount;

    /**
     * Constructor
     * 
     * @param seconds
     * @param maxCount
     */
    public FrequencyStrategy(int seconds, int maxCount) {
        super();
        this.seconds = seconds;
        this.maxCount = maxCount;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
