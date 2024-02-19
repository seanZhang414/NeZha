package cn.com.duiba.nezha.compute.common.model;


import cn.com.duiba.nezha.compute.alg.FM;
import cn.com.duiba.nezha.compute.alg.LR;
import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.AdvertModelEntity;
import cn.com.duiba.nezha.compute.api.dto.FeatureDto;
import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.bo.AdvertCtrLrModelBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerOrderFeatureConstant;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.support.FeatureParse;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/2/16.
 */
public class AdvertCtrLogisticRegressionPred2Test extends TestCase {

    private static String tableName = ConsumerOrderFeatureConstant.TABLE_NAME;
    private static String index = GlobalConstant.CONSUMER_FEATURE_ES_INDEX;
    private static String type = GlobalConstant.CONSUMER_FEATURE_ES_TYPE;
//    public static ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testSaveModel() throws Exception {
        // mid_lr_20170302


    }


    public void testReadModel() throws Exception {

    }

    public void testPredict() throws Exception {

//        AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRDtModelByKeyToMD(ModelKeyEnum.FM_CVR_MODEL_v611.getIndex(), "2018-04-15");
        AdvertModelEntity entity = AdvertCtrLrModelBo.getCTRModelByKeyFromMD(ModelKeyEnum.LR_CTR_MODEL_v004.getIndex());

        if (null == entity) {
            System.out.println("the model entity is null.");
            return;
        }


//        System.out.println("entity=" + JSON.toJSONString(entity));
//        System.out.println("entity2.getSerializerId()=" + JSON.toJSONString(entity.getSerializerId()));
        System.out.println("entity.getDt()=" + JSON.toJSONString(entity.getDt()));


//        AdvertCtrLrModelBo.savaCTRLastModelLocal(entity);
        System.out.println("s time1 = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        final LR lrModel = new LR(entity);

        lrModel.getFeatureIdxList();

        System.out.println("s time2 = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));


        Long consumerId = 5408037209999L;
        Long activityId = 1923917L;
        String gmtDate = DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD);


        final FeatureDto dto = new FeatureDto();


        System.out.println("dto = " + JSON.toJSONString(dto));
        Map<String, String> featureIdxMap = FeatureParse.getFeatureMap(dto);

        String featureStr ="{\"f604001\":\"1\",\"f501001\":\"Android\",\"f608001\":\"1\",\"f113001\":\"40104,04.01.0001\",\"f610001\":\"0\",\"f9913\":\"20\",\"f508002\":\"A59s\",\"f806001\":\"15\",\"f508001\":\"OPPO\",\"f9914\":\"com.android.bluetooth,com.android.browser,com.android.calculator2,com.android.calendar,com.android.captiveportallogin,com.android.certinstaller,com.android.contacts,com.android.defcontainer,com.android.documentsui,com.android.dreams.basic,com.android.externalstorage,com.android.htmlviewer,com.android.inputdevices,com.android.keychain,com.android.location.fused,com.android.managedprovisioning,com.android.mms,com.android.mms.service,com.android.onetimeinitializer,com.android.packageinstaller,com.android.pacprocessor,com.android.phone,com.android.printspooler,com.android.providers.applications,com.android.providers.calendar,com.android.providers.contacts,com.android.providers.downloads,com.android.providers.media,com.android.providers.settings,com.android.providers.telephony,com.android.providers.userdictionary,com.android.proxyhandler,com.android.server.telecom,com.android.settings,com.android.sharedstoragebackup,com.android.shell,com.android.stk,com.android.systemui,com.android.vendors.bridge.softsim,com.android.vpndialogs,com.android.wallpaper.livepicker,com.android.wallpapercropper,com.mediatek,com.mediatek.atci.service,com.mediatek.connectivity,com.mediatek.engineermode,com.mediatek.fwk.plugin,com.mediatek.gba,com.mediatek.hetcomm,com.mediatek.lbs.em2.ui,com.mediatek.mtklogger,com.mediatek.nlpservice,com.mediatek.providers.drm,com.mediatek.ygps,com.redteamobile.virtual.softsim,com.rzwifi.password,com.svox.pico,com.tencent.mm,org.simalliance.openmobileapi.service,se.dirac.acs\",\"f806002\":\"12\",\"f9915\":\"\\\\N,28,1,62,5,19,23,17,60,37,65,40,4,47,21,68,52\",\"f206001\":\"262\",\"f104001\":\"null\",\"f504001\":\"OPPO A59s\",\"f601001\":\"2\",\"f303001\":\"6\",\"f9912\":\"2063\",\"f603001\":\"1\",\"f110001\":\"1\",\"f607001\":\"0\",\"f502001\":\"11\",\"f502002\":\"4\",\"f611001\":\"2\",\"f206002\":\"260\",\"f403004\":\"0\",\"f9902\":\"android,com.amap.android.location,com.android.bluetooth,com.android.browser,com.android.calculator2,com.android.calendar,com.android.captiveportallogin,com.android.certinstaller,com.android.contacts,com.android.defcontainer,com.android.dlna.service,com.android.documentsui,com.android.dreams.basic,com.android.dreams.phototable,com.android.externalstorage,com.android.htmlviewer,com.android.incallui,com.android.inputdevices,com.android.keychain,com.android.keyguard,com.android.location.fused,com.android.managedprovisioning,com.android.mms,com.android.mms.service,com.android.onetimeinitializer,com.android.packageinstaller,com.android.pacprocessor,com.android.phone,com.android.printspooler,com.android.providers.applications,com.android.providers.calendar,com.android.providers.contacts,com.android.providers.downloads,com.android.providers.media,com.android.providers.settings,com.android.providers.telephony,com.android.providers.userdictionary,com.android.proxyhandler,com.android.server.telecom,com.android.settings,com.android.sharedstoragebackup,com.android.shell,com.android.stk,com.android.systemui,com.android.utk,com.android.vendors.bridge.softsim,com.android.vpndialogs,com.android.wallpaper.livepicker,com.android.wallpapercropper,com.cgshi.fruit1,com.cleanmaster.sdk,com.color.uiengine,com.coloros.activation,com.coloros.alarmclock,com.coloros.appmanager,com.coloros.backup.composer.app,com.coloros.backuprestore,com.coloros.backuprestore.remoteservice,com.coloros.blacklist,com.coloros.bootreg,com.coloros.cloud,com.coloros.compass,com.coloros.exserviceui,com.coloros.feedback,com.coloros.filemanager,com.coloros.findmyphone,com.coloros.fingerprint,com.coloros.flashlight,com.coloros.gallery3d,com.coloros.gesture,com.coloros.keyguard.notification,com.coloros.leather,com.coloros.mcs,com.coloros.newsimdetect,com.coloros.notificationmanager,com.coloros.operationmanual,com.coloros.oppoguardelf,com.coloros.oppomorningsystem,com.coloros.oppomultiapp,com.coloros.phonenoareainquire,com.coloros.photoeffects,com.coloros.pictorial,com.coloros.preventmode,com.coloros.providers.downloads.ui,com.coloros.recents,com.coloros.safe.service.framework,com.coloros.safecenter,com.coloros.sau,com.coloros.screenshot,com.coloros.simsettings,com.coloros.soundrecorder,com.coloros.speechassist,com.coloros.speechassist.engine,com.coloros.usbselection,com.coloros.video,com.coloros.wallpapers,com.coloros.weather,com.coloros.weather.service,com.coloros.widget.smallweather,com.coloros.wirelesssettings,com.criticallog,com.dropboxchmod,com.gnss.power,com.google.android.webview,com.hdkino.browser,com.iflytek.speechcloud,com.mediatek,com.mediatek.atci.service,com.mediatek.connectivity,com.mediatek.engineermode,com.mediatek.fwk.plugin,com.mediatek.gba,com.mediatek.hetcomm,com.mediatek.lbs.em2.ui,com.mediatek.miravision.ui,com.mediatek.mtklogger,com.mediatek.nlpservice,com.mediatek.providers.drm,com.mediatek.ygps,com.mobiletools.systemhelper,com.nearme.atlas,com.nearme.gamecenter,com.nearme.romupdate,com.nearme.statistics.rom,com.nearme.sync,com.nearme.themespace,com.nearme.themespacelib,com.oppo.autotest,com.oppo.bluetooth.pbapclient,com.oppo.c2u,com.oppo.camera,com.oppo.camera.doubleexposure,com.oppo.camera.facebeauty,com.oppo.camera.fastvideomode,com.oppo.camera.filter,com.oppo.camera.gif,com.oppo.camera.panorama,com.oppo.camera.professional,com.oppo.camera.superzoom,com.oppo.ctautoregist,com.oppo.dirac,com.oppo.engineeringmode.specialtest,com.oppo.engineermode,com.oppo.factorygps,com.oppo.fingerprints.fingerprintsensortest,com.oppo.fingerprints.service,com.oppo.gestureservice,com.oppo.launcher,com.oppo.market,com.oppo.music,com.oppo.ota,com.oppo.quicksearchbox,com.oppo.reader,com.oppo.resmonitor,com.oppo.sdcardservice,com.oppo.usagedump,com.oppo.usercenter,com.oppo.webview,com.redteamobile.roaming,com.redteamobile.virtual.softsim,com.rzwifi.password,com.sohu.inputmethod.sogouoem,com.svox.pico,com.ted.number,com.tencent.mm,com.ziipin.softkeyboard,com.zy.org.megabucks.by,oppo,org.simalliance.openmobileapi.eseterminal,org.simalliance.openmobileapi.service,org.simalliance.openmobileapi.uicc1terminal,org.simalliance.openmobileapi.uicc2terminal,se.dirac.acs\",\"f805001\":\"14\",\"f805002\":\"12\",\"f505001\":\"1700-2699\",\"f9906\":\"10,\\\\N,22,32,16,31\",\"f9907\":\"1004,\\\\N,2208,3205,1601,2203,2202,3106\",\"f9908\":\"1\",\"f602001\":\"2\",\"f606001\":\"-1\",\"f205002\":\"55\",\"f205001\":\"27\",\"f201001\":\"21054\",\"f102001\":\"20806,02.15.0008\",\"f106001\":\"5102\",\"f609001\":\"0\",\"f804002\":\"12\",\"f804001\":\"14\",\"f301001\":\"1579\",\"f605001\":\"1\",\"f108001\":\"3408\",\"f807002\":\"12\",\"f101001\":\"20976\",\"f807001\":\"14\",\"f507001\":\"3\",\"f503001\":\"6532\",\"f302001\":\"1579\",\"f306001\":\"2\"}";
        //        System.out.println("model.FList = " +lrModel.getFeatureIdxList());

//        String featureStr ="{\"f602001\":\"1\",\"f604001\":\"1\",\"f606001\":\"-1\",\"f501001\":\"Android\",\"f608001\":\"0\",\"f113001\":\"40103\",\"f610001\":\"0\",\"cf101201\":\"1565836007\",\"cf101301\":\"156583558\",\"f201001\":\"36007\",\"f102001\":\"22001\",\"f106001\":\"291\",\"f506001\":\"1\",\"f104001\":\"null\",\"f504001\":\"HUAWEINXT-DL00\",\"f609001\":\"0\",\"f601001\":\"1\",\"f301001\":\"3558\",\"f303001\":\"2\",\"f605001\":\"-1\",\"f603001\":\"1\",\"f110001\":\"1\",\"f607001\":\"0\",\"f502001\":\"8\",\"f108001\":\"3964\",\"f502002\":\"0\",\"f611001\":\"1\",\"f403004\":\"0\",\"f101001\":\"15658\",\"f505001\":\"1700-2699\",\"f503001\":\"31\",\"f302001\":\"3558\",\"f306001\":\"2\"}";
        System.out.println("start -2 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
        featureIdxMap = (Map) JSON.parseObject(featureStr);
        System.out.println("start -1 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

//
        Double ret = lrModel.predict(featureIdxMap);
        System.out.println("ret = " +ret);
        System.out.println("start 0 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

//        FeatureDto cf = new FeatureDto();
//        cf.setAccountId(101L);
//        cf.setActivityId(1025L);
//        cf.setActivityLastChargeNums(9L);
//        cf.setActivityLastGmtCreateTime("2017-10-26 10:11:20");
//        cf.setActivityOrderRank(2L);
//        cf.setActivitySubType("sd");
//        cf.setActivityType(2L);
//        cf.setActivityUseType("1");
//        cf.setAdvertId(21123L);
//        cf.setAdvertTags("1233");
//        cf.setAppBannerId("122333");
//        cf.setAppId(26223L);
//        cf.setCurrentGmtCreateTime("2017-10-26 10:11:50");
//        cf.setDayActivityOrderRank(2L);
//        cf.setDayOrderRank(10L);
//        cf.setMatchTagNums("120201");
//        cf.setSlotId(122L);
//        cf.setLastOperatingActivityId(124401L);
//        cf.setPutIndex(10L);
//        cf.setTimes(2L);
//        System.out.println("start 1 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));
//        FeatureParse.generateFeatureMapStatic(cf, featureIdxMap);
//        System.out.println("start 2 time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

//        Map<Long, Long> retMap = new HashMap<>();
//
//        FeatureParse.generateFeatureMapDynamic(cf, featureIdxMap);
//        Double ret3 = lrModel.predict(featureIdxMap);
//        System.out.println("ret3 = " +ret3);

//        for (int j = 0; j < 2; j++) {
//            long tt1 = System.currentTimeMillis();
//            for (int i = 0; i < 5; i++) {
////            System.out.println("i = " + i);
//                cf.setAdvertId((long) i);
//                cf.setMatchTagNums("120201");
//                FeatureParse.generateFeatureMapDynamic(cf, featureIdxMap);
//                Double ret3 = lrModel.predict(featureIdxMap);
//                System.out.println("ret3 = " +ret3);
//            }
//            long tt2 = System.currentTimeMillis();
//            if (retMap.get((tt2 - tt1)) == null) {
//                retMap.put((tt2 - tt1), (long) 0);
//            }
//            retMap.put((tt2 - tt1), retMap.get((tt2 - tt1)) + 1);
//        }


        System.out.println("end time = " + DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS));

//        for(int k=0;k<1000;k++){
//            if(retMap.get((long)k)!=null){
//                System.out.println("p time = "+k+",cnt="+retMap.get((long)k));
//            }
//        }

//        ReplayerUtil replayerUtil = new ReplayerUtil();
//        replayerUtil.setModel(lrModel);
//
//        System.out.println("featureIdxMap = ");
//        replayerUtil.predictWithInfo(featureIdxMap);
//
//        System.out.println("getFeatureInfoList() = \n " + replayerUtil.getFeatureInfoList());

    }

    private Long getRealRank(String longStr) {
        Long ret = 1L;

        if (longStr != null) {
            Long ret2 = getLong(longStr);
            ret = ret2 + 1;
        }
        return ret;
    }

    private Long getLong(String longStr) {
        Long ret = 0L;

        if (longStr != null) {
            ret = Long.valueOf(longStr);
        }
        return ret;
    }

    private String getString(Long longStr) {
        String ret = null;

        if (longStr != null) {
            ret = Long.toString(longStr);
        }
        return ret;
    }

    private void getConsumerOrderFeatureDto(Long consumerId, Long activityId, String gmtDate) {


        try {
            String consumerId2 = getString(consumerId);
            String activityDimId = getString(activityId);

            String mainKey = FeatureKey.getConsumerOrderFeatureRedisKey(
                    consumerId2,
                    activityDimId);

            String ret = MongoUtil.getMongoDb().findById(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKey);

            System.out.println("ConsumerOrderFeatureDto=" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
