package com.example.facerecogapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.Adapter.AbsentStudentListAdapter;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.Service.ServiceGenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.facerecogapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsentStudentList extends AppCompatActivity {

    private Button button;
    private ArrayList<Student> attendedStudentList;
    private ArrayList<Attendance> attendanceList;
    private ArrayList<Student> absentStudentList;
    private RecyclerView recyclerView;
    private SparseBooleanArray checkboxSparseBooleanArray;
    private AbsentStudentListAdapter absentStudentListAdapter;
    private Event event;
    private ArrayList<Student> approvedStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_student_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        button = (Button)findViewById(R.id.finish_btn);
        getAttendedStudentList();
        getAttendanceList();
        getApprovedStudentList();
        getEvent();
        this.absentStudentList = getAbsentStudentList();
        recyclerView = (RecyclerView) findViewById(R.id.layout_absent_student_recycleView);
        setRecycleViewAdapter();
        handleEvents();
    }

    private void getApprovedStudentList() {
        Bundle bundle = getIntent().getExtras();;
        approvedStudent = (ArrayList<Student>) bundle.getSerializable("approvedStudent");
    }

    private void setRecycleViewAdapter(){
        if(this.absentStudentList != null){
            absentStudentListAdapter = new AbsentStudentListAdapter(AbsentStudentList.this, this.absentStudentList);
            recyclerView.setAdapter(absentStudentListAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(AbsentStudentList.this));
        }else {
        }
    }

    private void getEvent(){
        Bundle bundle = getIntent().getExtras();;
        event = (Event) bundle.getSerializable("event");
    }

    private ArrayList<Student> getAbsentStudentList() {
        HashSet<Integer> attendedStudentIdSet = new HashSet<>();
        HashSet<Integer> approvedStudentIdSet = new HashSet<>();
        ArrayList<Student> absentStudentList = new ArrayList<>();
        for(int i = 0; i< attendedStudentList.size(); i++){
            int attendedStudentId = attendedStudentList.get(i).getId();
            attendedStudentIdSet.add(attendedStudentId);
        }
        if(approvedStudent != null && approvedStudent.size() != 0){
            for(int i = 0; i < approvedStudent.size(); i++){
                int approvedStudentId = approvedStudent.get(i).getId();
                approvedStudentIdSet.add(approvedStudentId);
            }
        }
        for(int i = 0; i< attendanceList.size(); i++){
            Student student = attendanceList.get(i).getStudent();
            int studentId = student.getId();
            if(attendedStudentIdSet.contains(studentId) || approvedStudentIdSet.contains(studentId)){
                continue;
            }else{
                absentStudentList.add(student);
            }
        }
        return absentStudentList;
    }

    private void getAttendanceList() {
        Bundle bundle = getIntent().getExtras();;
        attendanceList = (ArrayList<Attendance>) bundle.getSerializable("attendanceList");
    }

    private void getAttendedStudentList() {
        Bundle bundle = getIntent().getExtras();;
        attendedStudentList = (ArrayList<Student>) bundle.getSerializable("attendedStudentList");
    }

    private void handleEvents(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkboxSparseBooleanArray = absentStudentListAdapter.getcheckboxSparseBooleanArray();
                for(int i = 0; i< absentStudentList.size(); i++){
                    if(checkboxSparseBooleanArray.get(i)){
                        attendedStudentList.add(absentStudentList.get(i));
                    }
                }
                try{
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("attendedStudentList", attendedStudentList);
                    map.put("event", event);
                    AttendanceDetailAPI attendanceDetailAPI = ServiceGenerator.createService(AttendanceDetailAPI.class);
                    Call<Void> call = attendanceDetailAPI.addAttendanceDetail(map);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if(response.code() == 200 || response.code() == 201){
                                Intent intent = new Intent(AbsentStudentList.this, SuccessActivity.class);
                                startActivity(intent);
                            } else if(response.code() == 409){
                                Toast.makeText(getApplicationContext(), "Buổi học đã được điểm danh", Toast.LENGTH_SHORT).show();
                            }else if (response.code() == 400){
                                Toast.makeText(getApplicationContext(), "Lỗi kết nối tới server 400", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}