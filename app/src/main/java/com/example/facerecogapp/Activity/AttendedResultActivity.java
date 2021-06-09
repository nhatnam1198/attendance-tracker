package com.example.facerecogapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.Adapter.AttendedResultAdapter;
import com.example.facerecogapp.Adapter.AttendedResultEnabledCheckboxAdapter;
import com.example.facerecogapp.Adapter.AttendedStudentListAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendedResultActivity extends AppCompatActivity {
    private RecyclerView recycleView;
    private ArrayList<AttendanceDetail> attendanceDetailArrayList;
    private ArrayList<AttendanceDetail> updateAttendanceDetail;
    private Button confirmBtn;
    private boolean isEditBtnClicked = false;
    private AttendedResultEnabledCheckboxAdapter attendedResultEnabledCheckboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Kết quả điểm danh");
        setSupportActionBar(toolbar);
        recycleView = (RecyclerView)findViewById(R.id.attended_checked_event_recycleView);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
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
                    updateAttendanceDetail = value;
                    AttendedResultAdapter attendedResultAdapter = new AttendedResultAdapter(AttendedResultActivity.this, attendanceDetailArrayList);
                    recycleView.setAdapter(attendedResultAdapter);
                    recycleView.setLayoutManager(new LinearLayoutManager(AttendedResultActivity.this));
                }
                @Override
                public void onFailure(Throwable t) {

                }
            }, eventId);
        }
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditBtnClicked) {
                    startActivity(new Intent(AttendedResultActivity.this, MainActivity.class));
                    return;
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AttendedResultActivity.this);
                    dialog.setTitle("Xác nhận chỉnh sửa!")
                            .setMessage("Bạn chắc chắn muốn chỉnh sửa điểm danh buổi học này?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int which) {
                                    SparseBooleanArray checkBoxSparseBooleanArray = attendedResultEnabledCheckboxAdapter.getCheckBoxSparseBooleanArray();
                                    for (int i = 0; i < updateAttendanceDetail.size(); i++) {
                                        if (checkBoxSparseBooleanArray.get(i)) {
                                            updateAttendanceDetail.get(i).setStatus(Const.ATTENDED);
                                        } else {
                                            updateAttendanceDetail.get(i).setStatus(Const.ABSENT);
                                        }
                                        AttendanceDetailAPI attendanceDetailAPI = ServiceGenerator.createService(AttendanceDetailAPI.class);
                                        Call<ResponseBody> call = attendanceDetailAPI.updateAttendanceDetail(updateAttendanceDetail);
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.code() != 200) {
                                                    Toast.makeText(AttendedResultActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(AttendedResultActivity.this, "Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AttendedResultActivity.this, MainActivity.class));
                                                    return;
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(AttendedResultActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.attended_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_btn:
                isEditBtnClicked = true;
                attendedResultEnabledCheckboxAdapter = new AttendedResultEnabledCheckboxAdapter(AttendedResultActivity.this, attendanceDetailArrayList);
                recycleView.setAdapter(attendedResultEnabledCheckboxAdapter);
                recycleView.setLayoutManager(new LinearLayoutManager(AttendedResultActivity.this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}