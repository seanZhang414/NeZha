package cn.com.duiba.nezha.compute.alg;

import cn.com.duiba.nezha.compute.api.PredResultVo;

import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2017/7/11.
 */
public interface IAlgorithm {
    Double predict(Map<String, String> categoryMap);

    Double predict(List<String> categoryList);

    PredResultVo predictWithInfo(Map<String, String> categoryMap);

}
