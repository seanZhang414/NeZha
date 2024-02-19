package cn.com.duiba.nezha.compute.biz.utils.conf;


import java.io.FileNotFoundException;
import java.util.HashMap;

public class ConfigFactory {




    private static ConfigFactory instance = new ConfigFactory();
    private HashMap<String, ConfProperties> configMap = new HashMap<>();

    public static ConfigFactory getInstance() {
        return instance;
    }

    private ConfigFactory() {

    }

    synchronized public ConfProperties getConfigProperties(String filePath) throws FileNotFoundException {
        ConfProperties config = configMap.get(filePath);
        if (config == null) {
            config = new ConfProperties(filePath);
            configMap.put(filePath, config);
        }

        return config;
    }
}
