package cn.com.duiba.nezha.compute.biz.bo;

import cn.com.duiba.nezha.compute.api.cachekey.AdvertStatKey;
import cn.com.duiba.nezha.compute.api.vo.AdvertStatStatusVo;
import cn.com.duiba.nezha.compute.biz.constant.htable.AdvertStatConstant;
import cn.com.duiba.nezha.compute.biz.constant.htable.StatStatusConstant;
import cn.com.duiba.nezha.compute.biz.utils.hbase.HbaseUtil;
import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.util.DateUtil;
import cn.com.duiba.thirdparty.alarm.Dingding;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;

public class StatCheckBo {


    private static String tableName = StatStatusConstant.TABLE_NAME;

    private static String webhook = "https://oapi.dingtalk.com/robot/send?access_token=65f6d52acb34b661bc4be29e3e6e8576862a5246d6f6772da27d324299691006";

    private static int warnThreshold = 5;
    private static int delayThreshold = 10;
    private static String gmtTime = null;

    public static int getDelayThreshold() {
        return delayThreshold;
    }

    public static void setDelayThreshold(int delayThreshold2) {
        delayThreshold = delayThreshold2;
    }


    public static String getGmtTime() {
        return gmtTime;
    }

    public static void setGmtTime(String gmtTime2) {
        gmtTime = gmtTime2;
    }


    /**
     * 更新日志时间
     *
     * @throws Exception
     */
    public static void sendDingDing(String content) {

        try {
            updateTime(AdvertStatConstant.FM_WARN, DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));
//            send(webhook, content);
            Dingding.send(webhook, content);

        } catch (Exception e) {
            System.out.println(" updateTime happend error " + e);
        }

    }


    /**
     * 更新日志时间
     *
     * @throws Exception
     */
    public static void updateTime(String colName) {

        try {
            if (colName != null && gmtTime != null) {

                updateTime(colName, gmtTime);

            }

        } catch (Exception e) {
            System.out.println(" updateTime happend error " + e);
        }

    }


    /**
     * 更新日志时间
     *
     * @throws Exception
     */
    public static void updateTime(String colName, String gmtTime) throws Exception {

        if (colName != null && gmtTime != null) {


            HbaseUtil hbaseUtil = HbaseUtil.getInstance();

            String rowKey = AdvertStatKey.getAdvertStatStatusKey();


            Map<String, Map<String, String>> fvMap = new HashMap<>();

            Map<String, String> ctrColValMap = new HashMap<>();

            ctrColValMap.put(colName, gmtTime);

            fvMap.put(StatStatusConstant.FM_UPDATE_TIME, ctrColValMap);

            //
            hbaseUtil.insert(tableName, rowKey, fvMap);
            System.out.println("table " + tableName + ",updateTime= " + gmtTime);
        }

    }


    /**
     * @return
     * @throws Exception
     */
    public static Boolean getStatDelayStatus() {
        boolean ret = false;
        try {
            ret = getStatDelayStatus(getStatTime());
        } catch (Exception e) {
            System.out.println("getStatDelayStatus happend error " + e);
        }
        return ret;
    }

    /**
     * @return
     * @throws Exception
     */
    public static Boolean getStatDelayStatus(AdvertStatStatusVo advertStatStatusVo) throws Exception {
        boolean ret = false;

        try {

            String currentTime = advertStatStatusVo.getCurrentTime();
            String lastWarnTime = advertStatStatusVo.getLastWarnTime();
            String launchUpdateTime = advertStatStatusVo.getLaunchUpdateTime();
            String chargeUpdateTime = advertStatStatusVo.getChargeUpdateTime();
            String actClickUpdateTime = advertStatStatusVo.getActClickUpdateTime();
            String actExpUpdateTime = advertStatStatusVo.getActExpUpdateTime();
            String nezhaUpdateTime = advertStatStatusVo.getNezhaUpdateTime();

            List<Integer> delayList = new ArrayList<>();
            Integer launchDelayMinutes = getDelayMinutes(launchUpdateTime, currentTime);
            Integer chargeDelayMinutes = getDelayMinutes(chargeUpdateTime, currentTime);
            Integer actClickDelayMinutes = getDelayMinutes(actClickUpdateTime, currentTime);
            Integer actExpDelayMinutes = getDelayMinutes(actExpUpdateTime, currentTime);
            Integer lastWarnTimeMinutes = getDelayMinutes(lastWarnTime, currentTime);
            Integer nezhaDelayMinutes = getDelayMinutes(nezhaUpdateTime, currentTime);

            String titleDesc = "【告警】-【项目：数据回流】-【原因：数据消费延迟大于 " + warnThreshold + " 分钟】";
            String desc = "\n【详情】当前系统时间：" + currentTime + ", 上次警告时间：" + lastWarnTime + ", 距今时长：" + lastWarnTimeMinutes + " 分钟";
            String launchDesc = "\n【发券 日志】最后更新时间：" + launchUpdateTime + ", 延迟时长：" + launchDelayMinutes + " 分钟";
            String chargeDesc = "\n【计费 日志】最后更新时间：" + chargeUpdateTime + ", 延迟时长：" + chargeDelayMinutes + " 分钟";
            String actClickDesc = "\n【转换点击 日志】最后更新时间：" + actClickUpdateTime + ", 延迟时长：" + actClickDelayMinutes + " 分钟";
            String actExpDesc = "\n【转换曝光 日志】最后更新时间：" + actExpUpdateTime + ", 延迟时长：" + actExpDelayMinutes + " 分钟";
            String nezhaDesc = "\n【推荐引擎算法跟踪 日志】最后更新时间：" + nezhaUpdateTime + ", 延迟时长：" + nezhaDelayMinutes + " 分钟";
            String content = launchDesc + chargeDesc + actClickDesc + actExpDesc + nezhaDesc;

            System.out.println("数据消费状态： " + desc + content);

            if (getWarnStatus(launchDelayMinutes, chargeDelayMinutes, actClickDelayMinutes, nezhaDelayMinutes, lastWarnTimeMinutes)) {
                System.out.println("发送钉钉告警：");
                sendDingDing(titleDesc + desc + content);
            }


            if (getDataStopStatus(launchDelayMinutes, chargeDelayMinutes, actClickDelayMinutes, nezhaDelayMinutes, lastWarnTimeMinutes)) {
                ret = true;
            }

            System.out.println("是否停止数据回流： " + ret);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * @return
     * @throws Exception
     */
    public static Boolean getWarnStatus(Integer launchDelayMinutes,
                                        Integer chargeDelayMinutes,
                                        Integer actClickDelayMinutes,
                                        Integer nezhaDelayMinutes,
                                        Integer lastWarnTimeMinutes
    ) throws Exception {
        boolean ret = false;


        if (launchDelayMinutes != null && launchDelayMinutes > warnThreshold && (lastWarnTimeMinutes == null || lastWarnTimeMinutes > warnThreshold)) {
            ret = true;
        }

        if (actClickDelayMinutes != null && actClickDelayMinutes > warnThreshold && (lastWarnTimeMinutes == null || lastWarnTimeMinutes > warnThreshold)) {
            ret = true;
        }
        if (chargeDelayMinutes != null && chargeDelayMinutes > warnThreshold && (lastWarnTimeMinutes == null || lastWarnTimeMinutes > warnThreshold)) {
            ret = true;
        }

        if (nezhaDelayMinutes != null && nezhaDelayMinutes > warnThreshold && (lastWarnTimeMinutes == null || lastWarnTimeMinutes > warnThreshold)) {
            ret = true;
        }
        System.out.println("getWarnStatus=" + ret);
        return ret;
    }

    /**
     * @return
     * @throws Exception
     */
    public static Boolean getDataStopStatus(Integer launchDelayMinutes,
                                            Integer chargeDelayMinutes,
                                            Integer actClickDelayMinutes,
                                            Integer nezhaDelayMinutes,
                                            Integer lastWarnTimeMinutes) throws Exception {
        boolean ret = false;


        if (launchDelayMinutes != null && launchDelayMinutes > delayThreshold) {
            ret = true;
        }

        if (actClickDelayMinutes != null && actClickDelayMinutes > delayThreshold) {
            ret = true;
        }
        if (chargeDelayMinutes != null && chargeDelayMinutes > delayThreshold) {
            ret = true;
        }

        if (nezhaDelayMinutes != null && nezhaDelayMinutes > delayThreshold) {
            ret = true;
        }

        return ret;
    }


    /**
     * @return
     * @throws Exception
     */
    public static Boolean getStatDelayStatusOld(AdvertStatStatusVo advertStatStatusVo) throws Exception {
        boolean ret = false;

        String currentTime = advertStatStatusVo.getLaunchUpdateTime();
        String launchUpdateTime = advertStatStatusVo.getLaunchUpdateTime();
        String chargeUpdateTime = advertStatStatusVo.getChargeUpdateTime();
        String actClickUpdateTime = advertStatStatusVo.getActClickUpdateTime();


        if (getGreateThanThreshold(launchUpdateTime, currentTime, delayThreshold) ||
                getGreateThanThreshold(chargeUpdateTime, currentTime, delayThreshold) ||
                getGreateThanThreshold(actClickUpdateTime, currentTime, delayThreshold)) {

            ret = true;
        }
        System.out.println("currentTime= " + currentTime);
        System.out.println("launchUpdateTime= " + launchUpdateTime);
        System.out.println("chargeUpdateTime= " + chargeUpdateTime);
        System.out.println("actClickUpdateTime= " + actClickUpdateTime);


        System.out.println("getStatDelayStatus= " + ret);
        return ret;
    }


    /**
     * @return
     * @throws Exception
     */
    public static Integer getDelayMinutes(String updateTime, String currentTime) throws Exception {
        return DateUtil.getIntervalMinutes(updateTime, currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * @return
     * @throws Exception
     */
    public static Boolean getGreateThanThreshold(String updateTime, String currentTime, int deltaThreshold) throws Exception {
        boolean ret = false;
        if (updateTime != null) {
            Integer deltaTime = DateUtil.getIntervalMinutes(updateTime, currentTime, DateStyle.YYYY_MM_DD_HH_MM_SS);
            if (updateTime != null && deltaTime > deltaThreshold) {

                ret = true;
            }
        }

        return ret;
    }


    /**
     * @return
     * @throws Exception
     */
    public static Boolean getGreateThanThreshold(Integer delayMinute, int deltaThreshold) throws Exception {
        boolean ret = false;
        if (delayMinute != null && delayMinute > deltaThreshold) {

            ret = true;
        }

        return ret;
    }


    /**
     * @return
     * @throws Exception
     */
    public static AdvertStatStatusVo getStatTime() throws Exception {

        final AdvertStatStatusVo advertStatStatusVo = new AdvertStatStatusVo();

        advertStatStatusVo.setCurrentTime(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS));


        HbaseUtil hbaseUtil = HbaseUtil.getInstance();

        String rowKey = AdvertStatKey.getAdvertStatStatusKey();

        // 封装请求Map
        Map<String, Set<String>> fcMap = new HashMap<>();

        // 0 launch
        Set<String> colSet = new HashSet();

        // 0 launch
        colSet.add(StatStatusConstant.COL_LAUNCH);
        // 1 exposure
        colSet.add(StatStatusConstant.COL_EXPOSURE);
        // 2 click
        colSet.add(StatStatusConstant.COL_CLICK);
        // 3 charge
        colSet.add(StatStatusConstant.COL_CHARGE);

        // 4 act exp
        colSet.add(StatStatusConstant.COL_ACT_EXP);

        // 5 act click
        colSet.add(StatStatusConstant.COL_ACT_CLICK);

        // 6 warn
        colSet.add(StatStatusConstant.COL_WARN);

        // 7 nezha
        colSet.add(StatStatusConstant.COL_NEZHA);


        fcMap.put(StatStatusConstant.FM_UPDATE_TIME, colSet);

        // 数据读取
        hbaseUtil.getOneRow(tableName, rowKey, fcMap, new HbaseUtil.QueryCallback() {

                    @Override
                    public void process(List<Result> retList) throws Exception {

                        if (retList != null) {

                            Result ret = retList.get(0);

                            //
                            byte[] launchUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_LAUNCH));

                            byte[] chargeUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_CHARGE));

                            byte[] actExpUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_ACT_EXP));


                            byte[] actClickUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_ACT_CLICK));

                            byte[] warnUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_WARN));

                            byte[] nezhaUpdateTimeB = ret.getValue(
                                    Bytes.toBytes(StatStatusConstant.FM_UPDATE_TIME),
                                    Bytes.toBytes(StatStatusConstant.COL_NEZHA));


                            advertStatStatusVo.setLaunchUpdateTime(Bytes.toString(launchUpdateTimeB));
                            advertStatStatusVo.setChargeUpdateTime(Bytes.toString(chargeUpdateTimeB));

                            advertStatStatusVo.setActClickUpdateTime(Bytes.toString(actClickUpdateTimeB));
                            advertStatStatusVo.setActExpUpdateTime(Bytes.toString(actExpUpdateTimeB));
                            advertStatStatusVo.setLastWarnTime(Bytes.toString(warnUpdateTimeB));
                            advertStatStatusVo.setNezhaUpdateTime(Bytes.toString(nezhaUpdateTimeB));

                        }

                    }


                }

        );

        return advertStatStatusVo;
    }


//    public static String send(String roboot, String content) throws Exception {
//        try {
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//
//            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
//            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 3000);
//            HttpPost httpPost = new HttpPost(roboot);
//            httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
//            String textMsg = "{ \"msgtype\": \"text\", \"text\": {\"content\": \"" + content + "\"}}";
//            StringEntity se = new StringEntity(textMsg, "utf-8");
//            httpPost.setEntity(se);
//
//            HttpResponse response = httpClient.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            return EntityUtils.toString(entity, "utf-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
//        return  null;
//    }


}
