package cn.com.duiba.nezha.compute.biz.utils.es;

/**
 * Created by pc on 2016/11/18.
 */
public class ESConfig {

    private static final long serialVersionUID = -316102112618444933L;


    private String clusterName;
    private String clusterHostList;
    private int port;


    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterHostList() {
        return clusterHostList;
    }

    public void setClusterHostList(String clusterHostList) {
        this.clusterHostList = clusterHostList;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
