package cn.com.duiba.nezha.engine.biz.service.advert.merge;



import cn.com.duiba.nezha.engine.api.enums.AdvertMaterialGroupTypeEnum;
import cn.com.duiba.nezha.engine.api.enums.ResultCodeEnum;
import cn.com.duiba.nezha.engine.api.support.RecommendEngineException;
import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.AdvertMaterialRcmdCtrStatEntity;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortGroupVo;
import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortVo;
import cn.com.duiba.nezha.engine.common.utils.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by pc on 2016/12/22.
 */
@Service
public class AdvertMaterialMergeService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertMaterialMergeService.class);


    public List<AdvertMaterialResortGroupVo> getMergeMaterialMap(List<AdvertMaterialRcmdCtrStatEntity> entityList,
                                                                 List<Long> oldMaterialIdList,
                                                                 List<Long> newMaterialIdList) {

        // 0 初始化返回结果
        List<AdvertMaterialResortGroupVo> advertMaterialResortGroupVoList = new ArrayList<>();

        // 1 参数检验
        if (AssertUtil.isAllEmpty(oldMaterialIdList, newMaterialIdList)) {
            logger.warn("getMergeMaterialMap param cheak invalid", ResultCodeEnum.PARAMS_INVALID.getDesc());
            return advertMaterialResortGroupVoList;
        }

        try {

            // 2 解析CTR信息

            Map<Long, AdvertMaterialRcmdCtrStatEntity> materialCRTMap = new HashMap<>();
            if (AssertUtil.isNotEmpty(entityList)) {
                for (AdvertMaterialRcmdCtrStatEntity entity : entityList) {
                    materialCRTMap.put(entity.getMaterialId(), entity);
                }
            }

            // 3 分组
            List<AdvertMaterialResortVo> groupWithWeight = new ArrayList<>();
            List<AdvertMaterialResortVo> groupWithoutWeight = new ArrayList<>();

            // 遍历老素材列表
            if (AssertUtil.isNotEmpty(oldMaterialIdList)) {
                traverseOldMaterials(oldMaterialIdList, materialCRTMap, groupWithWeight, groupWithoutWeight);
            }

            // 遍历新素材列表
            if (AssertUtil.isNotEmpty(newMaterialIdList)) {
                traverseNewMaterials(newMaterialIdList, groupWithoutWeight);
            }

            // 4 封装结果列表
            // groupWithWeight
            if (AssertUtil.isNotEmpty(groupWithWeight)) {
                AdvertMaterialResortGroupVo advertMaterialResortGroupVo = getAdvertMaterialResortGroupVo(
                        AdvertMaterialGroupTypeEnum.WITH_WEIGHT_MATERIAL_TYPE.getIndex(),
                        groupWithWeight);
                advertMaterialResortGroupVoList.add(advertMaterialResortGroupVo);
            }

            // groupWithoutWeight
            if (AssertUtil.isNotEmpty(groupWithoutWeight)) {
                AdvertMaterialResortGroupVo advertMaterialResortGroupVo = getAdvertMaterialResortGroupVo(
                        AdvertMaterialGroupTypeEnum.WITHOUT_WEIGHT_MATERIAL_TYPE.getIndex(),
                        groupWithoutWeight);
                advertMaterialResortGroupVoList.add(advertMaterialResortGroupVo);
            }
        } catch (Exception e) {
            logger.error("getMergeMaterialMap happen error:{}", e);
            throw new RecommendEngineException("getMergeMaterialMap happen error", e);
        }

        return advertMaterialResortGroupVoList;

    }

    /**
     * 遍历老素材
     * @param oldMaterialIdList
     * @param materialCRTMap
     * @param groupWithWeight
     * @param groupWithoutWeight
     */
    private void traverseOldMaterials(List<Long> oldMaterialIdList, Map<Long, AdvertMaterialRcmdCtrStatEntity>
            materialCRTMap, List<AdvertMaterialResortVo> groupWithWeight, List<AdvertMaterialResortVo> groupWithoutWeight) {
        for (Long materialId : oldMaterialIdList) {


            // 从Map中获取 素材的CTR记录
            AdvertMaterialRcmdCtrStatEntity entity = materialCRTMap.get(materialId);

            // 构造排序对象
            AdvertMaterialResortVo materialResortVo = new AdvertMaterialResortVo();
            materialResortVo.setMaterialId(materialId);
            if (AssertUtil.isNotEmpty(entity)) {
                // 添加到 groupWithoutWeight
                materialResortVo.setRankScore(entity.getCtr());
                groupWithWeight.add(materialResortVo);


            } else {
                // 添加到 groupWithWeight
                groupWithoutWeight.add(materialResortVo);

            }
        }
    }

    /**
     * 遍历新素材
     * @param newMaterialIdList
     * @param groupWithoutWeight
     */
    private void traverseNewMaterials(List<Long> newMaterialIdList, List<AdvertMaterialResortVo> groupWithoutWeight) {
        for (Long materialId : newMaterialIdList) {
            AdvertMaterialResortVo materialResortVo = new AdvertMaterialResortVo();
            materialResortVo.setMaterialId(materialId);
            groupWithoutWeight.add(materialResortVo);
        }
    }

    private AdvertMaterialResortGroupVo getAdvertMaterialResortGroupVo(long groupType, List<AdvertMaterialResortVo> groupVo) {
        AdvertMaterialResortGroupVo advertMaterialResortGroupVo = new AdvertMaterialResortGroupVo();
        advertMaterialResortGroupVo.setMaterialGroupId(groupType);
        advertMaterialResortGroupVo.setMaterialResortVoList(groupVo);
        advertMaterialResortGroupVo.setMaterialNums(groupVo.size());

        return advertMaterialResortGroupVo;
    }

}
