package cn.com.duiba.nezha.engine.biz.remoteservice.impl.advert;

import cn.com.duiba.nezha.alg.alg.vo.StatDo;
import cn.com.duiba.nezha.engine.api.enums.ChargeTypeEnum;
import cn.com.duiba.nezha.engine.api.remoteservice.advert.RemoteAdvertPresentCtrService;
import cn.com.duiba.nezha.engine.biz.constant.GlobalConstant;
import cn.com.duiba.nezha.engine.biz.service.advert.ctr.AdvertMergeStatService;
import cn.com.duiba.wolf.dubbo.DubboResult;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RemoteAdvertPresentCtrServiceImpl.java , v 0.1 2017/6/21 下午3:10 ZhouFeng Exp $
 */
@RestController
public class RemoteAdvertPresentCtrServiceImpl implements RemoteAdvertPresentCtrService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteAdvertPresentCtrServiceImpl.class);

    @Autowired
    private AdvertMergeStatService advertMergeStatService;


    @Override
    public DubboResult<Map<Long, Double>> queryWithChargeType(Long advertId, Set<Long> appIds, Integer chargeType) {

        try {

            Map<Long, Double> map = new HashMap<>(appIds.size());

            List<StatDo> advertStatistic = advertMergeStatService.get(advertId, appIds);
            ImmutableMap<Long, StatDo> appIdMap = Maps.uniqueIndex(advertStatistic, StatDo::getAppId);

            for (Long appId : appIds) {

                // 融合CTR
                Double statCtr = Optional.ofNullable(appIdMap.get(appId).getCtr()).orElse(GlobalConstant
                        .INTERACT_DEFAULT_CTR);

                if (chargeType != null && chargeType.equals(ChargeTypeEnum.CPA.getValue())) {

                    //融合CVR
                    Double statCvr = Optional.ofNullable(appIdMap.get(appId).getCvr()).orElse(GlobalConstant
                            .INTERACT_DEFAULT_CVR);

                    statCtr = statCtr * statCvr;
                }

                map.put(appId, statCtr);

            }


            return DubboResult.successResult(map);
        } catch (Exception e) {
            logger.error("query present ctr failure,advertId:{},appIds:{},chargeType:{}", advertId, JSON.toJSONString
                    (appIds), chargeType, e);
            return DubboResult.failResult("query present ctr failure:" + e.getMessage());
        }

    }
}
