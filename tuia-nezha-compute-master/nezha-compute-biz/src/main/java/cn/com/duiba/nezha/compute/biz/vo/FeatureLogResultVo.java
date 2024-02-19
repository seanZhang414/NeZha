package cn.com.duiba.nezha.compute.biz.vo;


import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/11/16.
 */
public class FeatureLogResultVo implements Serializable {

    private static final long serialVersionUID = -316102112618444133L;
    private List<FeatureSyncVo> featureSyncVoList = null;

    public List<FeatureSyncVo> getFeatureSyncVoList() {
        return featureSyncVoList;
    }

    public void setFeatureSyncVoList(List<FeatureSyncVo> featureSyncVoList) {
        this.featureSyncVoList = featureSyncVoList;
    }

}
