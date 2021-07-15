package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Event;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StatisticsAPI {
    @GET("statistics/reportFile")
    Call<ResponseBody> getStatisticsSheet(@Query("subjectClassId") Integer subjectClassId, @Query("email") String email);
}
