//package cn.com.duiba.nezha.compute.alg.tf;
//
//
//import cn.com.duiba.nezha.compute.core.util.AssertUtil;
//import com.alibaba.fastjson.JSON;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import org.tensorflow.framework.DataType;
//import org.tensorflow.framework.TensorProto;
//import org.tensorflow.framework.TensorShapeProto;
//import tensorflow.serving.Model;
//import tensorflow.serving.Predict;
//import tensorflow.serving.PredictionServiceGrpc;
//
//import java.util.*;
//
//public class Client {
//
//
//    public static Map<String, ManagedChannel> channelMap;
//
//    public static String DF_INPUT = "feat_vals";
//
//    public static String DF_SIGNAtURE_NAME = "serving_default";
//
//    public static String DF_OUTPUT = "prob";
//
//    public static ManagedChannel getManagedChannel(String host, int port) {
//        ManagedChannel ret = null;
//
//        String key = host + "-" + port;
//        if (channelMap == null) {
//            channelMap = new HashMap<>();
//        }
//        if (!channelMap.containsKey(key)) {
//            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
//            channelMap.put(key, channel);
//        }
//
//        ret = channelMap.get(key);
//
//        return ret;
//    }
//
//    public static List<Float> predict(int featureNums,
//                                      List<Float> data,
//                                      String modelName,
//                                      ManagedChannel channel,
//
//                                      String signatureName,
//                                      String input,
//                                      String output) {
//        List<Float> ret = null;
//        if (channel != null && data.size() % featureNums == 0) {
//            //block模式
//            PredictionServiceGrpc.PredictionServiceBlockingStub stub = PredictionServiceGrpc.newBlockingStub(channel);
//            //创建请求
//            Predict.PredictRequest.Builder predictRequestBuilder = Predict.PredictRequest.newBuilder();
//            //模型名称和模型方法名预设
//            Model.ModelSpec.Builder modelSpecBuilder = Model.ModelSpec.newBuilder();
//            modelSpecBuilder.setName(modelName);
//            modelSpecBuilder.setSignatureName(signatureName);
//            predictRequestBuilder.setModelSpec(modelSpecBuilder);
//
//            //设置入参,访问默认是最新版本，如果需要特定版本可以使用tensorProtoBuilder.setVersionNumber方法
//
//            int totalSize = data.size();
//            int featureSize = totalSize / featureNums;
//
//            TensorProto.Builder tensorProtoBuilder = TensorProto.newBuilder();
//            tensorProtoBuilder.setDtype(DataType.DT_FLOAT);
//            TensorShapeProto.Builder tensorShapeBuilder = TensorShapeProto.newBuilder();
//            tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(featureNums));
//            tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(featureSize));
//            tensorProtoBuilder.setTensorShape(tensorShapeBuilder.build());
//
//            tensorProtoBuilder.addAllFloatVal(data);
//            predictRequestBuilder.putInputs(input, tensorProtoBuilder.build());
//            //访问并获取结果
//            Predict.PredictResponse predictResponse = stub.predict(predictRequestBuilder.build());
//            TensorProto tensorProto = predictResponse.getOutputsOrThrow(output);
//
//            ret = tensorProto.getFloatValList();
//
//        }
//        return ret;
//    }
//
//
//    public static Map<String, Double> predict(Map<String, List<Float>> dataMap,
//                                              String host,
//                                              int port,
//                                              String modelName) {
//        Map<String, Double> ret = new HashMap<>();
//        if (AssertUtil.isAllNotEmpty(dataMap, host, port, modelName)) {
//
//            List<String> keyList = new ArrayList<>(dataMap.keySet());
//
//            int featureNums = keyList.size();
//            int featureSize = 0;
//
//            //数据格式转化
//            List<Float> data = new ArrayList<>();
//            for (String key : keyList) {
//                data.addAll(dataMap.get(key));
//            }
//
//            //
//            ManagedChannel channel = getManagedChannel(host, port);
//
//            List<Float> preValueList = predict(featureNums, data, modelName, channel, DF_SIGNAtURE_NAME, DF_INPUT, DF_OUTPUT);
//
//            for (int i = 0; i < featureNums; i++) {
//                String key = keyList.get(i);
//                Float valueF = preValueList.get(i);
//                if (valueF != null) {
//                    ret.put(key, (double) preValueList.get(i));
//                }
//
//            }
//
//        }
//
//        return ret;
//    }
//
//
//    public static void main(String[] args) {
//        ManagedChannel channel = getManagedChannel("47.96.221.208", 9000);
//
//        // generate data
//        Map<String, List<Float>> dataMap = new HashMap<>();
//        for (int i = 0; i < 10; i++) {
//            List<Float> data = new ArrayList<>();
//            for (int j = 0; j < 302; j++) {
//                data.add((float)( Math.random()*0.0001));
//            }
//
//            dataMap.put(i + "-", data);
//        }
//
//        Map<String, Double> ret= predict(dataMap, "47.96.221.208", 9000, "model-1");
//
//        System.out.println("RET= : " + JSON.toJSONString(ret));
//
//    }
//}
