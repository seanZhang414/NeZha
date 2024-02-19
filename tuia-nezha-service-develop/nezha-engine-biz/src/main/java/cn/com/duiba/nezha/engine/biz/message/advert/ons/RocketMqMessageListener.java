package cn.com.duiba.nezha.engine.biz.message.advert.ons;

import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: RocketMqMessageListener.java , v 0.1 2017/8/24 上午11:42 ZhouFeng Exp $
 */
@Component("bootRocketMqMessageListener")
public class RocketMqMessageListener implements MessageListenerConcurrently {

    private static final Logger                                    logger               = LoggerFactory.getLogger(RocketMqMessageListener.class);

    private static final Map<String, AbstractMessageResultHandler> HANDLER_CALLBACK_MAP = new ConcurrentHashMap<>();

    @Value("${nezha.roi.topic}")
    private String                                                 tuiaRoiTopic;

    @Value("${nezha.bizlog.enable}")
    protected boolean                                              logBiz;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                    ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            // 经过duiba-spring-boot-starter的内部限制，实际上msgs里只会包含一个消息，但是不排除将来可能会改为多个，而且每个消息的topic都不同
            for (MessageExt message : msgs) {

                //判断消息如果为压测消息，则直接丢弃
                if (StringUtils.isNotEmpty(message.getUserProperty("perfTest"))) {
                    continue;
                }

                String topic = message.getTopic();
                if (topic.equals(tuiaRoiTopic)) {
                    AbstractMessageResultHandler handler = HANDLER_CALLBACK_MAP.get(message.getTags());
                    String body = new String(message.getBody(), "utf-8");
                    if (logBiz) {
                        logger.info("receive msg, tag:{} message:{}", message.getTags(), body);
                    }
                    handler.consumer(body);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 发生异常时通常要重新消费，如果在发生特殊异常时你不希望该消息重新消费，则自行加个catch分支，并返回ConsumeConcurrentlyStatus.CONSUME_SUCCESS
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public static void registerCallback(AbstractMessageResultHandler handler) {
        HANDLER_CALLBACK_MAP.put(handler.getListenTag(), handler);
    }
}
