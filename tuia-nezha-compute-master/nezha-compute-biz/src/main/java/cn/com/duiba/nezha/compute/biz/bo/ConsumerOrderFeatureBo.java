package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.FeatureKey;
import cn.com.duiba.nezha.compute.api.constant.GlobalConstant;
import cn.com.duiba.nezha.compute.api.dto.ConsumerOrderFeatureDto;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.constant.htable.ConsumerOrderFeatureConstant;
import cn.com.duiba.nezha.compute.biz.utils.hbase.HbaseUtil;
import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import cn.com.duiba.nezha.compute.biz.vo.OrderFeatureSyncVo;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import cn.com.duiba.nezha.compute.biz.utils.es.ElasticSearchUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;

public class ConsumerOrderFeatureBo {


    private static String tableName = ConsumerOrderFeatureConstant.TABLE_NAME;
    private static String index = GlobalConstant.CONSUMER_FEATURE_ES_INDEX;
    private static String type = GlobalConstant.CONSUMER_FEATURE_ES_TYPE;

//    public static ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);


//    public static MongoClientUtil mongoClientUtil = new MongoClientUtil(MongoDbConf.config);


    public void syncES(List<OrderFeatureSyncVo> orderFeatureSyncVoList,String biz) {

        Map<String, ConsumerOrderFeatureDto> mainKeyMaps = new HashMap<>();
        Map<String, ConsumerOrderFeatureDto> dateKeyMaps = new HashMap<>();
        // 同步
        if (orderFeatureSyncVoList != null) {

            for (OrderFeatureSyncVo orderFeatureSyncVo : orderFeatureSyncVoList) {
                String consumerId = orderFeatureSyncVo.getConsumerId();
                String activityDimId = orderFeatureSyncVo.getactivityDimId();
                String gmtDate = orderFeatureSyncVo.getGmtDate();
                ConsumerOrderFeatureDto dto = orderFeatureSyncVo.getConsumerOrderFeatureDto();

                String mainKey = FeatureKey.getConsumerOrderFeatureRedisKey(
                        consumerId,
                        activityDimId);

                String dateKey = FeatureKey.getConsumerOrderFeatureRedisDateKey(
                        consumerId,
                        activityDimId,
                        gmtDate);

                ConsumerOrderFeatureDto cftdto = getHSetMap(consumerId, activityDimId, gmtDate, dto,mainKey);


                ConsumerOrderFeatureDto cfddto = new ConsumerOrderFeatureDto();
                cfddto.setKey(dateKey);
                cfddto.setDayOrderRank(longStringStd(dto.getDayOrderRank()));
                mainKeyMaps.put(mainKey, cftdto);
                dateKeyMaps.put(dateKey, cfddto);

            }
        }

//        elasticSearchUtil.multiUpsertT(index, GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKeyMaps, 200, ProjectConstant.WEEK_2_EXPIRE);
//        mongoClientUtil.bulkWriteUpdateT(MongoDbConf.config.getDatabaseName(), GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKeyMaps);
        MongoUtil.bulkWriteUpdateT(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, mainKeyMaps,biz);


//        elasticSearchUtil.multiUpsertT(index, GlobalConstant.CONSUMER_FEATURE_ES_TYPE, dateKeyMaps, 200, ProjectConstant.DAY_2_EXPIRE);
//        mongoClientUtil.bulkWriteUpdateT(MongoDbConf.config.getDatabaseName(), GlobalConstant.CONSUMER_FEATURE_ES_TYPE, dateKeyMaps);
        MongoUtil.bulkWriteUpdateT(GlobalConstant.CONSUMER_FEATURE_ES_TYPE, dateKeyMaps,biz);


    }

    public ConsumerOrderFeatureDto getHSetMap(String consumerId, String activityDimId, String gmtDate, ConsumerOrderFeatureDto dto, String key) {

        // 同步

        ConsumerOrderFeatureDto ret = new ConsumerOrderFeatureDto();
        ret.setFirstOrderTime(dto.getFirstOrderTime());

        ret.setLastActivityId(dto.getLastActivityId());

        ret.setLastOrderChargeNums(longStringStd(dto.getLastOrderChargeNums()));

        ret.setLastOrderId(dto.getLastOrderId());

        ret.setLastOrderTime(dto.getLastOrderTime());

        ret.setOrderRank(longStringStd(dto.getOrderRank()));

        ret.setKey(key);

        return ret;
    }


    /**
     * 插入首单信息
     *
     * @param consumerId
     * @param activityId
     * @param gmtTime
     * @throws Exception
     */
    public void insertFirstOrderInfo(String consumerId, String activityId, String gmtTime) throws Exception {

        if (consumerId != null && gmtTime != null) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                    consumerId, activityId);

            String familyName = ConsumerOrderFeatureConstant.FM_INFO;
            String col = ConsumerOrderFeatureConstant.FM_INFO_COL_FIRST_ORDER_TIME;

            //
            hbaseUtil.insert(tableName, rowKey, familyName, col, gmtTime);

        }

    }

    /**
     * 插入最近活动信息
     *
     * @param consumerId
     * @param activityId
     * @param lastActivityId
     * @throws Exception
     */
    public void insertLastActivityInfo(String consumerId, String activityId, String lastActivityId) throws Exception {

        if (consumerId != null && lastActivityId != null) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                    consumerId, activityId);

            String familyName = ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY;
            String col = ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY_COL_ID;


            hbaseUtil.insert(tableName, rowKey, familyName, col, lastActivityId);


        }

    }


    /**
     * 插入最后一单
     *
     * @param consumerId
     * @param activityId
     * @param lastOrderChargeNums
     * @param lastOrderId
     * @param lastOrderGmtTime
     * @throws Exception
     */
    public void insertLastOrderInfo(String consumerId,
                                    String activityId,
                                    String lastOrderChargeNums,
                                    String lastOrderId,
                                    String lastOrderGmtTime
    ) throws Exception {

        if (consumerId != null) {


            HbaseUtil hbaseUtil = HbaseUtil.getInstance();


            Map<String, Map<String, String>> rowMap = new HashMap<>();

            String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                    consumerId, activityId);

            Map<String, Map<String, String>> fvMap = new HashMap<>();

            String lastOrderFamilyName = ConsumerOrderFeatureConstant.FM_LAST_ORDER;
            Map<String, String> lastOrderColValMap = new HashMap<>();

            lastOrderColValMap.put(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_ID, lastOrderId);
            lastOrderColValMap.put(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_TIME, lastOrderGmtTime);
            lastOrderColValMap.put(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_CHARGE_NUMS, longStringStd(lastOrderChargeNums));

            fvMap.put(lastOrderFamilyName, lastOrderColValMap);

            //
            hbaseUtil.insert(tableName, rowKey, fvMap);

        }

    }


    /**
     * 订单序列计数自增
     *
     * @param consumerId
     * @param activityId
     * @param gmtDate
     * @throws Exception
     */
    public void incrementOrderRank(String consumerId, String activityId, String gmtDate) throws Exception {

        if (consumerId != null && gmtDate != null) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                    consumerId, activityId);

            String familyName = ConsumerOrderFeatureConstant.FM_RANK;

            Map<String, Map<String, Long>> fcvMap = new HashMap<>();
            // 1
            Map<String, Long> colValMap = new HashMap<>();
            colValMap.put(ConsumerOrderFeatureConstant.FM_RANK_COL_GLOBAL, 1L);
            colValMap.put(gmtDate, 1L);
            fcvMap.put(familyName, colValMap);

            //
            hbaseUtil.incrementColumeValues(tableName, rowKey, fcvMap);

        }

    }


    /**
     * 订单计费计数自增
     *
     * @param consumerId
     * @param orderId
     * @throws Exception
     */
    public void incrementOrderChargeNums(String consumerId, String orderId) throws Exception {

        if (consumerId != null && orderId != null) {

            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                    consumerId, null);

            hbaseUtil.incrementColumeValue(tableName,
                    rowKey,
                    ConsumerOrderFeatureConstant.FM_CHARGE,
                    orderId, 1L);
        }

    }


    public String getOrderChargeNums(final String consumerId, final String orderId) throws Exception {

        String ret = null;
        final List<String> retLongList = new ArrayList<>();
        //
        if (consumerId == null || orderId == null) {
            return null;
        }


        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        final String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(
                consumerId, null);
        final String familyName = ConsumerOrderFeatureConstant.FM_CHARGE;
        String columnName = orderId;
        // 数据读取
        hbaseUtil.getOneRow(tableName, rowKey, familyName, columnName, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {
                if (retList != null) {
                    Result ret = retList.get(0);
                    byte[] orderChargeNumsB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_CHARGE),
                            Bytes.toBytes(orderId));
                    String orderChargeNums = MyStringUtil2.bytesToLongString(orderChargeNumsB);

                    retLongList.add(orderChargeNums);
                }
            }

        });

        if (retLongList.size() > 0) {
            ret = retLongList.get(0);
        }
        return ret;
    }


    public ConsumerOrderFeatureDto getConsumerOrderFeature(final String consumerId,
                                                           final String activityId,
                                                           final String gmtDate,
                                                           final String gmtTime,
                                                           final String gmtCurrentTime

    ) throws Exception {

        final ConsumerOrderFeatureDto consumerOrderFeatureDto = new ConsumerOrderFeatureDto();

        //
        if (AssertUtil.isAnyEmpty(consumerId, gmtDate)) {
            return consumerOrderFeatureDto;
        }


        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        String rowKey = FeatureKey.getConsumerOrderFeatureRowKey(consumerId, activityId);

        // 封装请求Map
        Map<String, Set<String>> fcMap = new HashMap<>();

        // 0 family info
        Set<String> fInfoSet = new HashSet();
        fInfoSet.add(ConsumerOrderFeatureConstant.FM_INFO_COL_FIRST_ORDER_TIME);
        fcMap.put(ConsumerOrderFeatureConstant.FM_INFO, fInfoSet);

        // 1 family rank
        Set<String> fRankSet = new HashSet();
        fRankSet.add(ConsumerOrderFeatureConstant.FM_RANK_COL_GLOBAL);
        fRankSet.add(gmtDate);
        fcMap.put(ConsumerOrderFeatureConstant.FM_RANK, fRankSet);


        // 3 family lastorder
        Set<String> fLastOrderSet = new HashSet();
        fLastOrderSet.add(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_ID);
        fLastOrderSet.add(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_TIME);
        fLastOrderSet.add(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_CHARGE_NUMS);
        fcMap.put(ConsumerOrderFeatureConstant.FM_LAST_ORDER, fLastOrderSet);

        // 4 family last activity
        Set<String> fLastActivitySet = new HashSet();
        fLastActivitySet.add(ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY_COL_ID);

        fcMap.put(ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY, fLastActivitySet);

        // 数据读取
        hbaseUtil.getOneRow(tableName, rowKey, fcMap, new HbaseUtil.QueryCallback() {

            @Override
            public void process(List<Result> retList) throws Exception {

                if (retList != null) {

                    Result ret = retList.get(0);
                    consumerOrderFeatureDto.setConsumerId(consumerId);


                    byte[] firstOrderTimeB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_INFO),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_INFO_COL_FIRST_ORDER_TIME));

                    byte[] orderRankB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_RANK),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_RANK_COL_GLOBAL));

                    byte[] dayOrderRankB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_RANK),
                            Bytes.toBytes(gmtDate));


                    byte[] lastOrderTimeB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_TIME));

                    byte[] lastOrderIdB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_ID));

                    byte[] lastOrderChargeNumsB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ORDER_COL_CHARGE_NUMS));

                    byte[] lastActivityIdB = ret.getValue(
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY),
                            Bytes.toBytes(ConsumerOrderFeatureConstant.FM_LAST_ACTIVITY_COL_ID));


                    String orderRank = MyStringUtil2.bytesToLongString(orderRankB);
                    String dayOrderRank = MyStringUtil2.bytesToLongString(dayOrderRankB);

                    String firstOrderTime = Bytes.toString(firstOrderTimeB);

                    String lastOrderTime = Bytes.toString(lastOrderTimeB);
                    String lastOrderId = Bytes.toString(lastOrderIdB);
                    String lastOrderChargeNums = Bytes.toString(lastOrderChargeNumsB);

                    String lastActivityId = Bytes.toString(lastActivityIdB);

                    consumerOrderFeatureDto.setOrderRank(orderRank);
                    consumerOrderFeatureDto.setDayOrderRank(dayOrderRank);
                    consumerOrderFeatureDto.setFirstOrderTime(firstOrderTime);
                    consumerOrderFeatureDto.setLastOrderTime(lastOrderTime);
                    consumerOrderFeatureDto.setLastOrderId(lastOrderId);
                    consumerOrderFeatureDto.setLastOrderChargeNums(lastOrderChargeNums);
                    consumerOrderFeatureDto.setLastActivityId(lastActivityId);

                }

            }

        });
        return consumerOrderFeatureDto;
    }


    public static String longStringStd(String longStr) {
        if (longStr == null) {
            return Long.toString(0);
        } else {
            return longStr;
        }

    }
}
