package com.example.facerecogapp.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.facerecogapp.Model.Event;

import java.util.ArrayList;

public class StatisticsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Event>> eventArrayList = new MutableLiveData<ArrayList<Event>>();
    public void setEventArrayList(ArrayList<Event> eventArrayList){
        this.eventArrayList.setValue(eventArrayList);
    }

    public LiveData<ArrayList<Event>> getEventArrayList(){
        return eventArrayList;
    }
}