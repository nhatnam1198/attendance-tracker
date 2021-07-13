package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.Teacher;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AccountAPI {
    @GET("teacher")
    Call<Teacher> getTeacherInfo(@Query("email") Integer email);
}
