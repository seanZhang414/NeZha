package cn.com.duiba.nezha.compute.common.model;

import cn.com.duiba.nezha.compute.common.model.activityselect.ActivitySelector;

import java.util.*;
import java.util.Comparator;

/**
 * Created by jiali on 2017/8/5.
 */
public class Test {


    public static Comparator<Double> iComparator = new Comparator<Double>() {
        @Override
        public int compare(Double c1, Double c2) {
            return (int) (c2 - c1 >= 0 ? 1 : -1);
        }
    };

    public static void main(String[] args) {

        boolean isGiveUp = false;
        double hourConvert = 300;
        double maxConvert = 2600;
        Calendar calendar = Calendar.getInstance();
        int minutes = 10;
        double speed = 0;
        if(minutes > 0)
        {
            speed = hourConvert / minutes;
            if(speed > 0.001) {
                double p = maxConvert / (speed * 60);
                isGiveUp = Math.random() > p ? true : false;
                if (hourConvert > maxConvert) isGiveUp = true;
                System.out.println(p);
            }
        }
    }

    static  double normlize(double val, double  max, double limit)
    {
        double norm = Math.min(val*limit/max, limit);
        return norm;
    }


}
