package cn.com.duiba.tuia.engine.activity.utils;

import java.util.UUID;

public class GamePathUtil {

    private GamePathUtil(){}
    /**
     * 拼接游戏大厅链接
     * 后面会带上towId 用于游戏大厅及其游戏首次赠送，每日赠送相关弹层逻辑
     * 后面会带上markId 全链路标识，目前只做到一级页面，同时也会在用户登录时长中部分日志中
     * 后面会带上pg=GAME 用于标识直投页的类型为游戏大厅
     * 后面会带上tenter=SOW 标识后面一级页面是从引擎跳转过来
     * 后面会带上dsm和dcm
     * @param slotId 广告位
     * @param pageId 直投页ID
     * @param appKey 媒体key
     * @param actHost 活动域名
     * @return
     */
    public static String buildClickUrl4GuidePage(Long slotId,Long pageId,String appKey,String actHost) {
        StringBuilder activityUrl = new StringBuilder("http://").append(actHost);
        activityUrl.append("/direct/index?id=");
        activityUrl.append(pageId).append("&slotId=").
                append(slotId).append("&login=normal&appKey=").
                           append(appKey);
        appendDsm(activityUrl, slotId);
        appendDcm(activityUrl, slotId);
        appendUid(activityUrl);
        appendGameMark(activityUrl);
        appendSOW(activityUrl);
        return activityUrl.toString();
    }


    private static void appendUid(StringBuilder activityUrl) {
        String uuid = UUID.randomUUID().toString();
        activityUrl.append("&towId=").append(uuid);
        activityUrl.append("&markId=").append(uuid);
    }

    private static void appendGameMark(StringBuilder activityUrl) {
        activityUrl.append("&pg=GAME");
    }

    private static void appendSOW(StringBuilder url) {
        url.append("&tenter=SOW");
    }

    private static void appendDsm(StringBuilder url, Long slotId) {
        url.append("&dsm=1.").append(slotId).append(".0.0");
    }

    private static void appendDcm(StringBuilder url, Long slotId) {
        url.append("&dcm=401.").append(slotId).append(".0.0");
    }


}
