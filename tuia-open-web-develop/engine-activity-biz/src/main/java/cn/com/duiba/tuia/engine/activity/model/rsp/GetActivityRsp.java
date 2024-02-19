package cn.com.duiba.tuia.engine.activity.model.rsp;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * GetActivityRsp
 */
public class GetActivityRsp implements Serializable {

    private static final long serialVersionUID = 1L;

    // 基础参数
    private String            request_id;           // NOSONAR
    // 广告请求ID 对应的请求ID，由接入方自定义

    private String            error_code;           // NOSONAR
    // 错误码
    // 请求相应出错时的错误接入码，用于问题排查。

    private Long              adslot_id;            // NOSONAR

    private Long              slotId;
    // 对应请求时填写的广告位ID
    // 广告参数
    /** 活动ID. */
    private String            activity_id;          // NOSONAR

    private String            ad_icon;              // NOSONAR

    private String            icon;
    // "广告"小图标

    private boolean           ad_icon_visible;      // NOSONAR
    // 是否展示"广告"小图标

    private boolean           icon_visible;      // NOSONAR

    private String            ad_close;             // NOSONAR
    // "广告"关闭小图标

    private String            close;

    private boolean           ad_close_visible;     // NOSONAR
    // 是否展示"广 告"关闭小图标

    private boolean           close_visible;     // NOSONAR

    /** 广告标题题. */
    private String            ad_title;             // NOSONAR

    private String            title;             // NOSONAR

    /** 活动标题. */
    private String            activity_title;       // NOSONAR

    /** 广告描述. */
    private String            description;

    /** 活动图片地址. */
    private String            img_url;              // NOSONAR

    /** 活动图片的高度. */
    private int               img_height;           // NOSONAR

    /** 活动图片的宽度. */
    private int               img_width;            // NOSONAR

    /** 广告内容 */
    private String            ad_content;           // NOSONAR

    private String            content;           // NOSONAR

    /** 广告类型. */
    private Integer           ad_type;              // NOSONAR

    private Integer           type;              // NOSONAR

    /** 活动URL. */
    private String            click_url;            // NOSONAR

    /** 结果有效时间 */
    private Long              expire;

    // 同盾埋点需要
    private String            wdata_token;          // NOSONAR

    // 服务端时间，日志统计需要
    private String            server_time;          // NOSONAR

    // 素材ID
    private Long              material_id;          // NOSONAR
    
    //素材库id
    private Long 			  sck_Id;               // NOSONAR

    // 用户点击行为日志
    private String            dcm;                  // NOSONAR

    // 素材列表
    private List<MaterialRsp> material_list;        // NOSONAR

    // 活动投放方式
    private Integer           activity_way;         // NOSONAR

    // 子活动投放方式
    private Integer           sub_activity_way;     // NOSONAR

    // 素材投放方式
    private Integer           material_way;         // NOSONAR

    private String            data1;                // 扩展字段1，由服务端返回，客户端回传

    private String            data2;                // 扩展字段2，由服务端返回，客户端回传

    //全链路唯一请求id
    private String            rid;

    private int               source;
    //活动来源  0-兑吧 1-推啊 2-流量引导页

    private long              app_id;               // NOSONAR
    //应用ID

    private String            device_id;            // NOSONAR
    
    private int        outputSource;  //活动投放来源：0非活动引擎，1活动引擎

    //当前引擎域名
    private String            host;

    public String getRequest_id() {// NOSONAR
        return request_id;
    }

    public void setRequest_id(String request_id) {// NOSONAR
        this.request_id = request_id;
    }

    public String getError_code() {// NOSONAR
        return error_code;
    }

    public void setError_code(String error_code) {// NOSONAR
        this.error_code = error_code;
    }

    public Long getAdslot_id() {// NOSONAR
        return adslot_id;
    }

    public void setAdslot_id(Long adslot_id) {// NOSONAR
        this.adslot_id = adslot_id;
    }

    public String getActivity_id() {// NOSONAR
        return activity_id;
    }

    public void setActivity_id(String activity_id) {// NOSONAR
        this.activity_id = activity_id;
    }

    public String getAd_icon() {// NOSONAR
        return ad_icon;
    }

    public void setAd_icon(String ad_icon) {// NOSONAR
        this.ad_icon = ad_icon;
    }

    public boolean isAd_icon_visible() {// NOSONAR
        return ad_icon_visible;
    }

    public void setAd_icon_visible(boolean ad_icon_visible) {// NOSONAR
        this.ad_icon_visible = ad_icon_visible;
    }

    public String getAd_close() {// NOSONAR
        return ad_close;
    }

    public void setAd_close(String ad_close) {// NOSONAR
        this.ad_close = ad_close;
    }

    public boolean isAd_close_visible() {// NOSONAR
        return ad_close_visible;
    }

    public void setAd_close_visible(boolean ad_close_visible) {// NOSONAR
        this.ad_close_visible = ad_close_visible;
    }

    public String getActivity_title() {// NOSONAR
        return activity_title;
    }

    public void setActivity_title(String activity_title) {// NOSONAR
        this.activity_title = activity_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {// NOSONAR
        return img_url;
    }

    public void setImg_url(String img_url) {// NOSONAR
        this.img_url = img_url;
    }

    public int getImg_height() {// NOSONAR
        return img_height;
    }

    public void setImg_height(int img_height) {// NOSONAR
        this.img_height = img_height;
    }

    public int getImg_width() {// NOSONAR
        return img_width;
    }

    public void setImg_width(int img_width) {// NOSONAR
        this.img_width = img_width;
    }

    public String getAd_title() {// NOSONAR
        return ad_title;
    }

    public void setAd_title(String ad_title) {// NOSONAR
        this.ad_title = ad_title;
    }

    public String getAd_content() {// NOSONAR
        return ad_content;
    }

    public void setAd_content(String ad_content) {// NOSONAR
        this.ad_content = ad_content;
    }

    public Integer getAd_type() {// NOSONAR
        return ad_type;
    }

    public void setAd_type(Integer ad_type) {// NOSONAR
        this.ad_type = ad_type;
    }

    public String getClick_url() {// NOSONAR
        return click_url;
    }

    public void setClick_url(String click_url) {// NOSONAR
        this.click_url = click_url;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public String getWdata_token() {// NOSONAR
        return wdata_token;
    }

    public void setWdata_token(String wdata_token) {// NOSONAR
        this.wdata_token = wdata_token;
    }

    public String getServer_time() {// NOSONAR
        return server_time;
    }

    public void setServer_time(String server_time) {// NOSONAR
        this.server_time = server_time;
    }

    public Long getMaterial_id() { // NOSONAR
        return material_id;
    }

    public void setMaterial_id(Long material_id) { // NOSONAR
        this.material_id = material_id;
    }

    public Long getSck_Id() {     // NOSONAR
		return sck_Id;
	}

	public void setSck_Id(Long sck_Id) {   // NOSONAR
		this.sck_Id = sck_Id;
	}

	public String getDcm() { // NOSONAR
        return dcm;
    }

    public void setDcm(String dcm) { // NOSONAR
        this.dcm = dcm;
    }

    public List<MaterialRsp> getMaterial_list() { // NOSONAR
        return material_list;
    }

    public void setMaterial_list(List<MaterialRsp> material_list) { // NOSONAR
        this.material_list = material_list;
    }

    public Integer getActivity_way() {// NOSONAR
        return activity_way;
    }

    public void setActivity_way(Integer activity_way) {// NOSONAR
        this.activity_way = activity_way;
    }

    public Integer getSub_activity_way() {
        return sub_activity_way;
    }

    public void setSub_activity_way(Integer sub_activity_way) {
        this.sub_activity_way = sub_activity_way;
    }

    public Integer getMaterial_way() {// NOSONAR
        return material_way;
    }

    public void setMaterial_way(Integer material_way) {// NOSONAR
        this.material_way = material_way;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public long getApp_id() { return app_id; }   // NOSONAR

    public void setApp_id(long app_id) { this.app_id = app_id; }  // NOSONAR

    public String getDevice_id() {    // NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {   // NOSONAR
        this.device_id = device_id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	public int getOutputSource() {
		return outputSource;
	}

	public void setOutputSource(int outputSource) {
		this.outputSource = outputSource;
	}

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isIcon_visible() {
        return icon_visible;
    }

    public void setIcon_visible(boolean icon_visible) {
        this.icon_visible = icon_visible;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public boolean getClose_visible() {
        return close_visible;
    }

    public void setClose_visible(boolean close_visible) {
        this.close_visible = close_visible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
