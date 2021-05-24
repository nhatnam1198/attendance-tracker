package com.example.facerecogapp.Model;

import java.io.Serializable;

public class Attendance implements Serializable {
    private Integer id;
    private  Student student;
    private SubjectClass subjectClass;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public SubjectClass getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(SubjectClass subjectClass) {
        this.subjectClass = subjectClass;
    }
}
