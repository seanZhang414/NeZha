package cn.com.duiba.nezha.engine.biz.message.advert.ons;

import cn.com.duiba.nezha.compute.common.model.RoiPidController;
import cn.com.duiba.nezha.compute.common.model.StatInfo;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.bo.advert.AdvertStatAssociationBo;
import cn.com.duiba.nezha.engine.biz.constant.MongoCollectionConstant;
import cn.com.duiba.nezha.engine.biz.domain.AdvertStatDo;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.CpaFactorEntity;
import cn.com.duiba.nezha.engine.common.utils.MultiStringUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.RoiHashKeyUtil;
import cn.com.duiba.wolf.utils.DateUtils;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 广告出价变更消息处理
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ROIFeeMessageHandler.java , v 0.1 2017/6/6 下午2:31 ZhouFeng Exp $
 */
@Component
@java.lang.SuppressWarnings("squid:S3776")
public class RoiClickMessageHandler extends AbstractMessageResultHandler {

    private static final String CLICK = "click";

    private static final String TIME = "time";

    private static final int THRESHOLD = 100;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private StringRedisTemplate nezhaStringRedisTemplate;


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdvertStatAssociationBo advertStatAssociationBo;

    @Override
    public String getListenTag() {
        return OnsRoiControllerMessageTag.ROI_CLICK.getTag();
    }

    @Override
    public void consumer(String message) {

        if (StringUtils.isNotBlank(message)) {

            JSONObject json = JSONObject.parseObject(message);


            String advertId = json.getString("adid");
            String targetCpaString = json.getString("targetCpa");
            String packageId = json.getString("packageId");
            String budgetPerDayString = json.getString("budgetPerDay");
            String appId = json.getString("appId");
            String slotId = json.getString("slotId");
            String activityId = json.getString("activityId");


            if (MultiStringUtils.isAnyBlank(advertId, targetCpaString, packageId, appId, slotId, activityId)) {
                logger.warn("conusmer message:{} error,illegal argument", message);
                return;
            }

            // 记录Cat曲线图
            Cat.logMetricForCount("roiClick");

            String appKey = RoiHashKeyUtil.getAppKey(appId);
            String slotKey = RoiHashKeyUtil.getSlotKey(slotId);
            String activityKey = RoiHashKeyUtil.getActivityKey(appId, activityId);


            //递增最近点击记录
            String recentlyClickKey = RedisKeyUtil.recentlyClickKey(advertId, packageId);


            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {

                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

                stringRedisConn.zIncrBy(recentlyClickKey, 1, appKey);
                stringRedisConn.zIncrBy(recentlyClickKey, 1, slotKey);
                stringRedisConn.zIncrBy(recentlyClickKey, 1, activityKey);
                stringRedisConn.expire(recentlyClickKey, 86400);

                return null;
            });


            String clickKey = RedisKeyUtil.roiClickKey(advertId, packageId);

            //目标CPA价格
            double targetCpa = Double.parseDouble(targetCpaString);
            //广告主日预算
            long budgetPerDay = StringUtils.isNotBlank(budgetPerDayString) ? Long.parseLong(budgetPerDayString) : -1;

            String lockKey = RedisKeyUtil.roiClickLockKey(advertId, packageId);
            try {
                // 自旋锁
                spinLock(lockKey);

                //防止异常状况导致key没有被删除，故设置失效时间
                stringRedisTemplate.expire(lockKey, 5, TimeUnit.SECONDS);


                //递增点击次数
                long clickTimes = stringRedisTemplate.opsForHash().increment(clickKey, CLICK, 1);

                if (clickTimes == 1) {
                    //如果点击次数自增完以后等于1，说明原来key不存在，则写入次数和创建时间戳

                    stringRedisTemplate.opsForHash().put(clickKey, TIME, String.valueOf(Instant.now().getEpochSecond()));
                    stringRedisTemplate.expire(clickKey, (long) DateUtils.getToTomorrowSeconds() + new Random().nextInt(100),
                            TimeUnit.SECONDS);
                }

                if (clickTimes < THRESHOLD) {
                    return;
                }

                Instant instant = Instant.ofEpochSecond(Long.parseLong((String) stringRedisTemplate.opsForHash().get
                        (clickKey, TIME)));

                //创建时间
                LocalDateTime createTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                if (createTime.plusMinutes(2).isBefore(LocalDateTime.now())) {
                    //创建时间加三分钟比当前时间早，说明已经过超过三分钟

                    //删除点击计数器
                    stringRedisTemplate.delete(clickKey);

                    String feeKey = RedisKeyUtil.roiFeeKey(advertId, packageId, LocalDate.now());
                    String cvrKey = RedisKeyUtil.roiCvrKey(advertId, packageId, LocalDate.now());

                    //根据点击数获取该广告下最近的点击记录的top49及其点击数
                    Set<ZSetOperations.TypedTuple<String>> dimClickTimes = stringRedisTemplate.opsForZSet()
                            .reverseRangeWithScores(recentlyClickKey, 0, 48);
                    Map<String, Long> dimClickTimesMap = new HashMap<>(dimClickTimes.size());
                    dimClickTimes.forEach(tuple -> dimClickTimesMap.put(tuple.getValue(), (long) tuple.getScore()
                            .doubleValue()));
                    dimClickTimesMap.put(RoiHashKeyUtil.getDefault(), clickTimes);
                    //取出的key为top50的各个维度key
                    List<String> recentlyKeys = new ArrayList<>(dimClickTimesMap.keySet());

                    //查询top50的最近父节点的调价因子
                    Map<String, Double> superNodeFactor = getSuperNodeFactor(advertId, packageId, recentlyKeys);


                    List<String> feeList = stringRedisTemplate.opsForHash().multiGet(feeKey, (List) recentlyKeys);
                    List<String> cvrList = stringRedisTemplate.opsForHash().multiGet(cvrKey, (List) recentlyKeys);


                    Map<String, Long> feeMap = new HashMap<>();
                    Map<String, Long> cvrMap = new HashMap<>();

                    for (int i = 0; i < recentlyKeys.size(); i++) {

                        String key = recentlyKeys.get(i);

                        String feeString = feeList.get(i);
                        String cvrString = cvrList.get(i);

                        if (StringUtils.isNotBlank(feeString)) {
                            feeMap.put(key, Long.parseLong(feeString));
                        }

                        if (StringUtils.isNotBlank(cvrString)) {
                            cvrMap.put(key, Long.parseLong(cvrString));
                        }

                    }

                    List<String> mongoIds = recentlyKeys.stream().map(key -> getKey(advertId, packageId, key))
                            .collect(Collectors.toList());

                    List<CpaFactorEntity> factors = mongoTemplate.find(Query.query(Criteria.where("_id").in
                            (mongoIds)), CpaFactorEntity.class, MongoCollectionConstant.CPA_FACTOR_COLLECTION);

                    Map<String, CpaFactorEntity> factorMap = Maps.uniqueIndex(factors, CpaFactorEntity::getId);

                    List<StatInfo> statInfos = new ArrayList<>(recentlyKeys.size());

                    //用于记录mongo中取出的数据是否为当天数据，如果不为当天数据，在更新时需要将数据的创建时间更新
                    Map<String, Boolean> isToday = new HashMap<>(statInfos.size());

                    Map<String, AdvertStatDo> statDataMap = getStatData(Long.parseLong(advertId), recentlyKeys);

                    //遍历top50的key，组装待计算对象
                    for (String key : recentlyKeys) {

                        String mongoId = getKey(advertId, packageId, key);

                        CpaFactorEntity entity = factorMap.get(mongoId);


                        isToday.put(mongoId, false);

                        double lastCvr = 0;
                        double lastFee = 0;


                        if (entity != null) {
                            Date gmtCreate = entity.getGmtCreate();
                            LocalDate localDate = gmtCreate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            //调价因子继承前一天的数据，累计计费和累计转换0点清零，即不使用前一天数据
                            if (localDate.isEqual(LocalDate.now())) {
                                lastCvr = entity.getCvr() == null ? 0 : entity.getCvr();
                                lastFee = entity.getFee() == null ? 0 : entity.getFee();

                                isToday.put(mongoId, true);
                            }
                        }

                        double lastFactor;
                        if (entity == null || entity.getFactor() == null || entity.getFactor().isNaN()) {
                            lastFactor = 1D;
                        } else {
                            lastFactor = entity.getFactor();
                        }

                        Long cvr = cvrMap.get(key);
                        cvr = cvr == null ? 0 : cvr;

                        Long fee = feeMap.get(key);
                        fee = fee == null ? 0 : fee;

                        Long click = dimClickTimesMap.get(key);
                        click = click == null ? 0 : click;

                        StatInfo statInfo = new StatInfo();
                        statInfo.setId(key);
                        statInfo.setSumFee(fee);
                        statInfo.setSumConv(cvr);
                        statInfo.setSumClick(click);
                        statInfo.setLastSumFee(lastFee);
                        statInfo.setLastSumConv(lastCvr);

                        statInfo.setFactor(lastFactor);
                        Double superFactor = superNodeFactor.get(key);
                        if (superFactor != null) {
                            statInfo.setParentFactor(superFactor);
                        }

                        AdvertStatDo statDto = statDataMap.get(key);
                        if (statDto != null) {
                            statInfo.setConv7d(Optional.ofNullable(statDto.getActClickCnt()).orElse(0L));
                            statInfo.setFee7d(Optional.ofNullable(statDto.getChargeFees()).orElse(0L));
                            statInfo.setClick7d(Optional.ofNullable(statDto.getChargeCnt()).orElse(0L));
                        }

                        statInfos.add(statInfo);
                    }

                    RoiPidController roiPidController = new RoiPidController();

                    //调用调价算法，计算出需要更新的数据
                    List<StatInfo> waitUpdate = roiPidController.getPriceFactor(statInfos, targetCpa,
                            budgetPerDay);

                    //删除已调价数据 
                    if (CollectionUtils.isNotEmpty(waitUpdate)) {
                        stringRedisTemplate.opsForZSet().remove(recentlyClickKey, waitUpdate.stream().map(StatInfo::getId).distinct().toArray());
                    }

                    //逐条更新数据
                    updateFactor(advertId, packageId, dimClickTimesMap, feeMap, cvrMap, isToday, waitUpdate);

                }

            } catch (Exception e) {
                logger.error("click handle error,advertId:{} packageId:{}", advertId, packageId);
                throw new RecommendEngineException("click handle error", e);
            } finally {
                stringRedisTemplate.delete(lockKey);
            }
        }
    }

    private void updateFactor(String advertId, String packageId, Map<String, Long> dimClickTimesMap, Map<String,
            Long> feeMap, Map<String, Long> cvrMap, Map<String, Boolean> isToday, List<StatInfo> waitUpdate) {
        for (StatInfo statInfo : waitUpdate) {

            String key = statInfo.getId();

            String mongoId = getKey(advertId, packageId, key);


            Long cvr = cvrMap.get(key);
            cvr = cvr == null ? 0 : cvr;

            Long fee = feeMap.get(key);
            fee = fee == null ? 0 : fee;

            Long click = dimClickTimesMap.get(key);
            click = click == null ? 0 : click;


            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(mongoId));

            Update update = new Update();
            update.set("advertId", advertId);
            update.set("packageId", packageId);
            update.set("cvr", cvr);
            update.set("fee", fee);
            update.set("click", click);
            Double factor = statInfo.getFactor();
            if (factor.isNaN()) {
                factor = 1D;
            }
            update.set("factor", factor);

            Boolean today = isToday.get(mongoId);
            //两种情况需要更新/设置创建时间：1.新数据  2.前一天数据
            if (today == null || !today) {
                update.set("gmtCreate", new Date());
            }

            update.set("gmtModified", new Date());
            update.setOnInsert("expireAt", getNextMorningTime());
            mongoTemplate.upsert(query, update, MongoCollectionConstant.CPA_FACTOR_COLLECTION);

            //调价因子单独写入redis，提高实时计算效率
            String redisKey = RedisKeyUtil.factorKey(Long.parseLong(advertId), Long.parseLong(packageId), key);
            nezhaStringRedisTemplate.opsForValue().set(redisKey, String.valueOf(factor), 1, TimeUnit.DAYS);

            logger.info("trigger pid,id:{} advertId:{} packageId:{} factor:{} feeSum:{} cvrSum:{}",
                    mongoId, advertId, packageId, statInfo.getFactor(), fee, cvr);
        }
    }

    private void spinLock(String lockKey) {
        while (!stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "")) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                logger.error("get redis roi click lock failure", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //在消息处理器中注册
        RocketMqMessageListener.registerCallback(this);
    }


    private String getKey(String advertId, String packageId, String suffix) {
        Joiner joiner = Joiner.on("_");
        return joiner.join(advertId, packageId, suffix);
    }


    /**
     * 获取第二天零点的时间
     *
     * @return
     */
    private Date getNextMorningTime() {


        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        LocalDateTime morning = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDayOfMonth(),
                0, 0, 0);

        return Date.from(morning.atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 获取不同维度的直接父节点的调价因子，如果不存在父节点，则返回1
     *
     * @param dimKeys
     * @return
     */
    private Map<String, Double> getSuperNodeFactor(String advertId, String packageId, Collection<String> dimKeys) {
        Map<String, Double> superFactorMap = new HashMap<>(dimKeys.size());
        Multimap<String, String> superKeyMap = LinkedListMultimap.create();
        Set<String> allSuperKeys = new HashSet<>();


        dimKeys.stream().filter(StringUtils::isNotBlank).forEach(dimKey -> {

            String defaultKey = getKey(advertId, packageId, RoiHashKeyUtil.getDefault());

            if (defaultKey.equals(dimKey)) {
                //默认维度不存在父节点
                superFactorMap.put(dimKey, 1D);
                return;
            }

            //往superKeyMap里添加父节点key的顺序不可修改，排在前面的节点优选被选择
            if (dimKey.contains(RoiHashKeyUtil.ACTIVITY)) {
                //活动维度父节点为APP和全局

                String appId = dimKey.substring(RoiHashKeyUtil.ACTIVITY.length() + 1).split("_")[0];
                String appKey = getKey(advertId, packageId, RoiHashKeyUtil.getAppKey(appId));

                allSuperKeys.add(appKey);
                superKeyMap.put(dimKey, appKey);
            }

            //所有维度的最粗粒的的父节点均为全局
            allSuperKeys.add(defaultKey);
            superKeyMap.put(dimKey, defaultKey);

        });

        List<CpaFactorEntity> factors = mongoTemplate.find(Query.query(new Criteria("_id").in(allSuperKeys)),
                CpaFactorEntity.class, MongoCollectionConstant.CPA_FACTOR_COLLECTION);

        ImmutableMap<String, CpaFactorEntity> factorMap = Maps.uniqueIndex(factors,CpaFactorEntity::getId);

        //遍历所有待查询的key列表，获取其最近父节点的factor
        Set<Map.Entry<String, Collection<String>>> entries = superKeyMap.asMap().entrySet();
        for (Map.Entry<String, Collection<String>> entry : entries) {

            String dimKey = entry.getKey();
            Collection<String> keys = entry.getValue();

            Double factor = null;

            for (String superKey : keys) {

                CpaFactorEntity entity = factorMap.get(superKey);
                if (entity == null || entity.getFactor() == null || entity.getFactor().isNaN()) {
                    continue;
                }
                factor = entity.getFactor();
                //找到第一个合法的factor后结束循环
                break;
            }

            superFactorMap.put(dimKey, factor != null ? factor : 1D);

        }

        return superFactorMap;
    }

    /**
     * 获取统计数据
     * 
     * @param advertId
     * @param dimKeys
     * @return
     */
    private Map<String, AdvertStatDo> getStatData(Long advertId, Collection<String> dimKeys) {

        Set<Long> appIds = new HashSet<>();

        Splitter splitter = Splitter.on("_").omitEmptyStrings().trimResults();

        Map<String, Long> keyAppMap = new HashMap<>(dimKeys.size());

        for (String key : dimKeys) {

            Long appId = null;

            if (key.contains(RoiHashKeyUtil.APP)) {
                // app维度取对应的appid，否则为null
                Iterable<String> split = splitter.split(key);

                List<String> strings = new ArrayList<>();

                for (String s : split) {
                    strings.add(s);
                }

                if (strings.size() == 2) {
                    appId = Long.parseLong(strings.get(1));
                    appIds.add(appId);
                }
            }

            keyAppMap.put(key, appId);

        }

        if (appIds.isEmpty()) {
            //如果app列表为空，则添加一个不存在的appid，保证查询时能查出广告全局的统计数据
            appIds.add(-1L);
        }

        // 读取数据
        Map<Long, AdvertStatDo> appId2StatMap = advertStatAssociationBo.get7DayStat(appIds, advertId, null);

        if (MapUtils.isEmpty(appId2StatMap)) {
            return MapUtils.EMPTY_MAP;
        }

        Map<String, AdvertStatDo> record = new HashMap<>(dimKeys.size());
        for (String dimKey : dimKeys) {
            record.put(dimKey, appId2StatMap.get(keyAppMap.get(dimKey)));
        }
        return record;

    }

}
