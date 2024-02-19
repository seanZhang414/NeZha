package cn.com.duiba.nezha.compute.alg;

import cn.com.duiba.nezha.compute.api.dto.CorrectionInfo;
import cn.com.duiba.nezha.compute.api.dto.NezhaStatDto;
import cn.com.duiba.nezha.compute.api.enums.PredRectifierEnum;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by pc on 2017/9/14.
 */
public class ModelPredRectifierTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetCorrectionFactor() throws Exception {

        NezhaStatDto nezhaStatDto =new NezhaStatDto();
        nezhaStatDto.setAdvertId(13675L);
        nezhaStatDto.setAlgType(206L);
        nezhaStatDto.setAppId(20453L);
        nezhaStatDto.setPreCtrAvg(0.18);
        nezhaStatDto.setPreCvrAvg(0.16);
        nezhaStatDto.setStatCtrAvg(0.56);
        nezhaStatDto.setStatCvrAvg(0.09);

        CorrectionInfo info = new CorrectionInfo();
        info.setAdvertId(13675L);
        info.setType(1L);
        info.setCurrentPreValue(0.75);
        info.setNezhaStatDto(nezhaStatDto);

        ModelPredRectifier.getCorrectionFactor(Arrays.asList(info));
        System.out.println("testGetCorrectionFactor CTR info.getCorrectionFactor()" + info.getCorrectionFactor());


        info.setType(2L);
        info.setCurrentPreValue(0.1);
        ModelPredRectifier.getCorrectionFactor(Arrays.asList(info));
        System.out.println("testGetCorrectionFactor CVR info.getCorrectionFactor()"+info.getCorrectionFactor());



    }

    public void testGetCorrectionReconstructionFactor() throws Exception {

        NezhaStatDto nezhaStatDto =new NezhaStatDto();
        nezhaStatDto.setAdvertId(101L);
        nezhaStatDto.setAlgType(201L);
        nezhaStatDto.setAppId(1011L);
        nezhaStatDto.setPreCtrAvg(0.3);
        nezhaStatDto.setStatCtrAvg(0.25);


        nezhaStatDto.setPreCvrAvg(0.12);
        nezhaStatDto.setStatCvrAvg(0.08);

        CorrectionInfo info = new CorrectionInfo();
        info.setAdvertId(101L);


        info.setType(1L);
        info.setCurrentPreValue(0.2);
        info.setNezhaStatDto(nezhaStatDto);

        ModelPredRectifier.getCorrectionReconstructionFactor(Arrays.asList(info));
        System.out.println("testGetCorrectionReconstructionFactor()  CTR1 info.getCorrectionFactor()" + info.getCorrectionFactor());
        System.out.println("testGetCorrectionReconstructionFactor()  CTR2 info.getReconstructionFactor()" + info.getReconstructionFactor());


        info.setType(2L);
        info.setCurrentPreValue(0.05);
        ModelPredRectifier.getCorrectionReconstructionFactor(Arrays.asList(info));
        System.out.println("testGetCorrectionReconstructionFactor()  CVR3 info.getCorrectionFactor()" + info.getCorrectionFactor());
        System.out.println("testGetCorrectionReconstructionFactor()  CVR4 info.getReconstructionFactor()" + info.getReconstructionFactor());


    }

    public void testSetCorrectionFactor() throws Exception {

        NezhaStatDto nezhaStatDto =new NezhaStatDto();
        nezhaStatDto.setAdvertId(101L);
        nezhaStatDto.setAlgType(201L);
        nezhaStatDto.setAppId(1011L);
        nezhaStatDto.setPreCtrAvg(0.3);
        nezhaStatDto.setPreCvrAvg(0.1);
        nezhaStatDto.setStatCtrAvg(0.25);
        nezhaStatDto.setStatCvrAvg(0.08);

        CorrectionInfo info = new CorrectionInfo();
        info.setAdvertId(101L);
        info.setType(1L);
        info.setCorrectionFactor(1.0);
        info.setReconstructionFactor(1.0);
        info.setCurrentPreValue(0.25);
        info.setNezhaStatDto(nezhaStatDto);

        ModelPredRectifier.setCorrectionFactor(info,PredRectifierEnum.COR);
        System.out.println("testSetCorrectionFactor() CTR info.getCorrectionFactor()" + info.getCorrectionFactor());


        info.setType(2L);
        info.setCorrectionFactor(1.0);
        info.setReconstructionFactor(1.0);
        info.setCurrentPreValue(0.1);
        ModelPredRectifier.setCorrectionFactor(info,PredRectifierEnum.COR);
        System.out.println("testSetCorrectionFactor() CVR info.getCorrectionFactor()"+info.getCorrectionFactor());

    }

    public void testSetReconstructionFactor() throws Exception {


        NezhaStatDto nezhaStatDto =new NezhaStatDto();
        nezhaStatDto.setAdvertId(101L);
        nezhaStatDto.setAlgType(201L);
        nezhaStatDto.setAppId(1011L);
        nezhaStatDto.setPreCtrAvg(0.3);
        nezhaStatDto.setStatCtrAvg(0.25);

        nezhaStatDto.setPreCvrAvg(0.12);
        nezhaStatDto.setStatCvrAvg(0.08);

        CorrectionInfo info = new CorrectionInfo();
        info.setAdvertId(101L);


        info.setType(1L);
        info.setCorrectionFactor(1.0);
        info.setReconstructionFactor(1.0);
        info.setCurrentPreValue(0.2);
        info.setNezhaStatDto(nezhaStatDto);

        ModelPredRectifier.setCorrectionFactor(info,PredRectifierEnum.REC);
        ModelPredRectifier.setReconstructionFactor(info);
        System.out.println("testSetReconstructionFactor()  CTR1 info.getCorrectionFactor()" + info.getCorrectionFactor());
        System.out.println("testSetReconstructionFactor()  CTR2 info.getReconstructionFactor()" + info.getReconstructionFactor());


        info.setType(2L);
        info.setCorrectionFactor(1.0);
        info.setReconstructionFactor(1.0);
        info.setCurrentPreValue(0.05);
        ModelPredRectifier.setCorrectionFactor(info,PredRectifierEnum.REC);
        ModelPredRectifier.setReconstructionFactor(info);
        System.out.println("testSetReconstructionFactor()  CVR3 info.getCorrectionFactor()" + info.getCorrectionFactor());
        System.out.println("testSetReconstructionFactor()  CVR4 info.getReconstructionFactor()" + info.getReconstructionFactor());




    }

    public void testNoiseSmoother() throws Exception {

    }

    public void testCorrectionValue() throws Exception {
        System.out.println("ModelPredRectifier.correctionValue(0.2,0.3, 0.25, 0.01, 0.8)="+ModelPredRectifier.correctionValue(0.2,0.3, 0.25, 0.01, 0.8,PredRectifierEnum.REC));
    }

    public void testReconstructionValue() throws Exception {
//        System.out.println("ModelPredRectifier.reconstructionValue(0.2,0.3, 0.2, 0.8)="+ModelPredRectifier.reconstructionValue(0.2, 0.3));
    }

    public void testSigmoidWithZoomAndIntervalMap() throws Exception {
        System.out.println("ModelPredRectifier.sigmoidWithZoomAndIntervalMap(0.2,0.3,0.5,1.0)="+ModelPredRectifier.sigmoidWithZoomAndIntervalMap(0.2/0.3-0.5, 0.1, 1.0, 10));
    }

    public void testSigmoidWithZoom() throws Exception {
        System.out.println("ModelPredRectifier.sigmoidWithZoom(0.0,2)="+ModelPredRectifier.sigmoidWithZoom(0.1-0.5,8));
    }

    public void testSigmoid() throws Exception {
        System.out.println("ModelPredRectifier.sigmoid(0.0)="+ModelPredRectifier.sigmoid(0.1-0.5));
    }
}