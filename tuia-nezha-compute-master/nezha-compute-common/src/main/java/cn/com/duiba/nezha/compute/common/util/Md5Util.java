package cn.com.duiba.nezha.compute.common.util;


import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by pc on 2016/12/2.
 */
public class Md5Util {
    /**
     * 字符串转MD5
     *
     * @param str 输入字符串
     * @return
     */
    private final static int RADIX = 16;

    public static String getMD5(String str) {
        String pwd = null;
        try {
            if (str == null) {
                return pwd;
            }
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes("UTF-8"));
            pwd = new BigInteger(1, md.digest()).toString(RADIX);
        } catch (Exception e) {
        }
        return pwd;
    }

}