package com.example.facerecogapp.CallBack;

import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;

import java.util.ArrayList;

public interface SubjectCallBack {
    void onSuccess(ArrayList<Subject> value);
    void onFailure();
}
