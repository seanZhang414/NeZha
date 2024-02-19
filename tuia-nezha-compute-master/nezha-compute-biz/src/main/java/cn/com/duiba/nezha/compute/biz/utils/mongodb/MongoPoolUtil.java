package cn.com.duiba.nezha.compute.biz.utils.mongodb;

import cn.com.duiba.nezha.compute.biz.constant.ProjectConstant;
import cn.com.duiba.nezha.compute.common.util.MyStringUtil2;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pc on 2016/11/18.
 */
public class MongoPoolUtil {

    private MongoPoolUtil() {
    }

    private static class MongoPoolHolder {
        private static final MongoPoolUtil instance = new MongoPoolUtil();
    }

    public static MongoPoolUtil getInstance() {
        return MongoPoolHolder.instance;
    }

    private static Map<String, MongoClient> maps = new HashMap<String, MongoClient>();

    public static MongoClient getPool(MongoConfig mongoConfig) {

        MongoClient mongoClient = null;

        String ip = mongoConfig.getIp();
        int port = mongoConfig.getPort();
        String passWord = mongoConfig.getPassWord();
        String userName = mongoConfig.getUserName();
        String databaseName = mongoConfig.getDatabaseName();
        String replSetName = mongoConfig.getReplSetName();

        String key = ip + ":" + databaseName;
        if (!maps.containsKey(key)) {

            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            List<ServerAddress> addrs = getAddressList(ip);

            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
            MongoCredential credential = MongoCredential.createScramSha1Credential(userName, databaseName, passWord.toCharArray());
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);

            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
//            builder.requiredReplicaSetName(replSetName);
            builder.connectionsPerHost(10);// 与目标数据库可以建立的最大链接数
            builder.connectTimeout(1000 * 3);// 与数据库建立链接的超时时间
            builder.maxWaitTime(1000 * 3);// 一个线程成功获取到一个可用数据库之前的最大等待时间
//            builder.threadsAllowedToBlockForConnectionMultiplier(50);
//            builder.maxConnectionIdleTime(0);
//            builder.maxConnectionLifeTime(0);
            builder.socketTimeout(1000*3);
            builder.socketKeepAlive(true);
            MongoClientOptions myOptions = builder.build();


            //通过连接认证获取MongoDB连接
            mongoClient = new MongoClient(addrs, credentials, myOptions);
//            mongoClient = new MongoClient(addrs, credentials);

            maps.put(key, mongoClient);
            System.out.println("put new pool");
        } else {
            mongoClient = maps.get(key);
        }
        return mongoClient;
    }

    public MongoClient getClient(MongoConfig mongoConfig) {
        MongoClient mongoClient = null;

        int count = 0;
        do {
            try {
                mongoClient = getPool(mongoConfig);
            } catch (Exception e) {
                e.printStackTrace();
                getPool(mongoConfig);
            }
        }
        while (mongoClient == null && count < ProjectConstant.RETRY_NUM);
        return mongoClient;
    }

    public static List<ServerAddress> getAddressList(String hostsStr, int port) {
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        try {
            if (hostsStr != null) {
                List<String> hostList = MyStringUtil2.stringToList(hostsStr, ",");
                if (hostList != null) {
                    for (String host : hostList) {
                        ServerAddress serverAddress = new ServerAddress(host, port);
                        addrs.add(serverAddress);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return addrs;
    }

    public static List<ServerAddress>  getAddressList(String hostsStr) {
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        try {
            if (hostsStr != null) {
                List<String> hostStrList = MyStringUtil2.stringToList(hostsStr, ",");

                if (hostStrList != null) {
                    for (String hostStr : hostStrList) {
                        List<String> node = MyStringUtil2.stringToList(hostStr, ":");
                        if (node != null && node.size() == 2) {
                            addrs.add(new ServerAddress(node.get(0), Integer.valueOf(node.get(1))));
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return addrs;
    }

}


