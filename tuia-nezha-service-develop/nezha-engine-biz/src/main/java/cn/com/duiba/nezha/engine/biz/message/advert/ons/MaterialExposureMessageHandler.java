package cn.com.duiba.nezha.engine.biz.message.advert.ons;

import org.springframework.stereotype.Component;

/**
 * 素材曝光消息
 *
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: MaterialExposureMessageHandler.java , v 0.1 2017/7/11 下午4:37 ZhouFeng Exp $
 */
@Component
public class MaterialExposureMessageHandler extends AbstractMessageResultHandler {

    @Override
    public String getListenTag() {
        return OnsRoiControllerMessageTag.MATERIAL_EXPOSURE.getTag();
    }

    @Override
    public void consumer(String message) {
        //不消费该消息
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //在消息处理器中注册
        RocketMqMessageListener.registerCallback(this);
    }

}
