package com.configx.web.locale;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Locale;

/**
 * Locale工具类
 * <p>
 * Created by zouzhirong on 2017/3/22.
 */
public class LocaleUtils extends org.apache.commons.lang.LocaleUtils {

    /**
     * 获取语言默认的国家代码
     *
     * @param languageCode
     * @return
     */
    public static String getDefaultCountry(String languageCode) {
        Locale locale = toLocale(languageCode);
        List<Locale> localeList = countriesByLanguage(locale.getLanguage());
        if (CollectionUtils.isEmpty(localeList)) {
            return null;
        }
        return localeList.get(0).getCountry().toLowerCase();
    }

}
