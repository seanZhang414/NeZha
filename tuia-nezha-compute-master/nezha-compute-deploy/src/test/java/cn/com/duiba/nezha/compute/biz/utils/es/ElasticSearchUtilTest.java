package cn.com.duiba.nezha.compute.biz.utils.es;

import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.api.dto.ConsumerOrderFeatureDto;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.vo.ConsumerOrderFeatureVo;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2017/3/21.
 */
public class ElasticSearchUtilTest extends TestCase {

    private ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetValueMap() throws Exception {

    }

    public void testGetValueT() throws Exception {

        Map<String, ConsumerOrderFeatureDto> dateKeyMaps = new HashMap<>();
        ConsumerOrderFeatureDto ddto = new ConsumerOrderFeatureDto();
        ddto.setDayOrderRank("1001");
        dateKeyMaps.put("tt-tt", ddto);


        Long v = 100L;
        elasticSearchUtil.multiUpsertT("test", "test", dateKeyMaps, 1000);
        ConsumerOrderFeatureDto dayOrderRank = elasticSearchUtil.getValueT("test", "test", "tt-tt", ConsumerOrderFeatureDto.class);
        System.out.println("v=" + JSON.toJSONString(dayOrderRank));

    }

    public void testGetValueString() throws Exception {
        String ret = elasticSearchUtil.getValueString("test", "test", "tt09");

        System.out.println("ret=" + JSON.toJSONString(ret));
    }

    public void testMultiIndex() throws Exception {
        Map<String, ConsumerOrderFeatureVo> jsonValueMap = new HashMap<>();
        ConsumerOrderFeatureVo vo1 = new ConsumerOrderFeatureVo();
        vo1.setConsumerId("201");
        vo1.setActivityId("00");
        jsonValueMap.put("tt09", vo1);
        ConsumerOrderFeatureVo vo2 = new ConsumerOrderFeatureVo();
        vo2.setConsumerId("202");
        vo2.setActivityId("00");
        jsonValueMap.put("tt101", vo2);


        BulkResponse bulkResponse = elasticSearchUtil.multiIndexT("test", "test", jsonValueMap, 100);
        System.out.println("bulkResponse=" + JSON.toJSONString(bulkResponse));


    }

    public void testIndex() throws Exception {
        ConsumerOrderFeatureVo vo = new ConsumerOrderFeatureVo();
        vo.setConsumerId("101");
        vo.setDayOrderRank("3");

        IndexResponse indexResponse = elasticSearchUtil.indexT("test", "test", "tt01", vo);
        System.out.println("indexResponse=" + JSON.toJSONString(indexResponse));


    }

    public void testUpdate() throws Exception {
//        ConsumerOrderFeatureVo vo=new ConsumerOrderFeatureVo();
//        vo.setConsumerId("101");
//        vo.setDayOrderRank("3");
//
//        UpdateResponse updateResponse=elasticSearchUtil.update("test", "test", "t02", JSON.toJSONString(vo));
//        System.out.println("updateResponse="+JSON.toJSONString(updateResponse));
    }


    public void testGet() throws Exception {

    }

    public void testMultiGetValueString() throws Exception {
        Map<String, String> ret = elasticSearchUtil.multiGetValueString("nz_ad", "stat", Arrays.asList("tt02", "tt01", "tt07", "tt09", "tt101", "tt12"));
        System.out.println("ret=" + JSON.toJSONString(ret));
    }

    public void testMultiGetValueT() throws Exception {



        Map<String, AdvertCtrStatDto> ret = elasticSearchUtil.multiGetValueT(
                "nz_advert",
                "test_ctr",
                Arrays.asList("nz_ad_ctr_stat_null_1_2_1001_cd_",
                        "nz_ad_ctr_stat_null_2_2_1001_cd_",
                        "nz_ad_ctr_stat_null_2_2_1001_r7d_",
                        "nz_ad_ctr_stat_null_2_2_1001_r7d_",
                        "nz_ad_ctr_stat_null_1_1_global_cd_",
                        "nz_ad_ctr_stat_null_1_1_global_cd_",
                        "nz_ad_ctr_stat_null_2_1_global_r7d_",
                        "nz_ad_ctr_stat_null_2_1_global_r7d_"), AdvertCtrStatDto.class);
        System.out.println("ret=" + JSON.toJSONString(ret));
    }

    public void testMultiGet() throws Exception {

    }

    public void testDelete() throws Exception {

    }

    public void testMultiReplace() throws Exception {
        Map<String, ConsumerOrderFeatureVo> jsonValueMap = new HashMap<>();
        ConsumerOrderFeatureVo vo1 = new ConsumerOrderFeatureVo();
        vo1.setConsumerId("201-1");
        jsonValueMap.put("tt11", vo1);
        ConsumerOrderFeatureVo vo2 = new ConsumerOrderFeatureVo();
        vo2.setConsumerId("202-2-2");
//        vo2.setActivityId("001");
        jsonValueMap.put("tt12", vo2);


        BulkResponse bulkResponse = elasticSearchUtil.multiUpsertT("test", "test", jsonValueMap, 100);
        System.out.println("bulkResponse=" + JSON.toJSONString(bulkResponse));
    }


    public void testUpdateWithInsert() throws Exception {
        ConsumerOrderFeatureVo vo = new ConsumerOrderFeatureVo();
        vo.setConsumerId("107-7");
        vo.setCurrentTime("tttt");
//        vo.setDayOrderRank("5");

        UpdateResponse updateResponse = elasticSearchUtil.upsertT("test", "test", "tt12", vo);
        System.out.println("updateResponse=" + JSON.toJSONString(updateResponse));
    }

}