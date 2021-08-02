package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IndentificationAPI {
    @Multipart
    @POST("recognition/student/image")
    Call<ArrayList<Student>> identifyPerson(@Part MultipartBody.Part imageFile);


    @Multipart
    @POST("recognition/student/video")
    Call<ArrayList<Student>> identifyPersonByCapturedVideo(@Part MultipartBody.Part file);
}
