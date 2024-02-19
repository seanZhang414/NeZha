package cn.com.duiba.nezha.compute.alg;

import cn.com.duiba.nezha.compute.alg.vo.BackendAdvertStatDo;
import cn.com.duiba.nezha.compute.alg.vo.StatDo;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.DataUtil;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendOptimizer {

    public static Map<String, Double> backendTypePriorityMap = new HashMap<>();


    static {
        backendTypePriorityMap.put("1", 1.0);
        backendTypePriorityMap.put("2", 1.0);
        backendTypePriorityMap.put("3", 1.0);
        backendTypePriorityMap.put("4", 0.0);
        backendTypePriorityMap.put("5", 0.0);
        backendTypePriorityMap.put("6", 0.0);
        backendTypePriorityMap.put("7", 0.0);
        backendTypePriorityMap.put("8", 0.2);
    }

    /**
     * 后端转化，可优化后端行为&广告认定
     *
     * @param appAdvertStatMap
     * @param globalAdvertStatMap
     * @return （广告ID、后端转化数据类型、统计信息）
     */
    public static Map<Long, BackendAdvertStatDo> getBackendAdvertStatInfo(Map<Long, StatDo> appAdvertStatMap,
                                                                          Map<Long, StatDo> globalAdvertStatMap) {
        Map<Long, BackendAdvertStatDo> ret = new HashMap<>();

        if (AssertUtil.isAllNotEmpty(globalAdvertStatMap)) {
            if (appAdvertStatMap == null) {
                appAdvertStatMap = new HashMap<>();
            }


            for (Map.Entry<Long, StatDo> entry : globalAdvertStatMap.entrySet()) {
                Long advertId = entry.getKey();
                StatDo globalStatDo = entry.getValue();
                StatDo appStatDo = appAdvertStatMap.get(advertId);

                String type = getTaskBackendType(globalStatDo);

                if (type != null) {
                    BackendAdvertStatDo backendAdvertStatDo = new BackendAdvertStatDo();
                    backendAdvertStatDo.setAdvertId(advertId);
                    backendAdvertStatDo.setBackendType(type);
                    backendAdvertStatDo.setAvgBackendCvr(getTaskBackendCvr(globalStatDo, type));
                    backendAdvertStatDo.setCBackendCvr(getTaskBackendCvr(appStatDo, type));

                    ret.put(advertId, backendAdvertStatDo);
                }


            }

        }

        return ret;
    }

    /**
     * 广告后端转化优化-智能竞价因子
     *
     * @param advertPreInfo  预估数据
     * @param advertStatInfo 统计数据
     * @return 优化因子
     */
    public static Map<Long, Double> getBackendAdvertInfo(Map<Long, Double> advertPreInfo,
                                                         Map<Long, BackendAdvertStatDo> advertStatInfo) {
        Map<Long, Double> ret = new HashMap<>();
        if (AssertUtil.isAllNotEmpty(advertPreInfo, advertStatInfo)) {
            for (Map.Entry<Long, BackendAdvertStatDo> entry : advertStatInfo.entrySet()) {

                Long advertId = entry.getKey();
                BackendAdvertStatDo backendAdvertStatDo = entry.getValue();
                Double preBackendCvr = advertPreInfo.get(advertId);

                if (backendAdvertStatDo != null && preBackendCvr != null) {
                    Double mergeCvr = getMergeBackendCvr(backendAdvertStatDo.getCBackendCvr(), preBackendCvr);
                    Double optFactor = getBackendOptFactor(mergeCvr, backendAdvertStatDo.getAvgBackendCvr());
                    if (optFactor != null) {
                        ret.put(advertId, optFactor);
                    }
                }

            }
        }

        return ret;
    }

    public static Double getBackendOptFactor(Double mergeCvr, Double avgCvr) {
        Double ret = null;
        if (AssertUtil.isAllNotEmpty(mergeCvr, avgCvr)) {

            Double ratio = (mergeCvr + 0.001) / (avgCvr + 0.001);
//            System.out.println("ratio=" + ratio + ",mCvr=" + mergeCvr + ",aCvr=" + avgCvr);

            if (ratio <= 1.0  ) {
                if(mergeCvr<0.1){
                ret = ModelPredRectifier.sigmoidWithZoomAndIntervalMap((ratio - 0.5), 0.1, 1.01, 8);
                }
                if(mergeCvr>=0.1){
                    ret = ModelPredRectifier.sigmoidWithZoomAndIntervalMap((ratio - 0.2), 0.1, 1.01, 8);
                }
            } else if (ratio <= 2.0) {
                ret = 1.05;
            } else {
                ret = 1.1;
            }

        }
        return ret;
    }

    public static Double getMergeBackendCvr(Double statCvr, Double preCvr) {
        Double ret = null;
        if (statCvr != null && preCvr != null) {
            ret = 0.4 * statCvr + 0.6 * preCvr;
        }
//        if (statCvr == null && preCvr != null) {
//            ret = 1 * preCvr;
//        }
//
//        if (statCvr != null && preCvr == null) {
//            ret = 1 * statCvr;
//        }
        return ret;
    }

    /**
     * 计算目标后端类型的转化率
     *
     * @param statDo
     * @param backendType
     * @return
     */
    public static Double getTaskBackendCvr(StatDo statDo, String backendType) {
        Double ret = null;
        if (statDo != null && backendType != null) {
            Long actClickCnt = statDo.getActClickCnt();
            Map<String, Long> backendCntMap = statDo.getBackendCntMap();
            if (actClickCnt != null && actClickCnt >= 30 && AssertUtil.isNotEmpty(backendCntMap)) {
                Long cnt = backendCntMap.get(backendType);
                if (cnt == null) {
                    cnt = 0L;
                }
                ret = DataUtil.division(cnt, actClickCnt, 4);
            }

        }

        return ret;

    }

    /**
     * 计算目标后端类型
     *
     * @param statDo
     * @return
     */
    public static String getTaskBackendType(StatDo statDo) {
        String ret = null;
        if (statDo != null) {
            Long actClickCnt = statDo.getActClickCnt();
            Map<String, Long> backendCntMap = statDo.getBackendCntMap();
            if (actClickCnt != null && actClickCnt >= 50 && AssertUtil.isNotEmpty(backendCntMap)) {

                // 添加优先级权重，并过滤
                Map<String, Double> filterMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : backendCntMap.entrySet()) {
                    String type = entry.getKey();
                    Long cnt = entry.getValue();
                    Double weight = backendTypePriorityMap.get(type);
                    if (cnt != null && cnt >= 10L && weight != null) {
                        filterMap.put(type, cnt * weight);
                    }
                }

                // 返回得分Top
                ret = getValueTopOneFromMap(filterMap);

            }

        }

        return ret;

    }


    /**
     * 获取map 依据value大小排序的 top1 key
     *
     * @param map
     * @return
     */
    public static String getValueTopOneFromMap(Map<String, Double> map) {
        String ret = null;
        if (AssertUtil.isNotEmpty(map)) {
            Double maxValue = 1.0;
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                Double val = entry.getValue();
                if (val != null && val > maxValue) {
                    maxValue = val;
                    ret = entry.getKey();
                }
            }
        }


        return ret;
    }


    public static void main(String[] args) {

        StatDo statDo = new StatDo();
        statDo.setAdvertId(1L);
        statDo.setActClickCnt(40L);
        Map<String, Long> bcMap = new HashMap<>();
//        bcMap.put("1",6L);
        bcMap.put("3", 4L);
        statDo.setBackendCntMap(bcMap);


        Map<Long, StatDo> appAdvertStatMap = new HashMap<>();
        appAdvertStatMap.put(1L, statDo);

        StatDo statDo2 = new StatDo();
        statDo2.setAdvertId(1L);
        statDo2.setActClickCnt(300L);
        Map<String, Long> bcMap2 = new HashMap<>();
        bcMap2.put("2", 40L);
        bcMap2.put("1", 50L);
        statDo2.setBackendCntMap(bcMap2);

        Map<Long, StatDo> globalAdvertStatMap = new HashMap<>();
        globalAdvertStatMap.put(1L, statDo2);

        Map<Long, BackendAdvertStatDo> ret1 = getBackendAdvertStatInfo(appAdvertStatMap, globalAdvertStatMap);
        //Map<Long, BackendAdvertStatDo> ret1= getBackendAdvertStatInfo(null,null);
        System.out.println("ret1=" + JSON.toJSONString(ret1));


        Map<Long, Double> advertPreInfo = new HashMap<>();
        advertPreInfo.put(1L, 0.1);

        Map<Long, Double> ret2 = getBackendAdvertInfo(advertPreInfo, ret1);
        System.out.println("ret2=" + JSON.toJSONString(ret2));
    }
}
