package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Student;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ShiftAPI {
    @GET("shift/list")
    Call<ArrayList<Shift>> getShiftList();
}
