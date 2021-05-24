package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AttendanceAPI {
    @GET("attendance/list")
    Call<ArrayList<Attendance>> getAttendanceBySubjectClassId(@Query("subjectClassId") Integer subjectClassId);
}
