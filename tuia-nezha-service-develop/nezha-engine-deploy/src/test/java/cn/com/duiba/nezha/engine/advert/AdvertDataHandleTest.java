package cn.com.duiba.nezha.engine.advert;

import cn.com.duiba.nezha.engine.api.dto.AdvertNewDto;
import cn.com.duiba.nezha.engine.biz.domain.advert.Advert;
import cn.com.duiba.nezha.engine.biz.domain.advert.OrientationPackage;
import cn.com.duiba.nezha.engine.biz.enums.RecommendMaterialType;
import cn.com.duiba.nezha.engine.biz.remoteservice.impl.advert.RemoteAdvertRecommendServiceImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdvertDataHandleTest {

    @Test
    public void test() {
        for (int i = 0; i < 2; i++) {
            Boolean invokeWeakFilter = i == 1;
            List<AdvertNewDto> advertNewDtos = CommendData.advertList();
            RemoteAdvertRecommendServiceImpl remoteAdvertRecommendService = new RemoteAdvertRecommendServiceImpl();
            HashMap<Long, Advert> advertMap = new HashMap<>();
            HashMap<Long, Long> timesMap = new HashMap<>();
            Set<OrientationPackage> advertOrientationPackages = new HashSet<>();
            remoteAdvertRecommendService.handleData(advertNewDtos, invokeWeakFilter, RecommendMaterialType.NONE, advertMap, timesMap, advertOrientationPackages);
            if (invokeWeakFilter) {
                assert advertMap.get(3L).getOrientationPackages().size() == 1;
            } else {
                assert advertMap.get(3L).getOrientationPackages().size() == 2;
            }
        }

    }
}
