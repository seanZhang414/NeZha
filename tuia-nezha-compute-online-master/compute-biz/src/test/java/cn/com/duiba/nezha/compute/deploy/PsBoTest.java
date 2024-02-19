package cn.com.duiba.nezha.compute.deploy;

import cn.com.duiba.nezha.compute.biz.bo.PsBo;
import cn.com.duiba.nezha.compute.biz.dto.PsModelBaseInfo;
import cn.com.duiba.nezha.compute.core.CollectionUtil;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import py4j.StringUtil;

import java.util.Arrays;

import static org.junit.Assert.*;


public class PsBoTest extends TestCase {

    @Test
    public void updatePsBaseInfoTest() {
        PsModelBaseInfo psModelBaseInfo = new PsModelBaseInfo();
        psModelBaseInfo.setModelId("test01");
        try {
            PsBo.updatePsBaseInfo("test01", psModelBaseInfo);
            PsModelBaseInfo ret = PsBo.getPsBaseInfo("test01");
            System.out.println("ret = "+ JSON.toJSONString(ret));
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void main() throws Exception{

        double[] data = new double[10];

        data[1]=2.0;
        data[2]=1.0;
        String str = CollectionUtil.toString(data);
        System.out.println(str);

    }
}