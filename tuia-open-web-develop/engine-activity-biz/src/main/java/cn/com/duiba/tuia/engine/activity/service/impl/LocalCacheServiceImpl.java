package cn.com.duiba.tuia.engine.activity.service.impl;

import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.activity.center.api.remoteservice.RemoteActivityService;
import cn.com.duiba.tuia.engine.activity.model.AdType;
import cn.com.duiba.tuia.engine.activity.remoteservice.ActivityDeliverSpmService;
import cn.com.duiba.tuia.engine.activity.remoteservice.ActivityService;
import cn.com.duiba.tuia.engine.activity.remoteservice.DomainShieldService;
import cn.com.duiba.tuia.engine.activity.remoteservice.MaterialSpecService;
import cn.com.duiba.tuia.engine.activity.remoteservice.MediaService;
import cn.com.duiba.tuia.engine.activity.remoteservice.SystemConfigService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.constant.ActivityConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.MaterialSpecificationConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.*;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActRatioService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteIpAreaLibraryService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActivitySortService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteSlotGameService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteSlotMaterialService;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteSlotSckService;
import cn.com.duiba.tuia.utils.CollectionUtil;
import cn.com.duiba.wolf.dubbo.DubboResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeMap;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author xuyenan
 * @createTime 2017/1/24
 */
@Service("localCacheService")
public class LocalCacheServiceImpl implements LocalCacheService {

    private static final Logger                               logger                       = LoggerFactory.getLogger(LocalCacheServiceImpl.class);
    // 默认列表KEY
    private static final long                                 DEFAULT_KEY                  = 0L;
    
    // 查询所有的appIds
    private static final Integer                              SELECT_ALL_APP_IDS           = 0;

    // 查询所有的slotIds
    private static final Integer                              SELECT_ALL_SLOT_IDS          = 1;

    @Autowired
    private ActivityService                                    activityService;
    @Autowired
    private MediaService                                       mediaService;
    @Autowired
    private MaterialSpecService                                materialSpecService;

    @Autowired
    private SystemConfigService                                systemConfigService;

    @Autowired
    private DomainShieldService                                domainShieldService;

    @Autowired
    private ActivityDeliverSpmService                          activityDeliverSpmService;

    @Autowired
    private RemoteSlotMaterialService                          remoteSlotMaterialService;

    @Autowired
    private RemoteSlotSckService                               remoteSlotSckService;
    @Autowired
    private RemoteActRatioService                              remoteActRatioService;
    @Autowired
    private RemoteIpAreaLibraryService remoteIpAreaLibraryService;

    @Autowired
    private RemoteSlotGameService                              remoteSlotGameService;

    @Autowired
    private RemoteActivitySortService remoteActivitySortService;

    @Autowired
    private RemoteActivityService remoteActivityService;


    private static final MediaAppDataDto MEDIA_NULL = new MediaAppDataDto();

    private static final ActivityManualPlanDto ACTIVITY_NULL = new ActivityManualPlanDto();

    private CacheLoader<Long, List<SlotGameDto>> slotGameListCacheLoader = new CacheLoader<Long, List<SlotGameDto>>() {
        @Override
        public List<SlotGameDto> load(Long slotId) {
            return remoteSlotGameService.selectBySlotId(slotId);
        }
    };

    private CacheLoader<String, List<ActivityDto>> enableGameListCacheLoader = new CacheLoader<String,
            List<ActivityDto>>() {
        @Override
        public List<ActivityDto> load(String key) throws Exception {
            return remoteActivitySortService.selectEnableGame();
        }
    };

    private CacheLoader<String, List<SlotActMaterial4Engine>> slotActMaterialCacheLoader = new CacheLoader<String, List<SlotActMaterial4Engine>>() {
    	@Override
    	public List<SlotActMaterial4Engine> load(String key) throws Exception {
    		String[] array = key.split(SplitConstant.SPLIT_COLON);
    		Long slotId = Long.valueOf(array[0]);
    		Long actId = Long.valueOf(array[1]);
    		Integer actSource = Integer.valueOf(array[2]);
    		return remoteSlotMaterialService.getSlotActMaterial4Engine(slotId, actId, actSource).getResult();
    	}
    };

    private LoadingCache<Long, List<SlotGameDto>> slotGameListCache = CacheBuilder.newBuilder()
            .concurrencyLevel(20).refreshAfterWrite(30,
                    TimeUnit.SECONDS).expireAfterWrite(60,
                    TimeUnit.SECONDS).build(slotGameListCacheLoader);

    private LoadingCache<String, List<ActivityDto>> enableGameListCache = CacheBuilder.newBuilder()
            .concurrencyLevel(20).refreshAfterWrite(30,
                    TimeUnit.SECONDS).expireAfterWrite(60,
                    TimeUnit.SECONDS).build(enableGameListCacheLoader);

    private LoadingCache<String, List<SlotActMaterial4Engine>> slotActMaterialCache = CacheBuilder.newBuilder()
            .concurrencyLevel(20).refreshAfterWrite(30,
                TimeUnit.SECONDS).expireAfterWrite(60,
                TimeUnit.SECONDS).build(slotActMaterialCacheLoader);
    
    private CacheLoader<Long, List<RspActivityDto>> slotActivityCacheLoader = new CacheLoader<Long, List<RspActivityDto>>() {

        @Override
        public List<RspActivityDto> load(Long key) throws Exception {
            List<RspActivityDto> activities;
            if (DEFAULT_KEY == key) {
                activities = activityService.getDefaultActivityList();
            } else {
                activities = activityService.getActivityBySlot(key);
            }
            return activities;
        }
    };

    private CacheLoader<Long, List<RspActivityDto>> engineLimitActivityCacheLoader = new CacheLoader<Long, List<RspActivityDto>>() {

        @Override
        public List<RspActivityDto> load(Long key) throws Exception {

            List<Long> testActivityIds = getTestActivities();
            List<RspActivityDto> testActivities = new ArrayList<>(testActivityIds.size());
            for (Long testActivityId : testActivityIds) {
                RspActivityDto e = new RspActivityDto();
                e.setId(testActivityId);// 算法必须
                e.setSource(1);// 算法必须
                e.setGmtCreate(new Date(0L));// 算法必须
                e.setIsDirectMedia(ActivityConstant.IS_NOT_DIRECT_MEDIA);// 过滤必须
                testActivities.add(e);
            }
            return testActivities;
        }

        /**
         * 获取测试活动id
         *
         * @return 测试活动id
         */
        private List<Long> getTestActivities() {
            String testActivityStr = getSystemConfigValue("test-activity-ids");
            try {
                if (StringUtils.isNotBlank(testActivityStr)) {
                    return Lists.newArrayList(StringUtils.split(testActivityStr, ",")).stream().map(Long::parseLong).collect(Collectors.toList());
                }
            } catch (Exception e) {
                logger.warn("getTestActivities is error,e:", e);
            }
            return Collections.emptyList();
        }
    };
    
    private CacheLoader<Long, GroupRatioDayuDto> actRatioCacheLoader = new CacheLoader<Long, GroupRatioDayuDto>() {
    	@Override
    	public GroupRatioDayuDto load(Long slotId) throws Exception {
    		return remoteActRatioService.getDeliveryAct(slotId);
    	}
    };

    private CacheLoader<Long, SlotCacheDto>      slotCacheLoader           = new CacheLoader<Long, SlotCacheDto>() {

        @Override
        public SlotCacheDto load(Long key)
            throws Exception {
            return mediaService.getSlot(key);
        }

    };

    private CacheLoader<Long, ManagerShieldStrategyDto> shieldStrategyCacheLoader = new CacheLoader<Long, ManagerShieldStrategyDto>() {
        @Override
        public ManagerShieldStrategyDto load(Long key) throws Exception {
            return mediaService.getShieldStrategy(key);
        }
    };

    private CacheLoader<String, MediaAppDataDto> mediaAppCacheLoader = new CacheLoader<String, MediaAppDataDto>() {

        @Override
        public MediaAppDataDto load(String key) throws Exception {
            MediaAppDataDto app = mediaService.getMediaAppByKey(key);
            if (app == null || !app.isValid()) {
                logger.info("App invalid, appKey=[{}]", key);
                return MEDIA_NULL;
            }
            return app;
        }
    };

    private CacheLoader<Long, ActivityManualPlanDto> activityManualCacheLoader = new CacheLoader<Long, ActivityManualPlanDto>() {

        @Override
        public ActivityManualPlanDto load(Long key) throws Exception {
            ActivityManualPlanDto plan = activityService.findActivityManualPlan(key);
            if (plan == null) {
                return ACTIVITY_NULL;
            }
            return plan;
        }
    };

    private CacheLoader<String, Map<String, ActivitySpmDto>> activitySpmCacheLoader = new CacheLoader<String, Map<String, ActivitySpmDto>>() {

        @Override
        public Map<String, ActivitySpmDto> load(String key) throws Exception {
            Integer statType = Integer.parseInt(key.split(SplitConstant.SPLIT_HYPHEN)[0]);
            if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == statType) {
                return activityService.getSlotSpm(null, statType);
            } else {
                Long slotId = Long.parseLong(key.split(SplitConstant.SPLIT_HYPHEN)[2]);
                return activityService.getSlotSpm(slotId, statType);
            }
        }
    };


    private CacheLoader<Long, List<MaterialCtrDto>> materialCtrCacheLoader = new CacheLoader<Long, List<MaterialCtrDto>>() {
        @Override
        public List<MaterialCtrDto> load(Long slotId) throws Exception {
            return materialSpecService
                .getMaterialCtrBySlotActivity(slotId);
        }
    };

    private CacheLoader<Long, TuiaActivityDto> tuiaActivityCacheLoader = new CacheLoader<Long, TuiaActivityDto>() {

        @Override
        public TuiaActivityDto load(Long key) throws Exception {
            return activityService.getTuiaActivity(key);
        }
    };

    private CacheLoader<Long, RspActivitySlotDto> activitySlotCacheLoader = new CacheLoader<Long, RspActivitySlotDto>() {

        @Override
        public RspActivitySlotDto load(Long key) throws Exception {
            RspActivitySlotDto activitySlotDto = activityService.getActivitySlotById(key);
            if (activitySlotDto == null) {
                return new RspActivitySlotDto();
            }
            return activitySlotDto;
        }
    };

    private CacheLoader<String, Map<String, ActivityRpmDto>> activityRpmCacheLoader = new CacheLoader<String, Map<String, ActivityRpmDto>>() {

        @Override
        public Map<String, ActivityRpmDto> load(String key) throws Exception {
            Integer statType = Integer.parseInt(key.split(SplitConstant.SPLIT_HYPHEN)[0]);
            if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == statType) {
                return activityService.getSlotRpm(null, statType);
            } else {
                Long slotId = Long.parseLong(key.split(SplitConstant.SPLIT_HYPHEN)[2]);
                return activityService.getSlotRpm(slotId, statType);
            }
        }
    };
    
    private CacheLoader<String, Map<String, ActivityRpmDto>> activityRpmInWeekCacheLoader = new CacheLoader<String, Map<String, ActivityRpmDto>>() {

        @Override
        public Map<String, ActivityRpmDto> load(String key) throws Exception {
            Integer statType = Integer.parseInt(key.split(SplitConstant.SPLIT_HYPHEN)[0]);
            if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == statType) {
                return activityService.getSlotRpmInWeek(null, statType);
            } else {
                Long slotId = Long.parseLong(key.split(SplitConstant.SPLIT_HYPHEN)[2]);
                return activityService.getSlotRpmInWeek(slotId, statType);
            }
        }
    };

    private CacheLoader<String, Map<String, List<ActivityRpmWithMainMeetDto>>> activityRpmWithMainMeetInWeekCacheLoader = new CacheLoader<String, Map<String, List<ActivityRpmWithMainMeetDto>>>() {

        @Override
        public Map<String, List<ActivityRpmWithMainMeetDto>> load(String key) throws Exception {
            Integer statType = Integer.parseInt(key.split(SplitConstant.SPLIT_HYPHEN)[0]);
            Long slotId = null;
            if (MaterialSpecificationConstant.STAT_TYPE_OVERALL != statType) {
                slotId = Long.parseLong(key.split(SplitConstant.SPLIT_HYPHEN)[2]);
            }
            return activityService.getSlotRpmWithMainMeetInWeek(slotId, statType);
        }
    };

    private CacheLoader<String, Map<String, ActivityRpmWithMainMeetDto>> activityMissRequestBySlotInWeekCacheLoader = new CacheLoader<String, Map<String, ActivityRpmWithMainMeetDto>>() {

        @Override
        public Map<String, ActivityRpmWithMainMeetDto> load(String key) throws Exception {
            return activityService.getMissRequestBySlotInWeek();
        }
    };
    
    private CacheLoader<String, Map<String, ActivityRpmDto>> activityRpmInWeekCacheLoader4manual = new CacheLoader<String, Map<String, ActivityRpmDto>>() {

        @Override
        public Map<String, ActivityRpmDto> load(String key) throws Exception {
            Integer statType = Integer.parseInt(key.split(SplitConstant.SPLIT_HYPHEN)[0]);
            if (MaterialSpecificationConstant.STAT_TYPE_OVERALL == statType) {
                return activityService.getSlotRpmInWeek4manual(null, statType);
            } else {
                Long slotId = Long.parseLong(key.split(SplitConstant.SPLIT_HYPHEN)[2]);
                return activityService.getSlotRpmInWeek4manual(slotId, statType);
            }
        }
    };
    
    private CacheLoader<Long, List<SlotSck>> slotSckCacheLoader = new CacheLoader<Long, List<SlotSck>>(){

        @Override
        public List<SlotSck> load(Long key) throws Exception {
            return remoteSlotSckService.selectWithRecommendSckBySlotId(key);
        }
        
    };

    /**
     * 广告位定制列表缓存
     */
    private LoadingCache<Long, List<RspActivityDto>> engineLimitActivityCache = CacheBuilder.newBuilder()
        .concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(engineLimitActivityCacheLoader);

    /**
     * 广告位定制列表缓存
     */
    private LoadingCache<Long, List<RspActivityDto>> slotActivityCache = CacheBuilder.newBuilder()
            .concurrencyLevel(20).refreshAfterWrite(30,
                    TimeUnit.SECONDS).expireAfterWrite(60,
                    TimeUnit.SECONDS).build(slotActivityCacheLoader);
    private LoadingCache<Long, GroupRatioDayuDto> actRatioCache = CacheBuilder.newBuilder()
    		.concurrencyLevel(20).refreshAfterWrite(30,
    				TimeUnit.SECONDS).expireAfterWrite(60,
    						TimeUnit.SECONDS).build(actRatioCacheLoader);
    /**
     * 广告位缓存
     */
    private LoadingCache<Long, SlotCacheDto> slotCache = CacheBuilder.newBuilder().maximumSize(2000)
        .concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(slotCacheLoader);

    /**
     * 广告位屏蔽策略缓存
     */
    private LoadingCache<Long, ManagerShieldStrategyDto> shieldStrategyCache = CacheBuilder.newBuilder()
        .maximumSize(2000).concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(shieldStrategyCacheLoader);

    /**
     * 媒体缓存
     */
    private LoadingCache<String, MediaAppDataDto> mediaAppCache = CacheBuilder.newBuilder()
        .concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(mediaAppCacheLoader);

    /**
     * 手动投放缓存
     */
    private LoadingCache<Long, ActivityManualPlanDto> activityManualCache = CacheBuilder
        .newBuilder().maximumSize(2000).concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(30,
            TimeUnit.SECONDS).build(activityManualCacheLoader);

    /**
     * 活动SPM缓存
     */
    private LoadingCache<String, Map<String, ActivitySpmDto>> activitySpmCache = CacheBuilder
        .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
            TimeUnit.SECONDS).expireAfterWrite(600,
            TimeUnit.SECONDS).build(activitySpmCacheLoader);

    /**
     * 素材CTR缓存
     */
    private LoadingCache<Long, List<MaterialCtrDto>> materialCtrCache = CacheBuilder.newBuilder()
            .concurrencyLevel(20).refreshAfterWrite(500,
                TimeUnit.SECONDS).expireAfterWrite(600,
                TimeUnit.SECONDS).build(materialCtrCacheLoader);
    
    /**
     * 推啊活动缓存
     */
    private LoadingCache<Long, TuiaActivityDto>    tuiaActivityCache = CacheBuilder.newBuilder()
        .concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(tuiaActivityCacheLoader);
    /**
     * 广告位定向模式缓存（仅投放、优先投放）
     */
    private LoadingCache<Long, RspActivitySlotDto> activitySlotCache = CacheBuilder.newBuilder()
        .concurrencyLevel(20).refreshAfterWrite(30,
            TimeUnit.SECONDS).expireAfterWrite(60,
            TimeUnit.SECONDS).build(activitySlotCacheLoader);

    /**
     * 活动RPM缓存
     */
    private LoadingCache<String, Map<String, ActivityRpmDto>> activityRpmCache = CacheBuilder
        .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
            TimeUnit.SECONDS).expireAfterWrite(600,
            TimeUnit.SECONDS).build(activityRpmCacheLoader);
    
    /**
     * 活动一周RPM缓存
     */
    private LoadingCache<String, Map<String, ActivityRpmDto>> activityRpmInWeekCache = CacheBuilder
        .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
            TimeUnit.SECONDS).expireAfterWrite(600,
            TimeUnit.SECONDS).build(activityRpmInWeekCacheLoader);

    /**
     * 活动一周更新RPM缓存(包含主会场)
     */
    private LoadingCache<String, Map<String, List<ActivityRpmWithMainMeetDto>>> activityRpmWithMainMeetInWeekCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
                    TimeUnit.SECONDS).expireAfterWrite(600,
                    TimeUnit.SECONDS).build(activityRpmWithMainMeetInWeekCacheLoader);

    /**
     * 流失率
     */
    private LoadingCache<String, Map<String, ActivityRpmWithMainMeetDto>> activityMissRequestBySlotInWeekCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
                    TimeUnit.SECONDS).expireAfterWrite(600,
                    TimeUnit.SECONDS).build(activityMissRequestBySlotInWeekCacheLoader);
    
    /**
     * 活动一周RPM缓存for手投
     */
    private LoadingCache<String, Map<String, ActivityRpmDto>> activityRpmInWeekCache4manual = CacheBuilder
        .newBuilder().concurrencyLevel(20).refreshAfterWrite(500,
            TimeUnit.SECONDS).expireAfterWrite(600,
            TimeUnit.SECONDS).build(activityRpmInWeekCacheLoader4manual);



    /**
     * 活动曝光数大于5000的活动列表
     */
    private LoadingCache<Long, List<ActivitySpmDto>> activityExposeCountMoreThan5000Cache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<Long, List<ActivitySpmDto>>() {
                @Override
                public List<ActivitySpmDto> load(Long l) throws Exception {
                    return activityDeliverSpmService.getExposeCountMoreThan5000();
                }
            });


    /**
     * JSSDK来源域名屏蔽缓存
     */
    private LoadingCache<Long, Set<String>> shieldDomainCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<Long, Set<String>>() {
                @Override
                public Set<String> load(Long slotId) throws Exception {
                    return domainShieldService.selectShieldDomainBySlotId(slotId);
                }
            });

    /**
     * 媒体对应的域名策略
     */
    private LoadingCache<Long, DomainConfigDto> appDomainConfigCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<Long, DomainConfigDto>() {
                @Override
                public DomainConfigDto load(Long appId) throws Exception {
                    return systemConfigService.selectDomainConfigByAppId(appId);
                }
            });

    /**
     * 手动投放广告位域名切换
     */
    private LoadingCache<Long, String> manualDomainCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<Long, String>() {
                @Override
                public String load(Long slotId) throws Exception {
                    return systemConfigService.selectActDomainBySlotId(slotId);
                }
            });

    /**
     * 手动投放广告位域名切换
     */
    private LoadingCache<String, SystemConfigDto> globalDomainCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(60, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS).build(new CacheLoader<String, SystemConfigDto>() {
                @Override
                public SystemConfigDto load(String key) throws Exception {
                    return systemConfigService.getGlobalHost(key);
                }
            });
    
    /**
     * 查询所有的媒体keys
     */
     private LoadingCache<Integer, Set<String>> appAllKeysCache = CacheBuilder
            .newBuilder().concurrencyLevel(20).refreshAfterWrite(10, TimeUnit.SECONDS)
            .expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<Integer, Set<String>>() {
                @Override
                public Set<String> load(Integer flag) throws Exception {
                        return mediaService.getAllAppKeys();
                }
            });
    
    /**
     * 查询所有的广告Ids缓存
     */
     private LoadingCache<Integer, Set<Long>> slotAllIdsCache = CacheBuilder
             .newBuilder().concurrencyLevel(20).refreshAfterWrite(10, TimeUnit.SECONDS)
             .expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<Integer, Set<Long>>() {
                 @Override
                 public Set<Long> load(Integer key) throws Exception {
                    
                      return mediaService.getAllSlotIds();
                 }
             });
     /** 错误的activity 缓存 */
    private LoadingCache<String, List<Long>> badActivityCache = CacheBuilder
            .newBuilder().maximumSize(5).refreshAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<Long>>() {
                @Override
                public List<Long> load(String key) throws Exception {
                    DubboResult<List<Long>> badActivityIds = remoteActivityService.findBadActivityIds();
                    List<Long> results = badActivityIds.getResult();
                    if(results==null){
                        return new ArrayList<>(0);
                    }
                    return results;
                }
            });

     /**
     * slotSckCache:广告位素材缓存
     */
    private LoadingCache<Long, List<SlotSck>> slotSckCache = CacheBuilder
             .newBuilder().concurrencyLevel(20).refreshAfterWrite(30, TimeUnit.SECONDS).expireAfterWrite(20, TimeUnit.SECONDS).build(slotSckCacheLoader);

    /** 引擎投放列表缓存 */
    private List<RspActivityDto>                              engineActivityList;

    /** 试投列表 */
    private List<RspActivityDto>                              newActivityList;

    @Resource
    private ExecutorService executorService;
    
    private LoadingCache<String, Optional<RspActivityDto>> activityDetailCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(90, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Optional<RspActivityDto>>() {
                    	@Override
                        public Optional<RspActivityDto> load(String key) {
                            return getActivityDetailById(key);
                        }
                        @Override
                        public ListenableFutureTask<Optional<RspActivityDto>> reload(final String key, Optional<RspActivityDto> prevGraph) {
                            ListenableFutureTask<Optional<RspActivityDto>> task = ListenableFutureTask.create(() -> {
                                return getActivityDetailById(key);
                            });
                            executorService.submit(task);
                            return task;
                        }
                    });

    private LoadingCache<Integer, Optional<TreeRangeMap<Long, IpAreaLibraryDto>>> loadingCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .refreshAfterWrite(24, TimeUnit.HOURS)
            .build(
                    new CacheLoader<Integer, Optional<TreeRangeMap<Long, IpAreaLibraryDto>>>() {
                        private TreeRangeMap<Long, IpAreaLibraryDto> getIpData() {
                            TreeRangeMap<Long, IpAreaLibraryDto> rangeMap = TreeRangeMap.create();
                            List<IpAreaLibraryDto> ipList = remoteIpAreaLibraryService.selectAll();
                            ipList.forEach(x -> rangeMap.put(Range.closed(x.getStartIpNum(), x.getEndIpNum()), x));
                            return rangeMap;
                        }

                        public Optional<TreeRangeMap<Long, IpAreaLibraryDto>> load(Integer key) {
                            logger.info("load key:" + key);
                            return Optional.ofNullable(getIpData());
                        }

                        @Override
                        public ListenableFutureTask<Optional<TreeRangeMap<Long, IpAreaLibraryDto>>> reload(final Integer key, Optional<TreeRangeMap<Long, IpAreaLibraryDto>> prevGraph) {
                            logger.info("reload key:" + key);
                            ListenableFutureTask<Optional<TreeRangeMap<Long, IpAreaLibraryDto>>> task = ListenableFutureTask.create(() -> Optional.ofNullable(getIpData()));
                            executorService.submit(task);
                            return task;
                        }
                    });

    public IpAreaLibraryDto findByIpLong(Long ipLong) {
        Optional<TreeRangeMap<Long, IpAreaLibraryDto>> ipList = loadingCache.getUnchecked(0);
        return ipList.map(longIpAreaLibraryDOTreeRangeMap -> Optional.ofNullable(longIpAreaLibraryDOTreeRangeMap.get(ipLong)).orElse(null)).orElse(null);
    }

    private Optional<RspActivityDto> getActivityDetailById(String key) {
        RspActivityDto result = null;
        List<String> params = Splitter.on(SplitConstant.SPLIT_HYPHEN).splitToList(key);
        Long activityId = Long.parseLong(params.get(0));
        Integer activitySource = Integer.parseInt(params.get(1));
        RspActivityDto activityDto = activityService.getActivityDetail(activityId, activitySource);
        if (activityDto != null && activityDto.getId() != null && activityDto.getSource() != null) {
            result = activityDto;
        }
        return Optional.ofNullable(result);
    }

    @Resource
    private ScheduledExecutorService                          scheduledExecutorService;

    @Override
    public List<SlotActMaterial4Engine> getSlotActMaterialList(Long slotId,Long actId,Integer actSource) {
    	try {
    		String key = slotId + SplitConstant.SPLIT_COLON + actId + SplitConstant.SPLIT_COLON + actSource;
    		return slotActMaterialCache.get(key);
    	} catch (Exception e) {
    		logger.warn("getSlotActMaterialList error, slotId={},actId={},actSource={}", slotId, actId, actSource, e);
    		return Collections.emptyList();
    	}
    }
    
    @Override
    public List<RspActivityDto> getSlotActivityList(Long slotId) {
        try {
            return slotActivityCache.get(slotId);
        } catch (Exception e) {
            logger.warn("getSlotActivityList error, slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }
    @Override
    public GroupRatioDayuDto getactRatioList(Long slotId) {
    	try {
    		return actRatioCache.get(slotId);
    	} catch (Exception e) {
    		logger.warn("getactRatioList error, slotId=" + slotId, e);
    		return null;
    	}
    }

    @Override
    public List<RspActivityDto> getEngineActivityList(Long slotId) {
        try {
            SlotCacheDto slotCacheDto = getSlotDetail(slotId);
            return removeNotFitAct(slotCacheDto, engineActivityList);
        } catch (Exception e) {
            logger.warn("getEngineActivityList error,slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RspActivityDto> getManualActivityList(Long slotId) {
        try {
            SlotCacheDto slotCacheDto = getSlotDetail(slotId);
            List<RspActivityDto> list = slotActivityCache.get(DEFAULT_KEY);
            return removeNotFitAct(slotCacheDto, list);
        } catch (Exception e) {
            logger.warn("getManualActivityList error,slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RspActivityDto> getNewActivityList(Long slotId) {
        try {
            SlotCacheDto slotCacheDto = getSlotDetail(slotId);
            return removeNotFitAct(slotCacheDto, newActivityList);
        } catch (Exception e) {
            logger.warn("getNewActivityList error,slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RspActivityDto> getEngineLimitActivityList(Long slotId) {
        try {
            SlotCacheDto slotCacheDto = getSlotDetail(slotId);
            List<RspActivityDto> list = engineLimitActivityCache.get(DEFAULT_KEY);
            return removeNotFitAct(slotCacheDto, list);
        } catch (Exception e) {
            logger.warn("getEngineLimitActivityList error,slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void refreshNewActivityList() {
        newActivityList = activityService.getNewActivityList();
    }

    @Override
    public RspActivityDto getActivityDetail(Long activityId, Integer activitySource) {
        try {
            Optional<RspActivityDto> activityDto = activityDetailCache.getUnchecked(activityId + SplitConstant.SPLIT_HYPHEN + activitySource);
            if (activityDto.isPresent()) {
                return activityDto.get();
            }
            return null;
        } catch (Exception e) {
            logger.warn("getActivityDetail error,activityId=[{}],activitySource=[{}],e:{}", activityId, activitySource,
                        e);
            return null;
        }
    }

    @Override
    public void refreshActivityDetail(Long activityId, Integer activitySource) {
        activityDetailCache.refresh(activityId + SplitConstant.SPLIT_HYPHEN + activitySource);
    }

    @Override
    public SlotCacheDto getSlotDetail(Long slotId) {
        try {
            if (slotId == null) {
                logger.warn("getSlotDetail error,slotId is null");
                return null;
            }
            return slotCache.get(slotId);
        } catch (Exception e) {
            logger.warn("getSlotDetail error,slotId=" + slotId, e);
            return null;
        }
    }

    @Override
    public MediaAppDataDto getMediaApp(String appKey) {
        if (appKey == null) {
            logger.info("getMediaApp error,appKey is null");
            return null;
        }

        try {
            return mediaAppCache.get(appKey);
        } catch (Exception e) {
            logger.warn("getMediaApp error,appKey=" + appKey, e);
            return null;
        }
    }

    @Override
    public ActivityManualPlanDto getActivityManual(Long slotId) {
        try {
            return activityManualCache.get(slotId);
        } catch (Exception e) {
            logger.warn("getActivityManual error,slotId=" + slotId, e);
            return null;
        }
    }

    @Override
    public Map<String, ActivitySpmDto> getSpmBySlot(Long slotId, Integer statType) {
        String key;
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == statType) {
            key = statType + SplitConstant.SPLIT_HYPHEN + "slot" + SplitConstant.SPLIT_HYPHEN + slotId;
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == statType) {
            Long appId = null;
            SlotCacheDto slot = getSlotDetail(slotId);
            if (slot != null) {
                appId = slot.getAppId();
            }
            key = statType + SplitConstant.SPLIT_HYPHEN + "app" + SplitConstant.SPLIT_HYPHEN + appId;
        } else {
            key = statType + SplitConstant.SPLIT_HYPHEN + "overall";
        }
        try {
            return activitySpmCache.get(key);
        } catch (Exception e) {
            logger.warn("getSpmBySlot error, key=" + key, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public List<MaterialCtrDto> getMaterialBySlotActivity(Long slotId) {
        try {
            return materialCtrCache.get(slotId);
        } catch (Exception e) {
            logger.warn("getMaterialBySlotActivity error,slotId=" + slotId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public TuiaActivityDto getTuiaActivity(Long activityId) {
        try {
            return tuiaActivityCache.get(activityId);
        } catch (Exception e) {
            logger.warn("getTuiaActivity error,key=" + activityId, e);
            return null;
        }
    }

    @Override
    public RspActivitySlotDto getActivitySlotById(Long slotId) {
        try {
            RspActivitySlotDto activitySlotDto = activitySlotCache.get(slotId);
            if (activitySlotDto.getSlotId() == null) {
                return null;
            }
            return activitySlotDto;
        } catch (Exception e) {
            logger.warn("getActivitySlotById error,key=" + slotId, e);
            return null;
        }
    }

    /**
     * 过滤广告位规格和活动规格不匹配的活动
     * 过滤广告位禁投的活动类型，但全部过滤时失效
     * @param slotCacheDto 广告位
     * @param list 活动列表
     */
    public List<RspActivityDto> removeNotFitAct(SlotCacheDto slotCacheDto, List<RspActivityDto> list) {
        //防止出现空异常
        if(list==null) return Collections.emptyList();

        //过滤定向及其规格
        List<RspActivityDto> suitableList = list.stream().filter(
                o -> notDirectMedia(slotCacheDto.getAppId(), o)).filter(
                o -> isSuitableSpecification(slotCacheDto, o)).collect(toList());

        //过滤屏蔽标签，屏蔽后如果为空则失效
        return shieldActIfNotNull(slotCacheDto, suitableList);
    }

    private boolean notDirectMedia(Long appId, RspActivityDto actDto) {

        //如果定向媒体列表不包含当前app这返回false，无法投出此活动
        return actDto.getIsDirectMedia() != ActivityConstant.IS_DIRECT_MEDIA || CollectionUtils.isEmpty(
                actDto.getDirectAppIds()) || actDto.getDirectAppIds().contains(appId);
    }

    private boolean isSuitableSpecification(SlotCacheDto slotCacheDto, RspActivityDto actDto){
        //手动投放不过滤规格
        if(slotCacheDto.getSlotType() == AdType.ADSENSE_TYPE_MANUAL) return true;
        //查询不到活动详情过滤
        RspActivityDto activityDetail = getActivityDetail(actDto.getId(), actDto.getSource());
        if(activityDetail==null) return false;
        //自定义类型需要符合格式
        if(slotCacheDto.getSlotType() == AdType.ADSENSE_TYPE_MATERIAL_SPEC){
            return activityDetail.getMsIdList() != null && activityDetail.getMsIdList().contains(slotCacheDto.getSlotMsId());
        }
        //其他类型不过滤
        return true;
    }

    private List<RspActivityDto> shieldActIfNotNull(SlotCacheDto slotCacheDto,List<RspActivityDto> activityDtoList){
        List<RspActivityDto> afterShieldResult = afterShield(slotCacheDto, activityDtoList);
        if(CollectionUtils.isEmpty(afterShieldResult)) return activityDtoList;
        return afterShieldResult;
    }
    
    public List<RspActivityDto> afterShield(SlotCacheDto slotCacheDto,
                                             List<RspActivityDto> result) {
        if(CollectionUtils.isEmpty(result)) {
            return result;
        }

        //屏蔽广告位禁投类型的活动
        List<RspActivityDto> afterShieldResult = new ArrayList<>();
        try {
            List<String> shieldTagsList = getShieldTagList(slotCacheDto);

            /**
             * 新增错误活动过滤，
             */
            List<Long> badActivityIds = badActivityCache.get("badActivityCache");

            if(CollectionUtils.isEmpty(shieldTagsList)){
                 shieldTagsList = new ArrayList<>();
            }


            for (RspActivityDto rsp : result) {
                RspActivityDto activityDetail = getActivityDetail(rsp.getId(), rsp.getSource());
                boolean isNeedAdd = true;
                //计算过滤标签
                if (StringUtils.isNotBlank(activityDetail.getTag())) {
                    String[] tags = getTagsFromActivityDetail(activityDetail);
                    isNeedAdd = Collections.disjoint(shieldTagsList, Arrays.asList(tags));
                }
                //计算错误活动
                if (!isNeedAdd || badActivityIds.contains(rsp.getId())) {
                    isNeedAdd = false;
                }
                if (isNeedAdd) {
                    afterShieldResult.add(rsp);
                }
            }

            //记录一下屏蔽导致无活动可以投发生的次数
            if(CollectionUtils.isEmpty(afterShieldResult)){
                logger.info("shield strategy fail, slotid=[{}]",slotCacheDto.getId());
            }

        } catch (Exception e) {
            logger.info("shieldStrategyCache.get error , slotid=[{}] , e:[{}]",slotCacheDto.getId(),e);
        }
        return afterShieldResult;
    }



    private List<String> getShieldTagList(SlotCacheDto slot){
        if (slot.getManagerStrategyId() == null) {
            return Collections.emptyList();
        }

        ManagerShieldStrategyDto shieldStrategyDto = getShieldStrategyDto(slot.getManagerStrategyId());
        //关联屏蔽策略，但是查询无结果
        if(shieldStrategyDto == null || StringUtils.isBlank(shieldStrategyDto.getShieldActivitys())){
            return Collections.emptyList();
        }

        //广告位屏蔽活动，格式为10202,10204
        String[] shieldTags = shieldStrategyDto.getShieldActivitys().split(",");
        return Arrays.asList(shieldTags);
    }

    private String[] getTagsFromActivityDetail(RspActivityDto activityDetail){
        String[] tags;
        //活动标签格式为["10202","10204"]，需要处理成10202 10204这样的数组与上述格式对应
        if( activityDetail.getSource() ==0) {
            tags = StringUtils.removePattern(activityDetail.getTag(), "\\[|\\]|\"")
                    .split(",");
        }else {
            tags = activityDetail.getTag().split(",");
        }
        return tags;
    }

    public ManagerShieldStrategyDto getShieldStrategyDto(Long id) {
        try {
            return shieldStrategyCache.get(id);
        } catch (Exception e) {
            return null;
        }
    }

    /** 刷新引擎活动列表，试投列表，活动详情缓存 */
    private void refreshActivityCache() {
        // 更新引擎投放列表
        List<RspActivityDto> engineList = activityService.getEngineActivityList();
        List<RspActivityDto> activityDetailList = activityService.getActivityPlanDetailList(CollectionUtil.getFieldList(engineList, "id"));
        for (RspActivityDto activityDto : activityDetailList) {
            String key = activityDto.getId() + SplitConstant.SPLIT_HYPHEN + activityDto.getSource();
            activityDetailCache.put(key, Optional.of(activityDto));
        }
        // 如果引擎投放列表为空，则应该是服务调用失败，不更新缓存中的引擎投放列表
        if (!CollectionUtils.isEmpty(engineList)) {
            engineActivityList = engineList;
        }
        // 更新试投列表
        List<RspActivityDto> newList = activityService.getNewActivityList();
        activityDetailList = activityService.getActivityPlanDetailList(CollectionUtil.getFieldList(newList, "id"));
        for (RspActivityDto activityDto : activityDetailList) {
            String key = activityDto.getId() + SplitConstant.SPLIT_HYPHEN + activityDto.getSource();
            activityDetailCache.put(key, Optional.of(activityDto));
        }
        newActivityList = newList;
    }

    @EventListener(cn.com.duiba.boot.event.MainContextRefreshedEvent.class)
    public void init() {
        loadingCache.refresh(0);
        // 数据初始化
        refreshActivityCache();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
                try {
                    refreshActivityCache();
                    logger.info("LocalCacheService Refresh Activity Success");
                } catch (Exception e) {
                    logger.warn("LocalCacheService Refresh Activity Error", e);
                }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public String getSystemConfigValue(Long key) {
        String configValue = "";
        try {
            String domainInfoDtoJsonString = manualDomainCache.get(key);
            configValue = JSONObject.parseObject(domainInfoDtoJsonString,DomainInfoDto.class).getActUrl();
            if (StringUtils.isBlank(configValue)) {

                SystemConfigDto systemConfigDto = globalDomainCache.get("tuia-activity-url");
                configValue = systemConfigDto.getTuiaValue();
            }
        } catch (Exception e) {
            logger.warn("LocalCacheService.getSystemConfigValue error,key=" + key, e);
        }
         return configValue;
    }

    @Override
    public String getSystemConfigValue(String key) {
        String configValue = "";
        try {
            SystemConfigDto systemConfigDto = globalDomainCache.get(key);
            if(systemConfigDto!=null){
                configValue = systemConfigDto.getTuiaValue();
            }
        } catch (Exception e) {
            logger.warn("LocalCacheService.getSystemConfigDto error,key=" + key, e);
        }
        return configValue;
    }

    @Override
    public DomainConfigDto getDomainConfigByAppId(Long appId) {
        try {
            return appDomainConfigCache.get(appId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public DomainInfoDto getDomainInfoDto(Long slotId){
        try {
            String domainInfoDtoJsonString = manualDomainCache.get(slotId);
            return JSONObject.parseObject(domainInfoDtoJsonString, DomainInfoDto.class);
        }catch (Exception e){
            logger.warn("LocalCacheService.getDomainInfoDto error,key=[{}]" , slotId, e);
        }
        return new DomainInfoDto();
    }

    @Override
    public Map<String, ActivityRpmDto> getRpmBySlot(Long slotId, Integer statType) {
        String key;
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == statType) {
            key = statType + SplitConstant.SPLIT_HYPHEN + "slot" + SplitConstant.SPLIT_HYPHEN + slotId;
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == statType) {
            Long appId = null;
            SlotCacheDto slot = getSlotDetail(slotId);
            if (slot != null) {
                appId = slot.getAppId();
            }
            key = statType + SplitConstant.SPLIT_HYPHEN + "app" + SplitConstant.SPLIT_HYPHEN + appId;
        } else {
            key = statType + SplitConstant.SPLIT_HYPHEN + "overall";
        }
        try {
            return activityRpmCache.get(key);
        } catch (Exception e) {
            logger.warn("getRpmBySlot error, key=" + key, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public Map<String, ActivityRpmDto> getRpmBySlotInWeek(Long slotId, Integer statType) {
        String key;
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == statType) {
            key = statType + SplitConstant.SPLIT_HYPHEN + "slot" + SplitConstant.SPLIT_HYPHEN + slotId;
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == statType) {
            Long appId = null;
            SlotCacheDto slot = getSlotDetail(slotId);
            if (slot != null) {
                appId = slot.getAppId();
            }
            key = statType + SplitConstant.SPLIT_HYPHEN + "app" + SplitConstant.SPLIT_HYPHEN + appId;
        } else {
            key = statType + SplitConstant.SPLIT_HYPHEN + "overall";
        }
        try {
            return activityRpmInWeekCache.get(key);
        } catch (Exception e) {
            logger.warn("getRpmBySlot error, key=" + key, e);
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, List<ActivityRpmWithMainMeetDto>> getRpmWithMainMeetBySlotInWeek(Long slotId, Integer statType) {
        String key;
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == statType) {
            key = statType + SplitConstant.SPLIT_HYPHEN + "slot" + SplitConstant.SPLIT_HYPHEN + slotId;
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == statType) {
            Long appId = null;
            SlotCacheDto slot = getSlotDetail(slotId);
            if (slot != null) {
                appId = slot.getAppId();
            }
            key = statType + SplitConstant.SPLIT_HYPHEN + "app" + SplitConstant.SPLIT_HYPHEN + appId;
        } else {
            key = statType + SplitConstant.SPLIT_HYPHEN + "overall";
        }
        try {
            return activityRpmWithMainMeetInWeekCache.get(key);
        } catch (Exception e) {
            logger.warn("getRpmBySlot error, key=" + key, e);
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, ActivityRpmWithMainMeetDto> getMissRequestBySlotInWeek(Integer statType) {
        try {
            return activityMissRequestBySlotInWeekCache.get("");
        } catch (Exception e) {
            logger.warn("getMissRequestBySlotInWeek error", e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public Map<String, ActivityRpmDto> getRpmBySlotInWeek4manual(Long slotId, Integer statType) {
        String key;
        if (MaterialSpecificationConstant.STAT_TYPE_SLOT == statType) {
            key = statType + SplitConstant.SPLIT_HYPHEN + "slot" + SplitConstant.SPLIT_HYPHEN + slotId;
        } else if (MaterialSpecificationConstant.STAT_TYPE_APP == statType) {
            Long appId = null;
            SlotCacheDto slot = getSlotDetail(slotId);
            if (slot != null) {
                appId = slot.getAppId();
            }
            key = statType + SplitConstant.SPLIT_HYPHEN + "app" + SplitConstant.SPLIT_HYPHEN + appId;
        } else {
            key = statType + SplitConstant.SPLIT_HYPHEN + "overall";
        }
        try {
            return activityRpmInWeekCache4manual.get(key);
        } catch (Exception e) {
            logger.warn("getRpmBySlot error, key=" + key, e);
            return Collections.emptyMap();
        }
    }


    @Override
    public List<ActivitySpmDto> getExposeCountMoreThan5000() {
        try {
            return activityExposeCountMoreThan5000Cache.get(DEFAULT_KEY);
        } catch (ExecutionException e) {
            logger.warn("getExposeCountMoreThan5000 error", e);
            return Collections.emptyList();
        }
    }

    @Override public Set<String> getShieldDomain(Long slotId) {
        try {
            return shieldDomainCache.get(slotId);
        } catch (ExecutionException e) {
            logger.warn("getShieldDomain error, slotId=[{}] , e:{}", slotId, e);
            return Collections.emptySet();
        }
    }

    @Override public SystemConfigDto getGlobalDomain() {
        try {
            return globalDomainCache.get("tuia-activity-url");
        } catch (ExecutionException e) {
            logger.warn("getGlobalDomain error", e);
            return null;
        }
    }

    @Override
    public Boolean validateAppKey(String appKey) {
        try {
            if (StringUtils.isBlank(appKey)) {
                return false;
            }
            Set<String> appKeys=appAllKeysCache.get(SELECT_ALL_APP_IDS);
            return appKeys.contains(appKey);
        } catch (ExecutionException e) {
            logger.warn("validateAppKey error , appKey=[{}] , e:{}", appKey, e);
            return false;
        }
    }

    @Override
    public Boolean validateSlotId(Long slotId) {
        try {
           if (slotId==null) {
                return false;
           }
           Set<Long> slotIds=slotAllIdsCache.get(SELECT_ALL_SLOT_IDS);
           return slotIds.contains(slotId);
        } catch (ExecutionException e) {
            logger.warn("validateSlotId error , slotId=[{}] , e:{}", slotId, e);
            return false;
        }
    }

    @Override
    public List<SlotSck> getSckBySlot(Long slotId) {
        try {
            return slotSckCache.get(slotId);
        } catch (ExecutionException e) {
            logger.warn("getSckBySlot error , slotId=[{}] , e:{}", slotId, e);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<SlotGameDto> getSlotGameList(Long slotId) {
        try {
            return slotGameListCache.get(slotId);
        } catch (ExecutionException e) {
            logger.warn("getSlotGameList error , slotId=[{}] , e:{}", slotId, e);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<ActivityDto> getEnableGameList() {
        try {
            return enableGameListCache.get("");
        } catch (ExecutionException e) {
            logger.warn("getEnableGameList error , slotId=[{}] , e:{}");
        }
        return Lists.newArrayList();
    }
}
