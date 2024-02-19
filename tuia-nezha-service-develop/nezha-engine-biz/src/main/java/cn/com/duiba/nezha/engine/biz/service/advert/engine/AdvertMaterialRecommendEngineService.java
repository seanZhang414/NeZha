package cn.com.duiba.nezha.engine.biz.service.advert.engine;

import cn.com.duiba.nezha.engine.api.dto.ReqAdvertMaterialDto;

/**
 * lwj
 * Created by lwj on 16/8/20.
 */
public interface AdvertMaterialRecommendEngineService {

    /**
     * 广告素材推荐
     *
     * @param reqAdvertMaterialDto 请求对象
     *
     * @return 广告素材对象
     */
    Long recommendMaterial(ReqAdvertMaterialDto reqAdvertMaterialDto);

}
