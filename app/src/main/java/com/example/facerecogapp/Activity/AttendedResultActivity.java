package com.example.facerecogapp.Activity;

import android.os.Bundle;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.Adapter.AttendedResultAdapter;
import com.example.facerecogapp.Adapter.AttendedStudentListAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendedResultActivity extends AppCompatActivity {
    private RecyclerView recycleView;
    private ArrayList<AttendanceDetail> attendanceDetailArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycleView = (RecyclerView)findViewById(R.id.attended_checked_event_recycleView);

        Bundle bundle = getIntent().getExtras();
        int eventId = 0;
        if(bundle != null){
            Event event = (Event) bundle.getSerializable("event");
            if(event != null){
                eventId = event.getId();
            }
        }
        if(eventId != 0){
            getAttendedStudentCheckedList(new ResultCallBack<AttendanceDetail>() {
                @Override
                public void onSuccess(ArrayList<AttendanceDetail> value) {
                    attendanceDetailArrayList = value;
                    AttendedResultAdapter attendedResultAdapter = new AttendedResultAdapter(AttendedResultActivity.this, attendanceDetailArrayList);
                    recycleView.setAdapter(attendedResultAdapter);
                    recycleView.setLayoutManager(new LinearLayoutManager(AttendedResultActivity.this));
                }
                @Override
                public void onFailure(Throwable t) {

                }
            }, eventId);
        }

    }

    private void getAttendedStudentCheckedList(ResultCallBack<AttendanceDetail> resultCallBack, Integer eventId) {
        AttendanceDetailAPI attendanceDetailAPI = ServiceGenerator.createService(AttendanceDetailAPI.class);
        Call<ArrayList<AttendanceDetail>> call = attendanceDetailAPI.getAttendedResultList(eventId);
        call.enqueue(new Callback<ArrayList<AttendanceDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<AttendanceDetail>> call, Response<ArrayList<AttendanceDetail>> response) {
                resultCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<AttendanceDetail>> call, Throwable t) {

            }
        });
    }
}