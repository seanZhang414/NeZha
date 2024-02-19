package cn.com.duiba.nezha.compute.biz.conf;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.common.util.conf.ConfigFactory;
import cn.com.duiba.nezha.compute.biz.utils.es.ESConfig;

/**
 * Created by pc on 2016/11/18.
 */
public class ElasticSearchUtilConf {

    public static ESConfig esConfig;


    static {
        try {
            initESConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void initESConfig() throws Exception {
        esConfig = new ESConfig();

        String clusterName = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.ES_CLUSTER_NAME);

        int clusterPost = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getInt(ProjectConstant.ES_CLUSTER_POST, 9300);

        String clusterHostListStr = ConfigFactory.getInstance()
                .getConfigProperties(ProjectConstant.CONFIG_PATH).getProperty(ProjectConstant.ES_CLUSTER_HOST);


        esConfig.setClusterHostList(clusterHostListStr);
        esConfig.setClusterName(clusterName);
        esConfig.setPort(clusterPost);

    }


}
