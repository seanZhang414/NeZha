package cn.com.duiba.tuia.engine.activity.test.acyivity;

import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.req.SpmActivityReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.engine.activity.remoteservice.ActivityService;
import cn.com.duiba.tuia.engine.activity.remoteservice.MaterialSpecService;
import cn.com.duiba.tuia.engine.activity.remoteservice.MediaService;
import cn.com.duiba.tuia.engine.activity.service.ActivityEngineService;
import cn.com.duiba.tuia.engine.activity.service.ActivitySpmService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.engine.activity.test.base.BaseJunit4Test;
import cn.com.duiba.tuia.ssp.center.api.constant.ActivityConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.MaterialSpecificationConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.MediaAppDataDto;
import cn.com.duiba.tuia.ssp.center.api.dto.ReqIdAndType;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivitySlotDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationAssortDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationItemContentDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotDto;
import cn.com.duiba.wolf.utils.UUIDUtils;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.internal.startup.Startup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ScheduledExecutorService;

import static cn.com.duiba.tuia.engine.activity.api.ApiCode.SLOT_EXPOSE_REACH_LIMIT;

/**
 * @author xuyenan
 * @createTime 2017/2/13
 */
public class ActivityExposeLimitTest extends BaseJunit4Test {

    @Autowired
    private ActivityEngineService     activityEngineService;

    @Autowired
    private ActivitySpmService        activitySpmService;

    @Autowired
    private Redis3Handler             redisHandler;

    @Autowired
    @Tested
    private LocalCacheService         localCacheService;
    @Injectable
    private MediaService              mediaService;
    @Injectable
    private ActivityService           activityService;
    @Injectable
    private MaterialSpecService       materialSpecService;
    @Injectable
    private ScheduledExecutorService  scheduledExecutorService;

    private static final String       APP_KEY               = "nvfiugnthnvjfbnvhgfb";
    private static final Long         APP_ID                = 9999L;
    private static final Long         SLOT_ID               = 9999L;
    private static final Long         SLOT_MS_ID            = 1L;
    private static final Integer      ACTIVITY_EXPOSE_LIMIT = 3;
    private static final Integer      SLOT_EXPOSE_LIMIT     = 100;
    private static final Long         ACTIVITY_ID           = 9999L;
    private static final Integer      ACTIVITY_TYPE         = 9999;
    private static final Long         ACTIVITY_ID_1         = 99999L;
    private static final Integer      ACTIVITY_TYPE_1       = 99999;
    private static final Long         ACTIVITY_ID_2         = 999999L;
    private static final Integer      ACTIVITY_TYPE_2       = 999999;
    private static final Long         ACTIVITY_ID_3         = 9999999L;
    private static final Integer      ACTIVITY_TYPE_3       = 9999999;
    private static final String       DEVICE_ID             = "unitTest";

    private static SlotCacheDto       slotCache;
    private static MediaAppDataDto    mediaApp;
    private static RspActivityDto     rspActivityDto;
    private static RspActivityDto     rspActivityDto1;
    private static RspActivityDto     rspActivityDto2;
    private static RspActivityDto     rspActivityDto3;
    private static RspActivitySlotDto rspActivitySlotDto;

    static {
        Startup.initializeIfPossible();

        slotCache = new SlotCacheDto();
        slotCache.setId(SLOT_ID);
        slotCache.setValid(true);
        slotCache.setActivityExposeLimit(ACTIVITY_EXPOSE_LIMIT);
        slotCache.setSlotExposeLimit(SLOT_EXPOSE_LIMIT);
        slotCache.setAppId(SLOT_ID);
        slotCache.setSlotMsId(SLOT_MS_ID);
        slotCache.setSlotType(SlotDto.ADSENSE_TYPE_MATERIAL_SPEC);

        mediaApp = new MediaAppDataDto();
        mediaApp.setAppKey(APP_KEY);
        mediaApp.setAppId(APP_ID);
        mediaApp.setValid(true);

        RspMaterialSpecificationDto rspMaterialSpecificationDto = new RspMaterialSpecificationDto();
        RspMaterialSpecificationAssortDto rspMaterialSpecificationAssortDto = new RspMaterialSpecificationAssortDto();
        RspMaterialSpecificationItemContentDto rspMaterialSpecificationItemContentDto = new RspMaterialSpecificationItemContentDto();
        rspMaterialSpecificationItemContentDto.setMsId(SLOT_MS_ID);
        rspMaterialSpecificationItemContentDto.setImageUrl("imageUrl");
        rspMaterialSpecificationItemContentDto.setImageHeight(200);
        rspMaterialSpecificationItemContentDto.setImageWidth(200);
        rspMaterialSpecificationAssortDto.setId(1L);
        rspMaterialSpecificationAssortDto.setIsNewContent(MaterialSpecificationConstant.MS_CONTENT_NEW);
        rspMaterialSpecificationAssortDto.setValue(Lists.newArrayList(rspMaterialSpecificationItemContentDto));
        rspMaterialSpecificationDto.setId(SLOT_MS_ID);
        rspMaterialSpecificationDto.setItemContentList(Lists.newArrayList(rspMaterialSpecificationAssortDto));

        rspActivityDto = new RspActivityDto();
        rspActivityDto.setId(ACTIVITY_ID);
        rspActivityDto.setType(ACTIVITY_TYPE);
        rspActivityDto.setName("test");
        rspActivityDto.setSource(ReqIdAndType.REQ_ACT_SOURCE_DUIBA);
        rspActivityDto.setMsIdList(Lists.newArrayList(SLOT_MS_ID));
        rspActivityDto.setMsList(Lists.newArrayList(rspMaterialSpecificationDto));
        rspActivityDto.setIsDirectMedia(ActivityConstant.IS_NOT_DIRECT_MEDIA);

        rspActivityDto1 = new RspActivityDto();
        rspActivityDto1.setId(ACTIVITY_ID_1);
        rspActivityDto1.setType(ACTIVITY_TYPE_1);
        rspActivityDto1.setName("test");
        rspActivityDto1.setSource(ReqIdAndType.REQ_ACT_SOURCE_DUIBA);
        rspActivityDto1.setMsIdList(Lists.newArrayList(SLOT_MS_ID));
        rspActivityDto1.setMsList(Lists.newArrayList(rspMaterialSpecificationDto));
        rspActivityDto1.setIsDirectMedia(ActivityConstant.IS_NOT_DIRECT_MEDIA);

        rspActivityDto2 = new RspActivityDto();
        rspActivityDto2.setId(ACTIVITY_ID_2);
        rspActivityDto2.setType(ACTIVITY_TYPE_2);
        rspActivityDto2.setName("test");
        rspActivityDto2.setSource(ReqIdAndType.REQ_ACT_SOURCE_DUIBA);
        rspActivityDto2.setMsIdList(Lists.newArrayList(SLOT_MS_ID));
        rspActivityDto2.setMsList(Lists.newArrayList(rspMaterialSpecificationDto));
        rspActivityDto2.setIsDirectMedia(ActivityConstant.IS_NOT_DIRECT_MEDIA);

        rspActivityDto3 = new RspActivityDto();
        rspActivityDto3.setId(ACTIVITY_ID_3);
        rspActivityDto3.setType(ACTIVITY_TYPE_3);
        rspActivityDto3.setName("test");
        rspActivityDto3.setSource(ReqIdAndType.REQ_ACT_SOURCE_DUIBA);
        rspActivityDto3.setMsIdList(Lists.newArrayList(SLOT_MS_ID));
        rspActivityDto3.setMsList(Lists.newArrayList(rspMaterialSpecificationDto));
        rspActivityDto3.setIsDirectMedia(ActivityConstant.IS_NOT_DIRECT_MEDIA);

        rspActivitySlotDto = new RspActivitySlotDto();
        rspActivitySlotDto.setSlotId(SLOT_ID);
        rspActivitySlotDto.setDirectMode(ActivityConstant.DIRECT_ADVERT_MODE_ONLY);
    }

    /**
     * 测试广告位曝光上限逻辑
     */
    @Test
    public void testSlotExposeLimit() {
        new Expectations() {// 模拟广告位

            {
                mediaService.getSlot(SLOT_ID);
                result = slotCache;
            }
        };
        new Expectations() {// 模拟APP

            {
                mediaService.getMediaAppByKey(APP_KEY);
                result = mediaApp;
            }
        };
        new Expectations() {// 模拟广告位定制列表

            {
                activityService.getActivityBySlot(SLOT_ID);
                result = Lists.newArrayList(rspActivityDto, rspActivityDto1);
            }
        };
        new Expectations() {// 模拟活动详情

            {
                activityService.getActivityDetail(ACTIVITY_ID, ACTIVITY_TYPE);
                result = rspActivityDto;
                activityService.getActivityDetail(ACTIVITY_ID_1, ACTIVITY_TYPE_1);
                result = rspActivityDto1;
            }
        };
        new Expectations() {

            {
                activityService.getActivitySlotById(SLOT_ID);
                result = rspActivitySlotDto;
            }
        };

        redisHandler.delete(RedisKeyConstant.getUserKey(SLOT_ID, DEVICE_ID));

        GetActivityReq req = new GetActivityReq();
        req.setAdslot_id(SLOT_ID);
        req.setApp_key(APP_KEY);
        req.setRequest_id(UUIDUtils.createUUID());
        req.setDevice_id(DEVICE_ID);

        SpmActivityReq spmActivityReq = new SpmActivityReq();
        spmActivityReq.setAdslot_id(SLOT_ID);
        spmActivityReq.setApp_key(APP_KEY);
        spmActivityReq.setDevice_id(DEVICE_ID);
        spmActivityReq.setOs_type(null);
        spmActivityReq.setType(0);

        SlotCacheDto slotCache = localCacheService.getSlotDetail(SLOT_ID);

        for (int i = 0; i < slotCache.getSlotExposeLimit(); i++) {
            // 模拟活动请求
            activityEngineService.getActivity(req);
            // 模拟活动曝光
            activitySpmService.spmActivity4native(spmActivityReq);
        }
        GetActivityRsp rsp = activityEngineService.getActivity(req);
        Assert.assertTrue(SLOT_EXPOSE_REACH_LIMIT.equals(rsp.getError_code()));
    }

    /**
     * 测试活动曝光上限逻辑
     */
    @Test
    public void testActivityExposeLimit() {

        redisHandler.delete(RedisKeyConstant.getUserKey(SLOT_ID, DEVICE_ID));

        GetActivityReq req = new GetActivityReq();
        req.setAdslot_id(SLOT_ID);
        req.setApp_key(APP_KEY);
        req.setRequest_id(UUIDUtils.createUUID());
        req.setDevice_id(DEVICE_ID);

        SpmActivityReq spmActivityReq = new SpmActivityReq();
        spmActivityReq.setAdslot_id(SLOT_ID);
        spmActivityReq.setApp_key(APP_KEY);
        spmActivityReq.setDevice_id(DEVICE_ID);
        spmActivityReq.setOs_type(null);
        spmActivityReq.setType(0);

        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(SLOT_ID);

        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
            Assert.assertTrue(activityEngineService.getActivity(req).getActivity_id().contains(ACTIVITY_ID.toString()));
            activitySpmService.spmActivity4native(spmActivityReq);
        }
        Assert.assertTrue(activityEngineService.getActivity(req).getActivity_id().contains(ACTIVITY_ID_1.toString()));
    }

    /**
     * 测试仅定制列表投放逻辑
     */
    @Test
    public void testCustomOutputOnly() {

        redisHandler.delete(RedisKeyConstant.getUserKey(SLOT_ID, DEVICE_ID));

        GetActivityReq req = new GetActivityReq();
        req.setAdslot_id(SLOT_ID);
        req.setApp_key(APP_KEY);
        req.setRequest_id(UUIDUtils.createUUID());
        req.setDevice_id(DEVICE_ID);

        SpmActivityReq spmActivityReq = new SpmActivityReq();
        spmActivityReq.setAdslot_id(SLOT_ID);
        spmActivityReq.setApp_key(APP_KEY);
        spmActivityReq.setDevice_id(DEVICE_ID);
        spmActivityReq.setOs_type(null);
        spmActivityReq.setType(0);

        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(SLOT_ID);

        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
            Assert.assertTrue(activityEngineService.getActivity(req).getActivity_id().contains(ACTIVITY_ID.toString()));
            activitySpmService.spmActivity4native(spmActivityReq);
        }
        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
            Assert.assertTrue(activityEngineService.getActivity(req).getActivity_id().contains(ACTIVITY_ID_1.toString()));
            activitySpmService.spmActivity4native(spmActivityReq);
        }
        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
            Assert.assertTrue(activityEngineService.getActivity(req).getActivity_id().contains(ACTIVITY_ID.toString()));
            activitySpmService.spmActivity4native(spmActivityReq);
        }
    }

//    /**
//     * 测试优先定制列表投放逻辑
//     */
//    @Test
//    public void testCustomOutputPrior() {
//
//        rspActivitySlotDto.setDirectMode(ActivityConstant.DIRECT_ADVERT_MODE_PRIOR);
//        slotCache.setActivityExposeLimit(1);
//
//        new Expectations() { // 模拟兑吧活动
//
//            {
//                activityService.getDefaultActivityList();
//                result = Lists.newArrayList(rspActivityDto, rspActivityDto1, rspActivityDto2, rspActivityDto3);
//
//                activityService.getEngineActivityList();
//                result = Lists.newArrayList(rspActivityDto, rspActivityDto1, rspActivityDto2);
//
//                activityService.getNewActivityList();
//                result = Lists.newArrayList(rspActivityDto3);
//
//                activityService.getActivityDetail(ACTIVITY_ID_2, ACTIVITY_TYPE_2);
//                result = rspActivityDto2;
//                activityService.getActivityDetail(ACTIVITY_ID_3, ACTIVITY_TYPE_3);
//                result = rspActivityDto3;
//
//                activityService.queryAndAddOnApp(ACTIVITY_ID_2, ACTIVITY_TYPE_2, APP_KEY);
//                result = adActivityDto2;
//                activityService.queryAndAddOnApp(ACTIVITY_ID_3, ACTIVITY_TYPE_3, APP_KEY);
//                result = adActivityDto3;
//            }
//        };
//
//        localCacheService.refreshEngineActivityList();
//        redisClient.del(CacheKeyConstant.getUserKey(SLOT_ID, DEVICE_ID));
//
//        GetActivityReq req = new GetActivityReq();
//        req.setAdslot_id(SLOT_ID);
//        req.setApp_key(APP_KEY);
//        req.setRequest_id(UUIDUtils.createUUID());
//        req.setDevice_id(DEVICE_ID);
//
//        SpmActivityReq spmActivityReq = new SpmActivityReq();
//        spmActivityReq.setAdslot_id(SLOT_ID);
//        spmActivityReq.setApp_key(APP_KEY);
//        spmActivityReq.setDevice_id(DEVICE_ID);
//        spmActivityReq.setOs_type(null);
//        spmActivityReq.setType(0);
//
//        SlotCacheDto slotCacheDto = localCacheService.getSlotDetail(SLOT_ID);
//
//        Set<String> visitedIds = new HashSet<>();
//        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
//            GetActivityRsp activityRsp = activityEngineService.getActivity(req);
//            visitedIds.add(activityRsp.getActivity_id());
//            activitySpmService.spmActivity4native(spmActivityReq);
//        }
//        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
//            GetActivityRsp activityRsp = activityEngineService.getActivity(req);
//            visitedIds.add(activityRsp.getActivity_id());
//            activitySpmService.spmActivity4native(spmActivityReq);
//        }
//        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
//            GetActivityRsp activityRsp = activityEngineService.getActivity(req);
//            visitedIds.add(activityRsp.getActivity_id());
//            activitySpmService.spmActivity4native(spmActivityReq);
//        }
//        for (int i = 0; i < slotCacheDto.getActivityExposeLimit(); i++) {
//            GetActivityRsp activityRsp = activityEngineService.getActivity(req);
//            visitedIds.add(activityRsp.getActivity_id());
//            activitySpmService.spmActivity4native(spmActivityReq);
//        }
//        Assert.assertTrue(visitedIds.size() >= 3);
//    }
}
