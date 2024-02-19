package cn.com.duiba.nezha.compute.milib.ModelCache;

import java.util.HashMap;
import java.util.Map;

public class ModelCache {
    public Map<String, Double> cache = new HashMap<>();

    public Double get(String key) {
        return cache.get(key);
    }

    public void set(String key, Double value) {
        cache.put(key, value);
    }

}
