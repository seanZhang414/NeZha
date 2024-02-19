package cn.com.duiba.nezha.engine.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pc on 2016/12/22.
 */
public class ReqAdvertMaterialDto implements Serializable {

    private static final long serialVersionUID = 523827427472072954L;
    private long appId;
    private long advertId;
    private Long oldMaterialTraffic;    // 老素材流量占比,取值 [0,100]
    private Long oldMaterialRatio;    // 老素材浮出系数,取值 [0,100]
    private List<Long> oldMaterialList;    // 老素材列表
    private List<Long> newMaterialList;    // 新素材列表

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }


    public long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(long advertId) {
        this.advertId = advertId;
    }


    public Long getOldMaterialTraffic() {
        return oldMaterialTraffic;
    }

    public void setOldMaterialTraffic(Long oldMaterialTraffic) {
        this.oldMaterialTraffic = oldMaterialTraffic;
    }


    public Long getOldMaterialRatio() {
        return oldMaterialRatio;
    }

    public void setOldMaterialRatio(Long oldMaterialRatio) {
        this.oldMaterialRatio = oldMaterialRatio;
    }

    public List<Long> getOldMaterialList() {
        return oldMaterialList;
    }

    public void setOldMaterialList(List<Long> oldMaterialList) {
        this.oldMaterialList = oldMaterialList;
    }

    public List<Long> getNewMaterialList() {
        return newMaterialList;
    }

    public void setNewMaterialList(List<Long> newMaterialList) {
        this.newMaterialList = newMaterialList;
    }
}
