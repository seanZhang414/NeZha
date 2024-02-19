package cn.com.duiba.nezha.engine.api.remoteservice.advert.fallback;

import cn.com.duiba.nezha.engine.api.dto.RcmdAdvertDto;
import cn.com.duiba.nezha.engine.api.dto.ReqAdvertNewDto;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertRecommendService;
import cn.com.duiba.wolf.dubbo.DubboResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertRecommendServiceFallback.java , v 0.1 2017/11/17 下午3:25 ZhouFeng Exp $
 */
public class RemoteAdvertRecommendServiceFallback implements RemoteAdvertRecommendService {
    @Override
    public DubboResult<RcmdAdvertDto> recommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId) {
        DubboResult<RcmdAdvertDto> result = new DubboResult<>();
        result.setSuccess(false);
        result.setMsg("fallback");
        return result;
    }

    @Override
    public List<RcmdAdvertDto> batchRecommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId) {
        return new ArrayList<>();
    }
}
