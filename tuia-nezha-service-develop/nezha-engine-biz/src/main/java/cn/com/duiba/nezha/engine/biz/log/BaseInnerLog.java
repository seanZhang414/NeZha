package cn.com.duiba.nezha.engine.biz.log;

import cn.com.duiba.nezha.engine.biz.entity.nezha.advert.BizLogEntity;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: BaseInnerLog.java , v 0.1 2017/7/20 下午4:49 ZhouFeng Exp $
 */
public class BaseInnerLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInnerLog.class);

    /**
     * 广告平台日志统一分组
     */
    public static final String ADVERT_INNER_LOG_GROUP = "1";

    /**
     * 发券日志
     */
    public static final String ADVERT_INNER_LOG_RECOMMEND = "38";

    public static final String ADVERT_INNER_LOG_FEATURE = "48";


    private static final String dateFormatter = "yyyy-MM-dd HH:mm:ss";

    public static void log(BizLogEntity bizLogEntity) {

        if (bizLogEntity != null) {
            BaseInnerRuleDto ruleDto = new BaseInnerRuleDto();
            ruleDto.setGroup(ADVERT_INNER_LOG_GROUP);
            ruleDto.setJson(JSON.toJSONString(bizLogEntity));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatter);
            ruleDto.setTime(simpleDateFormat.format(new Date()));
            ruleDto.setType(ADVERT_INNER_LOG_RECOMMEND);
            LOGGER.info(JSON.toJSONString(ruleDto));

        }

    }

    public static void log(Map<String, String> featureMap) {

        if (MapUtils.isNotEmpty(featureMap)) {
            BaseInnerRuleDto ruleDto = new BaseInnerRuleDto();
            ruleDto.setGroup(ADVERT_INNER_LOG_GROUP);
            ruleDto.setJson(JSON.toJSONString(featureMap));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatter);
            ruleDto.setTime(simpleDateFormat.format(new Date()));
            ruleDto.setType(ADVERT_INNER_LOG_FEATURE);
            LOGGER.info(JSON.toJSONString(ruleDto));

        }

    }

    private BaseInnerLog() {
        // 不可实例化类
    }

}
