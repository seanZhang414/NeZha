package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.ssp.center.api.dto.ActivitySpmDto;
import cn.com.duiba.tuia.ssp.center.api.remote.RemoteActivityDeliverySpmService;
import cn.com.duiba.wolf.dubbo.DubboResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by shenjunlin on 2017/8/15.
 */
@Service("activityDeliverSpmService")
public class ActivityDeliverSpmService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityDeliverSpmService.class);

    @Autowired
    private RemoteActivityDeliverySpmService remoteActivityDeliverySpmService;

    public List<ActivitySpmDto> getExposeCountMoreThan5000(){
        DubboResult<List<ActivitySpmDto>> result = remoteActivityDeliverySpmService.findByExposeCountAndStatType(5000, 3);
        if (!result.isSuccess()) {
            logger.error("getExposeCountMoreThan5000 error,msg=[{}],", result.getMsg());
            return Collections.emptyList();
        }
        return result.getResult();
    }
}
