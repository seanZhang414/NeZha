package cn.com.duiba.tuia.utils;

import java.util.List;
import java.util.Random;

/**
 * @author xuyenan
 * @createTime 2017/1/23
 */
public class FlowSplitUtils {

    private FlowSplitUtils() {
    }

    /**
     * 流量切分
     * 
     * @param params 所有比例
     * @return 比例对应的策略
     */
    public static int split(List<FlowSplit> params) {
        double total = 0;
        for (FlowSplit flowSplit : params) {
            total += flowSplit.getPercent();
        }
        double radio = total / 100;
        for (FlowSplit flowSplit : params) {
            flowSplit.setPercent(flowSplit.getPercent() / radio);    //NOSONAR
        }
        int random = new Random().nextInt(100);
        if (random < params.get(0).getPercent()) {
            return params.get(0).getCaseValue();
        }
        double start = 0;
        for (int i = 0; i < params.size(); i++) {
            start += params.get(i).getPercent();
            if(i==params.size()-1){
            	if (start <= random ) {
                    return params.get(i).getCaseValue();
                }
            }else{
            	if (start <= random && random < (start + params.get(i + 1).getPercent())) {
                    return params.get(i + 1).getCaseValue();
                }
            }
        }
        return -1;
    }
}
