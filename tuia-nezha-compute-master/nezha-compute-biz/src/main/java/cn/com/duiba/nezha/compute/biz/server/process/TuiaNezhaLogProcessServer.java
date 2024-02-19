package cn.com.duiba.nezha.compute.biz.server.process;


import cn.com.duiba.nezha.compute.api.dto.NezhaStatDto;
import cn.com.duiba.nezha.compute.biz.bo.StatCheckBo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.StatStatusConstant;
import cn.com.duiba.nezha.compute.biz.log.NezhaLog;
import cn.com.duiba.nezha.compute.biz.server.biz.NezhaEngineStatUpdateServer;
import cn.com.duiba.nezha.compute.biz.vo.AdvertStatVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderFeatureSyncVo;
import cn.com.duiba.nezha.compute.biz.vo.OrderLogResultVo;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.params.Params;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import com.alibaba.fastjson.JSONObject;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.List;

public class TuiaNezhaLogProcessServer extends BaseProcessServer implements ILogProcessServer<NezhaLog, NezhaStatDto> {

    public static TuiaNezhaLogProcessServer instance;


    public static TuiaNezhaLogProcessServer getInstance() {
        if (instance == null) {
            instance = new TuiaNezhaLogProcessServer();
        }
        return instance;
    }

    public void run(Iterator<String> partitionOfRecords, Long logType, String topic, Params.AdvertLogParams params) {

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run start");
        List<NezhaStatDto> nezhaStatDtoList = new ArrayList<>();

        while (partitionOfRecords.hasNext()) {

            // 1
            String logStr = (String) partitionOfRecords.next();
//            // 2
            NezhaStatDto nezhaStatDto = logProcess(logParse(logStr, logType), params);

            if (nezhaStatDto != null) {
                nezhaStatDtoList.add(nezhaStatDto);
            }
//            }
        }


        StatCheckBo.updateTime(StatStatusConstant.COL_NEZHA);


//        if (!StatCheckBo.getStatDelayStatus()) {

            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  NezhaEngineStatUpdateServer.syncES(nezhaStatDtoList, topic)");
            NezhaEngineStatUpdateServer.syncES(nezhaStatDtoList, topic);
            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run end");

//        }else{
//            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " WARN: batch run delay stop return");
//        }

    }


    /**
     * 推啊广告发券处理
     *
     * @param logStr
     * @throws Exception
     */
    public NezhaLog logParse(String logStr, Long logType) {
        NezhaLog logVo = new NezhaLog();
        if (logStr != null) {
            try {
                String logJsonStr = getJsonStr(logStr);
                String timeStr = getTimeStr(logStr);
                logVo = JSONObject.parseObject(logJsonStr, NezhaLog.class);
                logVo.setCurrentTime(timeStr);
                if (timeStr == null) {
                    logVo.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
                }
                StatCheckBo.setGmtTime(timeStr);
//                System.out.println("log= " + JSONObject.toJSONString(logVo));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logVo;
//        return null;
    }


    /**
     * 推啊广告发券处理
     *
     * @param nezhaLog
     * @throws Exception
     */

    public NezhaStatDto logProcess(NezhaLog nezhaLog, Params.AdvertLogParams params) {
        NezhaStatDto retVo = null;
        try {
            if (paramsValid(nezhaLog)) {
                // 业务 推荐引擎预处理
                retVo = NezhaEngineStatUpdateServer.logProcess(nezhaLog, params);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retVo;
    }

    /**
     * 参数合法性检验
     *
     * @param nezhaLog
     * @return
     */
    public Boolean paramsValid(NezhaLog nezhaLog) {
        Boolean ret = false;

        ret = true;
        return ret;
    }


}
