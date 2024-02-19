package cn.com.duiba.nezha.compute.deploy;

import cn.com.duiba.nezha.compute.api.enums.ModelKeyEnum;
import cn.com.duiba.nezha.compute.biz.replay.BaseReplayer;
import com.alibaba.fastjson.JSON;

import java.io.IOException;

/**
 * Created by pc on 2017/8/8.
 */
public class HelloWorld {
    private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=65f6d52acb34b661bc4be29e3e6e8576862a5246d6f6772da27d324299691006";

    public static void main(String[] args) throws IOException {


        System.out.println(System.getProperty("java.classpath"));

        try {
//            String str = "test";
////            CategoryFeatureDict dict = new CategoryFeatureDict();
////            dict.setFeature(str, null);
////            System.in.read();
////            System.out.println("re");
//            BaseReplayer.replay(ModelKeyEnum.FM_CTR_MODEL_v003.getIndex());

//            Dingding.send(webhook, "data sync test");


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.in.read();

    }
}
