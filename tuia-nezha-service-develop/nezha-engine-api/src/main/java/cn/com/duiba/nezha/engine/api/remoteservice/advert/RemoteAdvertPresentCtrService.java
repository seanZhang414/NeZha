package cn.com.duiba.nezha.engine.api.remoteservice.advert;

import cn.com.duiba.boot.netflix.feign.AdvancedFeignClient;
import cn.com.duiba.wolf.dubbo.DubboResult;

import java.util.Map;
import java.util.Set;

/**
 * 广告当天ctr
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertPresentCtrService.java , v 0.1 2017/6/21 下午3:06 ZhouFeng Exp $
 */
@AdvancedFeignClient
public interface RemoteAdvertPresentCtrService {


    /**
     * 查询广告在不同app下的ctr值
     *
     * @param advertId   广告ID
     * @param appIds     app id列表
     * @param chargeType 计费类型 1.CPC 2.CPA
     * @return key:appId,value:ctr
     */
    DubboResult<Map<Long, Double>> queryWithChargeType(Long advertId, Set<Long> appIds, Integer chargeType);

}