package com.example.facerecogapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.facerecogapp.Adapter.AttendedStudentListAdapter;
import com.example.facerecogapp.Adapter.StudentListAdapter;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.facerecogapp.R;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class AttendedStudent extends AppCompatActivity {

    private Button showAbsentStudent;
    private ArrayList<Student> attendedStudentList;
    private RecyclerView recyclerView;
    private TextView empty_text_view;
    private ArrayList<Attendance> attendanceList;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attended_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        showAbsentStudent = (Button)findViewById(R.id.showAbsentList);
        recyclerView = findViewById(R.id.attended_students_recycle_view);
        // ?empty_text_view = findViewById(R.id.empty_text_view);
        getAttendedStudents();
        getAttendaceList();
        getEvent();
        setRecycleViewAdapter();
        handleEvents();
    }

    private void getAttendaceList() {
        Bundle bundle = getIntent().getExtras();
        attendanceList = (ArrayList<Attendance>) bundle.getSerializable("attendanceArrayList");
    }
    private void getEvent(){
        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.getSerializable("event");
    }
    private void setRecycleViewAdapter( ) {
        if(this.attendedStudentList != null){
            AttendedStudentListAdapter studentListAdapter = new AttendedStudentListAdapter(AttendedStudent.this, this.attendedStudentList);
            recyclerView.setAdapter(studentListAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(AttendedStudent.this));
            // empty_text_view.setVisibility(View.VISIBLE);
        }else{
            empty_text_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

    }

    private void getAttendedStudents() {
        Bundle bundle = getIntent().getExtras();
        attendedStudentList = (ArrayList<Student>) bundle.getSerializable("attendedStudent");
    }


    public void handleEvents(){
        showAbsentStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AttendedStudent.this, AbsentStudentList.class);
//                if(attendedStudentList != null){
//                    intent.putExtra("attendedStudentList", (Serializable)attendedStudentList);
//                }
//                intent.putExtra("event", event);
//                intent.putExtra("attendanceList", attendanceList);
                Intent intent = new Intent(AttendedStudent.this, LeaveOfAbsenceActivity.class);
                if(attendedStudentList != null){
                    intent.putExtra("attendedStudentList", (Serializable)attendedStudentList);
                }
                intent.putExtra("event", event);
                intent.putExtra("attendanceList", attendanceList);
                startActivity(intent);
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