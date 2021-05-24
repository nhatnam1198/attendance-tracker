package com.example.facerecogapp.CallBack;

import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.Model.SubjectClass;

import java.util.ArrayList;

public interface SubjectClassCallBack {
    void onSuccess(ArrayList<SubjectClass> value);
    void onFailure();
}
