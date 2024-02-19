package cn.com.duiba.nezha.compute.biz.utils.jedis;

/**
 * Created by pc on 2016/11/18.
 */
public class JedisConfig {

    private static final long serialVersionUID = -316102112618444933L;

    private String ip;
    private int port;
    private String passWord;


    public String getIp(){return ip;}
    public void setIp(String ip){this.ip=ip;}

    public String getPassWord(){return passWord;}
    public void setPassWord(String passWord){this.passWord=passWord;}

    public int getPort(){return port;}
    public void setPort(int port){this.port=port;}

}
