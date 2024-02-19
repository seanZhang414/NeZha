package cn.com.duiba.nezha.engine.biz.message.advert.ons;

import cn.com.duiba.nezha.engine.biz.constant.MongoCollectionConstant;
import cn.com.duiba.nezha.engine.common.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 广告出价变更消息处理
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: ROIFeeMessageHandler.java , v 0.1 2017/6/6 下午2:31 ZhouFeng Exp $
 */
@Component
public class RoiCostMessageHandler extends AbstractMessageResultHandler {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String getListenTag() {
        return OnsRoiControllerMessageTag.ROI_COST.getTag();
    }

    @Override
    public void consumer(String message) {

        if (StringUtils.isNotBlank(message)) {

            JSONObject json = JSONObject.parseObject(message);


            String advertId = json.getString("adid");
            String packageId = json.getString("packageId");

            if (StringUtils.isBlank(advertId) || StringUtils.isBlank(packageId)) {
                logger.warn("conusmer message:{} error,illegal argument", message);
                return;
            }

            logger.info("roi cost change message:{}", message);

            String feeKey = RedisKeyUtil.roiFeeKey(advertId, packageId, LocalDate.now());
            String cvrKey = RedisKeyUtil.roiCvrKey(advertId, packageId, LocalDate.now());

            //某个广告修改出价时，清空计费累计和转换次数累计
            stringRedisTemplate.delete(feeKey);
            stringRedisTemplate.delete(cvrKey);

            //调价重置
            mongoTemplate.remove(Query.query(new Criteria("advertId").is(advertId).and("packageId").is(packageId)),
                    MongoCollectionConstant.CPA_FACTOR_COLLECTION);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //在消息处理器中注册
        RocketMqMessageListener.registerCallback(this);
    }
}
