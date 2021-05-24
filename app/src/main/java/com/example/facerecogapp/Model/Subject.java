package com.example.facerecogapp.Model;

import java.io.Serializable;

public class Subject implements Serializable {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private String name;

}
