package com.example.facerecogapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Serializable {
    private Integer id;
    private String name;
    private Integer shiftId;
    private Integer subjectClassId;


    private Date dateTime;
    private int status;
    private List<AttendanceDetail> attendanceDetailsList = new ArrayList<>();

    public List<AttendanceDetail> getAttendanceDetailsList() {
        return attendanceDetailsList;
    }

    public void setAttendanceDetailsList(List<AttendanceDetail> attendanceDetailsList) {
        this.attendanceDetailsList = attendanceDetailsList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SubjectClass getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(SubjectClass subjectClass) {
        this.subjectClass = subjectClass;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public SubjectClass subjectClass;
    public Shift shift;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(Integer subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
