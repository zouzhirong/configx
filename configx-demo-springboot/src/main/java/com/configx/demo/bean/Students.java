package com.configx.demo.bean;

import com.configx.client.annotation.ConfigBean;
import com.configx.client.annotation.VersionRefreshScope;
import org.simpleframework.xml.ElementList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;


@ConfigBean("students.xml") // @ConfigBean注解用来标记这是一个配置Bean，由一个配置值转换而来。
@VersionRefreshScope // @VersionRefreshScope注解用来标记这是一个可刷新的Bean，当配置更改时，这个Bean会重新创建，使用最新的配置。
public class Students {
    @ElementList(inline = true, entry = "student")
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing " + this);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Destroy " + this);
    }
}
