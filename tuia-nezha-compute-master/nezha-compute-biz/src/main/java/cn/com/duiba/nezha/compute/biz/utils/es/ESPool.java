package cn.com.duiba.nezha.compute.biz.utils.es;

import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/11/18.
 */
public class ESPool {

    private ESPool() {
    }

    private static class ESUtilHolder {
        private static final ESPool instance = new ESPool();
    }

    public static ESPool getInstance() {
        return ESUtilHolder.instance;
    }

    public static TransportClient client;


    public static void setPool(ESConfig esConfig) {

        String clusterName = esConfig.getClusterName();
        String clusterHostStr = esConfig.getClusterHostList();

        try {
            List<InetSocketTransportAddress> addressList = getAddressList(clusterHostStr);
            if (addressList != null && clusterName != null) {

                // 通过setting对象指定集群配置信息, 配置的集群名
                Settings settings = Settings.builder().put("cluster.name", clusterName) // 设置集群名
//                        .put("client.transport.nodes_sampler_interval", 5) //报错,
                        .put("client.transport.ping_timeout", TimeValue.timeValueSeconds(1)) // 报错, ping等待时间,
                        .build();

                client = TransportClient.builder()
                        .settings(settings)
                        .build()
                        .addTransportAddresses(addressList.toArray(new InetSocketTransportAddress[addressList.size()]));
                System.out.println("set es client");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static TransportClient getClient(ESConfig esConfig) {
        if (client == null) {
            setPool(esConfig);
        }
        return client;
    }

    public static List<InetSocketTransportAddress> getAddressList(String nodesStrList) {
        List<InetSocketTransportAddress> addressList = null;
        try {
            if (nodesStrList != null) {
                List<String> nodeStrList = MyStringUtil2.stringToList(nodesStrList, ",");

                if (nodeStrList != null) {
                    addressList = new ArrayList<>();
                    for (String nodeStr : nodeStrList) {
                        List<String> node = MyStringUtil2.stringToList(nodeStr, ":");
                        if (node != null && node.size() == 2) {
                            addressList.add(new InetSocketTransportAddress(
                                            InetAddress.getByName(node.get(0)),
                                            Integer.valueOf(node.get(1)))
                            );
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressList;
    }

}


