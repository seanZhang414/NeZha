package cn.com.duiba.nezha.engine.biz.vo.advert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuezhaoming on 16/8/2. <br>
 * 权重对象</br>
 */
public class RcmdTypeStatVo implements Serializable {

    private static final long serialVersionUID = -316104112618444933L;

    //广告ID
    private long advertId;

    //标签ID
    private List<String> tagList;

    //应用标签推荐类型
    private long appTagRcmdTypeSupport;
    //活动主题标签推荐类型
    private long acTagRcmdTypeSupport;
    //混合标签推荐类型
    private long appAndacMixTagRcmdTypeSupport;

    //全局标签推荐类型
    private long globalTagRcmdTypeSupport;


    public long getAdvertId() {
        return advertId;
    }

    public void setAdvertId(long advertId) {
        this.advertId = advertId;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public void addTagList(String tagId) {
        if (this.tagList==null){
            this.tagList = new ArrayList<>();
        }
        this.tagList.add(tagId);
    }


    public long getAppTagRcmdTypeSupport() {
        return appTagRcmdTypeSupport;
    }

    public void setAppTagRcmdTypeSupport(long appTagRcmdTypeSupport) {
        this.appTagRcmdTypeSupport = appTagRcmdTypeSupport;
    }

    public void addAppTagRcmdTypeSupport() {
        this.appTagRcmdTypeSupport += 1;
    }


    public long getAcTagRcmdTypeSupport() {
        return acTagRcmdTypeSupport;
    }

    public void setAcTagRcmdTypeSupport(long acTagRcmdTypeSupport) {
        this.acTagRcmdTypeSupport = acTagRcmdTypeSupport;
    }

    public void addAcTagRcmdTypeSupport() {
        this.acTagRcmdTypeSupport += 1;
    }


    public long getAppAndacMixTagRcmdTypeSupport() {
        return appAndacMixTagRcmdTypeSupport;
    }

    public void setAppAndacMixTagRcmdTypeSupport(long appAndacMixTagRcmdTypeSupport) {
        this.appAndacMixTagRcmdTypeSupport = appAndacMixTagRcmdTypeSupport;
    }

    public void addAppAndacMixTagRcmdTypeSupport() {
        this.appAndacMixTagRcmdTypeSupport += 1;
    }


    public long getGlobalTagRcmdTypeSupport() {
        return globalTagRcmdTypeSupport;
    }

    public void setGlobalTagRcmdTypeSupport(long globalTagRcmdTypeSupport) {
        this.globalTagRcmdTypeSupport = globalTagRcmdTypeSupport;
    }

    public void addGlobalTagRcmdTypeSupport() {
        this.globalTagRcmdTypeSupport += 1;
    }
}
