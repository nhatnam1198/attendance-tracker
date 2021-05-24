package com.example.facerecogapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.facerecogapp.R;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceRequestApprovalActivity extends AppCompatActivity {

    private Button confirmBtn;
    private Attendance attendance;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_request_approval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.absenceRequestToolbar);
        confirmBtn = findViewById(R.id.confirm_btn);
        Bundle bundle = getIntent().getExtras();
        attendance = (Attendance) bundle.getSerializable("attendance");
        intent = getIntent();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(AbsenceRequestApprovalActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Xác nhận duyệt")
                        .setMessage("Bạn có chắc muốn duyệt phiếu nghỉ này không")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                approveLeaveOfAbsenceRequest();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });
    }

    private void approveLeaveOfAbsenceRequest() {
        AttendanceDetailAPI attendanceDetailAPI = ServiceGenerator.createService(AttendanceDetailAPI.class);
        Call<Void> call = attendanceDetailAPI.approveLeaveOfAbsenceRequest(attendance.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() != 200){
                    Toast.makeText(AbsenceRequestApprovalActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AbsenceRequestApprovalActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}