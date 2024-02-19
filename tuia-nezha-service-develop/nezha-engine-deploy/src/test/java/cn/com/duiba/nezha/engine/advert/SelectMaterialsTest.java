package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.BaseTest;
import cn.com.duiba.nezha.engine.biz.domain.FeatureIndex;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.service.advert.AbstractAdvertRecommendService;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SelectMaterialsTest extends BaseTest {

    @Autowired
    private AbstractAdvertRecommendService interactAdvertRecommendService;

    @Autowired
    private StringRedisTemplate nezhaStringRedisTemplate;


    @Before
    public void dataPrepare() {
        String key = RedisKeyUtil.materialRankList(1L, 2L);
        String value = JSON.toJSONString(Lists.newArrayList(22L, 23L, 21L));
        nezhaStringRedisTemplate.opsForValue().set(key, value);
    }

    @Test
    public void test() {

        for (int i = 0; i < 100; i++) {
            for (RecommendMaterialType recommendMaterialType : RecommendMaterialType.values()) {
                Collection<Advert> advert = CommendData.getAdvert();
                Map<FeatureIndex, Double> ctrMap = this.ctrMap();
                Map<FeatureIndex, Double> cvrMap = this.cvrMap();
                interactAdvertRecommendService.handleTestMaterial(advert, 1L, recommendMaterialType, ctrMap, cvrMap);

                if (recommendMaterialType.equals(RecommendMaterialType.PREDICT)) {
                    assert advert.stream()
                            .filter(advert1 -> advert1.getId().equals(2L))
                            .findAny()
                            .get()
                            .getOrientationPackages()
                            .stream()
                            .filter(orientationPackage -> orientationPackage.getId().equals(22L))
                            .findAny()
                            .get()
                            .getMaterials()
                            .stream()
                            .findAny()
                            .get()
                            .getId()
                            .equals(21L);

                    assert advert.stream()
                            .filter(advert1 -> advert1.getId().equals(2L))
                            .findAny()
                            .get()
                            .getOrientationPackages()
                            .stream()
                            .filter(orientationPackage -> orientationPackage.getId().equals(23L))
                            .findAny()
                            .get()
                            .getMaterials()
                            .stream()
                            .findAny()
                            .get()
                            .getId()
                            .equals(22L);
                }
                if (recommendMaterialType.equals(RecommendMaterialType.STATIC)) {
                    Advert advert2 = advert.stream().filter(advert1 -> advert1.getId().equals(2L)).findAny().get();
                    System.out.println(advert2.getOrientationPackages()
                            .stream()
                            .filter(orientationPackage -> orientationPackage.getId().equals(22L))
                            .findAny()
                            .get()
                            .getMaterials()
                            .stream()
                            .findAny()
                            .get()
                            .getId());
                    System.out.println(advert2.getOrientationPackages()
                            .stream()
                            .filter(orientationPackage -> orientationPackage.getId().equals(23L))
                            .findAny()
                            .get()
                            .getMaterials()
                            .stream()
                            .findAny()
                            .get()
                            .getId());
                }
            }
        }
    }




    private Map<FeatureIndex, Double> ctrMap() {
        HashMap<FeatureIndex, Double> ctrMap = new HashMap<>();


        FeatureIndex featureIndex111 = FeatureIndex.newBuilder().advertId(1L).materialId(111L).build();
        FeatureIndex featureIndex112 = FeatureIndex.newBuilder().advertId(1L).materialId(112L).build();

        ctrMap.put(featureIndex111, 0.6);
        ctrMap.put(featureIndex112, 0.7);

        FeatureIndex featureIndex21 = FeatureIndex.newBuilder().advertId(2L).materialId(21L).build();
        FeatureIndex featureIndex22 = FeatureIndex.newBuilder().advertId(2L).materialId(22L).build();
        FeatureIndex featureIndex23 = FeatureIndex.newBuilder().advertId(2L).materialId(23L).build();

        ctrMap.put(featureIndex21, 0.6);
        ctrMap.put(featureIndex22, 0.6);
        ctrMap.put(featureIndex23, 0.3);

        return ctrMap;
    }

    private Map<FeatureIndex, Double> cvrMap() {
        HashMap<FeatureIndex, Double> cvrMap = new HashMap<>();

        FeatureIndex featureIndex21 = FeatureIndex.newBuilder().advertId(2L).materialId(21L).build();
        FeatureIndex featureIndex22 = FeatureIndex.newBuilder().advertId(2L).materialId(22L).build();
        FeatureIndex featureIndex23 = FeatureIndex.newBuilder().advertId(2L).materialId(23L).build();

        cvrMap.put(featureIndex21, 0.3);
        cvrMap.put(featureIndex22, 0.6);
        cvrMap.put(featureIndex23, 0.7);

        return cvrMap;
    }


}
