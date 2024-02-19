package cn.com.duiba.nezha.compute.common.util;

/**
 * Created by pc on 2016/12/5.
 */
public class IpUtil {

    private IpUtil() {
    }

    /**
     * 将xxx.xxx.xx.x类型的IP转换成10进制的long型串
     * @param ip
     * @return
     */
    public static long getIp10(String ip) {
        long ip10 = 0;
        String[] ss = ip.trim().split("\\.");
        for (int i = 0; i < 4; i++) {
            int n = 3-i;
            ip10 += Math.pow(256, n) * Integer.parseInt(ss[i]);
        }

        return ip10;
    }

    /**
     * 将long型串转换成xxx.xxx.xx.x型的IP
     * @param ip10
     * @return
     */
    public static String getIp(long ip10) {
        StringBuilder ip = new StringBuilder();
        long temp;
        for (int i = 3; i >= 0; i--) {
            temp = ip10 / (long) Math.pow(256, i) % 256;
            if (i == 3) {
                ip.append(temp);
            } else {
                ip.append("." + temp);
            }
        }
        return ip.toString();
    }
}
