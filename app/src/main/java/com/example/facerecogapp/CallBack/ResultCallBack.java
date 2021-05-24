package com.example.facerecogapp.CallBack;

import com.example.facerecogapp.Model.Shift;

import java.util.ArrayList;

public interface ResultCallBack<T> {
    void onSuccess(ArrayList<T> value);
    void onFailure(Throwable t);
}

