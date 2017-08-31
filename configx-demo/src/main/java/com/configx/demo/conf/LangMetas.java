package com.configx.demo.conf;

import com.configx.client.annotation.ConfigBean;
import com.configx.client.annotation.VersionRefreshScope;
import org.simpleframework.xml.ElementList;

import java.util.List;

@ConfigBean("lang.xml")
@VersionRefreshScope
public class LangMetas {
    @ElementList(inline = true, entry = "lang")
    private List<LangMeta> list;

}
