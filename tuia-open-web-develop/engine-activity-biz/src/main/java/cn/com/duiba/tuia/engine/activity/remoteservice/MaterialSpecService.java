package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.ssp.center.api.dto.MaterialCtrDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteMaterialSpecificationService;
import cn.com.duiba.wolf.dubbo.DubboResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author xuyenan
 * @createTime 2017/2/8
 */
@Service("materialSpecService")
public class MaterialSpecService {

    private static final Logger                       logger = LoggerFactory.getLogger(MaterialSpecService.class);

    @Autowired
    private RemoteMaterialSpecificationService remoteMaterialSpecificationService;

    /**
     * 获取某个广告位下某个活动的素材CTR列表
     * 
     * @param slotId 广告位ID
     * @param activityId 活动ID
     * @param activitySource 活动来源
     * @return 素材CTR列表
     */
    public List<MaterialCtrDto> getMaterialCtrBySlotActivity(Long slotId) {
        DubboResult<List<MaterialCtrDto>> result = remoteMaterialSpecificationService.getMaterialCtrBySlotId(slotId);
        if (!result.isSuccess()) {
            logger.error("getMaterialCtrBySlotActivity error,slotId={},msg={}",slotId,result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }
    
    /**
     * 将新素材移出试投列表
     * 
     * @param materialId 素材ID
     * @return Boolean
     */
    public Boolean changeMsItemContentStatus(Long materialId) {
        DubboResult<Boolean> result = remoteMaterialSpecificationService.changeMsItemContentStatus(materialId);
        if (!result.isSuccess()) {
            logger.error("changeMsItemContentStatus error,materialId={},msg={}",materialId,result.getMsg());
            return false;
        }
        return result.getResult();
    }
}
