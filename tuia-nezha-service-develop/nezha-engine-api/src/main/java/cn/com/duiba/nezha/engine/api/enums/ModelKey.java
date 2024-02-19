package cn.com.duiba.nezha.engine.api.enums;

public class ModelKey {

    private static final String MODEL_LAST_NEW_KEY_PREFIX = "nz_last_model_new_";

    private ModelKey() {
        // do nothing
    }
    public static String getLastModelNewKey(String modelKey) {

        return MODEL_LAST_NEW_KEY_PREFIX + modelKey + "_";
    }

}
