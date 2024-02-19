package cn.com.duiba.nezha.engine.common.utils;

import org.apache.commons.lang.StringUtils;

import java.util.stream.Stream;

public class MultiStringUtils {


    public static boolean isAnyBlank(String... args) {
        return Stream.of(args).anyMatch(StringUtils::isBlank);
    }

    private MultiStringUtils(){
        //不允许创建实例
    }

}
