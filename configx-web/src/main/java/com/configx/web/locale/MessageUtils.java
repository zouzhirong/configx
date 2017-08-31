package com.configx.web.locale;

import com.configx.web.support.ApplicationContextHelper;

import java.util.Locale;

/**
 * Created by zouzhirong on 2017/8/9.
 */
public class MessageUtils {

    public static String getMessage(String code) {
        return getMessage(code, "");
    }

    public static String getMessage(String code, String defaultMessage) {
        return ApplicationContextHelper.getContext().getMessage(code, null, defaultMessage, Locale.getDefault());
    }

}
