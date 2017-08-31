package com.configx.web.web.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zouzhirong on 2017/8/9.
 */
@Data
public class WebhookForm {

    private String name;

    private String url;

    private String contentType;

    private String secret;

    private int eventType;

    private Map<String, String> eventParams = new LinkedHashMap();

    private Map<String, String> requestParams = new LinkedHashMap<>();

}
