package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.common.params.Params;
import scala.collection.Iterator;

public interface ILogProcessServer<LV, LPV> {


    /**
     * 处理入口
     *
     * @param partitionOfRecords
     * @param logType
     */
    void run(Iterator<String> partitionOfRecords, Long logType,String topic,Params.AdvertLogParams params);


    /**
     * 推啊广告发券处理
     *
     * @param logStr
     * @throws Exception
     */
    LV logParse(String logStr, Long logType);


    /**
     * 推啊广告发券处理
     *
     * @param log
     * @throws Exception
     */

    LPV logProcess(LV log,Params.AdvertLogParams params);

    /**
     * 参数合法性检验
     *
     * @param log
     * @return
     */
    Boolean paramsValid(LV log);

}
