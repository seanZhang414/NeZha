package cn.com.duiba.tuia.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ClassName: UniqRequestIdGen 
 *
 * 获取唯一请求id
 * @link http://cf.dui88.com/pages/viewpage.action?pageId=5244211
 */
public class UniqRequestIdGen {

    private static AtomicLong   lastId         = new AtomicLong(); //自增id，线程安全
    private static final long START_TIME_STAMPT = System.currentTimeMillis();//服务启动时间戳

    private static final String IP             = getLocalAddress(); //服务ip

    private UniqRequestIdGen() {
    }

    private static String getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";    //NOSONAR
        }
    }

    /**
     * resolveReqId
     * 
     * 获取requestId
     */
    public static String resolveReqId() {
        return hexIp(IP) + Long.toString(START_TIME_STAMPT, Character.MAX_RADIX) + "-" + lastId.incrementAndGet();
    }

    
    /**
     * hexIp
     *
     * 奖ip转换为定长8位的16进制表示 ：255.255.255.255 --> FFFFFFFF
     */
    private static String hexIp(String ip) {
        StringBuilder sb = new StringBuilder();
        for (String seg : ip.split("\\.")) {
            String h = Integer.toHexString(Integer.valueOf(seg));
            if (h.length() == 1) {
                sb.append("0");
            }
            sb.append(h);
        }
        return sb.toString();
    }
}
