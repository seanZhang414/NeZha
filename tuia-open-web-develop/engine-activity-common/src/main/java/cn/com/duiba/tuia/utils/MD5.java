package cn.com.duiba.tuia.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 
 * ClassName: MD5 <br/>
 * date: 2016年12月8日 下午6:06:39 <br/>
 *
 * @author ZFZ
 * @since JDK 1.6
 */
public class MD5 {

    /** The logger. */
    private static Logger   logger    = LoggerFactory.getLogger(MD5.class);

    private static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
            "f"                      };

    private MD5             md;

    public MD5 getMD5() {
        if (md == null) {
            md = new MD5();
        }
        return md;
    }

    /**
     * 
     * md5:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author ZFZ
     * @param b
     * @return
     * @throws NoSuchAlgorithmException
     * @since JDK 1.6
     */
    public static String md5(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(b, 0, b.length);
        return byteArrayToHexString(md5.digest());
    }

    /**
     * 
     * md5:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author ZFZ
     * @param data
     * @return
     * @since JDK 1.6
     */
    public static String md5(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] b = data.getBytes("UTF8");
            md5.update(b, 0, b.length);
            return byteArrayToHexString(md5.digest());
        } catch (Exception e) {
            logger.error("md5 error", e);
            throw e;
        }
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
            sb.append(byteToHexString(b[i]));

        return sb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

}
