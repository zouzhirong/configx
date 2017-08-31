package com.configx.web.exception;

/**
 * Created by zouzhirong on 2017/8/26.
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private String code;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 错误参数信息
     */
    private Object[] args;

    public ConfigException(String code) {
        this.code = code;
    }

    public ConfigException(String code, String error, Object... args) {
        this.code = code;
        this.error = error;
        if (args != null) {
            this.args = args;
        }
    }

    /**
     * 返回错误码
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 返回异常信息
     *
     * @return
     */
    public String getError() {
        return error;
    }

    /**
     * 返回错误参数信息
     *
     * @return
     */
    public Object[] getArgs() {
        return args;
    }

}
