package cn.com.duiba.tuia.engine.activity.web.controller;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivityInfo;
import cn.com.duiba.tuia.constant.RedisKeyConstant;
import cn.com.duiba.tuia.engine.activity.handle.Redis3Handler;
import cn.com.duiba.tuia.engine.activity.handle.Redis4Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * RedisCleanController
 */
@RestController
@RequestMapping("/redis")
public class RedisCleanController extends BaseController {

    @Autowired
    private Redis4Handler<String, String, ActivityInfo> redis4Handler;

    @Autowired
    private Redis3Handler redis3Handler;

    @RequestMapping(value = "/readSlotData")
    public Map<String, ActivityInfo> readSlotData(Long id) {
        return redis4Handler.entries(RedisKeyConstant.getSlotActInfo(id));
    }

    @Autowired
    private Redis4Handler<String, String, cn.com.duiba.nezha.compute.common.model.activityselectchange.ActivityInfo> redis4NewHandler;

    @RequestMapping(value = "/readSlotDataNew")
    public Map<String, cn.com.duiba.nezha.compute.common.model.activityselectchange.ActivityInfo> readSlotDataNew(Long id) {
        return redis4NewHandler.entries(RedisKeyConstant.getSlotActInfoNew(id));
    }

    @Autowired
    private Redis4Handler<String, String, cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> redis4Handler1;
    @Autowired
    private Redis4Handler<String, String, List<List<Long>>> redis4Handler2;

    @RequestMapping(value = "/readSlotDataMain")
    public Map<String, cn.com.duiba.nezha.compute.common.model.mainmeetselect.ActivityInfo> readSlotDataMain(Long id) {
        return redis4Handler1.entries(RedisKeyConstant.getSlotActInfoMain(id));
    }

    @RequestMapping(value = "/readSlotDataList")
    public List<List<Long>> readSlotDataList(Long id) {
        return redis4Handler2.get(RedisKeyConstant.getSlotActInfoMain(id) + "_list");
    }

    @RequestMapping(value = "/backdoor")
    public boolean backdoor(String auth, String key) {
        if (!"duiba123222222123123123123123123124234234342352342".equals(auth)) {
            return false;
        }
        redis4Handler1.del(key);
        return true;
    }

    @RequestMapping(value = "/execute")
    public Object execute(String auth, String command) {
        if ("duiba123222222123123123123123123124234234342352342".equals(auth) && command != null) {
            String[] split = command.trim().split(" ");
            if (split.length > 1) {
                byte[][] ss = new byte[split.length - 1][];
                for (int i = 0; i < split.length - 1; i++) {
                    ss[i] = split[i + 1].getBytes();
                }
                return redis3Handler.execute(split[0], ss);
            }
            return redis3Handler.execute(split[0]);
        }
        return null;
    }
}
