package cn.com.duiba.nezha.compute.biz.support;

import cn.com.duiba.nezha.compute.biz.dto.PsModelSample;
import cn.com.duiba.nezha.compute.biz.utils.cachekey.PsKey;
import cn.com.duiba.nezha.compute.core.LabeledFeature;
import cn.com.duiba.nezha.compute.core.LabeledPoint;
import cn.com.duiba.nezha.compute.feature.FeatureCoder;

import cn.com.duiba.nezha.compute.feature.vo.Feature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vectors;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SampleParse {


    public static List<LabeledPoint> parse(List<PsModelSample> psModelSampleList, String modelId, boolean isCtr) {
        List<LabeledPoint> labeledPointList = new ArrayList<LabeledPoint>();
        try {
            if (psModelSampleList != null) {
                for (PsModelSample sample : psModelSampleList) {
//                    System.out.println("sample=" + sample.getFeature());
                    if (sample.getFeature() != null) {
                        LabeledPoint labeledPoint = parse(sample, modelId, isCtr);
                        if (labeledPoint != null) {
//                            System.out.println("labeledPoint=" + labeledPoint);
                            labeledPointList.add(labeledPoint);

                        }

                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledPointList;

    }

    public static List<LabeledFeature> parse(List<PsModelSample> psModelSampleList, boolean isCtr) {
        List<LabeledFeature> labeledFeatureList = new ArrayList<LabeledFeature>();
        try {
            if (psModelSampleList != null) {
                for (PsModelSample sample : psModelSampleList) {
//                    System.out.println("sample=" + sample.getFeature());
                    if (sample.getFeature() != null) {
                        LabeledFeature labeledFeature = parse(sample, isCtr);
                        if (labeledFeature != null) {
//                            System.out.println("labeledPoint=" + labeledPoint);
                            labeledFeatureList.add(labeledFeature);

                        }

                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledFeatureList;

    }

    public static LabeledFeature parse(PsModelSample psModelSample, boolean isCtr) {
        LabeledFeature labeledFeature = null;
        try {
            parseMap(psModelSample);

            if (psModelSample == null) {
                return labeledFeature;
            }

            if (isCtr) {


                if (psModelSample.getFeature() != null) {
//                    System.out.println("psModelSample.getIsClick()"+psModelSample.getIsClick()+",feature"+JSONObject.toJSONString(feature));

                    labeledFeature = toLabeledFeature(psModelSample.getFeature(), psModelSample.getIsClick());
                }


            } else if (valid(psModelSample.getOcpc()) && valid(psModelSample.getIsClick())) {
                labeledFeature = toLabeledFeature(psModelSample.getFeature(), psModelSample.getIsActClick());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledFeature;

    }


    public static LabeledPoint parse(PsModelSample psModelSample, String modelId, boolean isCtr) {
        LabeledPoint labeledPoint = null;
        try {
            parseMap(psModelSample);

            if (psModelSample == null) {
                return labeledPoint;
            }

            if (isCtr) {
                Feature feature = FeatureCoder.featureCode(psModelSample.getFeatureMap(), modelId);


                if (feature != null) {
//                    System.out.println("psModelSample.getIsClick()"+psModelSample.getIsClick()+",feature"+JSONObject.toJSONString(feature));

                    labeledPoint = toLabeledPoint(feature, psModelSample.getIsClick());
                }


            } else if (valid(psModelSample.getOcpc()) && valid(psModelSample.getIsClick())) {
                Feature feature = FeatureCoder.featureCode(psModelSample.getFeatureMap(), modelId);
                labeledPoint = toLabeledPoint(feature, psModelSample.getIsActClick());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledPoint;

    }


    public static LabeledFeature toLabeledFeature(String feature, Long isClick) {
        LabeledFeature labeledFeature = null;
        try {
            if (feature != null) {
                double label = 0.0;
                if (valid(isClick)) {
                    label = 1.0;
                }
                labeledFeature = new LabeledFeature(label, feature);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledFeature;

    }

    public static LabeledPoint toLabeledPoint(Feature feature, Long isClick) {
        LabeledPoint labeledPoint = null;
        try {
            if (feature != null) {
                SparseVector sv = Vectors.sparse(feature.size, feature.indices, feature.values).toSparse();
                double label = 0.0;
                if (valid(isClick)) {
                    label = 1.0;
                }
                labeledPoint = new LabeledPoint(label, sv);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return labeledPoint;

    }


    public static boolean valid(Long val) {
        boolean ret = false;
        if (val != null && val > 0) {
            ret = true;
        }
        return ret;
    }

    public static void parseMap(PsModelSample psModelSample) {

        try {
            if (psModelSample != null && psModelSample.getFeature() != null) {
                String feature = psModelSample.getFeature();
                Map featureMap = JSONObject.parseObject(feature);
                psModelSample.setFeatureMap(featureMap);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
