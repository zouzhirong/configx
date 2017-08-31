package com.configx.demo.conf;

import lombok.Getter;
import lombok.ToString;
import org.simpleframework.xml.Attribute;

/**
 * 语言配置
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Getter
@ToString
public class LangMeta {
    @Attribute
    private String lang;

    @Attribute
    private String name;

}
