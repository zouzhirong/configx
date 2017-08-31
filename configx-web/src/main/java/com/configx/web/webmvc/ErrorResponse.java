package com.configx.web.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zouzhirong on 2017/8/26.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    /**
     * 错误代码
     */
    private String code;

    /**
     * 错误消息的描述
     */
    private String msg;

    /**
     * 详细错误信息
     */
    private String error;
}
