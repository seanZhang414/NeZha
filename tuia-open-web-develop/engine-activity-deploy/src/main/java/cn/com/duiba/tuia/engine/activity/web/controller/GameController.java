package cn.com.duiba.tuia.engine.activity.web.controller;

import cn.com.duiba.tuia.engine.activity.exception.GameTypeException;
import cn.com.duiba.tuia.engine.activity.log.InnerLog2;
import cn.com.duiba.tuia.engine.activity.log.UserLoginTimeInnerLog;
import cn.com.duiba.tuia.engine.activity.model.req.GameReq;
import cn.com.duiba.tuia.engine.activity.service.GameService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.service.param.DefaultParamValidator;
import cn.com.duiba.tuia.ssp.center.api.dto.DomainConfigDto;
import cn.com.duiba.tuia.ssp.center.api.dto.MediaAppDataDto;
import cn.com.duiba.wolf.utils.UrlUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/gm")
public class GameController extends BaseController {
    private static final String   FINGER_PRINT        = "fingerprint";

    @Autowired
    private GameService gameService;

    @Autowired
    private DefaultParamValidator defaultParamValidator;

    @Autowired
    private LocalCacheService localCacheService;

    @RequestMapping("/index")
    @ResponseBody
    public ModelAndView index(GameReq param, HttpServletRequest request, HttpServletResponse response) {
        if (!defaultParamValidator.reqParamValidate(param.getAppKey(), param.getAdslotId())) {
            return null;
        }
        param.setDeviceId(getDeviceId(request, response));
        MediaAppDataDto app = localCacheService.getMediaApp(param.getAppKey());
        //同盾二期并行方案目前只做游戏大厅，游戏逻辑暂时不变
        try {
            String url = gameService.getUrl2(param, app.getAppId());
            logInner2(param, app, url);
            defSendRedirect(url, response);
        } catch (GameTypeException e) {
            ModelAndView modelAndView = new ModelAndView("web-middle/index");
            modelAndView.addObject("sessionId", request.getSession().getId());
            return modelAndView;
        }
        return null;
    }

    @RequestMapping("/get")
    public void get(GameReq param, HttpServletRequest request, HttpServletResponse response) {
        if (!defaultParamValidator.reqParamValidate(param.getAppKey(), param.getAdslotId())) {
            return;
        }
        param.setDeviceId(getDeviceId(request, response));
        MediaAppDataDto app = localCacheService.getMediaApp(param.getAppKey());
        String url = gameService.getUrl(param, app.getAppId());

        String lastUrl = getLastUrl(app.getAppId(), url);
        //用户停留时长需求，计算点击到游戏大厅或者游戏的时间
        logInner(param, app, lastUrl);

        try {
            response.sendRedirect(lastUrl);
        } catch (IOException e) {
            logger.warn("");
        }
    }

    /**
     * 游戏用户停留时长需求
     * @param param
     * @param app
     * @param lastUrl
     */
    private void logInner(GameReq param, MediaAppDataDto app, String lastUrl) {
        try {
            //这里wolf包里面会把大写转为小写，所以redirectUrl->redirecturl，towId->towId不是错误注意
            String redirectUrl = URLDecoder.decode(UrlUtils.uRLRequest(lastUrl).getOrDefault("redirecturl", ""),
                    "utf-8");
            logInner2(param, app, redirectUrl);
        } catch (UnsupportedEncodingException e) {
            logger.warn("我不相信会出");
        }
    }

    private void logInner2(GameReq param, MediaAppDataDto app, String redirectUrl) {
        if (StringUtils.isNotBlank(redirectUrl)) {
            Map<String, String> stringStringMap = UrlUtils.uRLRequest(redirectUrl);
            UserLoginTimeInnerLog innerLog = new UserLoginTimeInnerLog();
            innerLog.setDcm(stringStringMap.getOrDefault("dcm", ""));
            innerLog.setDsm(stringStringMap.getOrDefault("dsm", ""));
            innerLog.setSlotId(param.getAdslotId());
            innerLog.setAppId(app.getAppId());
            innerLog.setReqLocation(1);
            innerLog.setUid(stringStringMap.getOrDefault("towid", ""));
            InnerLog2.log("70",JSONObject.toJSONString(innerLog));
        }
    }

    /**
     * 从活动手投引擎连接到游戏或者游戏大厅
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("/get2")
    public void get2(GameReq param, HttpServletRequest request, HttpServletResponse response) {
        if (!defaultParamValidator.reqParamValidate(param.getAppKey(), param.getAdslotId())) {
            return;
        }
        if(param.getGameId()==null||param.getSource()==null){
            return;
        }
        param.setDeviceId(getDeviceId(request, response));
        MediaAppDataDto app = localCacheService.getMediaApp(param.getAppKey());
        String url = gameService.getUrlFromActUrl(param, app.getAppId());

        String lastUrl = getLastUrl(app.getAppId(), url);
        //用户停留时长需求，计算点击到游戏大厅或者游戏的时间
        logInner(param, app, lastUrl);
        try {
            response.sendRedirect(lastUrl);
        } catch (IOException e) {
            logger.warn("");
        }
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

    private String getLastUrl(Long appId, String targetUrl) {
        boolean isHttp = getIsHttp(appId);
        return sendRedirect(targetUrl, isHttp);
    }

    private String getDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = getCookie(FINGER_PRINT, request);
        if (StringUtils.isBlank(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            setCookie(FINGER_PRINT, deviceId, response);
        }
        return deviceId;
    }

}
