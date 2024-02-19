package cn.com.duiba.tuia.engine.activity.service;

import cn.com.duiba.tuia.activity.center.api.dto.GuidePageDto;
import cn.com.duiba.tuia.engine.activity.exception.GameTypeException;
import cn.com.duiba.tuia.engine.activity.model.req.GameReq;
import cn.com.duiba.tuia.engine.activity.remoteservice.ActivityService;
import cn.com.duiba.tuia.ssp.center.api.constant.ActivityConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.wolf.utils.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private ActivityService activityService;

    @Value("${tuia.engine.activity.web.host}")
    private String activityTuiaWebHost;

    @Value("${tuia.engine.activity.host}")
    private String engineActivityHost;


    public String getUrl(GameReq param, Long appId) {
        String redirectUrl = getRedirectUrl(param);
        return getGameUrl(param, appId, redirectUrl);
    }

    public String getUrl2(GameReq param, Long appId) {
        String redirectUrl = getRedirectUrl2(param);
        return getGameUrl2(param, appId, redirectUrl);
    }

    private String getGameUrl(GameReq param, Long appId, String redirectUrl) {
        if (StringUtils.isBlank(redirectUrl)) {
            GuidePageDto guidePageDto = new GuidePageDto();
            guidePageDto.setId(904L);
            redirectUrl = buildClickUrl4GuidePage(guidePageDto, param);
        }

        Map<String, String> defParamMap = getDefaultParamMap();
        redirectUrl = UrlUtils.appendParams(redirectUrl, defParamMap);

        // 推啊活动WEB系统做特殊处理添加设备ID
        String newTargetUrl = null;
        if (redirectUrl.contains(activityTuiaWebHost)) {
            newTargetUrl = replaceHost(appId, redirectUrl);

        } else {
            newTargetUrl = redirectUrl;
        }

        return handleUrl(param, newTargetUrl);
    }

    private String getGameUrl2(GameReq param, Long appId, String redirectUrl) {
        if (StringUtils.isBlank(redirectUrl)) {
            GuidePageDto guidePageDto = new GuidePageDto();
            guidePageDto.setId(904L);
            redirectUrl = buildClickUrl4GuidePage(guidePageDto, param);
        }

        Map<String, String> defParamMap = getDefaultParamMap();
        redirectUrl = UrlUtils.appendParams(redirectUrl, defParamMap);

        // 推啊活动WEB系统做特殊处理添加设备ID
        String newTargetUrl = null;
        if (redirectUrl.contains(activityTuiaWebHost)) {
            newTargetUrl = replaceHost(appId, redirectUrl);

        } else {
            newTargetUrl = redirectUrl;
        }

        return handleUrl2(param, newTargetUrl);
    }

    //从手投活动引擎连接到游戏或者游戏大厅
    public String getUrlFromActUrl(GameReq param, Long appId) {
        SlotGameDto slotGameDto = new SlotGameDto();
        slotGameDto.setGameId(param.getGameId());
        slotGameDto.setGameSource(param.getSource());
        slotGameDto.setSlotId(param.getAdslotId());
        String redirectUrl = getRedirectUrl4Game(slotGameDto, param);
        return getGameUrl(param, appId, redirectUrl);
    }

    private String replaceHost(Long appId, String targetUrl) {
        String result = targetUrl;
        String host = "";
        // 新域名策略
        DomainConfigDto domainConfigDto = localCacheService.getDomainConfigByAppId(appId);
        if (domainConfigDto != null) {
            host = domainConfigDto.getActUrl();
        }
        if (StringUtils.isNotBlank(host) && !StringUtils.equals(activityTuiaWebHost, host)) {
            result = StringUtils.replace(targetUrl, activityTuiaWebHost, host); // NOSONAR
        }
        return result;
    }

    private Map<String, String> getDefaultParamMap() {
        Map<String, String> map = new HashMap<>();
        map.put("tenter", "SOW");
        return map;
    }

    /**
     * 积分对接需求需要先验证登陆，所以先定向到登陆页面
     *
     * @param param
     * @param redirectUrl
     * @return
     */
    private String handleUrl(GameReq param, String redirectUrl) {
        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(param.getAdslotId());
        //判断怎么跳转url,第三方登陆或者是同盾
        if (slotCacheDto != null && slotCacheDto.getPlugBuoyConfigDto() != null && Objects.equals(slotCacheDto
                .getPlugBuoyConfigDto().getLoginType(), 3)) {
            try {
                URL url = new URL(redirectUrl);
                StringBuilder sb = new StringBuilder();
                sb.append(url.getProtocol()).append("://").append(url.getHost()).append("/consumer/autoLogin")
                  .append("?appKey=").append(param.getAppKey()).append("&slotId=").append(param.getAdslotId()).append
                        ("&deviceId=")
                  .append(param.getDeviceId())
                  .append("&muId=").append(param.getMuId()).append("&sign=").append(param.getSign())
                  .append("&timestamp=").append(param.getTimestamp()).append("&redirectUrl=").append(URLEncoder
                        .encode(redirectUrl, "utf-8")).append("&nickName=").append(URLEncoder.encode(param
                        .getNickName(), "utf-8"))
                  .append("&headUrl=")
                  .append(URLEncoder.encode(param.getHeadUrl(), "utf-8")).append("&sex=").append(param.getSex());
                if(StringUtils.isNotBlank(param.getCreateTime())){
                    sb.append("&createTime=").append(param.getCreateTime());
                }
                redirectUrl = sb.toString();
            } catch (Exception e) {
                logger.warn("redirectUrl url is error,{}", redirectUrl, e);
            }
        } else {
            try {
                URL url = new URL(redirectUrl);
                StringBuilder sb = new StringBuilder();
                sb.append(url.getProtocol()).append("://").append(url.getHost()).append("/consumer/loginIndex")
                        .append("?appKey=").append(param.getAppKey()).append("&tokenId=").append(param.getTokenId())
                        .append("&slotId=").append(param.getAdslotId()).append("&redirectUrl=").append(URLEncoder.encode(redirectUrl, "utf-8"));
                redirectUrl = sb.toString();
            } catch (Exception e) {
                logger.warn("redirectUrl url is error,{}", redirectUrl, e);
            }
        }
        return redirectUrl;
    }

    private String handleUrl2(GameReq param, String redirectUrl) {
        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(param.getAdslotId());
        //判断怎么跳转url,第三方登陆或者是同盾
        if (slotCacheDto != null && slotCacheDto.getPlugBuoyConfigDto() != null && Objects.equals(slotCacheDto
                .getPlugBuoyConfigDto().getLoginType(), 3)) {
            try {
                URL url = new URL(redirectUrl);
                StringBuilder sb = new StringBuilder();
                sb.append(url.getProtocol()).append("://").append(url.getHost()).append("/consumer/autoLogin")
                  .append("?appKey=").append(param.getAppKey()).append("&slotId=").append(param.getAdslotId()).append
                        ("&deviceId=")
                  .append(param.getDeviceId())
                  .append("&muId=").append(param.getMuId()).append("&sign=").append(param.getSign())
                  .append("&timestamp=").append(param.getTimestamp()).append("&redirectUrl=").append(URLEncoder
                        .encode(redirectUrl, "utf-8")).append("&nickName=").append(URLEncoder.encode(param
                        .getNickName(), "utf-8"))
                  .append("&headUrl=")
                  .append(URLEncoder.encode(param.getHeadUrl(), "utf-8")).append("&sex=").append(param.getSex());
                if(StringUtils.isNotBlank(param.getCreateTime())){
                    sb.append("&createTime=").append(param.getCreateTime());
                }
                redirectUrl = sb.toString();
            } catch (Exception e) {
                logger.warn("redirectUrl url is error,{}", redirectUrl, e);
            }
        }
        return redirectUrl;
    }

    private String getRedirectUrl(GameReq param) {
        List<SlotGameDto> slotGameList = localCacheService.getSlotGameList(param.getAdslotId());
        if (CollectionUtils.isEmpty(slotGameList)) {
            return null;
        }
        //目前仅支持一个
        return getRedirectUrl4Game(slotGameList.get(0), param);
    }

    private String getRedirectUrl2(GameReq param) {
        List<SlotGameDto> slotGameList = localCacheService.getSlotGameList(param.getAdslotId());
        if (CollectionUtils.isEmpty(slotGameList)) {
            return null;
        }
        //目前仅支持一个
        return getRedirectUrl4Game2(slotGameList.get(0), param);
    }

    private String getRedirectUrl4Game(SlotGameDto slotGameDto, GameReq param) {
        if (slotGameDto == null) {
            return null;
        }

        List<ActivityDto> enableGameList = localCacheService.getEnableGameList();
        Optional<ActivityDto> first = enableGameList.stream().filter(o -> Objects.equals(o.getActivityId(), slotGameDto.getGameId()) && Objects.equals(o
                .getSource(), slotGameDto.getGameSource())).findFirst();
        if (!first.isPresent()) {
            return null;
        }
        if (Objects.equals(slotGameDto.getGameSource(), ActivityConstant.REQ_GAME_SOURCE)) {
            return buildClickUrl4Game(param, slotGameDto.getGameId());
        } else if (Objects.equals(slotGameDto.getGameSource(), ActivityConstant.REQ_GAME_HALL)) {
            GuidePageDto guidePageDto = activityService.getGuidePageDetail(slotGameDto.getGameId());
            return buildClickUrl4GuidePage(guidePageDto, param);
        }
        return null;
    }

    private String getRedirectUrl4Game2(SlotGameDto slotGameDto, GameReq param) {
        if (slotGameDto == null) {
            return null;
        }

        List<ActivityDto> enableGameList = localCacheService.getEnableGameList();
        Optional<ActivityDto> first = enableGameList.stream().filter(o -> Objects.equals(o.getActivityId(), slotGameDto.getGameId()) && Objects.equals(o
                .getSource(), slotGameDto.getGameSource())).findFirst();
        if (!first.isPresent()) {
            return null;
        }
        if (Objects.equals(slotGameDto.getGameSource(), ActivityConstant.REQ_GAME_SOURCE)) {
            throw new GameTypeException();
        } else if (Objects.equals(slotGameDto.getGameSource(), ActivityConstant.REQ_GAME_HALL)) {
            GuidePageDto guidePageDto = activityService.getGuidePageDetail(slotGameDto.getGameId());
            return buildClickUrl4GuidePage(guidePageDto, param);
        }
        return null;
    }

    @Value("${tuia.engine.activity.web.host}")
    private String activityWebHost;

    private String buildClickUrl4GuidePage(GuidePageDto guidePageDto, GameReq req) {
        StringBuilder activityUrl = new StringBuilder("http://").append(activityWebHost);
        activityUrl.append("/direct/index?id=");
        activityUrl.append(guidePageDto.getId()).append("&slotId=").
                append(req.getAdslotId()).append("&login=normal&appKey=").
                append(req.getAppKey());
        appendDsm(activityUrl, req.getAdslotId());
        appendDcm(activityUrl, req.getAdslotId());
        appendUid(activityUrl);
        appendGameMark(activityUrl);

        return activityUrl.toString();
    }

    private String buildClickUrl4Game(GameReq req, Long gameId) {
        StringBuilder activityUrl = new StringBuilder("http://").append(activityWebHost);
        activityUrl.append("/game/index?id=");
        activityUrl.append(gameId).append("&slotId=").
                append(req.getAdslotId()).append("&login=normal&appKey=").
                append(req.getAppKey());
        appendDsm(activityUrl, req.getAdslotId());
        appendDcm(activityUrl, req.getAdslotId());
        appendUid(activityUrl);
        // 来源于趣晒
        if (StringUtils.isNotBlank(req.getContextToken())) {
            activityUrl.append("&contextToken=").append(req.getContextToken());
            if (StringUtils.isNotBlank(req.getShare_way())) {
                activityUrl.append("&share_way=").append(req.getShare_way());
            }
            if (null != req.getDeep()) {
                activityUrl.append("&deep=").append(req.getDeep());
            } else {
                activityUrl.append("&deep=").append(0);
            }
            if (null != req.getShareTextId()) {
                activityUrl.append("&shareTextId=").append(req.getShareTextId());
            }
        }
        return activityUrl.toString();
    }

    private void appendUid(StringBuilder activityUrl) {
        String uuid = UUID.randomUUID().toString();
        activityUrl.append("&towId=").append(uuid);
        activityUrl.append("&markId=").append(uuid);
    }

    private void appendGameMark(StringBuilder activityUrl) {
        activityUrl.append("&pg=GAME");
    }

    public void appendDsm(StringBuilder url, Long slotId) {
        url.append("&dsm=1." + slotId + ".0.0");
    }

    public void appendDcm(StringBuilder url, Long slotId) {
        url.append("&dcm=401." + slotId + ".0.0");
    }
}
