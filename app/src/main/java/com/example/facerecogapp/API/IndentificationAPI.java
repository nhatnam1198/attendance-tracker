package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IndentificationAPI {
    @Multipart
    @POST("recognition/student/image")
    Call<ArrayList<Student>> indentifyPerson(@Part MultipartBody.Part imageFile);
}
