package cn.com.duiba.tuia.engine.activity.web.controller;

import cn.com.duiba.tuia.engine.activity.api.ApiCode;
import cn.com.duiba.tuia.engine.activity.api.ApiResult;
import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmErrorReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmTerminalReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.engine.activity.model.rsp.MaterialRsp;
import cn.com.duiba.tuia.engine.activity.service.ActivityEngineService;
import cn.com.duiba.tuia.engine.activity.service.ActivitySpmService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.service.param.DefaultParamValidator;
import cn.com.duiba.tuia.ssp.center.api.dto.DomainConfigDto;
import cn.com.duiba.tuia.utils.AjaxUtils;
import cn.com.duiba.tuia.utils.UrlUtil;
import cn.com.duiba.wolf.perf.timeprofile.RequestTool;
import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * ActivityController SDK V1.5新增字段, http://cf.dui88.com/pages/viewpage.action?pageId=4500879
 */
@Controller
@RequestMapping("/api/v1/activity")
public class ActivityController extends BaseController {

    @Autowired
    private ActivityEngineService activityEngineService;

    @Autowired
    private ActivitySpmService    activitySpmService;

    private static final String PARAM_ERROR_MESSAGE = "参数错误";

    private static final String SYSTEM_ERROR_MESSAGE = "ERROR";

    private static final String SUCCESS_MESSAGE = "SUCCESS";

    private static final String SPM_ACTIVITY_METHOD_ERROR = "activityEngineService.spmActivity error";

	private static final String[] R_SEARAH_DOMAINS = { "yun.duiba.com.cn", "yun.tuia.cn" };

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
    private DefaultParamValidator defaultParamValidator;

    /**
     * getActivity4native
     *
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("get")
    public void getActivity4native(GetActivityReq param, HttpServletRequest request, HttpServletResponse response) {
        try {
            if(!defaultParamValidator.reqParamValidate(param)){
                logger.info("Get getActivityReq param error, slotid=[{}] , appkey=[{}] , device_id=[{}]",
                            param.getAdslot_id(), param.getApp_key(), param.getDevice_id());
                AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
                return;
            }

            String ipv4 = RequestTool.getIpAddr(request);
            param.setIpv4(ipv4);
            String host =request.getHeader(HttpHeaders.HOST);
            param.setHost(host);
            GetActivityRsp activityRsp = this.activityEngineService.getActivity4native(param);
            // 更改查询新域名策略的接口
            DomainConfigDto domainConfigDto = localCacheService.getDomainConfigByAppId(param.getApp_id());
            // 处理click_url
            domainSwitch(activityRsp, domainConfigDto);
            //将活动素材中的yun.duiba.com.cn替换为engine.host
			replaceDuibaUrl2Tuia(activityRsp, request, domainConfigDto);
            AjaxUtils.renderJson(response, activityRsp);
        } catch (Exception e) {
            logger.info("activityEngineService.getActivity error", e);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    /**
     * 将素材中yun.duiba.com.cn替换为yun.tuia.cn
     * @return
     */
    private void replaceDuibaUrl2Tuia(GetActivityRsp activityRsp, HttpServletRequest request, DomainConfigDto domainConfigDto){
		if (null != activityRsp) {
			String domain = request.getServerName();
			String tuiaDomain = StringUtils.replace(domain, "engine", "yun");
            String materialUrl = "";
			if(domainConfigDto != null) {
                materialUrl = domainConfigDto.getMaterialUrl();
            }
			if(StringUtils.isNotBlank(materialUrl)){
			    tuiaDomain = materialUrl;
            }
			String [] replaceDomain = {tuiaDomain,tuiaDomain};
			String imgUrl = StringUtils.replaceEach(activityRsp.getImg_url(), R_SEARAH_DOMAINS, replaceDomain);
			String adIcon = StringUtils.replaceEach(activityRsp.getAd_icon(), R_SEARAH_DOMAINS, replaceDomain);
			String adClose = StringUtils.replaceEach(activityRsp.getAd_close(), R_SEARAH_DOMAINS, replaceDomain);
			// 将活动素材中的yun.duiba.com.cn替换为engine.host

            activityRsp.setImg_url(imgUrl);
            activityRsp.setAd_close(adClose);
            activityRsp.setAd_icon(adIcon);
            List<MaterialRsp> materialRsps = activityRsp.getMaterial_list();
            if (null != materialRsps) {
                for (MaterialRsp rsp : materialRsps) {
                    String mUrl = StringUtils.replaceEach(rsp.getImage_url(), R_SEARAH_DOMAINS, replaceDomain);
                    rsp.setImage_url(mUrl);
                }
            }
		}
    }

    /**
     * spmActivity4native
     *
     * @param param
     * @param black_box
     * @param request
     * @param response
     * @param sdata
     * @param nsdata
     */
    @RequestMapping("spm")
    public void spmActivity4native(SpmActivityReq param, String sdata, String nsdata, String black_box, HttpServletRequest request, HttpServletResponse response) {  //NOSONAR
        try {
            if (!this.isValidSPMParam(param)) {
                logger.info("Spm spmActivityReq param error, param=[{}]", param);
                AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
                return;
            }

            String ipv4 = RequestTool.getIpAddr(request);
            param.setIp(ipv4);
            if (StringUtils.isNotBlank(black_box)) {
                param.setToken_id(black_box);
            }
            param.setRid(param.getData1());
            String clickUrl = StringEscapeUtils.unescapeHtml3(param.getClick_url());
            param.setClick_url(clickUrl);
            if(StringUtils.isBlank(param.getRid()) && StringUtils.isNotBlank(clickUrl)){
                param.setRid(getQuerySimple("tck_rid_6c8",clickUrl));
            }
            param.setData2(StringEscapeUtils.unescapeHtml3(param.getData2()));
            this.activitySpmService.spmActivity4native(param, sdata, nsdata);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SUCCESS, SUCCESS_MESSAGE));
        } catch (Exception e) {
            logger.info(SPM_ACTIVITY_METHOD_ERROR, e);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    private String createTokenIfAbsent(String wdataToken) {
        if (StringUtils.isBlank(wdataToken)) {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }
        return wdataToken;
    }

    /**
     * getActivity4web
     *
     * @param param
     * @param callback
     * @param wdata_token
     * @param request
     * @param response
     */
    @RequestMapping("get4web")
    public void getActivity4web(GetActivityReq param, String callback, String wdata_token, HttpServletRequest request, HttpServletResponse response) {  // NOSONAR
        try {
            if (param.getSlotId() != null) {
                param.setAdslot_id(param.getSlotId());
            }
            if (!StringUtils.isEmpty(param.getSlotSize())) {
                param.setAdslot_size(param.getSlotSize());
            }
            if(!defaultParamValidator.reqParamValidate(param)){
                logger.info("Get getActivityReq param error, slotid=[{}] , appkey=[{}] , device_id=[{}]",
                            param.getAdslot_id(), param.getApp_key(), param.getDevice_id());
                AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
                return;
            }

            String ipv4 = RequestTool.getIpAddr(request);
            param.setIpv4(ipv4);
            String host =request.getHeader(HttpHeaders.HOST);
            param.setHost(host);
            String referer =request.getHeader(HttpHeaders.REFERER);
            GetActivityRsp activityRsp ;
            GetActivityRsp rsp = activityEngineService.domainShield(param, referer);
            if (rsp != null) {
                activityRsp = rsp;
            }else {
                activityRsp= this.activityEngineService.getActivity(param);
            }
            // 查询新域名策略预警
            DomainConfigDto domainConfigDto = localCacheService.getDomainConfigByAppId(param.getApp_id());
            // 处理jssdk中click_url
            domainSwitch(activityRsp, domainConfigDto);

            String wdataToken = createTokenIfAbsent(wdata_token);
            activityRsp.setWdata_token(wdataToken);
            //将活动素材中的yun.duiba.com.cn替换为engine.host
			replaceDuibaUrl2Tuia(activityRsp, request, domainConfigDto);
			//将分拿回对象中敏感字段替换掉
            activityRsp.setClose(activityRsp.getAd_close());
            activityRsp.setClose_visible(activityRsp.isAd_close_visible());
            activityRsp.setContent(activityRsp.getAd_content());
            activityRsp.setIcon(activityRsp.getAd_icon());
            activityRsp.setIcon_visible(activityRsp.isAd_icon_visible());
            activityRsp.setTitle(activityRsp.getAd_title());
            activityRsp.setType(activityRsp.getAd_type());
            activityRsp.setSlotId(activityRsp.getAdslot_id());
            JSONObject jsonObject =(JSONObject)JSONObject.toJSON(activityRsp);
           /*  jsonObject.remove("ad_close");
            jsonObject.remove("ad_close_visible");
            jsonObject.remove("ad_icon");
            jsonObject.remove("ad_icon_visible");
            jsonObject.remove("ad_title");
            jsonObject.remove("ad_type");
            jsonObject.remove("adslot_id");
            jsonObject.remove("ad_content");*/
            AjaxUtils.renderJsonp(response, callback, jsonObject);
        } catch (Exception e) {
            logger.info("activityEngineService.getActivity error", e);
            AjaxUtils.renderJsonp(response, callback, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    private void domainSwitch(GetActivityRsp activityRsp, DomainConfigDto domainConfigDto) {
        if (StringUtils.isNotBlank(activityRsp.getClick_url())) {
            String clickUrl = activityRsp.getClick_url();
            if (clickUrl.contains(activityTuiaWebHost)) {
                    String activityWebHost = "";
                    if(domainConfigDto!=null){
                        activityWebHost = domainConfigDto.getActUrl();
                    }
                    if(StringUtils.isNotBlank(activityWebHost)&&!StringUtils.equals(activityTuiaWebHost, activityWebHost)){
                        clickUrl = StringUtils.replace(clickUrl, activityTuiaWebHost, activityWebHost);
                    }
            }
            if(domainConfigDto!=null && Objects.equals(domainConfigDto.getProtocolHeader(), DomainConfigDto.PROTOCOL_HTTP)){
                clickUrl =  UrlUtil.httpsToHttp(clickUrl);
            }else if(domainConfigDto!=null && Objects.equals(domainConfigDto.getProtocolHeader(), DomainConfigDto.PROTOCOL_HTTPS)) {
                clickUrl =  UrlUtil.http2Https(clickUrl);
            }
            activityRsp.setClick_url(clickUrl);
        }
    }

    /**
     * spmActivity4web
     * jssdk 日志记录
     *
     * @param param
     * @param callback
     * @param wdata_token
     * @param request
     * @param response
     */
    @RequestMapping(value = "spm4web", method = {RequestMethod.GET, RequestMethod.POST})
    public void spmActivity4web(SpmActivityReq param, String callback, String wdata_token, HttpServletRequest request, HttpServletResponse response) {  //NOSONAR
        try {
            if (param.getSlotId() != null) {
                param.setAdslot_id(param.getSlotId());
                param.setAdslotId(param.getSlotId());
            }
            if (!this.isValidSPMParam(param)) {
                AjaxUtils.renderJson(response, new ApiResult(ApiCode.PARAM_ERROR, PARAM_ERROR_MESSAGE));
                return;
            }

            String ipv4 = RequestTool.getIpAddr(request);
            param.setIp(ipv4);
            param.setToken_id(wdata_token);
            param.setUa(getUA(request));
            param.setRid(param.getData1());
            String clickUrl = StringEscapeUtils.unescapeHtml3(param.getClick_url());
            param.setClick_url(clickUrl);
            if(StringUtils.isBlank(param.getRid()) && StringUtils.isNotBlank(clickUrl)){
                param.setRid(getQuerySimple("tck_rid_6c8",clickUrl));
            }
            param.setData2(StringEscapeUtils.unescapeHtml3(param.getData2()));
            this.activitySpmService.spmActivity4web(param);
            AjaxUtils.renderJsonp(response, callback, new ApiResult(ApiCode.SUCCESS, SUCCESS_MESSAGE));
        } catch (Exception e) {
            logger.info(SPM_ACTIVITY_METHOD_ERROR, e);
            AjaxUtils.renderJsonp(response, callback, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    /**
     * report
     *
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("report")
    public void report(SpmTerminalReq param, HttpServletRequest request, HttpServletResponse response) {
        try {
            String ipv4 = RequestTool.getIpAddr(request);
            param.setIp(ipv4);
            this.activitySpmService.spmTerminal4native(param);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SUCCESS, SUCCESS_MESSAGE));
        } catch (Exception e) {
            logger.info(SPM_ACTIVITY_METHOD_ERROR, e);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    /**
     * reportError
     *
     * @param param
     * @param request
     * @param response
     */
    @RequestMapping("error")
    public void reportError(SpmErrorReq param, HttpServletRequest request, HttpServletResponse response) {
        try {
            String ipv4 = RequestTool.getIpAddr(request);
            param.setIp(ipv4);
            this.activitySpmService.spmError4native(param);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SUCCESS, SUCCESS_MESSAGE));
        } catch (Exception e) {
            logger.error("ActivityV2Controller.reportError error", e);
            AjaxUtils.renderJson(response, new ApiResult(ApiCode.SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

  /**
   * 点击/曝光参数校检，合格返回true
   *
   * @param param
   * @return 输入参数是否合法
   */
  private boolean isValidSPMParam(SpmActivityReq param) {
    if (param.getApp_key() == null
        || param.getAdslot_id() == null
        || param.getDevice_id() == null
        || param.getActivity_id() == null
        || param.getType() == null) {
      return false;
    }
    return true;
  }
}
