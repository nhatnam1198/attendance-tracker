package com.example.facerecogapp.Model;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private Integer id;
    private String name;
    private String email;
    private String phoneNumber;
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
