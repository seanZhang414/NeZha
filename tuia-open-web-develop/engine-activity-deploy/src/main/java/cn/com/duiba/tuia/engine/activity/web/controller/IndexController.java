package cn.com.duiba.tuia.engine.activity.web.controller;


import cn.com.duiba.tuia.engine.activity.api.ApiCode;
import cn.com.duiba.tuia.engine.activity.api.ApiResult;
import cn.com.duiba.tuia.engine.activity.log.InnerLog;
import cn.com.duiba.tuia.engine.activity.log.StatExtLog;
import cn.com.duiba.tuia.engine.activity.model.req.CustomParam;
import cn.com.duiba.tuia.engine.activity.model.req.ManualParamReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityManualReq;
import cn.com.duiba.tuia.engine.activity.model.req.UserInfoLogReq;
import cn.com.duiba.tuia.engine.activity.remoteservice.SystemConfigService;
import cn.com.duiba.tuia.engine.activity.service.*;
import cn.com.duiba.tuia.engine.activity.service.param.DefaultParamValidator;
import cn.com.duiba.tuia.engine.activity.utils.GamePathUtil;
import cn.com.duiba.tuia.ssp.center.api.constant.ActivityConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.tuia.utils.AjaxUtils;
import cn.com.duiba.tuia.utils.GzipUtils;
import cn.com.duiba.tuia.utils.UniqRequestIdGen;
import cn.com.duiba.tuia.utils.UrlUtil;
import cn.com.duiba.wolf.utils.UrlUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static cn.com.duiba.tuia.constant.ActivityURLConstants.*;

/**
 * IndexController
 */
@Controller
@RequestMapping("/index")
public class IndexController extends BaseController {

    private static final String FINGER_PRINT = "fingerprint";

    private static final String PARAM_ERROR_MESSAGE = "参数错误";

    private static final String SYSTEM_CONFIG = "tuia_buoy_config_iframe_20180612";

    @Value("${tuia.engine.activity.web.host}")
    private String activityTuiaWebHost;

    @Value("${tuia.engine.activity.duibaweb.host}")
    private String activityDuibaWebHost;

    @Value("${tuia.engine.activity.duibahcweb.host}")
    private String activityDuibaHCWebHost;

    @Value("${tuia.engine.activity.host}")
    private String engineActivityHost;

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private ActivityEngineService activityEngineService;

    @Autowired
    private ActivitySpmService activitySpmService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private DefaultParamValidator defaultParamValidator;

    @Autowired
    private GameService gameService;

    private String getDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = getCookie(FINGER_PRINT, request);
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            setCookie(FINGER_PRINT, deviceId, response);
        }
        return deviceId;
    }

    private String getTckLoc(String url) {
        // 兑吧主会场 , url形式：http://home.m.dui88.com/mainMeeting/index?id=36
        if (url.contains(activityDuibaHCWebHost)) {
            String id = getQuerySimple("id", url);
            return TCK_LOC_DUIBA_MAIN + id;
        }

        // 兑吧新活动, url形式：
        // http://activity.m.duiba.com.cn/newtools/index?id=1904968&openBs=openbs&from=login&appKey=41N5c5CMPqAJjmAj5QwUq78Zk9MD&adslotId=435
        if (url.contains(activityDuibaWebHost)) {
            String id = getQuerySimple("id", url);
            return TCK_LOC_DUIBA_NEW + id;
        }

        // 推啊活动, url形式：http://activity.tuia.cn/activity/index?id=1&slotId1=13&slotId2=12
        if (url.contains(activityTuiaWebHost)) {
            String id = getQuerySimple("id", url);

            if (url.contains("mainMeet")) {
                return TCK_LOC_GUIDEPAGE_MAIN + id;
            } else if (url.contains("actCenter")) {
                return TCK_LOC_GUIDEPAGE_CENTER + id;
            }
            return TCK_LOC_TUIA + id;
        }
        return null;
    }

    private int checkUrl(String url, Integer actSource) {
        //spm日志 如果是游戏或者游戏大厅则直接返回游戏或游戏大厅的source
        if (actSource != null && (actSource == ReqIdAndType.REQ_GAME_HALL || actSource == ReqIdAndType.REQ_GAME_SOURCE)) {
            return actSource;
        }
        if (url == null) return -1;
        if (url.contains(activityDuibaHCWebHost) || url.contains(activityDuibaWebHost)) {
            return 0;
        }
        if (url.contains(activityTuiaWebHost)) {

            if ((url.contains("mainMeet") || url.contains("actCenter"))) {
                return 2;
            } else {
                return 1;
            }
        }

        return -1;
    }

    private boolean validateCustomParam(CustomParam customParam, String signature) {
        return StringUtils.isNotBlank(signature) && customParam != null && customParam.getNonce() != null
                && customParam.getTimestamp() != null && StringUtils.isNotBlank(customParam.getMd());
    }

    private boolean validateSignature(CustomParam customParam, String signature) {
        // 校验签名
        try {
            // 将用户参数转化为map
            Map<String, Object> cpMap = JSON.parseObject(JSON.toJSONString(customParam));
            // 对map的key进行排序
            TreeSet<String> sortedKeySet = Sets.newTreeSet(cpMap.keySet());
            // 拼接要签名的参数字符串
            StringBuilder signSB = new StringBuilder();
            sortedKeySet.forEach(key -> signSB.append(key).append("=").append(cpMap.get(key)).append("&"));
            String signStr = signSB.delete(signSB.length() - 1, signSB.length()).toString();
            // 对参数字符串进行SHA-1
            String sha1SignStr = DigestUtils.sha1Hex(signStr);
            // 校验生成的签名与用户传的签名是否一致
            return StringUtils.equals(sha1SignStr, signature);
        } catch (Exception e) {
            logger.info("validateSignature error! customParam is {} , signature is {}",
                    JSONObject.toJSONString(customParam), signature);
            return false;
        }
    }

    /**
     * 解析MD
     *
     * @param customParam
     * @return
     */
    private String unzipMD(CustomParam customParam) {
        try {
            return new String(GzipUtils.unzip(Base64.getDecoder().decode(customParam.getMd())));
        } catch (IOException e) {
            logger.info("GzipUtils.unzip error! gzipStr is {} ", customParam.getMd());
        }
        return "";
    }

    private void lodMdInfo(String mdStr, Long appId, String deviceId, Long slotId) {
        UserInfoLogReq infoLogReq = new UserInfoLogReq();
        infoLogReq.setAppId(appId);
        infoLogReq.setMd(mdStr);
        infoLogReq.setDeviceId(deviceId);
        infoLogReq.setLogType(UserInfoLogReq.LOG_TYPE_SYNC);
        infoLogReq.setSlotId(slotId);
        InnerLog.log("42", infoLogReq);

        /**
         * 扩展打印另一份用户信息，给数据使用
         */
        StatExtLog.userInfoExtLog(infoLogReq);
    }

    /**
     * activity
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/activity")
    public ModelAndView activity(@Valid @ModelAttribute ManualParamReq manualParamReq, HttpServletRequest request,
                                 HttpServletResponse response) {
        if (!defaultParamValidator.reqParamValidate(manualParamReq)) {
            return null;
        }
        String appKey = manualParamReq.getAppKey();
        Long adslotId = manualParamReq.getAdslotId();
        Long activityId = manualParamReq.getActivityId();
        String signature = StringEscapeUtils.unescapeHtml3(manualParamReq.getSignature());
        CustomParam customParam = new CustomParam();
        customParam.setTimestamp(manualParamReq.getTimestamp());
        customParam.setMd(StringEscapeUtils.unescapeHtml3(manualParamReq.getMd()));
        customParam.setNonce(manualParamReq.getNonce());

        MediaAppDataDto app = localCacheService.getMediaApp(appKey);

        // 设备信息先取URL中的device_id
        String deviceId = manualParamReq.getDevice_id();
        String mdStr = "";
        // 校验用户参数和签名
        if (validateCustomParam(customParam, signature)) {
            customParam.setAppSecret(app.getAppSecret());
            if (!validateSignature(customParam, signature)) {
                logger.info("validate error app id is [{}], slot id is [{}], deviceId=[{}]", app.getAppId(), adslotId, deviceId);
                return null;
            }
            // 解析MD信息
            mdStr = unzipMD(customParam);
        }
        deviceId = getSlotDeviceId(manualParamReq, request, response, deviceId, mdStr);

        JSONObject mdJson = JSONObject.parseObject(mdStr);
        String nt = null;
        if (mdJson != null) {
//            sendDeviceInfoApiInvoker.send(mdJson.getString("apps"), mdJson.getString("os"), mdJson.getString("imei"), mdJson.getString("idfa"), deviceId);
//            baichuanClient.execute(mdJson.getString("os"), mdJson.getString("imei"),mdJson.getString("idfa"),deviceId);
            nt = mdJson.getString("nt");
        }

        // 记录设备信息日志appID、deviceId、md
        if (!StringUtils.isEmpty(mdStr)) {
            lodMdInfo(mdStr, app.getAppId(), deviceId, adslotId);
        }

        String rid = UniqRequestIdGen.resolveReqId();
        ActivityManualPlanDto activityManualPlan = this.activityEngineService.getActivityManualPlan(appKey, adslotId,
                activityId,
                deviceId, rid);
        if (checkClickUrl(appKey, adslotId, activityManualPlan)) {
            return null;
        }

        String referer = request.getHeader(HttpHeaders.REFERER);
        String host = request.getHeader(HttpHeaders.HOST);
        // 记录访问日志
        SpmActivityManualReq spm = new SpmActivityManualReq();
        spm.setApp_key(appKey);
        spm.setDevice_id(deviceId);
        spm.setIp(getIP(request));
        spm.setOs_type(getOS(request));
        spm.setSlot_id(adslotId);
        spm.setUa(getUA(request));
        spm.setActivity_id(activityManualPlan.getActivityId());
        spm.setSource(checkUrl(activityManualPlan.getActivityUrl(), activityManualPlan.getActSource()));
        spm.setRid(rid);
        spm.setReferer(referer);
        spm.setHost(host);
        spm.setIdfa(manualParamReq.getIdfa());
        spm.setImei(manualParamReq.getImei());
        spm.setOutPutSource(activityManualPlan.getLaunchType());
        spm.setSubActivityWay(activityManualPlan.getSubActivityWay());
        spm.setClickUrl(activityManualPlan.getActivityUrl());
        spm.setModel(getModel(request));
        spm.setConnect_type(nt);
        this.activitySpmService.spmActivity4manual(spm);

        //如果是游戏
        if (Objects.equals(ActivityConstant.REQ_GAME_SOURCE, activityManualPlan.getActSource())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("web-middle/index2");
            modelAndView.addObject("gameId", activityManualPlan.getActivityId());
            modelAndView.addObject("source", activityManualPlan.getActSource());
            modelAndView.addObject("sessionId", request.getSession().getId());
            return modelAndView;
        }

        //如果是游戏大厅，同盾二期并行方案，目前只做游戏大厅
        if (Objects.equals(ActivityConstant.REQ_GAME_HALL, activityManualPlan.getActSource())) {
            String redirctUrl = GamePathUtil.buildClickUrl4GuidePage(adslotId, activityManualPlan.getActivityId(), appKey,
                    activityTuiaWebHost);
            String afterSwitchUrl = this.replaceHost(app.getAppId(), redirctUrl);

            redirectUrl(response, sendRedirect(afterSwitchUrl, getIsHttp(app.getAppId())));
            return null;
        }
        return redircct(manualParamReq, request, response, app, deviceId, rid, activityManualPlan);


    }

    @Nullable
    private ModelAndView redircct(@Valid @ModelAttribute ManualParamReq manualParamReq, HttpServletRequest request, HttpServletResponse response, MediaAppDataDto app, String deviceId, String rid, ActivityManualPlanDto activityManualPlan) {
        //添加参数
        Map<String, String> map = getAddParam(manualParamReq);
        //非定时投放
        if (activityManualPlan.getLaunchType() != null && activityManualPlan.getLaunchType() != SlotDto.TIMING_PUTWAY) {
            String redirectUrl = handlePollingPutWay(app.getAppId(), activityManualPlan, map);
            redirectUrl(response, judgeApp(app, redirectUrl));
            return null;
        }
        String targetUrl = activityManualPlan.getActivityUrl();
        // 简单链接类型
        if (StringUtils.isNotBlank(targetUrl)) {
            String redirectUrl = handleSimpleActivityLink(app.getAppId(), request, rid, activityManualPlan, targetUrl,
                    map, deviceId);
            redirectUrl(response, judgeApp(app, redirectUrl));
        }
        return null;
    }


    private String judgeApp(MediaAppDataDto app, String url) {
        SystemConfigDto dubboResult = systemConfigService.getSystemConfig(SYSTEM_CONFIG);
        String result = "";
        if (dubboResult != null) {
            result = dubboResult.getTuiaValue();
        }
        if (StringUtils.isNotBlank(result)) {
            List appIdArray = Arrays.asList(result.split(","));
            List<Long> appIds = new ArrayList<>(appIdArray.size());
            appIdArray.forEach(e -> {
                appIds.add(Long.valueOf(e.toString()));
            });
            if (appIds.contains(app.getAppId())) {
                URI uri = URI.create(url);
                try {
                    return uri.getScheme() + "://" + uri.getHost() + "/activity/iframeMiddleIndex?url=" + URLEncoder.encode(url, "utf-8");
                } catch (Exception e) {
                    return url;
                }
            }
        }
        return url;
    }

    private boolean checkClickUrl(String appKey, Long adslotId, ActivityManualPlanDto activityManualPlan) {
        //如果是游戏或者游戏大厅则不需要url
        if (activityManualPlan == null || (StringUtils.isBlank(activityManualPlan.getActivityUrl()) && activityManualPlan.getActSource() != 20 && activityManualPlan.getActSource() != 21)) {
            logger.info("Adslot manual activity url is null, appKey=[{}], adslotId = [{}]", appKey, adslotId);
            return true;
        }
        return false;
    }


    private void redirectUrl(HttpServletResponse response, String redirectUrl) {
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            logger.warn("send redirect io exception");
        }
    }

    private Map<String, String> getAddParam(@Valid @ModelAttribute ManualParamReq manualParamReq) {
        Map<String, String> map = new HashMap<>();
        map.put("tenter", "SOW");
        if (StringUtils.isNotBlank(manualParamReq.getUserId())) {
            map.put("tck_userId_674", manualParamReq.getUserId());
        }
        return map;
    }

    /**
     * 得到媒体从广告位传入的deviceId 或者 userId
     *
     * @param manualParamReq
     * @param request
     * @param response
     * @param deviceId
     * @param mdStr
     * @return
     */
    private String getSlotDeviceId(ManualParamReq manualParamReq, HttpServletRequest request, HttpServletResponse response, String deviceId, String mdStr) {
        // 取出mdStr的deviceID信息
        if (!StringUtils.isEmpty(mdStr) && StringUtils.isEmpty(deviceId)) {
            JSONObject mdJson = JSONObject.parseObject(mdStr);
            if (mdJson != null) {
                Object mdDeviceId = mdJson.get("device_id");
                if (mdDeviceId != null && !StringUtils.isEmpty(mdDeviceId.toString())) {
                    deviceId = mdDeviceId.toString();
                }
            }
        }
        if (!StringUtils.isEmpty(manualParamReq.getUserId()) && StringUtils.isEmpty(deviceId)) {
            deviceId = manualParamReq.getUserId();
        }
        if (StringUtils.isEmpty(deviceId)) {
            deviceId = getDeviceId(request, response);
        }
        return deviceId;
    }

    private String handleSimpleActivityLink(Long appId, HttpServletRequest request,
                                            String rid, ActivityManualPlanDto activityManualPlan, String targetUrl,
                                            Map<String, String> paramMap, String deviceId) {
        // 推啊活动WEB系统做特殊处理添加设备ID
        String newTargetUrl = null;
        if (targetUrl.contains(activityTuiaWebHost)) {
            String curHost = request.getHeader("host");
            if (!StringUtils.equals(curHost, engineActivityHost)) {
                targetUrl = replaceHost(appId, targetUrl);
            }
            newTargetUrl = targetUrl + "&deviceId=" + deviceId + "&" + RID_KEY + "=" + rid + "&"
                    + TCK_LOC_KEY + "=" + getTckLoc(activityManualPlan.getActivityUrl());
        } else {
            newTargetUrl = targetUrl;
        }
        newTargetUrl = UrlUtils.appendParams(newTargetUrl, paramMap) + "&";
        boolean isHttp = getIsHttp(appId);
        return sendRedirect(newTargetUrl, isHttp);
    }

    private boolean getIsHttp(Long appId) {
        boolean isHttp = false;
        // 新域名策略
        DomainConfigDto domainConfigDto = localCacheService.getDomainConfigByAppId(appId);
        if (domainConfigDto != null) {
            isHttp = domainConfigDto.getProtocolHeader() != null && domainConfigDto.getProtocolHeader() == DomainConfigDto.PROTOCOL_HTTP;
        }
        return isHttp;
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

    private String handlePollingPutWay(Long appId,
                                       ActivityManualPlanDto activityManualPlan, Map<String, String> parmMap) {
        String targetUrl = activityManualPlan.getActivityUrl();
        if (targetUrl != null && targetUrl.contains(activityTuiaWebHost)) {
            targetUrl = replaceHost(appId, targetUrl);
        }
        targetUrl = UrlUtils.appendParams(targetUrl, parmMap);
        return sendRedirect(targetUrl, getIsHttp(appId));
    }

    @RequestMapping("/log")
    public void logInfo(@Valid @ModelAttribute ManualParamReq manualParamReq, HttpServletRequest request,
                        HttpServletResponse response) {

        String appKey = manualParamReq.getAppKey();
        String signature = StringEscapeUtils.unescapeHtml3(manualParamReq.getSignature());
        CustomParam customParam = new CustomParam();
        customParam.setTimestamp(manualParamReq.getTimestamp());
        customParam.setMd(StringEscapeUtils.unescapeHtml3(manualParamReq.getMd()));
        customParam.setNonce(manualParamReq.getNonce());
        customParam.setUi(manualParamReq.getUi());
        customParam.setEc(manualParamReq.getEc());
        Long slotId = manualParamReq.getAdslotId();

        MediaAppDataDto app = localCacheService.getMediaApp(appKey);
        if (app == null || !app.isValid()) {
            logger.info("app is invalid, appKey=[{}]", appKey);
            return;
        }
        if (!validateCustomParam(customParam, signature)) {
            return;
        }
        customParam.setAppSecret(app.getAppSecret());
        if (!validateSignature(customParam, signature)) {
            return;
        }
        try {
            UserInfoLogReq infoLogReq = new UserInfoLogReq();
            String mdStr = new String(GzipUtils.unzip(Base64.getDecoder().decode(customParam.getMd())));
            if (StringUtils.isNotBlank(customParam.getUi())) {
                String uiStr = new String(GzipUtils.unzip(Base64.getDecoder().decode(customParam.getUi())));
                infoLogReq.setUi(uiStr);
            }
            if (StringUtils.isNotBlank(customParam.getEc())) {
                String ecStr = new String(GzipUtils.unzip(Base64.getDecoder().decode(customParam.getEc())));
                infoLogReq.setEc(ecStr);
            }
            infoLogReq.setAppId(app.getAppId());
            infoLogReq.setMd(mdStr);
            infoLogReq.setLogType(UserInfoLogReq.LOG_TYPE_AYNC);
            infoLogReq.setSlotId(slotId);
            InnerLog.log("42", infoLogReq);

            /**
             * 扩展打印另一份用户信息，给数据使用
             */
            StatExtLog.userInfoExtLog(infoLogReq);
        } catch (IOException e) {
            logger.info("logInfo error! customParam is {} ", JSONObject.toJSONString(customParam));
        }
    }

    @RequestMapping("/image")
    public void redirect2Image(String appKey, Long adslotId, String deviceId,
                               String md,
                               Long timestamp,
                               Long nonce,
                               String signature,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (!(localCacheService.validateAppKey(appKey)) || !(localCacheService.validateSlotId(adslotId))) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", appKey, adslotId);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
            return;
        }
        String deviceIdCopy = deviceId;
        if (StringUtils.isBlank(deviceIdCopy)) {
            deviceIdCopy = getDeviceId(request, response);
        }
        signature = StringEscapeUtils.unescapeHtml3(signature);
        CustomParam customParam = new CustomParam();
        customParam.setTimestamp(timestamp);
        customParam.setMd(StringEscapeUtils.unescapeHtml3(md));
        customParam.setNonce(nonce);

        String mdStr = null;
        MediaAppDataDto app = localCacheService.getMediaApp(appKey);
        // 校验用户参数和签名
        if (validateCustomParam(customParam, signature)) {
            customParam.setAppSecret(app.getAppSecret());
            if (!validateSignature(customParam, signature)) {
                logger.info("validate error app id is [{}], slot id is [{}], deviceId=[{}]", app.getAppId(), adslotId, deviceIdCopy);
                return;
            }
            // 解析MD信息
            mdStr = unzipMD(customParam);
        }
        if (!StringUtils.isEmpty(mdStr)) {
            JSONObject mdJson = JSONObject.parseObject(mdStr);
            if (StringUtils.isBlank(deviceId) && mdJson != null && !StringUtils.isEmpty(mdJson.getString("device_id"))) {
                deviceIdCopy = mdJson.getString("device_id");
            }
            // 记录设备信息日志appID、deviceId、md
            lodMdInfo(mdStr, app.getAppId(), deviceIdCopy, adslotId);
        }
        setCookie(FINGER_PRINT, deviceIdCopy, response);
        String materialUrl = materialService.getMaterialUrl(request.getScheme(), adslotId, deviceIdCopy);
        // 记录曝光日志,通过访问日志

        if (StringUtils.isNotBlank(materialUrl)) {
            // 重定向素材
            try {
                response.sendRedirect(materialUrl);
            } catch (IOException e) {
                logger.error("BaseController sendRedirect error", e);
            }
            // sendRedirect(materialUrl, response);
        }
    }

    @RequestMapping("/image/get")
    public void getImage(String appKey, Long adslotId, String deviceId,
                         String md,
                         Long timestamp,
                         Long nonce,
                         String signature,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        if (!(localCacheService.validateAppKey(appKey)) || !(localCacheService.validateSlotId(adslotId))) {
            logger.info("app is invalid, appKey=[{}], adslotId = [{}]", appKey, adslotId);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
            return;
        }
        String deviceIdCopy = deviceId;
        if (StringUtils.isBlank(deviceIdCopy)) {
            deviceIdCopy = getDeviceId(request, response);
        }
        signature = StringEscapeUtils.unescapeHtml3(signature);
        CustomParam customParam = new CustomParam();
        customParam.setTimestamp(timestamp);
        customParam.setMd(StringEscapeUtils.unescapeHtml3(md));
        customParam.setNonce(nonce);

        String mdStr = null;
        MediaAppDataDto app = localCacheService.getMediaApp(appKey);
        // 校验用户参数和签名
        if (validateCustomParam(customParam, signature)) {
            customParam.setAppSecret(app.getAppSecret());
            if (!validateSignature(customParam, signature)) {
                logger.info("validate error app id is [{}], slot id is [{}], deviceId=[{}]", app.getAppId(), adslotId, deviceIdCopy);
                return;
            }
            // 解析MD信息
            mdStr = unzipMD(customParam);
        }
        if (!StringUtils.isEmpty(mdStr)) {
            JSONObject mdJson = JSONObject.parseObject(mdStr);
            if (StringUtils.isBlank(deviceId) && mdJson != null && !StringUtils.isEmpty(mdJson.getString("device_id"))) {
                deviceIdCopy = mdJson.getString("device_id");
            }
            // 记录设备信息日志appID、deviceId、md
            lodMdInfo(mdStr, app.getAppId(), deviceIdCopy, adslotId);
        }
        setCookie(FINGER_PRINT, deviceIdCopy, response);
        String materialUrl = materialService.getMaterialUrl(request.getScheme(), adslotId, deviceIdCopy);
        // 记录曝光日志,通过访问日志

        if (StringUtils.isNotBlank(materialUrl)) {
            // 返回素材流
            String httpsUrl = UrlUtil.http2Https(materialUrl);
            try {
                URL url = new URL(httpsUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);

                ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                try (InputStream inStream = connection.getInputStream()) {
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = inStream.read(buffer)) != -1) {
                        byteOutStream.write(buffer, 0, len);
                    }
                }

                byte[] outData = byteOutStream.toByteArray();

                response.setContentType(connection.getContentType());
                try (OutputStream outStream = response.getOutputStream()) {
                    outStream.write(outData);
                    outStream.flush();
                }
            } catch (Exception e) {
                logger.info("getImage error , appKey=[{}] , slotId = [{}]", appKey, adslotId, e);
            }
        }
    }
}
