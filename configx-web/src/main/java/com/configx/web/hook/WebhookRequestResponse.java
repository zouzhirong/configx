package com.configx.web.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * Created by zouzhirong on 2017/8/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequestResponse {

    /**
     * 请求头
     */
    private MultiValueMap<String, String> requestHeaders;

    /**
     * 请求体
     */
    private Object requestBody;

    /**
     * 响应状态码
     */
    private int statusCode;

    /**
     * 响应头
     */
    private MultiValueMap<String, String> responseHeaders;

    /**
     * 响应内容
     */
    private String responseBody;

    /**
     * 异常
     */
    private Exception exception;

    public WebhookRequestResponse(HttpHeaders requestHeaders, Object requestBody, ResponseEntity<String> responseEntity) {
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.statusCode = responseEntity.getStatusCode().value();
        this.responseHeaders = responseEntity.getHeaders();
        this.responseBody = responseEntity.getBody();
    }

    public WebhookRequestResponse(HttpHeaders requestHeaders, Object requestBody, Exception exception) {
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.exception = exception;
    }
}
