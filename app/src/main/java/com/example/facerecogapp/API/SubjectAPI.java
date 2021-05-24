package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SubjectAPI {
    @GET("subject/list")
    Call<ArrayList<Subject>> getShiftList();
}
