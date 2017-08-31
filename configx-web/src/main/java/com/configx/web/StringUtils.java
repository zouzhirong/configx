package com.configx.web;

/**
 * Created by zouzhirong on 2017/8/31.
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    private static final String DELIMITERS = ",; \t\n";

    /**
     * 分隔
     *
     * @param str
     * @return
     */
    public static String[] splitByDelimiters(String str) {
        return org.springframework.util.StringUtils.tokenizeToStringArray(str, DELIMITERS);
    }

}
