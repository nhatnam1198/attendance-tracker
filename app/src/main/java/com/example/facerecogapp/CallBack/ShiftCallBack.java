package com.example.facerecogapp.CallBack;

import com.example.facerecogapp.Model.Shift;

import java.util.ArrayList;

import javax.xml.transform.Result;

public interface ShiftCallBack {
    void onSuccess(ArrayList<Shift> value);
    void onFailure();
}
