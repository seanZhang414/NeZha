package cn.com.duiba.nezha.engine.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by pc on 2016/12/2.
 */
public class Md5Util {
    private static final Logger logger = LoggerFactory.getLogger(Md5Util.class);
    /**
     * 字符串转MD5
     *
     * @param str 输入字符串
     * @return
     */
    private static final int RADIX = 16;
    public static String getMD5(String str) {
        String pwd = null;
        try {
            if(str==null){
                logger.warn("getMD5 params is null ","params invalid");
                return pwd;
            }
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes(StandardCharsets.UTF_8));
            pwd = new BigInteger(1, md.digest()).toString(RADIX);
        } catch (Exception e) {
            logger.warn("Md5Util.getMD5 happened error:{}", e);
        }
        return pwd;
    }

    private Md5Util(){
        //不允许创建实例
    }

}