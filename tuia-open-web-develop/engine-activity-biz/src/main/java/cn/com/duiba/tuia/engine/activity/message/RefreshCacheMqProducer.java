package cn.com.duiba.tuia.engine.activity.message;

import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by huazheng on 2017/9/5.
 */
@Component
public  class RefreshCacheMqProducer {

    private static Logger logger = LoggerFactory.getLogger(RefreshCacheMqProducer.class);

    @Value("${duiba.rocketmq.topic.refreshcache}")
    String refreshTopic;

    @Autowired
    private DefaultMQProducer rocketMqProducer;

    public  void sendMsg(String tag, String body){
        Message msg = new Message(refreshTopic,tag, body.getBytes(Charset.forName("utf-8")));
        msg.setKeys(UUID.randomUUID().toString());
        try{
            rocketMqProducer.send(msg);
        }catch(Exception e){
            logger.error("消息发送失败",e);
        }
    }


}
