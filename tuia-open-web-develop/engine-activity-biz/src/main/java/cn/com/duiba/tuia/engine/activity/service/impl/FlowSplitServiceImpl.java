package cn.com.duiba.tuia.engine.activity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.com.duiba.tuia.constant.FlowSplitConstant;
import cn.com.duiba.tuia.engine.activity.service.FlowSplitService;
import cn.com.duiba.tuia.ssp.center.api.constant.SplitConstant;
import cn.com.duiba.tuia.utils.FlowSplit;
import cn.com.duiba.tuia.utils.FlowSplitUtils;

/**
 * @author xuyenan
 * @since 2017/1/24
 */
@Service("flowSplitService")
public class FlowSplitServiceImpl implements InitializingBean, FlowSplitService {

    private List<FlowSplit> actOutput;
    private List<FlowSplit> materialOutput;
    private double          materialDiff;
    private int             actNewToOld;
    private int             materialNewToOld;
    
    private List<FlowSplit> manualActOutput;
    
    private List<FlowSplit> engineOutput;

    private List<FlowSplit> engineOutputTest;

    @Value("${tuia.engine.activity.flowSplit.config}")
    private String          config;

    @Override
    public int getActOutputWay() {
        return FlowSplitUtils.split(actOutput);
    }

    @Override
    public int getMaterialOutputWay() {
        return FlowSplitUtils.split(materialOutput);
    }

    @Override
    public double getMaterialDiffPercent() {
        return materialDiff;
    }

    @Override
    public int getActNewToOldBound() {
        return actNewToOld;
    }

    @Override
    public int getMaterialNewToOldBound() {
        return materialNewToOld;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, Double> map = new HashMap<>();
        for (String flowSplit : config.split(SplitConstant.SPLIT_SEMICOLON)) {
            map.put(flowSplit.split(SplitConstant.SPLIT_COLON)[0], Double.valueOf(flowSplit.split(SplitConstant.SPLIT_COLON)[1]));
        }

        actOutput = new ArrayList<>(6);
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_ENGINE_OUTPUT, map.get("ActEngineOutput")));
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_ENGINE2_OUTPUT, map.get("ActEngine2Output")));
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_MANUAL_OUTPUT, map.get("ActManualOutput")));
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_NEW_OUTPUT, map.get("ActNewOutput")));
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_RPM_ENGINE_OUTPUT, map.get("ActRpmEngineOutput")));
        actOutput.add(new FlowSplit(FlowSplitConstant.ACT_RPM2_ENGINE_OUTPUT, map.get("ActRpm2EngineOutput")));

        materialOutput = new ArrayList<>(2);
        materialOutput.add(new FlowSplit(FlowSplitConstant.NEW_MATERIAL_OUTPUT, map.get("NewMaterialOutput")));
        materialOutput.add(new FlowSplit(FlowSplitConstant.OLD_MATERIAL_OUTPUT, map.get("OldMaterialOutput")));
        
        manualActOutput = new ArrayList<>(2);
        manualActOutput.add(new FlowSplit(FlowSplitConstant.MANUAL_ACT_ENGINE_OUTPUT, map.get("ManualActEngineOutPut")));
        manualActOutput.add(new FlowSplit(FlowSplitConstant.MANUAL_ACT_ENGINE2_OUTPUT, map.get("ManualActEngine2OutPut")));
        
        engineOutput = new ArrayList<>(2);
        engineOutput.add(new FlowSplit(FlowSplitConstant.ENGINE_MANUAL_OUTPUT, map.get("EngineManualOutput")));
        engineOutput.add(new FlowSplit(FlowSplitConstant.ENGINE_ALL_OUTPUT, map.get("EngineAllOutput")));

        engineOutputTest = new ArrayList<>(3);
        engineOutputTest.add(new FlowSplit(FlowSplitConstant.MANUAL_OUTPUT, map.get("ManualOutput")));
        engineOutputTest.add(new FlowSplit(FlowSplitConstant.ENGINE_OUTPUT, map.get("EngineOutput")));
        engineOutputTest.add(new FlowSplit(FlowSplitConstant.ENGINE_LIMIT_OUTPUT, map.get("EngineLimitOutput")));
        
        materialDiff = map.get("MaterialDiffPercent");
        actNewToOld = map.get("ActNewToOldBound").intValue();
        materialNewToOld = map.get("MaterialNewToOldBound").intValue();
    }

    @Override
    public int getManualActOutput() {
        return FlowSplitUtils.split(manualActOutput);
    }

    @Override
    public int getEngineOutput() {
        return FlowSplitUtils.split(engineOutput);
    }

    @Override
    public int getEngineOutputTest() {
        return FlowSplitUtils.split(engineOutputTest);
    }
}
