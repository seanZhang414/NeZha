package cn.com.duiba.nezha.engine.biz.message.advert.ons;

import cn.com.duiba.nezha.engine.common.utils.MultiStringUtils;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import cn.com.duiba.nezha.engine.common.utils.RoiHashKeyUtil;
import cn.com.duiba.wolf.utils.DateUtils;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

/**
 * 计费统计消息处理
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ROIFeeMessageHandler.java , v 0.1 2017/6/6 下午2:31 ZhouFeng Exp $
 */
@Component
public class RoiFeeMessageHandler extends AbstractMessageResultHandler {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String getListenTag() {
        return OnsRoiControllerMessageTag.ROI_FEE.getTag();
    }

    @Override
    public void consumer(String message) {

        if (StringUtils.isNotBlank(message)) {

            JSONObject json = JSONObject.parseObject(message);


            String advertId = json.getString("adid");
            String feeString = json.getString("fee");
            String packageId = json.getString("packageId");
            String appId = json.getString("appId");
            String slotId = json.getString("slotId");
            String activityId = json.getString("activityId");


            if (MultiStringUtils.isAnyBlank(advertId, feeString, packageId, appId, slotId, activityId)) {
                logger.warn("conusmer message:{} error,illegal argument", message);
                return;
            }
            // 记录Cat曲线图
            Cat.logMetricForCount("roiFee");

            String key = RedisKeyUtil.roiFeeKey(advertId, packageId, LocalDate.now());

            long fee = Long.parseLong(feeString);

            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {

                StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

                //增加四个维度的计费
                stringRedisConn.hIncrBy(key, RoiHashKeyUtil.getDefault(), fee);
                stringRedisConn.hIncrBy(key, RoiHashKeyUtil.getAppKey(appId), fee);
                stringRedisConn.hIncrBy(key, RoiHashKeyUtil.getSlotKey(slotId), fee);
                stringRedisConn.hIncrBy(key, RoiHashKeyUtil.getActivityKey(appId, activityId), fee);
                //设置失效时间到明天
                stringRedisConn.expire(key, (long) DateUtils.getToTomorrowSeconds() + new Random().nextInt(100));
                return null;
            });

        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //在消息处理器中注册
        RocketMqMessageListener.registerCallback(this);
    }
}
