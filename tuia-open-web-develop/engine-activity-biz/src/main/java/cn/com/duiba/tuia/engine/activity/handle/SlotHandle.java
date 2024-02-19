package cn.com.duiba.tuia.engine.activity.handle;

import cn.com.duiba.tuia.engine.activity.model.AdType;
import cn.com.duiba.tuia.ssp.center.api.dto.SlotDto;

public class SlotHandle {
	
	private SlotHandle(){
		throw new IllegalStateException("SlotHandle class");
	}
	
	public static boolean isTimePutWay(int slotType, Integer putWay){
		return slotType == AdType.ADSENSE_TYPE_MANUAL && (putWay == null || putWay == SlotDto.TIMING_PUTWAY);
	}
	
	public static boolean isRatioPutWay(int putWay){
		return putWay == SlotDto.RATIO_PUTWAY;
	}
	
	public static boolean isManualPolling(int slotType, Integer activityPutWay){
    	return SlotDto.ADSENSE_TYPE_MANUAL == slotType && activityPutWay != null && activityPutWay != SlotDto.TIMING_PUTWAY;
    }

}
