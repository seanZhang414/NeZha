package cn.com.duiba.nezha.engine.biz.remoteservice.impl.advert;


import cn.com.duiba.nezha.engine.api.dto.ReqAdvertMaterialDto;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertMaterialRecommendService;
import cn.com.duiba.nezha.engine.biz.service.advert.engine.impl.AdvertMaterialRecommendEngineServiceImpl;
import cn.com.duiba.wolf.dubbo.DubboResult;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by xuezhaoming on 16/8/2.
 */
@RestController
public class RemoteAdvertMaterialRecommendServiceImpl implements RemoteAdvertMaterialRecommendService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteAdvertMaterialRecommendServiceImpl.class);

    @Autowired
    AdvertMaterialRecommendEngineServiceImpl advertMaterialRecommendService;

    /**
     * 广告素材推荐
     *
     * @param reqAdvertMaterialDto 请求对象
     * @return 广告素材对象
     */
    @Override
    public DubboResult<Long> recommendMaterial(ReqAdvertMaterialDto reqAdvertMaterialDto) {
        try {
            Preconditions.checkNotNull(reqAdvertMaterialDto, "reqAdvertMaterialDto is null!");
            Long materialId = advertMaterialRecommendService.recommendMaterial(reqAdvertMaterialDto);
            return DubboResult.successResult(materialId);
        } catch (Exception e) {
            logger.error("recommendMaterial happened error :{}", e);
            return DubboResult.failResult("recommendMaterial happend error " + e.getMessage());
        }
    }

}
