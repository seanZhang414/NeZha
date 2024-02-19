package cn.com.duiba.nezha.engine.biz.service.advert.feature.impl;

import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.constant.MongoCollectionConstant;
import cn.com.duiba.nezha.engine.biz.domain.AppInstallDo;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AppInstallFeature;
import cn.com.duiba.nezha.engine.biz.service.advert.feature.ConsumerAppInstallService;
import cn.com.duiba.wolf.perf.timeprofile.DBTimeProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ConsumerAppInstallServiceImpl.java , v 0.1 2018/1/11 下午4:02 ZhouFeng Exp $
 */
@Service
public class ConsumerAppInstallServiceImpl implements ConsumerAppInstallService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerAppInstallServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    private LoadingCache<String, AppInstallDo> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, AppInstallDo>() {
                @Override
                public AppInstallDo load(String key) {
                    throw new UnsupportedOperationException("not support single query");
                }

                @Override
                public Map<String, AppInstallDo> loadAll(Iterable<? extends String> keys) {

                    DBTimeProfile.enter("queryInstalledApp");

                    List<AppInstallDo> record = mongoTemplate.find(
                            Query.query(Criteria.where("id").in(Lists.newArrayList(keys))),
                            AppInstallDo.class,
                            MongoCollectionConstant.APP_LIST);

                    DBTimeProfile.release();

                    Map<String, AppInstallDo> id2doMap = record.stream().collect(toMap(AppInstallDo::getId, Function.identity()));

                    return StreamSupport.stream(keys.spliterator(), false)
                            .collect(toMap(Function.identity(),
                                    id -> Optional.ofNullable(id2doMap.get(id)).orElse(new AppInstallDo())));
                }
            });

    @Override
    public AppInstallFeature getFeature(List<String> appNames) {

        if (CollectionUtils.isEmpty(appNames)) {
            return AppInstallFeature.DEFAULT;
        }

        try {
            DBTimeProfile.enter("ConsumerAppInstallService.getAppInstallFeature");

            ImmutableMap<String, AppInstallDo> record = cache.getAll(appNames);

            ImmutableCollection<AppInstallDo> datas = record.values();


            List<String> firstCategories = this.mapperToList(datas, AppInstallDo::getCategory1_id);
            Map<String, Long> firstCategoryCount = this.count(datas, AppInstallDo::getCategory1_id);

            List<String> secondCategories = this.mapperToList(datas, AppInstallDo::getCategory2_id);
            Map<String, Long> secondCategoryCount = this.count(datas, AppInstallDo::getCategory2_id);

            List<String> clusterIdList = this.mapperToList(datas, AppInstallDo::getClass_id);

            List<String> importantAppList = datas.stream()
                    .filter(AppInstallDo::isImportant)
                    .map(AppInstallDo::getId)
                    .collect(Collectors.toList());

            //只要其中有一个为游戏，则判断有游戏
            boolean hasGame = datas.stream().anyMatch(AppInstallDo::isGame);

            return new AppInstallFeature.Builder()
                    .firstCategory(firstCategories)
                    .secondCategory(secondCategories)
                    .clusterIdList(clusterIdList)
                    .importantAppList(importantAppList)
                    .firstCategoryCount(firstCategoryCount)
                    .secondCategoryCount(secondCategoryCount)
                    .hasGame(hasGame)
                    .build();

        } catch (Exception e) {
            LOGGER.error("query app install feature error :{}", e);
            return AppInstallFeature.DEFAULT;
        } finally {
            DBTimeProfile.release();
        }
    }

    private List<String> mapperToList(Collection<AppInstallDo> datas, Function<AppInstallDo, String> mapper) {
        return datas
                .stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(str -> GlobalConstant.SPLITTER.splitToList(str).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, Long> count(Collection<AppInstallDo> datas, Function<AppInstallDo, String> mapper) {
        return datas
                .stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(str -> GlobalConstant.SPLITTER.splitToList(str).stream())
                .collect(groupingBy(Function.identity(), counting()));
    }
}
