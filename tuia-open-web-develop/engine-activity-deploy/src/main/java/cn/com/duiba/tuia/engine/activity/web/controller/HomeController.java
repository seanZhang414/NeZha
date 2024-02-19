package cn.com.duiba.tuia.engine.activity.web.controller;

import javax.annotation.Resource;

import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.engine.activity.service.ActivityFilterService;
import cn.com.duiba.tuia.ssp.center.api.dto.RspActivityDto;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Resource
    private ActivityFilterService activityFilterService;
    private static final String MSG_INDEX = "welcome tuia-open-web";

    @RequestMapping(value = "/")
    @ResponseBody
    public String index() {
        return MSG_INDEX;
    }

    @RequestMapping(value = "/getNextActivity")
    @ResponseBody
    public String getNextActivity(@RequestParam(value = "slotId") Long slotId) {
        RspActivityDto rspActivityDto = activityFilterService.getNextActivity(slotId, "", FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT);
        return JSON.toJSONString(rspActivityDto);
    }
    @RequestMapping(value = "/compareActivity")
    @ResponseBody
    public String compareActivityInfo(@RequestParam(value = "slotId") Long slotId) {
        Boolean compareActivityInfo = activityFilterService.compareActivityInfo(slotId);
        return JSON.toJSONString(compareActivityInfo);
    }
}