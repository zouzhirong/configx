package com.configx.demo.bean;

import com.configx.client.version.VersionContextHolder;
import org.simpleframework.xml.ElementList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

public class Students {
    @ElementList(inline = true, entry = "student")
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @PreDestroy
    public void destroy() {
        long version = VersionContextHolder.getCurrentVersion();
        System.out.println("Students destroy, version=" + version);
    }
}
