package cn.com.duiba.tuia.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UAUtils
 */
public class UAUtils {
    
    private static final Logger logger     = LoggerFactory.getLogger(UAUtils.class);

    private static final Pattern PATTERN_IPHONE  = Pattern.compile("\\((iPhone);(.+?)\\)");    // iPhone

    private static final Pattern PATTERN_ANDROID = Pattern.compile(";\\s?(\\S*?\\s?\\S*?)\\s?(Build)?/"); // Android

    private UAUtils(){
    }
    
    /**
     * parseUA
     * 
     * @param userAgent
     * @return UAData
     */
    public static UAData parseUA(String userAgent){
        UAData data = new UAData();
        data.setUa(userAgent);
        try {
            // IOS
            Matcher iphoneMatcher = PATTERN_IPHONE.matcher(userAgent);
            if (iphoneMatcher.find()) {
                data.setOsType("iOS");
                data.setModel("iPhone");
                data.setVendor("apple");
                return data;
            }
            
            // Android
            Matcher androidMatcher = PATTERN_ANDROID.matcher(userAgent);
            if (androidMatcher.find() && androidMatcher.groupCount() > 0) {
                String model = androidMatcher.group(1).trim();
                data.setOsType("Android");
                data.setModel(model);
                data.setVendor(null);
            }
        } catch(Exception e) {
            logger.warn("Parse ua failed.", e);
        }
        return data;
    }
}
