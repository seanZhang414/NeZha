package cn.com.duiba.tuia.engine.activity.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * HeartBeatController
 */
@Controller
@RequestMapping("/test")
public class HeartBeatController {

    /**
     * LB心跳监测端口
     * 
     * @param response
     * @return boolean
     */
    @RequestMapping("/obtainAdvert")
    @ResponseBody
    public boolean obtainAdvert(HttpServletResponse response) {
        return true;
    }

    /**
     * https心跳检测
     *
     * @return boolean
     */
    @RequestMapping("/heartBeat")
    @ResponseBody
    public String heartBeat() {
        return "success";
    }
}
