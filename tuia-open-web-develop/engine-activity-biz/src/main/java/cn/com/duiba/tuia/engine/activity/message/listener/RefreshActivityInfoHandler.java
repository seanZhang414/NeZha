package cn.com.duiba.tuia.engine.activity.message.listener;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.tuia.constant.CacheKeyConstant;
import cn.com.duiba.tuia.engine.activity.service.ActivityFilterService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RefreshActivityInfoHandler extends AbstractMessageResultHandler {



    @Autowired
    private ActivityFilterService activityFilterService;

    @Override
    public String getListenTag() {
        return CacheKeyConstant.getRefreshActivityinfoTag();
    }

    @Override
    public void consumer(String message) {
        if (StringUtils.isNotBlank(message)) {
            activityFilterService.refreshActivityInfo(JSON.parseObject(message, ActivityInfo.class));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //在消息处理器中注册
        RocketMqMessageListener.registerCallback(this);
    }
}
