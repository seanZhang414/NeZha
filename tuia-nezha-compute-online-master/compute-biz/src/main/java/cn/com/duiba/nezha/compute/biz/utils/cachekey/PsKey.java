package cn.com.duiba.nezha.compute.biz.utils.cachekey;

import cn.com.duiba.nezha.compute.core.ReverseUtil;

import java.util.ArrayList;
import java.util.List;

public class PsKey {


    public static List<String> getRowKeyOfOrderIds(List<String> timeList) {

        List<String> ret = new ArrayList<>();
        for (String str : timeList) {
            if (str != null) {
                ret.add(getRowKeyOfOrderId(str));
            }
        }
        return ret;

    }


    public static List<String> getRowKeyOfPsSamples(List<String> orderIdList) {

        List<String> ret = new ArrayList<>();
        for (String str : orderIdList) {
            if (str != null) {
                ret.add(getRowKeyOfPsSample(str));
            }
        }
        return ret;

    }

    public static String getRowKeyOfOrderId(String time) {
        return ReverseUtil.reverseByStack(time);
    }

    public static String getRowKeyOfPsSample(String orderId) {
        return ReverseUtil.reverseByStack(orderId);
    }

    public static String getRowKeyOfPsBaseInfo(String modelId) {
        return ReverseUtil.reverseByStack(modelId) + "_ps_bi";
    }

    public static String getRowKeyOfModel(String modelId) {
        return ReverseUtil.reverseByStack(modelId);
    }

    public static String getParRowKey(String prefix, int parId) {
        return prefix + "_" + parId;
    }


    public static String getMatrixParRowKeySubPrefix(String modelId, int version, String mKey) {
        return ReverseUtil.reverseByStack(modelId) + "_" + version + "_m_" + mKey;
    }

    public static String getMatrixParRowKeyPrefix(String subPrefix, int nCols) {
        return subPrefix + "_" + nCols;
    }

    public static String getVectorParRowKeyPrefix(String modelId, int version, String vKey) {
        return ReverseUtil.reverseByStack(modelId) + "_" + version + "_v_" + vKey;
    }

    public static String getValKey(String modelId, int version) {
        return ReverseUtil.reverseByStack(modelId) + "_" + version + "_val_";
    }


}
