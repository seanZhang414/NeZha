package cn.com.duiba.nezha.engine.biz.support.advert;

import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertMaterialResortVo;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by pc on 2016/8/29.
 */

public class ComparatorAdvertMaterialResortVo implements Comparator<AdvertMaterialResortVo>, Serializable {
    @Override
    public int compare(AdvertMaterialResortVo o1, AdvertMaterialResortVo o2) {

        // 按照分值排序,逆序
        if (o1.getRankScore() <= o2.getRankScore()) {
            return  1;
        } else {
            return  -1;
        }

    }
}