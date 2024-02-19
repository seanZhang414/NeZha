package cn.com.duiba.nezha.engine.api.remoteservice.advert;

import cn.com.duiba.boot.netflix.feign.AdvancedFeignClient;
import cn.com.duiba.nezha.engine.api.dto.ReqAdvertMaterialDto;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.fallback.RemoteAdvertMaterialRecommendServiceFallback;
import cn.com.duiba.wolf.dubbo.DubboResult;

/**
 * lwj
 * Created by lwj on 16/8/20.
 */
@AdvancedFeignClient(fallback = RemoteAdvertMaterialRecommendServiceFallback.class)
public interface RemoteAdvertMaterialRecommendService {

    /**
     * 广告素材推荐
     *
     * @param reqAdvertMaterialDto 请求对象
     *
     * @return 广告素材对象
     */
    DubboResult<Long> recommendMaterial(ReqAdvertMaterialDto reqAdvertMaterialDto);

}
