package cn.com.duiba.tuia.engine.activity.service.impl.strategy.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by shenjunlin on 2017/8/15.
 */
@Component
public class StrategyEngineFactory {

    @Autowired
    private NOMaterialStrategyEngine noMaterialStrategyEngine;
    @Autowired
    private ActivitySelector4ManualStrategyEngine activitySelector4ManualStrategyEngine;
    @Autowired
    private ActivitySelector4XStrategyEngine activitySelector4XStrategyEngine;

    public NOMaterialStrategyEngine getNoMaterialStrategyEngine() {
        return this.noMaterialStrategyEngine;
    }

    public ActivitySelectorStrategyEngine getActivitySelector4ManualStrategyEngine() {
        return this.activitySelector4ManualStrategyEngine;
    }

    public ActivitySelectorStrategyEngine getActivitySelector4XStrategyEngine() {
        return this.activitySelector4XStrategyEngine;
    }
}
