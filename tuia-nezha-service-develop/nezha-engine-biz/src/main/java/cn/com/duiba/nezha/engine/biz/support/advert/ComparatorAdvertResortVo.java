package cn.com.duiba.nezha.engine.biz.support.advert;

import cn.com.duiba.nezha.engine.biz.vo.advert.AdvertResortVo;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by pc on 2016/8/29.
 */

public class ComparatorAdvertResortVo implements Comparator<AdvertResortVo> ,Serializable {
    @Override
    public int compare(AdvertResortVo o1,AdvertResortVo o2) {
        int flag = 0;
        // 1.先按推荐类型排序，逆序
        if(Double.compare(o1.getRankScore(),o2.getRankScore())> 0){
            flag = -1;
        }else if (Double.compare(o1.getRankScore(),o2.getRankScore())<0){
            flag = 1;
        }

        return flag;
    }
}