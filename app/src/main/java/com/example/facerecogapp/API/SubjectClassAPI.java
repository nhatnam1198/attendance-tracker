package com.example.facerecogapp.API;

import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.Model.SubjectClass;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SubjectClassAPI {
    @GET("subjectClass/list")
    Call<ArrayList<SubjectClass>> getSubjectClassListBySubjectIdAndTeacherId(@Query("subjectId") Integer subjectId, @Query("teacherId") Integer teacherId);

    @GET("subjectClass/teacher/list")
    Call<ArrayList<SubjectClass>> getSubjectClassByTeacherId(@Query("teacherId") Integer teacherId);
}
