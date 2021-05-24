package com.example.facerecogapp.Model;

import java.io.Serializable;

public class Student implements Serializable {
    private int id;
    private String name;
    private String email;
    private String studentCode;

    public String getStudentCode() {
        return studentCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
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
}