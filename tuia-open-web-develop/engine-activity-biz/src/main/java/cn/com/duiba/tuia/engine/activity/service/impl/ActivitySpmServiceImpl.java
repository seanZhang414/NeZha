package cn.com.duiba.tuia.engine.activity.service.impl;

import cn.com.duiba.tuia.engine.activity.api.Frequency4RedisSupport;
import cn.com.duiba.tuia.engine.activity.api.SdkCipherUtils;
import cn.com.duiba.tuia.engine.activity.log.*;
import cn.com.duiba.tuia.engine.activity.model.req.*;
import cn.com.duiba.tuia.engine.activity.service.ActivitySpmService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.tongdun.FraudApiInvoker;
import cn.com.duiba.tuia.engine.activity.tongdun.FraudApiResponse;
import cn.com.duiba.tuia.ssp.center.api.dto.IpAreaLibraryDto;
import cn.com.duiba.tuia.ssp.center.api.dto.MediaAppDataDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Base64;
import java.util.Map;
import java.util.TreeSet;

/**
 * ActivitySpmServiceImpl
 */
@Repository("activitySpmService")
public class ActivitySpmServiceImpl implements ActivitySpmService {

    private static final Logger    logger = LoggerFactory.getLogger(ActivitySpmServiceImpl.class);

    @Autowired
    private Frequency4RedisSupport      frequency4RedisSupport;

    @Autowired
    private LocalCacheService           localCacheService;

    @SuppressWarnings("unused")
	private void appendTongdunInfo(SpmActivityReq req, String osType) {
        FraudApiResponse fraudResponse = null;
        if ("web".equalsIgnoreCase(osType)) {
            fraudResponse = FraudApiInvoker.invoke4web(req);
        } else if ("ios".equalsIgnoreCase(osType)) {
            fraudResponse = FraudApiInvoker.invoke4ios(req);
        } else if ("android".equalsIgnoreCase(osType)) {
            fraudResponse = FraudApiInvoker.invoke4android(req);
        }

        if (fraudResponse != null) {
            req.setSeq_id(fraudResponse.getSeq_id());
            req.setFinal_decision(fraudResponse.getFinal_decision());
            req.setDevice_info(fraudResponse.getDevice_id());
        }
    }

    private void spmActivity(SpmActivityReq req) {
        MediaAppDataDto app = localCacheService.getMediaApp(req.getApp_key());
        if (app == null || app.getAppId() <= 0) {
            logger.info("App is invalid for spm api, appKey=[{}].", req.getApp_key());
            return;
        }
        req.setApp_id(app.getAppId());
        req.setServerTime((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        try {
            // 增加用户信息解密后打印日志
            if (checkJsSDKConsumerInfo(req, app.getAppSecret(), app.getAppId())) {
                String html3Md = StringEscapeUtils.unescapeHtml3(req.getMd());
                String mdStr = new String(Base64.getDecoder().decode(html3Md));
                req.setMd(mdStr);

                JSONObject mdJson = JSONObject.parseObject(mdStr);
                if (mdJson != null) {
//                    sendDeviceInfoApiInvoker.send(mdJson.getString("apps"), mdJson.getString("os"), mdJson.getString("imei"), mdJson.getString("idfa"), req.getDevice_id());
//                    baichuanClient.execute(mdJson.getString("os"), mdJson.getString("imei"),mdJson.getString("idfa"),req.getDevice_id());
                }
            } else {
                req.setMd("");
            }
        } catch (Exception e) {
            logger.info("jssdk decrypt error ", req.getMd());
        }

        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(req.getAdslot_id());
        if (slotCacheDto == null || (slotCacheDto.getAppId() != null && !slotCacheDto.getAppId().equals(app.getAppId()))) {
            // 做兼容处理，广告位存在appId信息时，必须与app一致
            logger.info("Slot not belong to app, slotId=[{}], appId=[{}].", req.getAdslot_id(), req.getApp_id());
            return;
        }

        if (req.getType() == 0) {
            frequency4RedisSupport.increaseFrequency(req.getAdslot_id(), req.getDevice_id(), slotCacheDto.getSlotExposeLimit(), slotCacheDto.getActivityExposeLimit());
            StatActExposeLog.log();
            InnerLog.log("2", req); //2 (广告位曝光日志)
        } else if (req.getType() == 1) {
            frequency4RedisSupport.resetFrequency(req.getAdslot_id(), req.getDevice_id(), slotCacheDto.getSlotExposeLimit(), slotCacheDto.getActivityExposeLimit());
            StatActClickLog.log();
            InnerLog.log("3", req); //3 (广告位点击日志)
        }
    }


    /**
     *  校验jssdk是否传入用户信息，以及传入用户信息是否正确
     * @param req
     * @param appSecret
     * @param appId
     * @return
     */
    private Boolean checkJsSDKConsumerInfo(SpmActivityReq req,String  appSecret,Long appId){
        // 校验用户参数和签名
        if (validateCustomParam(req)) {
            CustomParam customParam = new CustomParam();
            customParam.setTimestamp(req.getTimestamp());
            customParam.setMd(StringEscapeUtils.unescapeHtml3(req.getMd()));
            customParam.setNonce(req.getNonce());
            customParam.setAppSecret(appSecret);
            if (!validateSignature(customParam, req.getSignature())) {
                logger.info("validate error app id is [{}], slot id is [{}], deviceId=[{}]",appId,req.getAdslot_id(),req.getDevice_id());
                return Boolean.FALSE;
            }
            return  Boolean.TRUE;
        }
        return  Boolean.FALSE;
    }

    private boolean validateCustomParam(SpmActivityReq req) {
        return StringUtils.isNotBlank(req.getSignature())  &&  req.getNonce() != null
                && req.getTimestamp() != null && StringUtils.isNotBlank(req.getMd());
    }

    private boolean validateSignature(CustomParam customParam, String signature) {
        // 校验签名
        try {
            // 将用户参数转化为map
            @SuppressWarnings("unchecked")
            Map<String, Object> cpMap = (Map<String, Object>) JSON.parse(JSON.toJSONString(customParam));
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

    @Override
    public void spmActivity4native(SpmActivityReq req) {
        spmActivity(req);
    }

    @Override
    public void spmActivity4web(SpmActivityReq req) {
        if (StringUtils.isNotBlank(req.getUa())) {
            UAData data = UAUtils.parseUA(req.getUa());
            req.setModel(data.getModel());
        }
        // req.setConnect_type();
        spmActivity(req);
        spmGeoAddress(req.getDevice_id(), null, null, req.getIp());
        spmDeviceInfo(req.getDevice_id(), req.getUa(), null, null, null);
    }

    @Override
    public void spmActivity4manual(SpmActivityManualReq req) {
        MediaAppDataDto app = localCacheService.getMediaApp(req.getApp_key());
        if (app == null || app.getAppId() <= 0) {
            logger.info("App is invalid for spm manual, appKey=[{}].", req.getApp_key());
            return;
        }

        Long appId = app.getAppId();
        req.setApp_id(appId);
        req.setDay((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));
        StatActManualLog.log();
        InnerLog.log("35", req);
        spmGeoAddress(req.getDevice_id(), null, null, req.getIp());
    }

    @Override
    public void spmTerminal4native(SpmTerminalReq req) {
        StatActTerminalLog.log(req);
    }

    private SpmSDataReq logSpmSData(String sdata,SpmActivityReq req) {
        try {
            String decodeSData = SdkCipherUtils.decodeData(sdata);
            if (StringUtils.isNotBlank(decodeSData)) {
                SpmSDataReq sdataReq = SdkCipherUtils.toPropertyBean(new SpmSDataReq(), decodeSData);
                if (StringUtils.isNotBlank(sdataReq.getDevice_id())) {
                    sdataReq.setSlotId(req.getAdslot_id());
                    sdataReq.setAppId(req.getApp_id());
                    InnerLog.log("16", sdataReq); // 16（设备信息SDK-part1）（SDK上报设备信息，对应sdata数据） 
                }
                return sdataReq;
            }
        } catch (Exception e) {
            logger.warn("decode sdata failed.sdata=[{}]", sdata,e);
        }
        return null;
    }

    private SpmNSDataReq logSpmNSData(String nsdata,SpmActivityReq req) {
        try {
            String decodeNSData = SdkCipherUtils.decodeData(nsdata);
            if (StringUtils.isNotBlank(decodeNSData)) {
                SpmNSDataReq nsdataReq = SdkCipherUtils.toPropertyBean(new SpmNSDataReq(), decodeNSData);
                if (StringUtils.isNotBlank(nsdataReq.getDevice_id())) {
                    nsdataReq.setSlotId(req.getAdslot_id());
                    nsdataReq.setAppId(req.getApp_id());
                    InnerLog.log("17", nsdataReq); //17（设备信息SDK-part2）（SDK上报设备信息，对应nsdata数据）
                }
                return nsdataReq;
            }
        } catch (Exception e) {
            logger.warn("decode nsdata failed.nsdata=[{}]",nsdata, e);
        }
        return null;
    }

    private void spmDeviceInfo(String deviceId, String ua, String osType, String vendor, String model) {
        DeviceInfoLog deviceLog = new DeviceInfoLog();
        deviceLog.setDevice_id(deviceId);
        if (StringUtils.isNotBlank(ua)) {
            UAData data = UAUtils.parseUA(ua);
            deviceLog.setModel(data.getModel());
            deviceLog.setOs_type(data.getOsType());
            deviceLog.setVendor(data.getVendor());
        } else {
            deviceLog.setModel(model);
            deviceLog.setOs_type(osType);
            deviceLog.setVendor(vendor);
        }
        InnerLog.log("9", deviceLog); //9（设备信息记录）
    }

    private void spmGeoAddress(String deviceId, String longitude, String latitude, String ipAddr) {
        GeoData data = null;
        if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
            data = GeoUtils.getGeo(longitude, latitude);
        }

        if (data == null && StringUtils.isNotBlank(ipAddr)) {
            try {
                IpAreaLibraryDto byIpLong = localCacheService.findByIpLong(IpAreaLibraryDto.convertIpLong(ipAddr));
                if (byIpLong != null) {
                    data = new GeoData();
                    data.setCountry(byIpLong.getCountry());
                    data.setProvince(byIpLong.getProvince());
                    data.setCity(byIpLong.getCity());
                    data.setJson(JSON.toJSONString(byIpLong));
                }
                // data = GeoUtils.getGeoLocal(ipAddr);
			} catch (NegativeArraySizeException e) {
				logger.warn("解析ip出错，ip={}",ipAddr);
			}
        }

        if (data != null) {
            GeoAddrLog addrLog = new GeoAddrLog();
            addrLog.setDevice_id(deviceId);
            addrLog.setCountry(data.getCountry());
            addrLog.setProvince(data.getProvince());
            addrLog.setCity(data.getCity());
            addrLog.setDistrict(data.getDistrict());
            addrLog.setTownship(data.getTownship());
            addrLog.setJson(data.getJson());
            addrLog.setTime((new DateTime()).toString("yyyy-MM-dd HH:mm:ss"));

            InnerLog.log("10", addrLog); //10（地域信息记录）
        }
    }

    @Override
    public void spmActivity4native(SpmActivityReq req, String sdata, String nsdata) {
        String os_type = null;
        String imei = null;
        String idfa = null;
        String app_list = null;
        MediaAppDataDto app = localCacheService.getMediaApp(req.getApp_key());
        if (app == null || app.getAppId() <= 0) {
            logger.info("App is invalid for spm api, appKey=[{}].", req.getApp_key());
            return;
        }
        req.setApp_id(app.getAppId());
        if (StringUtils.isNotBlank(sdata)) {
            SpmSDataReq sdataReq = logSpmSData(sdata,req);
            if (sdataReq != null) {
                req.setModel(sdataReq.getModel());
                spmDeviceInfo(req.getDevice_id(), null, sdataReq.getOs_type(), sdataReq.getVendor(), sdataReq.getModel());
                os_type = sdataReq.getOs_type();
                imei = sdataReq.getImei();
                idfa = sdataReq.getIdfa();
            }else {
                logger.warn("spmActivity4Native error,req=[{}]",req);
            }
        }
        if (StringUtils.isNotBlank(nsdata)) {
            SpmNSDataReq nsdataReq = logSpmNSData(nsdata,req);
            if (nsdataReq != null) {
                req.setConnect_type(nsdataReq.getConnection_type());
                spmGeoAddress(req.getDevice_id(), nsdataReq.getLongitude(), nsdataReq.getLatitude(), req.getIp());
                app_list = nsdataReq.getApp_list();
            }else {
                logger.warn("spmActivity4Native error,req=[{}]",req);
            }
        }

//        sendDeviceInfoApiInvoker.send(app_list, os_type, imei, idfa, req.getDevice_id());
//        baichuanClient.execute(os_type, imei, idfa, req.getDevice_id());
        spmActivity4native(req);
    }

    @Override
    public void spmError4native(SpmErrorReq req) {
        StatActErrorLog.log(req);
    }

}
