package cn.com.duiba.nezha.engine.api.remoteservice.advert;

import cn.com.duiba.boot.cat.CatWithArgs;
import cn.com.duiba.boot.netflix.feign.AdvancedFeignClient;
import cn.com.duiba.nezha.engine.api.dto.RcmdAdvertDto;
import cn.com.duiba.nezha.engine.api.dto.ReqAdvertNewDto;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.fallback.RemoteAdvertRecommendServiceFallback;
import cn.com.duiba.wolf.dubbo.DubboResult;

import java.util.List;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertRecommendService.java , v 0.1 2017/11/6 下午4:50 ZhouFeng Exp $
 */

@AdvancedFeignClient(fallback = RemoteAdvertRecommendServiceFallback.class)
public interface RemoteAdvertRecommendService {

    /**
     * 广告统一推荐接口
     *
     * @param reqAdvertNewDto 推荐请求参数
     * @param strategyId      策略ID
     * @return 推荐结果
     */
    @CatWithArgs(argIndexes = 1)
    DubboResult<RcmdAdvertDto> recommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId);

    /**
     * 批量推荐广告
     *
     * @param reqAdvertNewDto 推荐请求参数
     * @param strategyId      策略ID
     * @return 批量广告
     */
    @CatWithArgs(argIndexes = 1)
    List<RcmdAdvertDto> batchRecommend(ReqAdvertNewDto reqAdvertNewDto, String strategyId);

}
