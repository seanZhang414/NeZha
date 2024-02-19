package cn.com.duiba.nezha.compute.api.dict;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/12/15.
 */
public class CategoryFeatureDict implements Serializable {


    private static final long serialVersionUID = -316102112618444923L;

    private Map<String, List<String>> dict = new HashMap<>();

    //
    public Map<String, List<String>> getFeatureDict() {
        return this.dict;
    }

    public void setFeatureDict(Map<String, List<String>> dict) {
        this.dict = dict;
    }

    //
    public List<String> getFeature(String featureIdx) {
        return this.dict.get(featureIdx);
    }

    public void setFeature(String featureIdx, List<String> featureMap) {
        this.dict.put(featureIdx, featureMap);
    }

}
