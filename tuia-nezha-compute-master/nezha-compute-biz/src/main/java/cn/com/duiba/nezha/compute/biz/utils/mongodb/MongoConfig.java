package cn.com.duiba.nezha.compute.biz.utils.mongodb;

/**
 * Created by pc on 2016/11/18.
 */
public class MongoConfig {

    private static final long serialVersionUID = -316102112618444933L;

    private String ip;
    private int port;
    private String passWord;
    private String userName;
    private String databaseName;
    private String replSetName;

    public String getIp(){return ip;}
    public void setIp(String ip){this.ip=ip;}

    public String getPassWord(){return passWord;}
    public void setPassWord(String passWord){this.passWord=passWord;}

    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}

    public String getDatabaseName(){return databaseName;}
    public void setDatabaseName(String databaseName){this.databaseName=databaseName;}

    public int getPort(){return port;}
    public void setPort(int port){this.port=port;}

    public String getReplSetName(){return replSetName;}
    public void setReplSetName(String replSetName){this.replSetName=replSetName;}

}
