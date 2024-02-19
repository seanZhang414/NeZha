package cn.com.duiba.tuia.utils;

import java.util.Random;

/**
 * 随机数
 * @author weny.cai on 2018/7/31.
 */
public class RandomUtil {

    public static final int halfNum = 50;

    public static int getRandom(){
        Random r = new Random();
        return r.nextInt(100);
    }

    /**
     * 随机数100
     * 根据50分成
     * 两半
     * @return
     */
    public static boolean getHalf(){
        if(RandomUtil.getRandom()>halfNum){
            return true;
        }
        return false;
    }

}
