package com.configx.demo.bean;

import org.simpleframework.xml.Attribute;

public class Student {
    @Attribute
    private String id;

    @Attribute
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
