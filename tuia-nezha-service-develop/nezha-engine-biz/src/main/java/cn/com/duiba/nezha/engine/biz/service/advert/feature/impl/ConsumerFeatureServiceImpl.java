package cn.com.duiba.nezha.engine.biz.service.advert.feature.impl;

import cn.com.duiba.nezha.engine.biz.domain.ConsumerFeatureDO;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ConsumerFeatureService;
import cn.com.duiba.nezha.engine.common.utils.HBaseResultCreater;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConsumerFeatureServiceImpl.java , v 0.1 2017/10/27 下午5:37 ZhouFeng Exp $
 */
@Service
public class ConsumerFeatureServiceImpl implements ConsumerFeatureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerFeatureServiceImpl.class);

    private static final String            TABLE_NAME          = "consumer_order_feature";

    private static final byte[]            FAMILY              = "cf".getBytes();

    private static final String            DATE_PATTERN        = "yyyy-MM-dd";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final Long            DEFAULT_ORDER_COUNT = 0L;

    private static final Map<String, List<String>> staticQueries = ImmutableMap.of("cf",
                                                                                         Lists.newArrayList("order_cnt",
                                                                                                            "first_order_time",
                                                                                                            "last_order_id",
                                                                                                            "last_order_time",
                                                                                                            "is_billing",
                                                                                                            "last_activity_id"));
    @Autowired
    private HbaseTemplate                  hbaseTemplate;

    @Override
    public Map<String, ConsumerFeatureDO> getFeatures(List<String> rowKeys) {

        // 去重
        List<String> keys = rowKeys.stream().distinct().collect(Collectors.toList());

        Map<String, ConsumerFeatureDO> record = new HashMap<>(keys.size());
        try {
            DBTimeProfile.enter("ConsumerFeatureService.getFeatures");

            return hbaseTemplate.execute(TABLE_NAME, table -> {

                byte[] currentDayColumnName = LocalDate.now().format(DATE_TIME_FORMATTER).getBytes();

                List<Get> gets = keys.stream().map(rowKey -> {
                    Get get = new Get(rowKey.getBytes());

                    for (Map.Entry<String, List<String>> entry : staticQueries.entrySet()) {
                        byte[] family = entry.getKey().getBytes();
                        List<String> qualifiers = entry.getValue();

                        for (String qualifier : qualifiers) {
                            get.addColumn(family, qualifier.getBytes());
                        }
                    }

                    // 当前日期字段，在不同时间查询的字段将会发生变化
                    get.addColumn(FAMILY, currentDayColumnName);

                    return get;

                }).collect(Collectors.toList());

                DBTimeProfile.enter("getFeaturesFromHBase");
                Result[] results = table.get(gets);
                DBTimeProfile.release();

                for (int i = 0; i < results.length; i++) {
                    Result result = results[i];

                    String rowKey = keys.get(i);

                    HBaseResultCreater.of(result, ConsumerFeatureDO.class).build().ifPresent(feature->{
                        Long currentDayOrderCount = Optional.ofNullable(getValue(result, FAMILY, currentDayColumnName))
                                .orElse(DEFAULT_ORDER_COUNT);

                        feature.setCurrentDayOrderCount(currentDayOrderCount);

                        record.put(rowKey, feature);
                    });
                }
                return record;
            });
        } catch (Exception e) {
            LOGGER.error("query consumer feature error:{} ", e);
        } finally {
            DBTimeProfile.release();
        }

        return record;

    }

    private Long getValue(Result result, byte[] family, byte[] column) {
        if (result == null) {
            return null;
        }
        byte[] value = result.getValue(family, column);

        if (value == null || value.length == 0) {
            return null;
        }
        return Bytes.toLong(value);

    }
}
