package cn.com.duiba.tuia.engine.activity.model.req;

import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * SpmActivityReq
 * SDK 2.1 @link http://cf.dui88.com/pages/viewpage.action?pageId=5243485
 */
public class SpmActivityReq extends ManualParamReq implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int  DATA_SOURCE_BIT  = 10;

    // 客户端请求字段
    //@NotNull(message = "广告位id不可为空")
    private Long              adslot_id;            // NOSONAR

    private Long              slotId;
    // 必填
    // 广告位id

    @NotBlank(message = "应用key不可为空")
    private String            app_key;              // NOSONAR
    // 必填
    // 应用key
    // 代码位在推啊媒体平台（以下简称推啊SSP）提交媒体信息，可获得应用ID。示例：XXXX

    @NotBlank(message = "设备ID不可为空")
    private String            device_id;            // NOSONAR
    // 设备唯一ID，用于区分不同用户设备

    @NotNull(message = "活动id不可为空")
    private String            activity_id;          // NOSONAR 目前暂时用做 activityId,materialId两个字段共用
    // 活动id

    private String            ua;                   // 客户端ua

    private String            ip;                   // 客户端ip

    private String            os_type;              // NOSONAR
    // 描述操作系统的可选值集合。包括iOS
    // 和Android

    @NotNull(message = "请求类型不可为空")
    private Integer           type;                 // 曝光OR点击,
                                                    // 0曝光、1点击

    // 服务端日志需要字段
    private Long              app_id;               // NOSONAR
    // 应用Id，服务端通过appKey获取

    // 同盾字段
    private String            event_id;             // NOSONAR
    // 同盾事件标识

    private String            token_id;             // NOSONAR
    // 同盾会话标识Key

    private String            seq_id;               // NOSONAR
    // 同盾请求序列号，每个请求进来都分配一个全局唯一的id

    private String            final_decision;       // NOSONAR
    // 同盾最终的风险决策结果

    private String            device_info;          // NOSONAR
    // 同盾设备指纹信息

    private String            serverTime;           // 服务端时间，日志统计需要

    private String            data1;                // 扩展字段1，由服务端返回，客户端回传

    private String            data2;                // 扩展字段2，由服务端返回，客户端回传

    // 以下SDK V1.5新增字段, http://cf.dui88.com/pages/viewpage.action?pageId=4500879
    // 广告位点击活动链接URL，示例：http://activity.tuia.cn/activity/index?id=199&slotId=727&login=normal&appKey=4HwXeb5Nbzs68fUKpYicnPCHs9WK&deviceId=4mdAkEa4UOoqYWuNTnEo1490134321527&tck_rid_6c8=0a1ce5bbj1ht8284-37254058&tck_loc_c5d=tactivity-199&dcm=401.727.0.2776&
    private String            click_url;            // NOSONAR


    // SPM ID
    private String            spm_id;               // NOSONAR

    //全链路唯一请求id
    private String            rid;

    //此次请求所在的页面地址  JSSDK必填
    private String            page_url;             // NOSONAR
    
    //请求来源地址  JSSDK必填
    private String            sdk_source;            // NOSONAR

    //推啊SDK系统类型（Android，iOS，JSSDK）  必填
    private String            sdk_type;              // NOSONAR

    //推啊SDK版本号（例如：2.0.1）  必填
    private String            sdk_version;           // NOSONAR

    //string 是 此次请求、曝光、点击的应用包名
    private String app_package;                      // NOSONAR


    // 增加jssdk支持传入用户数据,全部非必填，兼容以前版本  －－ 2018.01.16  cdm   --start
    // cf:http://cf.dui88.com/pages/viewpage.action?pageId=6852987

    private String md;


    // 增加jssdk支持传入用户数据,全部非必填，兼容以前版本  －－ 2018.01.16  cdm   --end

    private String refer_host; //广告位曝光日志中refer中的host，需要解析到三级域名，如www.baidu.com  --add 20180201 -cdm
    // http://cf.dui88.com/pages/viewpage.action?pageId=9766391
    // 设备机型 根据user_agent解析出设备机型
    private String model;
    // 网络类型（2G,3G,WIFI等）
    private String connect_type;// NOSONAR

    public String getRefer_host() {
        return refer_host;
    }

    public void setRefer_host(String refer_host) {
        this.refer_host = refer_host;
    }

    public Long getAdslot_id() {// NOSONAR
        return adslot_id;
    }

    public void setAdslot_id(Long adslot_id) {// NOSONAR
        this.adslot_id = adslot_id;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getApp_key() {// NOSONAR
        return app_key;
    }

    public void setApp_key(String app_key) {// NOSONAR
        this.app_key = app_key;
    }

    public String getDevice_id() {// NOSONAR
        return device_id;
    }

    public void setDevice_id(String device_id) {// NOSONAR
        this.device_id = device_id;
    }

    public Long getActivity_id() {// NOSONAR
        //临时解决activity_id=字符串null的判断，导致Long.valueOf报NumberFormatException
        if (!StringUtils.isEmpty(activity_id) && !activity_id.equalsIgnoreCase("null")) {
            String data = activity_id.split(SplitConstant.SPLIT_COMMA)[0];
            if (data.length() == DATA_SOURCE_BIT) {
                return Long.valueOf(data.substring(1));
            }
            return Long.valueOf(data);
        }
        return null;
    }

    public void setActivity_id(String activity_id) {// NOSONAR
        this.activity_id = activity_id;
    }
    
    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOs_type() {// NOSONAR
        return os_type;
    }

    public void setOs_type(String os_type) {// NOSONAR
        this.os_type = os_type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getApp_id() {// NOSONAR
        return app_id;
    }

    public void setApp_id(Long app_id) {// NOSONAR
        this.app_id = app_id;
    }

    public String getEvent_id() {// NOSONAR
        return event_id;
    }

    public void setEvent_id(String event_id) {// NOSONAR
        this.event_id = event_id;
    }

    public String getToken_id() {// NOSONAR
        return token_id;
    }

    public void setToken_id(String token_id) {// NOSONAR
        this.token_id = token_id;
    }

    public String getSeq_id() {// NOSONAR
        return seq_id;
    }

    public void setSeq_id(String seq_id) {// NOSONAR
        this.seq_id = seq_id;
    }

    public String getFinal_decision() {// NOSONAR
        return final_decision;
    }

    public void setFinal_decision(String final_decision) {// NOSONAR
        this.final_decision = final_decision;
    }

    public String getDevice_info() {// NOSONAR
        return device_info;
    }

    public void setDevice_info(String device_info) {// NOSONAR
        this.device_info = device_info;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getClick_url() {// NOSONAR
        return click_url;
    }

    public void setClick_url(String click_url) {// NOSONAR
        this.click_url = click_url;
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

    public Long getMaterial_id() {// NOSONAR
        if (!StringUtils.isEmpty(activity_id)) {
            String[] array = activity_id.split(SplitConstant.SPLIT_COMMA);
            if (array.length >= 2 && array[1] != null) {
                return Long.parseLong(array[1]);
            }
        }
        return null;
    }

    public Long getSck_id() {// NOSONAR
    	if (!StringUtils.isEmpty(activity_id)) {
    		String[] array = activity_id.split(SplitConstant.SPLIT_COMMA);
    		if (array.length >= 3 && array[2] != null) {
    			return Long.parseLong(array[2]);
    		}
    	}
    	return null;
    }
    
    public Integer getSource() {
        if (!StringUtils.isEmpty(activity_id)) {
            String data = activity_id.split(SplitConstant.SPLIT_COMMA)[0]; // 活动来源 = 0 兑吧， =1 推啊  =2 流量引导页
            if (data.length() == DATA_SOURCE_BIT) {
                return Integer.valueOf(data.substring(0, 1)) - 1; // 前台带的是 1 兑吧， 2 推啊， 3 流量引导页
            }
            return 0; // 默认兑吧来源
        }
        return null;
    }

    public String getSpm_id() {    // NOSONAR
        return spm_id;
    }

    public void setSpm_id(String spm_id) {    // NOSONAR
        this.spm_id = spm_id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getPage_url() {    // NOSONAR
        return page_url;
    }

    public void setPage_url(String page_url) {   // NOSONAR
        this.page_url = page_url;
    }

    public String getSdk_source() {    // NOSONAR
		return sdk_source;
	}

	public void setSdk_source(String sdk_source) {    // NOSONAR
		this.sdk_source = sdk_source;
	}

	public String getSdk_type() {   // NOSONAR
        return sdk_type;
    }

    public void setSdk_type(String sdk_type) {    // NOSONAR
        this.sdk_type = sdk_type;
    }

    public String getSdk_version() {    // NOSONAR
        return sdk_version;
    }

    public void setSdk_version(String sdk_version) {   // NOSONAR
        this.sdk_version = sdk_version;
    }

    public String getApp_package() {   // NOSONAR
        return app_package;
    }

    public void setApp_package(String app_package) {   // NOSONAR
        this.app_package = app_package;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getConnect_type() {
        return connect_type;
    }

    public void setConnect_type(String connect_type) {
        this.connect_type = connect_type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


}
