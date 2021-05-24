package com.example.facerecogapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.Adapter.AttendedStudentListAdapter;
import com.example.facerecogapp.Adapter.LeaveOfAbsenceAdapter;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveOfAbsenceActivity extends AppCompatActivity {

    private Event event;
    private  ArrayList<AttendanceDetail> studentList;
    private ArrayList<Attendance> attendanceList;
    private ArrayList<Student> attendedStudentList;
    private RecyclerView recycleView;
    private Button showAbsentListBtn;
    private ArrayList<Student> approvedStudentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_of_absence);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycleView = findViewById(R.id.leave_of_absence_recycle_view);

        getEvent();
        getAttendanceList();
        getAttendedStudentList();

        getLeaveOfAbsentRequestList(new ResultCallBack<AttendanceDetail>() {
            @Override
            public void onSuccess(ArrayList<AttendanceDetail> value) {
                studentList = value;
                if(studentList != null && studentList.size() != 0){
                    setRecycleViewAdapter();
                    for(int i = 0; i< studentList.size(); i++){
                        Student student = studentList.get(i).getAttendance().getStudent();
                        if(studentList.get(i).getStatus() == Const.ALLOWED){
                            approvedStudentList.add(student);
                        }
                    }
                }else{
                    Intent intent = new Intent(LeaveOfAbsenceActivity.this, AbsentStudentList.class);
                    if(attendedStudentList != null){
                        intent.putExtra("attendedStudentList", (Serializable)attendedStudentList);
                    }
                    intent.putExtra("event", event);
                    intent.putExtra("attendanceList", attendanceList);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, event.getId());
        showAbsentListBtn = (Button)findViewById(R.id.showAbsentListBtn);
        showAbsentListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaveOfAbsenceActivity.this, AbsentStudentList.class);
                if(attendedStudentList != null){
                    intent.putExtra("attendedStudentList", (Serializable)attendedStudentList);
                }
                if(approvedStudentList != null && approvedStudentList.size() != 0){
                    intent.putExtra("approvedStudent", (Serializable)approvedStudentList);
                }
                intent.putExtra("event", event);
                intent.putExtra("attendanceList", attendanceList);
                startActivity(intent);
            }
        });
    }

    private void setRecycleViewAdapter() {
        if(this.studentList != null){
            LeaveOfAbsenceAdapter leaveOfAbsenceAdapter = new LeaveOfAbsenceAdapter(LeaveOfAbsenceActivity.this, studentList);
            recycleView.setAdapter(leaveOfAbsenceAdapter);
            recycleView.setLayoutManager(new LinearLayoutManager(LeaveOfAbsenceActivity.this));
            // empty_text_view.setVisibility(View.VISIBLE);
        }else{
//            empty_text_view.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        }
    }

    private void getLeaveOfAbsentRequestList(ResultCallBack<AttendanceDetail> resultCallBack, int eventId) {
        AttendanceDetailAPI attendanceDetailAPI = ServiceGenerator.createService(AttendanceDetailAPI.class);
        Call<ArrayList<AttendanceDetail>> call = attendanceDetailAPI.getLeaveOfAbsentRequestList(eventId);
        call.enqueue(new Callback<ArrayList<AttendanceDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<AttendanceDetail>> call, Response<ArrayList<AttendanceDetail>> response) {
                studentList = response.body();
                resultCallBack.onSuccess(studentList);
            }

            @Override
            public void onFailure(Call<ArrayList<AttendanceDetail>> call, Throwable t) {

            }
        });
    }

    private void getEvent(){
        Bundle bundle = getIntent().getExtras();;
        event = (Event) bundle.getSerializable("event");
    }
    private void getAttendanceList() {
        Bundle bundle = getIntent().getExtras();;
        attendanceList = (ArrayList<Attendance>) bundle.getSerializable("attendanceList");
    }

    private void getAttendedStudentList() {
        Bundle bundle = getIntent().getExtras();;
        attendedStudentList = (ArrayList<Student>) bundle.getSerializable("attendedStudentList");
    }
}