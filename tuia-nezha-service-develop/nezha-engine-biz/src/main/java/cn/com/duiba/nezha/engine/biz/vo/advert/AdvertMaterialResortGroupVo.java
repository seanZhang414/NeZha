package cn.com.duiba.nezha.engine.biz.vo.advert;

import java.util.List;

/**
 * Created by pc on 2016/12/22.
 */
public class AdvertMaterialResortGroupVo {

    private long materialGroupId;   // 素材投放组 ID
    private long materialGroupTraffic;    // 素材投放组流量总占比,取值 [0,100]
    private long materialNums;

    private List<AdvertMaterialResortVo> materialResortVoList;    // 素材投放组重排序列表

    public long getMaterialGroupId() { return materialGroupId; }

    public void setMaterialGroupId(long materialGroupId) { this.materialGroupId = materialGroupId; }

    public long getMaterialGroupTraffic() {
        return materialGroupTraffic;
    }

    public void setMaterialGroupTraffic(long materialGroupTraffic) {
        this.materialGroupTraffic = materialGroupTraffic;
    }

    public List<AdvertMaterialResortVo> getMaterialResortVoList() {
        return materialResortVoList;
    }

    public void setMaterialResortVoList(List<AdvertMaterialResortVo> materialResortVoList) {
        this.materialResortVoList = materialResortVoList;
    }

    public long getMaterialNums() { return materialNums; }

    public void setMaterialNums(long materialNums) { this.materialNums = materialNums; }

}
