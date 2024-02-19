package cn.com.duiba.tuia.engine.activity.model;



/**
 * Created by sunjiangrong . 16/11/14 .
 */
public final class AdType {

    /** 广告位类型 0-插屏. */
    public static final int    ADSENSE_TYPE_PLAQUE                        = 0; // NOSONAR
    /** 广告位类型 1-横幅. */
    public static final int    ADSENSE_TYPE_STREAMER_BANNER               = 1; // NOSONAR
    /** 广告位类型 2-信息流. */
    public static final int    ADSENSE_TYPE_INFORMATION_STREAM            = 2; // NOSONAR
    /** 广告位类型 3-banner. */
    public static final int    ADSENSE_TYPE_BANNER                        = 3; // NOSONAR
    /** 广告位类型 4-浮标. */
    public static final int    ADSENSE_TYPE_BUOY                          = 4; // NOSONAR
    /** 广告位类型 5-应用墙. */
    public static final int    ADSENSE_TYPE_APP_WALL                      = 5; // NOSONAR
    /** 广告位类型 6-开屏. */
    public static final int    ADSENSE_TYPE_OPEN_SCREEN                   = 6; // NOSONAR
    /** 广告位类型 7-自定义. */
    public static final int    ADSENSE_TYPE_USER_DEFINED                  = 7; // NOSONAR
    /** 广告位类型 8-手动投放. */
    public static final int    ADSENSE_TYPE_MANUAL                        = 8; // NOSONAR
    /** 广告位类型 9-规格类型广告位 */
    public static final int    ADSENSE_TYPE_MATERIAL_SPEC                 = 9; // NOSONAR

    private AdType() {
    }




//    /**
//     * valueOf
//     *
//     * @param msId
//     * @return Integer
//     */
	public static Integer valueOf(Long msId) {
		if (msId == null) {
			return null;
		} else if (msId == 1L) {
			/** 广告位类型 0-插屏. */
			return ADSENSE_TYPE_PLAQUE;
		} else if (msId == 2L) {
			/** 广告位类型 1-横幅. */
			return ADSENSE_TYPE_STREAMER_BANNER;
		} else if (msId == 3L || msId == 4L) {
			/** 广告位类型 2-信息流. */
			return ADSENSE_TYPE_INFORMATION_STREAM;
		} else if (msId == 5L) {
			/** 广告位类型 3-banner. */
			return ADSENSE_TYPE_BANNER;
		} else if (msId == 6L) {
			/** 广告位类型 4-浮标. */
			return ADSENSE_TYPE_BUOY;
		} else if (msId == 7L) {
			/** 广告位类型 5-应用墙. */
			return ADSENSE_TYPE_APP_WALL;
		} else if (msId == 8L) {
			/** 广告位类型 6-开屏. */
			return ADSENSE_TYPE_OPEN_SCREEN;
		} else if (msId == 9L) {
			/** 广告位类型 7-自定义. */
			return ADSENSE_TYPE_USER_DEFINED;
		}
		// 其它类型都为自定义
		return ADSENSE_TYPE_USER_DEFINED;
	}
}
