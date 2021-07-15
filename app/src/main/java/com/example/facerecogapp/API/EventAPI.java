package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventAPI {
    @POST("event")
    @Headers("Content-Type: application/json")
    Call<Void> addEvent(@Body Event event);
    @PUT("event")
    @Headers("Content-Type: application/json")
    Call<Void> updateEvent(@Body Event event);
    @GET("event/{date}")
    Call<ArrayList<Event>> getEventByDateAndUserName(@Path("date") String eventDate, @Query("userName")String userName);

    @GET("event/")
    Call<ArrayList<Event>> getEventListBySubjectClassId(@Query("subjectClassId") Integer subjetClassId, @Query("email")String userEmail);

    @DELETE("event/")
    Call<ResponseBody> deleteEvent(@Query("eventId") Integer eventId);
}
