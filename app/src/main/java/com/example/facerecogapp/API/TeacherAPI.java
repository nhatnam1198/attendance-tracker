package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Teacher;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TeacherAPI {
    @GET("teacher")
    Call<Teacher> getTeacher(@Query("email") String email);
}
