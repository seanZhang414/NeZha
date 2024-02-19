package cn.com.duiba.tuia.spring;

/**
 * @Title: TuiaPropertiesFactoryBean.java
 * @Package: cn.com.duiba.tuia.tool
 * @author: leiliang
 * @date: 2016年5月9日 下午5:52:01
 * @version: V1.0
 */

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;

import cn.com.duiba.tuia.utils.BlowfishUtils;

/**
 * @ClassName: TuiaPropertiesFactoryBean
 * @author leiliang
 * @date 2016年5月9日 下午5:52:01
 */
public class TuiaPropertiesFactoryBean extends PropertiesFactoryBean {

    private static final String PREFIX = "dbseccode";

    private static final String secret = "CNxgrtFG2nYQUfu";// NOSONAR

    @Override
    protected Properties createProperties() throws IOException {
        Properties properties = super.createProperties();
        Properties p = (Properties) properties.clone();
        for (Object key : p.keySet()) {
            if (key instanceof String) {
                String skey = (String) key;
                String value = (String) properties.get(skey);
                if (value != null && value.startsWith(PREFIX)) {
                    // 如果是加密的，进行解密操作
                    String newValue = decode(value);
                    properties.put(skey, newValue);
                }
            }
        }
        return properties;
    }

    private String decode(String value) {
        String newValue = null;
        if (value.startsWith(PREFIX)) {
            newValue = value.substring(PREFIX.length());
            return BlowfishUtils.decryptBlowfish(newValue, secret);
        }
        return newValue;
    }
}
