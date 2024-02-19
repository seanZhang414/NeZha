package cn.com.duiba.nezha.compute.biz.check;

import cn.com.duiba.nezha.compute.biz.utils.mongodb.MongoUtil;
import junit.framework.TestCase;

/**
 * Created by pc on 2017/3/13.
 */
public class AdvertStatCheckTest2 extends TestCase {

    public String advert_id;
    public String app_id;

    public void setUp() throws Exception {
        super.setUp();

        advert_id="10275";
        app_id ="26744";

    }

    public void tearDown() throws Exception {

    }


    /**
     * 数据回流数据，mongodb线上读取
     *
     * @throws Exception
     */
    public void testGetMongodbAdvertStat() throws Exception {



        String mongoStatDto1 = MongoUtil.getMongoDb().findById("advert_material_info", "24215_7260_7375");
        String mongoStatDto2 = MongoUtil.getMongoDb().findById("advert_material_info", "24215_7260_15235");
        String mongoStatDto3 = MongoUtil.getMongoDb().findById("advert_material_info", "-1_14329_16414");
        String mongoStatDto4 = MongoUtil.getMongoDb().findById("advert_material_info", "-1_7260_15235");
        System.out.println(mongoStatDto1);
        System.out.println(mongoStatDto2);
        System.out.println(mongoStatDto3);
        System.out.println(mongoStatDto4);



        //String mongoStatDto1 = MongoUtil.getMongoDb().findById("hourly_data", "nz_ad_s_s_d_22762_7260_null_2017091213");
        //System.out.println(mongoStatDto1);

        //15235 7.6%  7375 4.1%

    }
    

}