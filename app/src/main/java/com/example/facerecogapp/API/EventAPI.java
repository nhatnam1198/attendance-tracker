package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface EventAPI {
    @POST("event")
    @Headers("Content-Type: application/json")
    Call<Void> addEvent(@Body Event event);

    @GET("event/{date}")
    Call<ArrayList<Event>> getEventByDate(@Path("date") String eventDate);
}
