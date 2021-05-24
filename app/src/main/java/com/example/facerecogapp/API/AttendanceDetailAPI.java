package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AttendanceDetailAPI {
    @POST("attendanceDetail")
    @Headers("Content-Type: application/json")
    Call<Void> addAttendanceDetail(@Body HashMap<String, Object> map);

    @GET("attendanceDetail/attendedResult/list")
    Call<ArrayList<AttendanceDetail>> getAttendedResultList(@Query("eventId") Integer eventId);

    @GET("attendanceDetail/leaveOfAbsence/list")
    Call<ArrayList<AttendanceDetail>> getLeaveOfAbsentRequestList(@Query("eventId") Integer eventId);

    @PUT("attendanceDetail/absence")
    @Headers("Content-Type: application/json")
    Call<Void> approveLeaveOfAbsenceRequest(@Body Integer attendanceId);
}
