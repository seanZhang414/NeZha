package cn.com.duiba.nezha.engine.api.remoteservice.advert.fallback;

import cn.com.duiba.nezha.engine.api.dto.ReqAdvertMaterialDto;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertMaterialRecommendService;
import cn.com.duiba.wolf.dubbo.DubboResult;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertMaterialRecommendServiceFallback.java , v 0.1 2017/11/17 下午3:27 ZhouFeng Exp $
 */
public class RemoteAdvertMaterialRecommendServiceFallback implements RemoteAdvertMaterialRecommendService {
    @Override
    public DubboResult<Long> recommendMaterial(ReqAdvertMaterialDto reqAdvertMaterialDto) {
        DubboResult<Long> result = new DubboResult<>();
        result.setSuccess(false);
        result.setMsg("fallback");

        return result;
    }
}
