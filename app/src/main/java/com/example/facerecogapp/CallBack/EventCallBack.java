package com.example.facerecogapp.CallBack;

import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Subject;

import java.util.ArrayList;

public interface EventCallBack {
    void onSuccess(ArrayList<Event> value);
    void onFailure();
}
