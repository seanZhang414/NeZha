package cn.com.duiba.nezha.engine.biz.service.advert.material;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface AdvertMaterialService {

    Map<Long, List<Long>> getMaterialRankList(Long appId, Collection<Long> advertIds);
}
