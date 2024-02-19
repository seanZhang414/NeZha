package cn.com.duiba.nezha.compute.biz.utils.es;

import cn.com.duiba.nezha.compute.api.dto.AdvertCtrStatDto;
import cn.com.duiba.nezha.compute.biz.conf.ElasticSearchUtilConf;
import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.biz.vo.AdvertCtrStatVo;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2017/3/21.
 */
public class ElasticSearchUtilTest2 extends TestCase {

    private ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil(ElasticSearchUtilConf.esConfig);

    public void setUp() throws Exception {
        super.setUp();
        Map<String,String> s=new  HashMap<>();
        s.put(null,"asda");
        System.out.println("map="+JSON.toJSONString(s));

    }

    public void tearDown() throws Exception {

    }


    public void testMultiGetValueT() throws Exception {

        List<AdvertCtrStatVo> advertCtrStatVoList = new ArrayList<>();
        AdvertCtrStatDto dto = new AdvertCtrStatDto();
        dto.setCtr(0.6);

        AdvertCtrStatVo advertCtrStatVo2 = new AdvertCtrStatVo();
        advertCtrStatVo2.setAdvertId("7515");
        advertCtrStatVo2.setAdvertType("1");
        advertCtrStatVo2.setStatDimId("1022");
        advertCtrStatVo2.setAdvertCtrStatDto(dto);
        advertCtrStatVoList.add(advertCtrStatVo2);


        Map<String, String> jsonKVMap = new HashMap<>();
        // 同步
        if (advertCtrStatVoList != null) {

            for (AdvertCtrStatVo advertCtrStatVo : advertCtrStatVoList) {
                String advertId = advertCtrStatVo.getAdvertId();
                String advertType = advertCtrStatVo.getAdvertType();
                String advertStatDimType = advertCtrStatVo.getAdvertStatDimType();
                String statDimId = advertCtrStatVo.getStatDimId();
                String statIntervalId = advertCtrStatVo.getStatIntervalId();
                AdvertCtrStatDto advertCtrStatDto = advertCtrStatVo.getAdvertCtrStatDto();
                System.out.println("dto=" + JSON.toJSONString(dto));

//                String keyId = AdvertStatKey.getAdvertCtrStatRedisKey(advertId,
//                        advertType,
//                        advertStatDimType,
//                        statDimId,
//                        statIntervalId);
//                Map<String, String> map = new HashedMap();
//                map.put(advertId, JSON.toJSONString(advertCtrStatDto));
//                jsonKVMap.put(keyId, JSON.toJSONString(map));

            }
        }
        elasticSearchUtil.multiUpsert("testindex", "testtype4", jsonKVMap, 200, ProjectConstant.WEEK_1_EXPIRE);
    }

    public void testUpdateWithInsert() throws Exception {
        Map<String, String> jsonKVMap = new HashMap<>();
        Map vo = new HashMap<>();
        vo.put("f1","v1");
        vo.put("f2","v20");
        vo.put("f3",JSON.toJSONString(vo));

        jsonKVMap.put("r100", JSON.toJSONString(vo));
//        elasticSearchUtil.upsertT("testindex", "testtype2", "r1", vo,100);


        Map vo1 = new HashMap<>();
        vo1.put("f1","v1");
        vo1.put("f2","{\"chargeCnt\":10,\"ctr\":1.11112,\"launchCnt\":9}");
        vo1.put("f10",JSON.toJSONString(vo1));
        jsonKVMap.put("r101", JSON.toJSONString(vo1));
//        elasticSearchUtil.upsertT("testindex", "testtype2", "3", vo1,100);
        elasticSearchUtil.multiUpsert("testindex", "testtype3", jsonKVMap, 200, ProjectConstant.WEEK_1_EXPIRE);

    }


}