package cn.com.duiba.tuia.constant;

import java.util.Date;

import cn.com.duiba.wolf.utils.DateUtils;

public class RedisKeyConstant {

	private static final String PREFIX = "TOW_";
	
	/**广告位曝光频次用户维度key**/
	private static final String AD_EXPOSURE_FREQUENCY_USER = "K00_";
	/**
     * 广告位曝光次数
     */
	private static final String FIELD_SLOT_FREQUENCY = "SF";
	/**
     * 活动曝光次数
     */
	private static final String FIELD_ACTIVITY_FREQUENCY = "NAF";
	/**
     * 用户活动访问历史
     */
    private static final String FIELD_VISIT_HISTORY = "VH";
	/**
	 * 用户活动访问索引
	 */
	private static final String FIELD_VISIT_INDEX = "VI";

	/**
	 * 用户活动访问重置次数
	 */
	private static final String FIELD_ACTIVITY_RESET = "AR";
	/**
     * 用户素材访问历史
     */
    private static final String MATERIAL_VISIT_HISTORY = "MVH";
    
    /**
     * 用户素材访问历史(手动投放专用)
     */
    private static final String MANUAL_MATERIAL_VISIT_HISTORY = "MMVH";


	/**
     * 新素材曝光数量
     */
	private static final String NEW_MATERIAL_EXPOSE_COUNT = "K01_";
	
	/**
	 * 广告位新素材曝光数量
	 */
	private static final String SLOT_NEW_MATERIAL_EXPOSE_COUNT = "K04_";
	/**
     * 新活动曝光数量
     */
	private static final String NEW_ACT_EXPOSE_COUNT = "K02_";
	
	/**
	 * SLOT_ACTINFO:广告位活动算法模型
	 */
	private static final String SLOT_ACTINFO = "K03_";

	/**
	 * SLOT_ACTINFO_NEW:广告位活动新算法模型
	 */
	private static final String SLOT_ACTINFO_NEW = "K08_";

	/**
	 * SLOT_ACTINFO:广告位活动算法模型(主会场)
	 */
	private static final String SLOT_ACTINFO_MAIN = "K07_";
	/**
	 * 唯品会安装情况
	 */
	private static final String VIP_INSTALL = "K05_";
	/**
	 * device_id对应的Android&iOS的IMEI IDFA
	 */
	private static final String VIP_MUID = "MUID";

	/**
	 * 第三方 用户安装信息
	 * hash结构：
	 * hashKey:
	 * 淘宝：taobao
	 */
	private static final String THIRD_INSTALL = PREFIX + "K06_";


	private RedisKeyConstant() {
	}

	/**
     * 曝光频次-获取用户维度的key
     * @param slotId
     * @param deviceId
     * @return key
     */
	public static String getUserKey(Long slotId, String deviceId){
		String date = DateUtils.getDayStr(new Date());
        return PREFIX + AD_EXPOSURE_FREQUENCY_USER + slotId + "_" + deviceId + "_" + date;
	}

	/**
	 * 活动参与次数散列域key
	 * @return
	 */
	public static String getActivityReset() {
		return FIELD_ACTIVITY_RESET;
	}

	/**
	 * 广告位曝光次数散列域key
	 * @return
	 */
	public static String getSlotFrequency() {
        return FIELD_SLOT_FREQUENCY;
    }
	
	/**
	 * 活动曝光次数散列域key
	 * @return
	 */
	public static String getActivityFrequency() {
        return FIELD_ACTIVITY_FREQUENCY;
    }
	
	/**
     * 用户活动访问历史
     * @return
     */
	public static String getVisitHistory() {
        return FIELD_VISIT_HISTORY;
    }

	/**
	 * 用户活动访问索引
	 * @return
	 */
	public static String getVisitIndex() {
		return FIELD_VISIT_INDEX;
	}
	
	/**
     * 用户素材访问历史
     * @return
     */
	public static String getMaterialVisitHistory() {
        return MATERIAL_VISIT_HISTORY;
    }
	
	
	/**
     * 用户素材访问历史
     * @return
     */
    public static String getManualMaterialVisitHistory() {
        return MANUAL_MATERIAL_VISIT_HISTORY;
    }
	
	
	/**
     * 获取新素材曝光量KEY
     * 
     * @param materialId 素材ID
     * @return KEY
     */
    public static String getNewMaterialExposeCount(Long materialId) {
    	return PREFIX + NEW_MATERIAL_EXPOSE_COUNT + materialId;
    }
    
    public static String getSlotNewMaterialExposeCount(Long sckId) {
    	return PREFIX + SLOT_NEW_MATERIAL_EXPOSE_COUNT + sckId;
    }
    
    /**
     * 获取新活动曝光量KEY
     * 
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return KEY
     */
    public static String getNewActExposeCount(Long activityId, Integer activitySource) {
        return PREFIX + NEW_ACT_EXPOSE_COUNT + activityId + "-" + activitySource;
    }
    
    
    /**
     * getSlotActInfo:获取广告位活动算法模型key. <br/>
     *
     * @param slotId 广告位id
     * @return key
     */
    public static String getSlotActInfo(Long slotId) {
        return PREFIX + SLOT_ACTINFO + slotId;
    }

	/**
	 * 获取广告位新活动算法模型key.
	 *
	 * @param slotId 广告位id
	 * @return key
	 */
	public static String getSlotActInfoNew(Long slotId) {
		return PREFIX + SLOT_ACTINFO_NEW + slotId;
	}

	/**
	 * 获取广告位活动算法模型key(主会场).
	 *
	 * @param slotId 广告位id
	 * @return key
	 */
	public static String getSlotActInfoMain(Long slotId) {
		return PREFIX + SLOT_ACTINFO_MAIN + slotId;
	}
	public static String getVipInstall() {
		return PREFIX + VIP_INSTALL;
	}
	public static String getVipMuid() {
		return PREFIX + VIP_MUID;
	}

    /**
     * 第三方用户安装情况
     * 目前接入公司：淘宝
     */
    public static String getThirdInstall(String deviceId) {
        return THIRD_INSTALL + deviceId;
    }

}
