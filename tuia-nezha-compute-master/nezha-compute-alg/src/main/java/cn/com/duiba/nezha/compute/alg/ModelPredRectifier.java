package cn.com.duiba.nezha.compute.alg;

import cn.com.duiba.nezha.compute.api.dto.CorrectionInfo;
import cn.com.duiba.nezha.compute.api.dto.NezhaStatDto;
import cn.com.duiba.nezha.compute.api.enums.PredRectifierEnum;
import cn.com.duiba.nezha.compute.common.util.AssertUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pc on 2017/9/12.
 */
public class ModelPredRectifier {
    private static Logger logger = Logger.getLogger(ModelPredRectifier.class);


    /**
     * 批量模型预估值统计水平纠偏
     *
     * @param advertCorrectionInfoList
     * @throws Exception
     */
    public static void getCorrectionFactor(List<CorrectionInfo> advertCorrectionInfoList) throws Exception {

        if (AssertUtil.isEmpty(advertCorrectionInfoList)) {
            return;
        }
        for (CorrectionInfo info : advertCorrectionInfoList) {
            if (info != null) {
                // 1 调节因子赋默认值
                info.setCorrectionFactor(1.0);
                info.setReconstructionFactor(1.0);
                setCorrectionFactor(info, PredRectifierEnum.COR);
            }

        }


    }

    /**
     * 批量模型预估值 统计水平纠偏 & 分布重构
     *
     * @param advertCorrectionInfoList
     * @throws Exception
     */
    public static void getCorrectionReconstructionFactor(List<CorrectionInfo> advertCorrectionInfoList) throws Exception {


        if (AssertUtil.isEmpty(advertCorrectionInfoList)) {
            return;
        }
        for (CorrectionInfo info : advertCorrectionInfoList) {
            if (info != null) {
                // 1 调节因子赋默认值
                info.setCorrectionFactor(1.0);
                info.setReconstructionFactor(1.0);

                setCorrectionFactor(info, PredRectifierEnum.REC);
                setReconstructionFactor(info);
            }

        }

    }


    /**
     * 模型预估值统计水平纠偏
     *
     * @param info
     * @throws Exception
     */
    public static void setCorrectionFactor(CorrectionInfo info, PredRectifierEnum predRectifierEnum) throws Exception {

        if (info == null) {
            logger.warn("setCorrectionFactor input invalid,with info = null");
            return;
        }
        if (info.getCurrentPreValue() == null) {
            logger.warn("setCorrectionFactor input invalid,with info.getCurrentPreValue() = null");
            return;
        }

        if (info.getCurrentPreValue() < 0.0000000001) {
            return;
        }

        if (info.getNezhaStatDto() == null) {
            return;
        }


        // 2 解析
        Long type = info.getType();
        Double currentPreValue = info.getCurrentPreValue();
        NezhaStatDto nezhaStatDto = info.getNezhaStatDto();

        Double avgPreValue = null;
        Double avgFeedbackValue = null;
        Double vUpperLimit = null;
        Double vLowerLimit = null;

        Double fUpperLimit = null;
        Double fLowerLimit = null;
        Long launchCnt = 0L;

        if (type.equals(1L)) {

            // CTR
            avgPreValue = nezhaStatDto.getPreCtrAvg();
            avgFeedbackValue = nezhaStatDto.getStatCtrAvg();
            launchCnt = nezhaStatDto.getCtrLaunchCnt();


            vUpperLimit = 0.9999;
            vLowerLimit = 0.01;

            fUpperLimit = 3.0;
            fLowerLimit = 0.2;

        }
        if (type.equals(2L)) {

            // CVR
            avgPreValue = nezhaStatDto.getPreCvrAvg();
            avgFeedbackValue = nezhaStatDto.getStatCvrAvg();
            launchCnt = nezhaStatDto.getCvrLaunchCnt();

            vUpperLimit = 0.9999;
            vLowerLimit = 0.0005;
            fUpperLimit = 3.0;
            fLowerLimit = 0.2;
        }


        Double newCurrentPreValue = correctionValue(currentPreValue, avgPreValue, avgFeedbackValue, vLowerLimit, vUpperLimit, predRectifierEnum);

        Double currentPreFactor = noiseSmoother(newCurrentPreValue / currentPreValue, fLowerLimit, fUpperLimit);

        info.setCorrectionFactor(currentPreFactor);


    }

    /**
     * 批量模型预估值 分布重构
     *
     * @param info
     * @throws Exception
     */
    public static void setReconstructionFactor(CorrectionInfo info) throws Exception {

        if (info == null) {
            logger.warn("setReconstructionFactor input invalid,with info = null");
            return;
        }

        if (info.getCurrentPreValue() == null) {
            logger.warn("setReconstructionFactor input invalid,with info.getCurrentPreValue() = null");
            return;
        }

        if (info.getNezhaStatDto() == null) {
            return;
        }


        // 2 解析
        Long type = info.getType();
        Double currentPreValue = info.getCurrentPreValue();
        NezhaStatDto nezhaStatDto = info.getNezhaStatDto();
        Long launchCnt = 0L;
        Double avgFeedbackValue = null;

        Double lowerLimit = null;
        Double upperLimit = null;
        Double midVal = null;
        Double zoom = null;
        Double fBratio = null;
        boolean isCtr = true;
        if (type.equals(1L)) {
            // CTR
            avgFeedbackValue = nezhaStatDto.getStatCtrAvg();

            lowerLimit = 0.1;
            upperLimit = 1.00;
            midVal = 0.04;
            zoom = 40.0;
            fBratio = 0.1;

        }

        if (type.equals(2L)) {
            // CVR
            avgFeedbackValue = nezhaStatDto.getStatCvrAvg();

            lowerLimit = 0.1;
            upperLimit = 1.0;
            midVal = 0.04;
            zoom = 40.0;
            fBratio = 0.1;
            isCtr = false;
        }


        Double correctionFactor = info.getCorrectionFactor();
        Double newCurrentPreValue = currentPreValue * correctionFactor;
        Double reconstructionValue = reconstructionValue(isCtr, newCurrentPreValue, avgFeedbackValue, fBratio, zoom, midVal, lowerLimit, upperLimit);
        Double reconstructionFactor = noiseSmoother(reconstructionValue / newCurrentPreValue, 0.2, 1.5);
        info.setReconstructionFactor(reconstructionFactor);


    }


    /**
     * 结果平滑,限定调节范围
     *
     * @param point
     * @param upperLimit
     * @param lowerLimit
     * @return
     * @throws Exception
     */
    public static Double noiseSmoother(Double point, Double lowerLimit, Double upperLimit) throws Exception {
        Double ret = point;

        if (AssertUtil.isAnyEmpty(upperLimit, lowerLimit)) {
            logger.warn("noiseSmoother input invalid,with upperLimit=" + upperLimit + ",lowerLimit=" + lowerLimit);
            return ret;
        }

        if (point != null) {
            ret = point > upperLimit ? upperLimit : (point < lowerLimit ? lowerLimit : point);
        }

        return ret;
    }


    /**
     * @param currentPreValue
     * @param avgPreValue
     * @param avgFeedbackValue
     * @param lowerLimit
     * @param upperLimit
     * @return
     * @throws Exception
     */
    public static Double correctionValue(Double currentPreValue, Double avgPreValue, Double avgFeedbackValue, Double lowerLimit, Double upperLimit, PredRectifierEnum predRectifierEnum) throws Exception {
        Double ret = currentPreValue;

        if (AssertUtil.isAnyEmpty(currentPreValue, avgPreValue, avgFeedbackValue, upperLimit, lowerLimit)) {
            return ret;
        }

        if (currentPreValue > 0.0 && avgPreValue > 0.0 && avgFeedbackValue > 0.0) {
            // 对数正太分布,同方差,调整
            Double lndValue = noiseSmoother(currentPreValue * avgFeedbackValue / avgPreValue, lowerLimit, upperLimit);

            // 正太分布,同方差,调整
            Double ndValue = noiseSmoother(currentPreValue + avgFeedbackValue - avgPreValue, lowerLimit, upperLimit);


            // 调节策略

            // 交叉调节
            if (avgPreValue > avgFeedbackValue) {
                // 偏高 -
                ret = Math.max(lndValue, ndValue);
            } else {
                // 偏低 +
                ret = Math.min(lndValue, ndValue);
            }

            // 融合调节
            ret = 0.8 * ret + (1 - 0.8) * currentPreValue;

        }

        return ret;
    }

    /**
     * @param newCurrentPreValue
     * @param avgFeedbackValue
     * @return
     * @throws Exception
     */
    public static Double reconstructionValue(boolean isCtr, Double newCurrentPreValue, Double avgFeedbackValue, double fBratio, double zoom, double midVal, double lowerLimit, double upperLimit) throws Exception {
        Double ret = newCurrentPreValue;
        Double inhibitoryFactor = 1.0;
        if (AssertUtil.isAnyEmpty(newCurrentPreValue, avgFeedbackValue)) {
            return ret;
        }

        if (newCurrentPreValue > 0.0 && avgFeedbackValue > 0.0001) {

            // 1、打压 相对值
            if (newCurrentPreValue < avgFeedbackValue && !isCtr) {
                Double ratio = (newCurrentPreValue + 0.001) / (avgFeedbackValue + 0.001);
                inhibitoryFactor = sigmoidWithZoomAndIntervalMap((ratio - 0.25), 0.2, 1.0, 15);
            }
            if (newCurrentPreValue >= 2 * avgFeedbackValue && !isCtr) {
                inhibitoryFactor = 1.05;
            }


            Double mergeVal = (1 - fBratio) * newCurrentPreValue + fBratio * avgFeedbackValue;


            Double reconstructionFactor = 1.0;
            // 2、重构 绝对值
            if (mergeVal < 0.5) {
                reconstructionFactor = sigmoidWithZoomAndIntervalMap(mergeVal - midVal, lowerLimit, upperLimit, zoom) + 0.001 * (1 - ret);
            }

            if (mergeVal >= 0.6) {
                reconstructionFactor = 1.05;
            }

            ret = ret * inhibitoryFactor * reconstructionFactor;
        }

        return ret;
    }


    /**
     * @param x
     * @param lowerLimit
     * @param upperLimit
     * @param zoom
     * @return
     */
    public static Double sigmoidWithZoomAndIntervalMap(double x, double lowerLimit, double upperLimit, double zoom) {

        return lowerLimit + (upperLimit - lowerLimit) * sigmoidWithZoom(x, zoom);


    }


    /**
     * @param x
     * @param zoom
     * @return
     */
    public static Double sigmoidWithZoom(double x, double zoom) {
        return sigmoid(x * zoom);
    }


    /**
     * @param x
     * @return
     */
    public static Double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }


    public static void main(String[] args) {

        try {
            NezhaStatDto nezhaStatDto = new NezhaStatDto();
            nezhaStatDto.setAdvertId(13675L);
            nezhaStatDto.setCtrLaunchCnt(1000000L);
            nezhaStatDto.setCvrLaunchCnt(1000000L);
            nezhaStatDto.setAlgType(206L);
            nezhaStatDto.setAppId(20453L);

            nezhaStatDto.setPreCtrAvg(0.15);
            nezhaStatDto.setStatCtrAvg(0.14);

            nezhaStatDto.setPreCvrAvg(0.16);
            nezhaStatDto.setStatCvrAvg(0.09);


            CorrectionInfo info = new CorrectionInfo();
            info.setAdvertId(13675L);
            info.setType(1L);
            info.setCurrentPreValue(0.059);
            info.setNezhaStatDto(nezhaStatDto);

            ModelPredRectifier.getCorrectionReconstructionFactor(Arrays.asList(info));
            System.out.println("testGetCorrectionFactor CTR info.getCorrectionFactor()" + info.getCorrectionFactor());
            System.out.println("testGetCorrectionFactor CTR info.getReconstructionFactor()" + info.getReconstructionFactor());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
