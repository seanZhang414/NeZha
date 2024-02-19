package cn.com.duiba.tuia.engine.activity.service;

/**
 * @author xuyenan
 * @createTime 2017/2/4
 */
public interface FlowSplitService {

    /**
     * 按比例获取活动投放方式
     *
     * @return 活动投放方式
     */
    int getActOutputWay();

    /**
     * 按比例获取苏素材投放方式
     *
     * @return 素材投放方式
     */
    int getMaterialOutputWay();

    /**
     * 获取素材偏差比例
     * 
     * @return 素材偏差比例
     */
    double getMaterialDiffPercent();

    /**
     * 获取新活动变成老活动的曝光数边界
     * 
     * @return 曝光数
     */
    int getActNewToOldBound();

    /**
     * 获取新素材变成老素材的曝光数边界
     * 
     * @return 曝光数
     */
    int getMaterialNewToOldBound();
    
    /**
     * getManualActOutput:获取手投活动投放方式. <br/>
     *
     * @return 手投投放方式
     * @since JDK 1.6
     */
    int getManualActOutput();
    
    /**
     * getEngineOutput:获取引擎投放方式. <br/>
     *
     * @author Administrator
     * @return 引擎投放方式
     * @since JDK 1.6
     */
    int getEngineOutput();

    /**
     * 获取限定活动引擎投放方式. <br/>
     *
     * @return 引擎投放方式
     * @author Administrator
     */
    int getEngineOutputTest();
}
