package cn.com.duiba.nezha.engine.biz.service.advert.ctr;

import cn.com.duiba.nezha.engine.api.enums.ResultCodeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.bo.advert.AdvertMaterialRcmdCtrStatBo;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertMaterialRcmdCtrStatEntity;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/10/18.
 */
@Service
public class AdvertMaterialCtrByStatService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialCtrByStatService.class);
    @Autowired
    AdvertMaterialRcmdCtrStatBo advertMaterialRcmdCtrStatBo;

    public List<AdvertMaterialRcmdCtrStatEntity> getMaterialList(long appId, long advertId, List<Long> oldMaterialIdList, List<Long> newMaterialIdList) {

        List<AdvertMaterialRcmdCtrStatEntity> advertMaterialRcmdCtrStatEntityList = new ArrayList<>();

        // 参数判断
        if (AssertUtil.isAllEmpty(oldMaterialIdList, newMaterialIdList)) {
            logger.warn("getMaterialList param cheak invalid", ResultCodeEnum.PARAMS_INVALID.getDesc());
            return advertMaterialRcmdCtrStatEntityList;
        }


        try {

            List<Long> materialIdList = new ArrayList<>();
            if (AssertUtil.isNotEmpty(oldMaterialIdList)) {
                materialIdList.addAll(oldMaterialIdList);
            }

            if (AssertUtil.isNotEmpty(newMaterialIdList)) {
                materialIdList.addAll(newMaterialIdList);
            }
            advertMaterialRcmdCtrStatEntityList = advertMaterialRcmdCtrStatBo.getMaterials(appId,advertId,materialIdList);

        } catch (Exception e) {
            logger.error("getAdvertStat happen error:{}", e);
            throw new RecommendEngineException("getAdvertStat happen error", e);
        }

        return advertMaterialRcmdCtrStatEntityList;


    }


}
