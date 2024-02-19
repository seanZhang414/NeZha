package cn.com.duiba.nezha.compute.api.cachekey;

/**
 * Created by pc on 2017/2/17.
 */
public class ModelKey {

    public static final String HBASE_PROFIX = "nz_cof_"; // consumer order rank

    public static final String REDIS_PROFIX = "nzh_e_cof_"; // consumer order rank

    // 模型Key
    private static final String MODEL_DT_KEY_PREFIX = "nz_dt_model_";

    // 模型Key
    private static final String MODEL_LAST_KEY_PREFIX = "nz_last_model_";

    public static String getConsumerOrderFeatureRowKey(String consumerId, String activityId) {

        return HBASE_PROFIX + consumerId + "_" + activityId + "_";
    }


    /**
     * @param modelKey
     * @param dt
     * @return
     */
    public static String getDtModelKey(String modelKey, String dt) {

        return MODEL_DT_KEY_PREFIX + modelKey + "_" + dt + "_";
    }

    /**
     * @param modelKey
     * @return
     */
    public static String getLastModelKey(String modelKey) {

        return MODEL_LAST_KEY_PREFIX + modelKey + "_";
    }

}
