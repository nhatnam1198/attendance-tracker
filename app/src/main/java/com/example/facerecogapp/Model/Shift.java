package com.example.facerecogapp.Model;

import java.io.Serializable;

public class Shift implements Serializable {
    private Integer id;
    private String name;

    public Shift() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
