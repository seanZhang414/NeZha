package cn.com.duiba.nezha.compute.biz.dao;

import cn.com.duiba.nezha.compute.api.dto.ConsumerOrderFeatureDto;
import cn.com.duiba.nezha.compute.biz.log.AdvertOrderLog;
import cn.com.duiba.nezha.compute.common.util.ObjectDynamicCreator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/2/4.
 */
public class ConsumerOrderFeatureTest extends TestCase {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCurrent() throws Exception {
        testB();
    }

    public void testA() throws Exception {

        String s0 =null;
        String s1 = "ssd";
        byte[] sb0=null;
        byte[] sb1=Bytes.toBytes(s1);
        System.out.println("sb0 ="+sb0);
        System.out.println("sb1 ="+sb1);
        System.out.println("s0 ="+ Bytes.toString(sb0));
        System.out.println("s1 ="+Bytes.toString(sb1));
        System.out.println("s0 long =" + Long.valueOf(Bytes.toString(sb0)));
    }

    public void testB() throws Exception {

        ConsumerOrderFeatureDto dto = new ConsumerOrderFeatureDto();
        dto.setConsumerId("123456");
        dto.setOrderRank("5");

        String jsonString = JSON.toJSONString(dto);

        JSONObject jSON = JSONObject.parseObject(jsonString);


        System.out.println("consumerId=" + jSON.get("consumerId"));
        System.out.println("orderRank=" + jSON.get("orderRank"));
        System.out.println("order=" + jSON.get("order"));

        Map<String,String> map = ObjectDynamicCreator.getFieldVlaue(dto);

        System.out.println("consumerId="+map.get("consumerId"));
        System.out.println("orderRank=" + map.get("orderRank"));
        System.out.println("order=" + map.toString());
    }

    public void testC() throws Exception {
        ConsumerOrderFeatureDto dto = new ConsumerOrderFeatureDto();
        dto.setConsumerId("123154");
        dto.setDayOrderRank("20");
        dto.setFirstOrderTime("20161111");
        dto.setLastActivityId("4552223");
        dto.setLastOrderChargeNums("2");
        dto.setLastOrderId("455675");
        dto.setLastOrderTime("20152255");

        Map<String, String> mainValMap = new HashMap<>();

        if (dto.getFirstOrderTime() != null) {
            mainValMap.put("firstOrderTime", dto.getFirstOrderTime());
        }

        if (dto.getLastActivityId() != null) {
            mainValMap.put("lastActivityId", dto.getLastActivityId());
        }

        mainValMap.put("lastOrderChargeNums", dto.getLastOrderChargeNums());

        if (dto.getLastOrderId()!=null){
            mainValMap.put("lastOrderId", dto.getLastOrderId());
        }
        if (dto.getLastOrderTime()!=null){
            mainValMap.put("lastOrderTime", dto.getLastOrderTime());
        }
        mainValMap.put("consumerId", null);

        String objStr = JSON.toJSONString(mainValMap);
        ConsumerOrderFeatureDto dto2 =JSON.parseObject(objStr, ConsumerOrderFeatureDto.class);
        System.out.println("dto2 = "+JSON.toJSONString(dto2));

    }

    public void testD() throws Exception {
        AdvertOrderLog vo = new AdvertOrderLog();
        String src="{\"activityId\":1877985,\"activityUseType\":0,\"advertId\":4892,\"appId\":7466,\"buttonType\":\"40003\",\"consumerId\":472794718,\"duibaActivityId\":16178,\"duibaActivityType\":12,\"fee\":0,\"info\":\"25294189857734\",\"infoType\":\"2\",\"ip\":\"122.227.120.193\",\"loginType\":\"1\",\"materialId\":4848,\"orderId\":\"25294189857734\",\"os\":\"1\",\"tag\":\"[\\\"10201\\\"]\",\"time\":\"2017-02-21 00:30:12\",\"timestamp\":1487608212876,\"type\":\"3\",\"ua\":\"IOS\",\"userAgent\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13F69 pregnancy/7.0.1 Duiba/1.0.1\"}\n";
        JSONObject json = JSONObject.parseObject(src);
//
//        String consumerId = String.valueOf(json.get("consumerId"));
//        vo.setConsumerId(consumerId);
//
//        String activityId = String.valueOf(json.get("activityId"));
//        vo.setActivityId(activityId);
//
//        String orderId = String.valueOf(json.get("orderId"));
//        vo.setOrderId(orderId);
//
//        String gmtTime = String.valueOf(json.get("time"));
//        vo.setGmtTime(gmtTime);
//        vo.setGmtDate(DateUtil.getDate(gmtTime));
//        vo.setCurrentTime(DateUtil.getCurrentTime());
//        System.out.println("vo=" + JSON.toJSONString(vo));



    }
}