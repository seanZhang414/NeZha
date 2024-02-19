package cn.com.duiba.tuia.engine.activity.remoteservice;

import cn.com.duiba.tuia.activity.center.api.remoteservice.RemoteTuiaUserService;
import cn.com.duiba.wolf.dubbo.DubboResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shenjunlin on 2017/8/17.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RemoteTuiaUserService remoteTuiaUserService;

    public Long getConsumerId(Long appId, String deviceId){
        DubboResult<Long> result = remoteTuiaUserService.findByAppIdAndDeviceId(appId, deviceId);
        if (!result.isSuccess()){
            log.error("getConsumerId error, msg=[{}],", result.getMsg());
        }
        return result.getResult();
    }
}
