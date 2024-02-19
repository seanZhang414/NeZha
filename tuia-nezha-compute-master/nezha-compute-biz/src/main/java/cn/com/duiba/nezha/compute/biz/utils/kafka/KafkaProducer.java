package cn.com.duiba.nezha.compute.biz.utils.kafka;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

/**
 * Created by pc on 2017/2/28.
 */
public class KafkaProducer {

    private Producer producer = null;
    private String zkList = null;
    private String brokerList = null;


    public KafkaProducer(String zkList,String brokerList) {
        this.zkList = zkList;
        this.brokerList =brokerList;
    }

    public void sendMessage(String topic, String message) {
        try {
            getProducer().send(new KeyedMessage<String, String>(topic, message));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Producer getProducer() {
        if (producer == null) {
            createProducer();
        }
        return producer;

    }

    private void createProducer() {
        producer = new Producer<Integer, String>(new ProducerConfig(getProps()));
    }

    private Properties getProps() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", zkList);//声明zk
        properties.put("serializer.class", StringEncoder.class.getName());
        properties.put("metadata.broker.list", brokerList);// 声明kafka broker
        return properties;
    }

}
