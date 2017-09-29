package com.configx.demo.bean;

import org.simpleframework.xml.ElementList;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class Students implements InitializingBean, DisposableBean {
    @ElementList(inline = true, entry = "student")
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing " + this);
    }

    @Override
    public void destroy() {
        System.out.println("Destroy " + this);
    }
}
