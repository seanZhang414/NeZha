package cn.com.duiba.tuia.engine.activity.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import cn.com.duiba.tuia.activity.center.api.constant.PageType;
import cn.com.duiba.tuia.activity.center.api.dto.GuidePageDto;
import cn.com.duiba.tuia.activity.center.api.dto.TuiaActivityDto;
import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.api.ApiCode;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.handle.SlotHandle;
import cn.com.duiba.tuia.engine.activity.model.ActivityRspDataExtend;
import cn.com.duiba.tuia.engine.activity.model.AdType;
import cn.com.duiba.tuia.engine.activity.model.req.GetActivityReq;
import cn.com.duiba.tuia.engine.activity.model.rsp.GetActivityRsp;
import cn.com.duiba.tuia.engine.activity.model.rsp.MaterialRsp;
import cn.com.duiba.tuia.engine.activity.remoteservice.MaterialSpecService;
import cn.com.duiba.tuia.engine.activity.service.ActivityMaterialService;
import cn.com.duiba.tuia.engine.activity.service.FlowSplitService;
import cn.com.duiba.tuia.engine.activity.service.LocalCacheService;
import cn.com.duiba.tuia.ssp.center.api.constant.MaterialSpecificationConstant;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.ssp.center.api.dto.MaterialCtrDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationAssortDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationDto;
import cn.com.duiba.tuia.ssp.center.api.dto.RspMaterialSpecificationItemContentDto;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotActMaterial4Engine;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotCacheDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteSlotMaterialService;

/**
 * ActivitySource
 */
@Repository("activityMaterialService")
public class ActivityMaterialServiceImpl implements ActivityMaterialService {

    protected static Logger     logger               = LoggerFactory.getLogger(ActivityMaterialServiceImpl.class);
    private static final Logger actOutputErrorLogger = LoggerFactory.getLogger("ActOutputErrorLog");

    private static final String AD_ICON_IMG          = "https://yun.duiba.com.cn/upload/8ujO01480924005776.png";
    private static final String AD_CLOSE_IMG         = "https://yun.duiba.com.cn/upload/4UyUC1480924005775.png";
    private static final int    ONE_YEAR = 365;
    private static final int    ONE_MINUTE    = 1;
    @Autowired
    private FlowSplitService    flowSplitService;
    @Autowired
    private LocalCacheService   localCacheService;
    @Autowired
    private Redis3Handler redis3Handler;
    
    @Autowired
    private MaterialSpecService materialSpecService;
    
    @Autowired
    private RemoteSlotMaterialService remoteSlotMaterialService;

    @Value("${tuia.engine.activity.web.host}")
    private String activityWebHost;

    /**
     * 获取素材
     */
    private RspMaterialSpecificationAssortDto getMsItemContentGroup(Long activityId, Integer activitySource, SlotCacheDto slot, GetActivityRsp activityRsp) {
        RspActivityDto rspActivityDto = localCacheService.getActivityDetail(activityId, activitySource);
        if (rspActivityDto == null) {
            return null;
        }
        // 素材分流按比例切分
        RspMaterialSpecificationAssortDto result = null;
        int materialOutputWay = flowSplitService.getMaterialOutputWay();
        activityRsp.setMaterial_way(materialOutputWay);
        switch (materialOutputWay) {
            case FlowSplitConstant.NEW_MATERIAL_OUTPUT:
                result = getRspMaterialSpecificationAssortDtoByNewMaterial(activityId, activitySource, slot, activityRsp, rspActivityDto);
                break;
            case FlowSplitConstant.OLD_MATERIAL_OUTPUT:
                result = getRspMaterialSpecificationAssortDtoByOldMaterial(activityId, activitySource, slot, activityRsp, rspActivityDto);
                break;
            default:
                break;
        }
        return result;
    }

    private RspMaterialSpecificationAssortDto getRspMaterialSpecificationAssortDtoByOldMaterial(Long activityId, Integer activitySource, SlotCacheDto slot, GetActivityRsp activityRsp, RspActivityDto rspActivityDto) {
        RspMaterialSpecificationAssortDto result = getOldMaterial(rspActivityDto.getMsList(), slot, activityRsp.getDevice_id());
        // 如果没有老素材就使用新素材
        if (result == null) {
            result = getNewMaterial(rspActivityDto.getMsList(), slot.getSlotMsId(), activityId, activitySource);
            activityRsp.setMaterial_way(FlowSplitConstant.NEW_MATERIAL_OUTPUT);
        }
        return result;
    }

    private RspMaterialSpecificationAssortDto getRspMaterialSpecificationAssortDtoByNewMaterial(Long activityId, Integer activitySource, SlotCacheDto slot, GetActivityRsp activityRsp, RspActivityDto rspActivityDto) {
        RspMaterialSpecificationAssortDto result = getNewMaterial(rspActivityDto.getMsList(), slot.getSlotMsId(), activityId, activitySource);
        // 如果没有新素材就使用老素材
        if (result == null) {
            result = getOldMaterial(rspActivityDto.getMsList(), slot, activityRsp.getDevice_id());
            activityRsp.setMaterial_way(FlowSplitConstant.OLD_MATERIAL_OUTPUT);
        }
        return result;
    }

    /**
     * 从新素材列表中取素材
     */
    @SuppressWarnings("unchecked")
    private RspMaterialSpecificationAssortDto getNewMaterial(List<RspMaterialSpecificationDto> dataSource, Long slotMsId, Long activityId, Integer activitySource) {
        for (RspMaterialSpecificationDto ms : dataSource) {
            if (ms.getId().equals(slotMsId)) {
                List<RspMaterialSpecificationAssortDto> newList = Lists.newArrayList(CollectionUtils.select(ms.getItemContentList(),
                        (Object o) -> MaterialSpecificationConstant.MS_CONTENT_NEW == ((RspMaterialSpecificationAssortDto) o).getIsNewContent()));

                if (!CollectionUtils.isEmpty(newList)) {
                    RspMaterialSpecificationAssortDto itemContentGroup = newList.get(new Random().nextInt(newList.size()));
                    addNewMaterialExposeCount(itemContentGroup.getId(), activityId, activitySource);
                    return itemContentGroup;
                }
            }
        }
        return null;
    }

    /**
     * 新素材曝光量+1，到达2000后变为老素材
     */
    private void addNewMaterialExposeCount(Long materialId, Long activityId, Integer activitySource) {
    	String key = RedisKeyConstant.getNewMaterialExposeCount(materialId);
        long count = redis3Handler.increment(key,1L);
        if(count < 2){
        	redis3Handler.expire(key, ONE_YEAR, TimeUnit.DAYS);
        }
        if (count >= flowSplitService.getMaterialNewToOldBound() && materialSpecService.changeMsItemContentStatus(materialId)) {
            // 改变素材标记位
            redis3Handler.expire(key, ONE_MINUTE, TimeUnit.MINUTES);
            // 本地素材缓存更新
            localCacheService.refreshActivityDetail(activityId, activitySource);
        }
    }

    /**
     * 从老素材列表中取素材
     */
    @SuppressWarnings("unchecked")
    private RspMaterialSpecificationAssortDto getOldMaterial(List<RspMaterialSpecificationDto> dataSource, SlotCacheDto slot, String deviceId) {
        for (RspMaterialSpecificationDto ms : dataSource) {
            if (ms.getId().equals(slot.getSlotMsId())) {
                List<RspMaterialSpecificationAssortDto> oldList = Lists.newArrayList(CollectionUtils.select(ms.getItemContentList(), (Object o) -> MaterialSpecificationConstant.MS_CONTENT_OLD == ((RspMaterialSpecificationAssortDto) o).getIsNewContent()));
                if (!CollectionUtils.isEmpty(oldList)) {
                    return getMaterialByCtr(oldList, slot.getId(), deviceId);
                }
            }
        }
        return null;
    }

    /**
     * 素材经CTR排序后按流量比获取素材
     *
     * @param dataSource 素材列表
     * @return 素材
     */
    private RspMaterialSpecificationAssortDto getMaterialByCtr(List<RspMaterialSpecificationAssortDto> dataSource, Long slotId, String deviceId) {
        List<MaterialCtrDto> ctrList = new ArrayList<>(); //有ctr数据的素材
        double maxCtr = getMaxCtr(dataSource, slotId, ctrList);
        if (CollectionUtils.isEmpty(ctrList)) {
            return dataSource.get(new Random().nextInt(dataSource.size()));
        }
        List<MaterialCtrDto> filterList = filterMaterByCtr(ctrList, maxCtr);
        if (CollectionUtils.isEmpty(filterList)) {
            return null;
        }

        MaterialCtrDto materialCtr = getMaterialCtrDto(slotId, deviceId, filterList);
        
        for (RspMaterialSpecificationAssortDto itemContentGroup : dataSource) {
        	if(itemContentGroup.getMaterialId() == null){
        		if (materialCtr.getMaterialId().equals(itemContentGroup.getId())) {
                	updateMaterialHistory(slotId, deviceId, materialCtr.getMaterialId());
                    return itemContentGroup;
                }
        	}else{
        		if (materialCtr.getSckId().equals(itemContentGroup.getMaterialId())) {
                	updateMaterialHistory(slotId, deviceId, materialCtr.getSckId());
                    return itemContentGroup;
                }
        	}
        }
        return null;
    }

    private MaterialCtrDto getMaterialCtrDto(Long slotId, String deviceId, List<MaterialCtrDto> filterList) {
        MaterialCtrDto materialCtr = null;
        int filterListSize = filterList.size();
        for(int i=0;i<filterListSize;i++){
        	Long materialId = null;
        	if(filterList.get(i).getSckId() == null){
        		materialId = filterList.get(i).getMaterialId();
        	}else{
        		materialId = filterList.get(i).getSckId();
        	}
        	if(isNotInHistory(slotId, deviceId, materialId)){
        		materialCtr = filterList.get(i);
        		break;
        	}
        }
        if(materialCtr == null){
        	materialCtr = filterList.get(0);
        }
        return materialCtr;
    }

    /**
     * 排除不满足CTR的素材，并且从大到小排序
     * @param ctrList
     * @param maxCtr
     * @return
     */
    private List<MaterialCtrDto> filterMaterByCtr(List<MaterialCtrDto> ctrList, Double maxCtr) {
        if (maxCtr.equals(0d)) {
            return Collections.emptyList();
        }
        List<MaterialCtrDto> filterList = Lists.newArrayList();
        for (MaterialCtrDto materialCtr : ctrList) {
            // 排除不满足条件的素材
            double diff = (maxCtr - materialCtr.getCtr()) / maxCtr;
            if (diff * 100 <= flowSplitService.getMaterialDiffPercent()) {
                filterList.add(materialCtr);
            }
        }
        Collections.sort(filterList, (MaterialCtrDto o1, MaterialCtrDto o2) -> {
                if(o1.getCtr()==null && o2.getCtr()==null){
                    return 0;
                }
                if(o1.getCtr()==null){
                    return 1;
                }
                if(o2.getCtr()==null){
                    return -1;
                }
                return o2.getCtr().compareTo(o1.getCtr());
        });
        return filterList;
    }

    private double getMaxCtr(List<RspMaterialSpecificationAssortDto> dataSource, Long slotId, List<MaterialCtrDto> ctrList) {
        double maxCtr = 0;
        Map<Long, MaterialCtrDto> ctrMap = getCtrMap(slotId);
        // 筛选有CTR数据的素材
        for (RspMaterialSpecificationAssortDto itemContentGroup : dataSource) {
            MaterialCtrDto materialCtr = ctrMap.get(itemContentGroup.getId());
            if (materialCtr != null && materialCtr.getCtr() > 0) {
                ctrList.add(materialCtr);
                if (materialCtr.getCtr() > maxCtr) {
                    maxCtr = materialCtr.getCtr();
                }
            }
        }
        return maxCtr;
    }

    private boolean isNotInHistory(Long slotId, String deviceId,Long materialId){
    	String key = RedisKeyConstant.getUserKey(slotId, deviceId);
    	Object materialVisitHistory = redis3Handler.hGet(key, RedisKeyConstant.getMaterialVisitHistory());
    	if(materialVisitHistory == null){
    		return true;
    	}
    	Long visitHistory = Long.valueOf(materialVisitHistory.toString());
        return !Objects.equals(visitHistory,materialId);
    }
    
    /**
     * 更新素材投放历史
     */
    private void updateMaterialHistory(Long slotId, String deviceId,Long materialId) {
    	String key = RedisKeyConstant.getUserKey(slotId, deviceId);
    	redis3Handler.hSet(key, RedisKeyConstant.getMaterialVisitHistory(), materialId.toString());
    }
    

    /**
     * 获取某广告位下某活动的素材MAP
     * 
     */
    private Map<Long, MaterialCtrDto> getCtrMap(Long slotId) {
        List<MaterialCtrDto> ctrList = localCacheService.getMaterialBySlotActivity(slotId);
        Map<Long, MaterialCtrDto> map = new HashMap<>();
        for (MaterialCtrDto materialCtr : ctrList) {
            map.put(materialCtr.getMaterialId(), materialCtr);
        }
        return map;
    }

    private List<MaterialRsp> getActivityMaterial(List<RspMaterialSpecificationItemContentDto> itemContentList) {
        if (CollectionUtils.isEmpty(itemContentList)) {
            return Collections.emptyList();
        }
        List<MaterialRsp> materialItemList = new ArrayList<>();
        for (RspMaterialSpecificationItemContentDto content : itemContentList) {
            MaterialRsp materialRsp = new MaterialRsp();
            materialRsp.setImage_url(content.getImageUrl());
            materialRsp.setText(content.getLetter());
            materialRsp.setMs_item_id(content.getMsItemId());
            materialRsp.setImage_width(content.getImageWidth());
            materialRsp.setImage_height(content.getImageHeight());
            materialRsp.setItem_type(content.getItemType());
            materialItemList.add(materialRsp);
        }
        return materialItemList;
    }

    private Long[] appendMaterial4tuia(Long activityId, Integer activitySource, SlotCacheDto adslot, GetActivityRsp activityRsp) {
    	SlotActMaterial4Engine slotActMaterial = appendSlotActMaterial(adslot.getId(), activityId, activitySource, activityRsp);
    	if(slotActMaterial == null){
    		RspMaterialSpecificationAssortDto itemContentGroup = getMsItemContentGroup(activityId, activitySource, adslot, activityRsp);
    		if (itemContentGroup == null) {
    			actOutputErrorLogger.info("Get Material From Tuia Error:SlotId=[{}],activityId=[{}],activitySource=[{}]", adslot.getId(), activityId, activitySource);
    			return new Long[0];
    		}
    		activityRsp.setMaterial_id(itemContentGroup.getId());
    		activityRsp.setSck_Id(itemContentGroup.getMaterialId());
    		activityRsp.setMaterial_list(getActivityMaterial(itemContentGroup.getValue()));
    		
    		for (MaterialRsp material : activityRsp.getMaterial_list()) {
    			if (StringUtils.isNotBlank(material.getImage_url())) {
    				// 规格素材数据格式,兼容现有数据
    				activityRsp.setImg_url(material.getImage_url());
    				activityRsp.setImg_width(material.getImage_width());
    				activityRsp.setImg_height(material.getImage_height());
    			}
    		}
    	}else{
    		activityRsp.setMaterial_id(0L);//该id逐渐废弃，新需求只是用sckId;此处设置为零是为了不影响老数据
    		activityRsp.setSck_Id(slotActMaterial.getSckId());
    		activityRsp.setImg_url(slotActMaterial.getImgUrl());
    		activityRsp.setImg_width(slotActMaterial.getImgWidth());
			activityRsp.setImg_height(slotActMaterial.getImgHeight());
    	}
    	return new Long[]{activityRsp.getMaterial_id(),activityRsp.getSck_Id()}; //1:素材规格表id,2:素材库id
    }
    
    private SlotActMaterial4Engine appendSlotActMaterial(Long slotId,Long actId,Integer actSource, GetActivityRsp activityRsp){
    	//1.获取广告位活动定制素材列表
    	List<SlotActMaterial4Engine> slotActMaterialList = localCacheService.getSlotActMaterialList(slotId, actId, actSource);
    	if(slotActMaterialList.isEmpty()){
    		return null;
    	}
    	//2.新老素材分流
    	int materialOutputWay = flowSplitService.getMaterialOutputWay();
    	SlotActMaterial4Engine result = null;
    	switch (materialOutputWay) {
	        case FlowSplitConstant.NEW_MATERIAL_OUTPUT:
	            result = getSlotActNewMaterial(slotActMaterialList, activityRsp, slotId, actId, actSource);
	            if(result == null){
	            	result = getSlotActOldMaterial(slotActMaterialList, slotId, activityRsp);
	            }
	            break;
	        case FlowSplitConstant.OLD_MATERIAL_OUTPUT:
	            result = getSlotActOldMaterial(slotActMaterialList, slotId, activityRsp);
	            if(result == null){
	            	result = getSlotActNewMaterial(slotActMaterialList, activityRsp, slotId, actId, actSource);
	            }
	            break;
	        default:
	            break;
    	}
    	return result;
    }
    //广告位活动定制素材 获取新素材算法
    private SlotActMaterial4Engine getSlotActNewMaterial(List<SlotActMaterial4Engine> slotActMaterialList, GetActivityRsp activityRsp,
    		Long slotId, Long actId, Integer actSource){
    	List<SlotActMaterial4Engine> newMaterialList = slotActMaterialList.stream()
    					   .filter(o->o.getIsNewContent() == 1)
    					   .collect(Collectors.toList());
    	if(newMaterialList.isEmpty()){
    		return null;
    	}
    	SlotActMaterial4Engine result = newMaterialList.get(new Random().nextInt(newMaterialList.size()));
    	//挑选新素材后事件
    	activityRsp.setMaterial_way(FlowSplitConstant.NEW_MATERIAL_OUTPUT);
		newToOldSlotMaterial(result.getId(), result.getSckId(), slotId, actId, actSource);
		
    	return result;
    }
    
    //广告位活动定制素材 获取老素材算法
    private SlotActMaterial4Engine getSlotActOldMaterial(List<SlotActMaterial4Engine> slotActMaterialList,
    		Long slotId, GetActivityRsp activityRsp){
    	List<SlotActMaterial4Engine> oldMaterialList = slotActMaterialList.stream()
    			.filter(o->o.getIsNewContent() == 2)
    			.collect(Collectors.toList());
    	if(oldMaterialList.isEmpty()){
    		return null;
    	}
    	//获取CTR最大值 -> 获取CTR>0的素材 -> 过滤筛选条件85%的素材 -> 按CTR降序排列素材 -> 获取素材历史之外的高Ctr素材
    	List<MaterialCtrDto> ctrList = localCacheService.getMaterialBySlotActivity(slotId);
    	if(ctrList.isEmpty()){
    		return null;
    	}
        Map<Long, MaterialCtrDto> ctrMap = new HashMap<>();
        for (MaterialCtrDto materialCtr : ctrList) {
        	ctrMap.put(materialCtr.getSckId(), materialCtr);
        }
        SlotActMaterial4Engine[] array = new SlotActMaterial4Engine[1];
        Optional<SlotActMaterial4Engine> slotActMaterial = oldMaterialList.stream()
        		.filter(o->{
        			MaterialCtrDto ctrDto = ctrMap.get(o.getSckId());
        			if(ctrDto == null){
        				return false;
        			}
        			Double ctr = ctrDto.getCtr();
        			return ctr == null?false:ctr>0;
        		})
				.sorted(Comparator.comparing(o -> ctrMap.get(o.getSckId()).getCtr(),
						Comparator.nullsLast(Comparator.reverseOrder())))
				.peek(o->{
							if(array[0] == null){
								array[0] = o;
							}
						})
				.filter(o->{
					double maxCtr = ctrMap.get(array[0].getSckId()).getCtr();
					double diff = (maxCtr - ctrMap.get(o.getSckId()).getCtr()) / maxCtr;
		            return diff * 100 <= flowSplitService.getMaterialDiffPercent();
				})
				.filter(o->isNotInHistory(slotId, activityRsp.getDevice_id(), o.getSckId()))
				.findFirst();
        SlotActMaterial4Engine result = null;
	    if(array[0] == null){//没有CTR>0老素材时，老素材随机出
	    	result = oldMaterialList.get(new Random().nextInt(oldMaterialList.size()));
	    }else if(slotActMaterial.isPresent()){
	    	result = slotActMaterial.get();//算法挑选的老素材
	    }else {
	    	result = array[0];
	    }
	    //挑选老素材后事件
	    activityRsp.setMaterial_way(FlowSplitConstant.OLD_MATERIAL_OUTPUT);
	    updateMaterialHistory(slotId, activityRsp.getDevice_id(), result.getSckId());
	    
	    return result;
    }
    
    private void newToOldSlotMaterial(Long id, Long sckId, Long slotId, Long activityId, Integer actSource) {
    	String key = RedisKeyConstant.getSlotNewMaterialExposeCount(id);
        long count = redis3Handler.increment(key,1L);
        if(count < 2){
        	redis3Handler.expire(key, ONE_YEAR, TimeUnit.DAYS);
        }
        if (count == flowSplitService.getMaterialNewToOldBound()) {
            // 改变素材标记位
            redis3Handler.expire(key, ONE_MINUTE, TimeUnit.MINUTES);
            // 只更新一次
            remoteSlotMaterialService.updateOldMaterial(slotId, activityId, actSource, sckId);
        }
    }

    @Override
    public GetActivityRsp toActivityResponse(GetActivityReq req, String advertTitle, Integer activitySource, TuiaActivityDto activityTuia, SlotCacheDto adslot, Integer activityOutputWay,String desc) {
        try {
            GetActivityRsp activityRsp = new GetActivityRsp();
            activityRsp.setActivity_id(activityTuia.getId().toString());

            activityRsp.setSource(1);
            activityRsp.setAd_title(advertTitle);
            activityRsp.setAd_type(adslot.getSlotType());
            activityRsp.setActivity_title(activityTuia.getTitle());
            activityRsp.setDescription(desc);
            activityRsp.setClick_url(buildClickUrl4Activity(activityTuia, adslot, req));
            if(SlotHandle.isManualPolling(adslot.getSlotType(), adslot.getActivityPutWay()) || SlotHandle.isTimePutWay(adslot.getSlotType(), adslot.getActivityPutWay())){
            	return activityRsp;
            }

            activityRsp.setAd_icon(AD_ICON_IMG);
            activityRsp.setAd_close(AD_CLOSE_IMG);
            activityRsp.setAd_icon_visible(adslot.isVisibleOfIco());
            activityRsp.setAd_close_visible(adslot.isVisibleOfCloseButton());

            activityRsp.setActivity_way(activityOutputWay);

            // 规格兼容SDK广告位类型
            Integer adType = AdType.valueOf(adslot.getSlotMsId());
            if (adType != null) {
                activityRsp.setAd_type(adType);
            }else {
                logger.info("slot adType is null, slot=[{}]",adslot);
            }
            // 添加活动来源到活动ID
            if (activityRsp.getActivity_id() != null && activityRsp.getActivity_id().length() <= 9) {
                String data = "2" + StringUtils.leftPad(activityRsp.getActivity_id(), 9, '0'); // 推啊来源活动，前台传2，日志存1
                activityRsp.setActivity_id(data);
                // 推啊规格素材数据格式和投放类型
                // 自定义类型不能带上素材ID
                activityRsp.setDevice_id(req.getDevice_id());
                Long[] materialId = appendMaterial4tuia(activityTuia.getId(), activitySource, adslot, activityRsp);
                buildDataExtend(materialId, activityRsp, adType, data);
            } else {
                logger.error("Tuia Activity Id too long, id={}", activityRsp.getActivity_id());
            }

            activityRsp.setError_code(ApiCode.SUCCESS);
            return activityRsp;
        } catch (Exception e) {
            logger.info("ToActivityResponse error,req=[{}]", req);
        }
        return null;
    }

    @Override
    public GetActivityRsp toActivityResponse(GetActivityReq req, String advertTitle, Integer activitySource, GuidePageDto guidePageDto, SlotCacheDto adSlot, Integer activityOutputWay,String desc) {
        try {
            GetActivityRsp activityRsp = new GetActivityRsp();
            activityRsp.setActivity_id(guidePageDto.getId().toString());

            activityRsp.setSource(2);
            activityRsp.setAd_title(advertTitle);
            activityRsp.setAd_type(adSlot.getSlotType());
            activityRsp.setActivity_title(guidePageDto.getTitle());
            activityRsp.setDescription(desc);


            activityRsp.setClick_url(buildClickUrl4GuidePage(guidePageDto,adSlot, req));
            if(SlotHandle.isManualPolling(adSlot.getSlotType(), adSlot.getActivityPutWay()) || SlotHandle.isTimePutWay(adSlot.getSlotType(), adSlot.getActivityPutWay())){
            	return activityRsp;
            }

            activityRsp.setAd_icon(AD_ICON_IMG);
            activityRsp.setAd_close(AD_CLOSE_IMG);
            activityRsp.setAd_icon_visible(adSlot.isVisibleOfIco());
            activityRsp.setAd_close_visible(adSlot.isVisibleOfCloseButton());

            activityRsp.setActivity_way(activityOutputWay);

            // 规格兼容SDK广告位类型
            Integer adType = AdType.valueOf(adSlot.getSlotMsId());
            if (adType != null) {
                activityRsp.setAd_type(adType);
            }else {
                logger.info("slot adType is null, slot=[{}]",adSlot);
            }
            // 添加活动来源到活动ID
            if (activityRsp.getActivity_id() != null && activityRsp.getActivity_id().length() <= 9) {
                String data = "3" + StringUtils.leftPad(activityRsp.getActivity_id(), 9, '0'); // 来源流量引导页，前台传3，日志存2
                activityRsp.setActivity_id(data);
                // 推啊规格素材数据格式和投放类型
                // 自定义类型不能带上素材ID
                activityRsp.setDevice_id(req.getDevice_id());
                Long[] materialId = appendMaterial4tuia(guidePageDto.getId(), activitySource, adSlot, activityRsp);
                buildDataExtend(materialId, activityRsp, adType, data);
            } else {
                logger.error("GuidePageDto Id too long, id={}" , activityRsp.getActivity_id());
            }

            activityRsp.setError_code(ApiCode.SUCCESS);
            return activityRsp;
        } catch (Exception e) {
            logger.info("ToActivityResponse error,req=[{}]", req);
        }
        return null;
    }
    
    

    private String buildClickUrl4GuidePage(GuidePageDto guidePageDto, SlotCacheDto adSlot, GetActivityReq req){
        StringBuilder activityUrl = new StringBuilder("http://").append(activityWebHost);
        if (PageType.ACTCENTER.getCode().equals(guidePageDto.getPageType())) {
            activityUrl.append("/actCenter/index?id=");
        } else if (PageType.MAINMEET.getCode().equals(guidePageDto.getPageType())) {
            activityUrl.append("/mainMeet/index?id=");
        } else if (PageType.DIRECT.getCode().equals(guidePageDto.getPageType())) {
            activityUrl.append("/direct/index?id=");
        }
        activityUrl.append(guidePageDto.getId()).append("&slotId=").
                append(adSlot.getId()).append("&login=normal&appKey=").
                append(req.getApp_key()).append("&deviceId=").
                append(req.getDevice_id());
        appendDsm(activityUrl,adSlot);
        return activityUrl.toString();
    }

    private String buildClickUrl4Activity(TuiaActivityDto activityTuia, SlotCacheDto adSlot, GetActivityReq req){
        StringBuilder activityUrl = new StringBuilder("http://").
                append(activityWebHost).append("/activity/index?id=").
                append(activityTuia.getId()).append("&slotId=").
                append(adSlot.getId()).append("&login=normal&appKey=").
                append(req.getApp_key()).append("&deviceId=").
                append(req.getDevice_id());
        appendDsm(activityUrl, adSlot);
        return activityUrl.toString();
    }

    public void appendDsm(StringBuilder url, SlotCacheDto slot){
        url.append("&dsm=1." + slot.getId() + ".0.0");
    }

    private void buildDataExtend(Long[] materialId, GetActivityRsp activityRsp, Integer adType, String data){
        ActivityRspDataExtend dataExtend = new ActivityRspDataExtend();
        String cUrl = StringUtils.replaceEach(activityRsp.getClick_url(), new String[]{"http://activity.tuia.cn", "https://activity.tuia.cn"}, new String[]{"", ""});
        dataExtend.setClickUrl(cUrl);
        if(materialId != null && materialId[0] != null){
            dataExtend.setMaterialId(materialId[0]);
            dataExtend.setSckId(materialId[1]);
            activityRsp.setData2(JSONObject.toJSONString(dataExtend));
            if (adType != AdType.ADSENSE_TYPE_USER_DEFINED) {
                activityRsp.setActivity_id(data + SplitConstant.SPLIT_COMMA + materialId[0]);
                if(materialId[1] != null){
                    activityRsp.setActivity_id(activityRsp.getActivity_id() + SplitConstant.SPLIT_COMMA + materialId[1]);
                }
            }
        } else {
            activityRsp.setData2(JSONObject.toJSONString(dataExtend));
        }
    }
}
