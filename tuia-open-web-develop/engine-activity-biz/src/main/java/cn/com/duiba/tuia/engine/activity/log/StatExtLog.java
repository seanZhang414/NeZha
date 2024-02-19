package cn.com.duiba.tuia.engine.activity.log;

import cn.com.duiba.tuia.engine.activity.model.req.UserInfoLogReq;
import cn.com.duiba.tuia.utils.JsonUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author :  chengdeman
 * @Description : 统计扩展日志   20180131-数据算法需要另外一个分析日志，暂用这个扩展日志
 * @Date :  18/1/31
 */
public final class StatExtLog {

    private static Logger log = LoggerFactory.getLogger(StatExtLog.class);

    private StatExtLog() {
    }

    /**
     * log
     * 
     * @param req
     */
    public static void userInfoExtLog(UserInfoLogReq infoLogReq) {
        String logInfo = format(infoLogReq);
        log.info(logInfo);
    }

    private static String format(UserInfoLogReq req) {
        JSONObject json = JSONObject.parseObject(JSON.toJSONString(req));
        json.put("md",null);
        if(StringUtils.isNotBlank(req.getMd())) {
            JSONObject md = JSONObject.parseObject(req.getMd());
            if(md != null && CollectionUtils.isNotEmpty(md.entrySet())) {
                md.entrySet().forEach(o -> {
                    json.put(o.getKey(), o.getValue());
                });
            }
        }
        return JsonUtils.objectToString(json);
    }
}
