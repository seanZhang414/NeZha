package cn.com.duiba.nezha.compute.biz.app;

import cn.com.duiba.nezha.compute.alg.ftrl.FMModel;
import cn.com.duiba.nezha.compute.biz.bo.SampleBo;
import cn.com.duiba.nezha.compute.biz.bo.SyncBo;
import cn.com.duiba.nezha.compute.biz.constant.ModelConstant;
import cn.com.duiba.nezha.compute.biz.dto.LaunchLog;
import cn.com.duiba.nezha.compute.biz.ps.PsAgent;
import cn.com.duiba.nezha.compute.biz.support.LogParse;
import cn.com.duiba.nezha.compute.core.LabeledPoint;
import cn.com.duiba.nezha.compute.core.enums.DateStyle;
import cn.com.duiba.nezha.compute.core.model.local.LocalModel;
import cn.com.duiba.nezha.compute.core.model.local.LocalVector;
import cn.com.duiba.nezha.compute.core.util.DateUtil;
import cn.com.duiba.nezha.compute.feature.vo.Feature;
import cn.com.duiba.nezha.compute.mllib.evaluate.Evaluater;
import cn.com.duiba.nezha.compute.mllib.fm.ftrl.*;

import com.alibaba.fastjson.JSON;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.List;


public class SparseFMWithFTRLApp {

    public static void runTest(String featureModelId, boolean isCtr, Iterator<String> partitionOfRecords, double sampleRatio, boolean isReplay, int partNums) throws Exception {

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run start");
//
        Boolean status = true;
        // 1 获取订单列表
        List<String> orderIdList = new ArrayList<>();
        while (partitionOfRecords.hasNext()) {

            String orderId = partitionOfRecords.next();
            if (Math.random() > (1 - 1.0)) {
                orderIdList.add(orderId);
            }


        }

        // 2 获取样本列表
        LabeledPoint[] dataArray = SampleBo.getSampleByOrderIdList(featureModelId, isCtr, orderIdList);


        // 3 训练模型
        if (dataArray.length > 0) {
            if (isReplay) {

                replay(featureModelId, dataArray);
            } else {
                train(featureModelId, dataArray, partNums);
            }


        }


        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run end");
    }


    public static void runOnLine(String featureModelId, boolean isCtr, Iterator<String> partitionOfRecords, int delay, boolean isReplay, int partNums, double sampleRatio) throws Exception {

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run start");
//
        Boolean initStatus = true;

        // 1 获取订单列表
        List<String> orderIdList = new ArrayList<>();
        int minDelay = 0;
        int maxDelay = 0;

        while (partitionOfRecords.hasNext()) {


            // 1 解析日志
            LaunchLog log = LogParse.logParse(partitionOfRecords.next());

            if (Math.random() < sampleRatio) {
                if (initStatus) {
                    minDelay = log.getDelaySeconds();
                    maxDelay = log.getDelaySeconds();
                    initStatus = false;
                }
                if (log.getDelaySeconds() < minDelay) {
                    minDelay = log.getDelaySeconds();
                }

                if (log.getDelaySeconds() > maxDelay) {
                    maxDelay = log.getDelaySeconds();
                }


                orderIdList.add(log.getOrderId());
            }


        }

        // 2 获取样本列表
        System.out.println("minDelay= " + minDelay + ",maxDelay=" + maxDelay);
        if (minDelay <= delay) {
            int sleepS = (delay - minDelay);
            System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  sleep（" + sleepS + ")");
            Thread.sleep(1000 * sleepS);
        }
        LabeledPoint[] dataArray = SampleBo.getSampleByOrderIdList(featureModelId, isCtr, orderIdList);

        // 3 训练模型
        if (dataArray.length > 0) {
            if (isReplay) {
                System.out.println("replay");
                replay(featureModelId, dataArray);
            } else {
                System.out.println("train");
                train(featureModelId, dataArray, partNums);
            }


        }

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  batch run end");
    }


    /**
     * @param modelId
     * @param data
     * @throws Exception
     */
    public static void replay(String modelId, LabeledPoint[] data) throws Exception {

        FMFTRL fmftrl = SyncBo.getLocalModel(modelId);

        FM PsModel = fmftrl.toFM();
        FMModel JModel = fmftrl.toJFM();

//        System.out.println("ps model="+JSON.toJSONString(PsModel));
//        System.out.println("J model="+JSON.toJSONString(JModel));

        Evaluater evaluater = new Evaluater();
        for (LabeledPoint point : data) {
            Feature feature = new Feature(point.feature().size(),
                    point.feature().indices(),
                    point.feature().values());

            double preJVal = JModel.predict(feature);
            double prePsVal = PsModel.predict(point.feature());

            if (Math.random() > 0.1) {
                System.out.println("preJVal=" + preJVal + ",prePSVal=" + prePsVal + ",label=" + point.label());
            }

            evaluater.add(point.label(), preJVal, 1.0);

        }
        evaluater.getLevelMap("PsModel replay");
        evaluater.print();

//        SyncBo.delete(modelId);


    }

    /**
     * @param modelId
     * @param dataArray
     * @throws Exception
     */
    public static void train(String modelId, LabeledPoint[] dataArray, int partNums) throws Exception {

        int dim = dataArray[0].feature().size();

        PsAgent psAgent = new PsAgent();
        psAgent.setModelId(modelId);
        psAgent.setParSize(ModelConstant.MODEL_PAR_SIZE);
        psAgent.setDim(dim);

//        psAgent.delete();

        // prepare hyperParams
        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + " start, prepare hyperParams ，dim=" + dim);
        FMFTRLHyperParams hyperParams = SyncBo.getFMFTRLHyperParams(modelId);

        // prepare searchLocalModel
        LocalModel searchLocalModel = SparseFMWithFTRL.searchModel(dataArray, dim, hyperParams.factorNum());

        // pull ps model
        boolean status = psAgent.pull(searchLocalModel, false);

        // ps model to local model
        LocalModel localModel = psAgent.getLocalModel();

        // train model
        SparseFMWithFTRL2 model = new SparseFMWithFTRL2();

        model.setAlpha(hyperParams.alpha()).
                setBeta(hyperParams.beta()).
                setLambda1(hyperParams.lambda1()).
                setLambda2(hyperParams.lambda2()).
                setRho1(hyperParams.rho1()).
                setRho2(hyperParams.rho2()).
                setLocalModel(localModel);

        model.setDim(dim).
                setFacotrNum(hyperParams.factorNum());

        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  model.train(dataArray)");
        model.train(dataArray, partNums);

        // get incr model
        LocalModel incrModel = model.getLocalIncrModel();

        // push ps  model
        System.out.println(DateUtil.getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS_SSS) + "  psAgent.push(incrModel)");
        psAgent.push(incrModel, partNums);

    }


}
