package cn.com.duiba.nezha.engine.biz.message.advert.ons;

/**
 * ONS roi维稳消息tag
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: OnsRoiControllerMessageTag.java , v 0.1 2017/6/6 下午2:32 ZhouFeng Exp $
 */
public enum OnsRoiControllerMessageTag {

    /**
     * 广告点击消息
     */
    ROI_CLICK("click"),

    /**
     * 计费统计消息
     */
    ROI_FEE("fee"),

    /**
     * 广告成本价格变更
     */
    ROI_COST("cost"),

    /**
     * 转化统计消息
     */
    ROI_CVR("cvr"),

    /**
     * 素材点击消息
     */
    MATERIAL_CLICK("material_click"),

    /**
     *
     */
    MATERIAL_EXPOSURE("material_exposure"),

    ;

    private String tag;

    OnsRoiControllerMessageTag(String tag) {
        this.tag = tag;
    }


    public String getTag() {
        return tag;
    }
}
