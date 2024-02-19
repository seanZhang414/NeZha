package cn.com.duiba.nezha.compute.api.cachekey;
/**
 * Created by pc on 2017/2/17.
 */
public class NezhaStatKey {

    public static final String PROFIX = "nze_ad_stat"; //

    /**
     * 哪吒引擎模型预估Hbase存储
     * @param algType
     * @param advertId
     * @param appId
     * @return
     */
    public static String getNezhaStatHbaseKey(Long algType,
                                                    Long advertId,
                                                    Long appId) {


        return PROFIX+"_"+algType+"_"+advertId+"_"+appId;


    }

    /**
     * 哪吒引擎模型预估MongoDb存储
     * @param algType
     * @param advertId
     * @param appId
     * @return
     */
    public static String getNezhaStatMongoDbKey(Long algType,
                                              Long advertId,
                                              Long appId) {

        return PROFIX+"_"+algType+"_"+advertId+"_"+appId;


    }
}
