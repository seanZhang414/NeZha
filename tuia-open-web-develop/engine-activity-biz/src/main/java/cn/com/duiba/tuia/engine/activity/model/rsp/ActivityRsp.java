package cn.com.duiba.tuia.engine.activity.model.rsp;

import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;

/**
 * @author xuyenan
 * @since 2017/2/12
 */
public class ActivityRsp {

    public static final int ENGINE_OUTPUT = 1;

    //算法投放
    public static final int ENGINE2_OUTPUT = 2;

    private RspActivityDto activityDto;// 投放活动
    private Integer outputWay;  // 投放方式
    private int subOutputWay;  // 测试广告位子投放方式: 仅投1 优投人工2 优投算法3 纯算法4 其他0
    private int outputSource;  //活动投放来源：0非活动引擎，1活动引擎

    public RspActivityDto getActivityDto() {
        return activityDto;
    }

    public void setActivityDto(RspActivityDto activityDto) {
        this.activityDto = activityDto;
    }

    public Integer getOutputWay() {
        return outputWay;
    }

    public void setOutputWay(Integer outputWay) {
        this.outputWay = outputWay;
    }

    public int getSubOutputWay() {
        return subOutputWay;
    }

    public void setSubOutputWay(int subOutputWay) {
        this.subOutputWay = subOutputWay;
    }

    public int getOutputSource() {
        return outputSource;
    }

    public void setOutputSource(int outputSource) {
        this.outputSource = outputSource;
    }
}
