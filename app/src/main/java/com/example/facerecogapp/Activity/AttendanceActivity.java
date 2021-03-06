package com.example.facerecogapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.facerecogapp.API.AttendanceAPI;
import com.example.facerecogapp.API.IndentificationAPI;
import com.example.facerecogapp.Adapter.StudentListAdapter;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.Other.UnsafeOkHttpClient;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    File tempFile;
    private Button checkButton;
    private static final  int REQUEST_IMAGE_CAPTURE = 101;
    private static final  int REQUEST_VIDEO_CAPTURE = 1;
    private static final  int TAKE_PICTURE = 50;
    private static final  int ACTIVITY_SELECT_IMAGE = 60;
    private Integer eventId;
    private Integer subjectClassId;
    private ArrayList<Attendance> attendanceArrayList;
    private Event event;
    private TextView dateTextView;
    private TextView subjectClassTextView;
    private static final CharSequence TAKE_PICTURE_STRING = "Ch???p ???nh";
    private static final CharSequence PICK_PICTURE_STRING = "Ch???n ???nh";
    private static final CharSequence TAKE_VIDEO_STRING = "Quay phim";
    private static final CharSequence CANCEL = "Cancel";
    private static final int VIDEO_CAPTURE = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.attendance_toolbar);
        toolbar.setTitle("Danh s??ch h???c sinh");
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        checkButton = findViewById(R.id.button);
        dateTextView = findViewById(R.id.date_text_view);
        subjectClassTextView = findViewById(R.id.subject_class_text_view);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            event = (Event) extras.getSerializable("event");
            eventId = event.getId();
            subjectClassId = extras.getInt("subjectClassId");
            dateTextView.setText(event.getDateTime());
            subjectClassTextView.setText(event.getSubjectClass().getName());

        }
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.attendance_recycle_view);
        fetchAttendanceList(new ResultCallBack<Attendance>() {
            @Override
            public void onSuccess(ArrayList<Attendance> value) {
                attendanceArrayList = value;
                StudentListAdapter studentListAdapter = new StudentListAdapter(AttendanceActivity.this, attendanceArrayList);
                recyclerView.setAdapter(studentListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(AttendanceActivity.this, t.toString(), Toast.LENGTH_SHORT);
            }
        }, subjectClassId);
        handleEvent();



    }

    private void fetchAttendanceList(ResultCallBack<Attendance> resultCallBack, Integer subjectClassId) {
        AttendanceAPI attendanceAPI = ServiceGenerator.createService(AttendanceAPI.class);
        Call<ArrayList<Attendance>> call = attendanceAPI.getAttendanceBySubjectClassId(subjectClassId);
        call.enqueue(new Callback<ArrayList<Attendance>>() {
            @Override
            public void onResponse(Call<ArrayList<Attendance>> call, Response<ArrayList<Attendance>> response) {
                if(response.isSuccessful()){
                    resultCallBack.onSuccess(response.body());
                }else{
                    Toast.makeText(AttendanceActivity.this, R.string.HTTP500, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Attendance>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, R.string.CONNECTION_FAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handleEvent(){
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    captureImageCameraOrGallery();
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                contentSelectionIntent.setType("*/*");
//                Intent[] intentArray = new Intent[]{takePictureIntent,takeVideoIntent};
//                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Ch???n ph????ng th???c ??i???m danh");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//                startActivityForResult(chooserIntent, CAPTURE_MEDIA_RESULT_CODE);


//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                }
            }
        });
    }

    public void captureImageCameraOrGallery() {
        final CharSequence[] options = { TAKE_PICTURE_STRING, PICK_PICTURE_STRING, TAKE_VIDEO_STRING,
                CANCEL };
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AttendanceActivity.this);

        builder.setTitle("Select");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(TAKE_PICTURE_STRING)) {
                    try {
                        Intent cameraIntent = new Intent(
                              MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PICTURE);
                    } catch (ActivityNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } else if (options[which].equals(PICK_PICTURE_STRING)) {
                        Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                }else if (options[which].equals(TAKE_VIDEO_STRING)){
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(intent, VIDEO_CAPTURE);
                }
                else if (options[which].equals(CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        Dialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.MaterialAlertDialog_MaterialComponents_Title_Icon;

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            try {
                this.tempFile = File.createTempFile("image", ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (requestCode == TAKE_PICTURE) {
                Bundle extras = intent.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                byte[] byteArr = convertBitmapToByteArr(imageBitmap);
                // Starts writing the bytes in it
                try {
                    FileOutputStream os = new FileOutputStream(tempFile);
                    os.write(byteArr);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendImageToServer(tempFile);
            } else if(requestCode == ACTIVITY_SELECT_IMAGE) {
                Uri imageUri = intent.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    FileOutputStream os = new FileOutputStream(tempFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while((len=inputStream.read(buf))>0){
                        os.write(buf,0,len);
                    }
                    os.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendImageToServer(tempFile);
            }else if(requestCode == VIDEO_CAPTURE){
                Uri videoUri = intent.getData();

                sendCapturedVideoToServer(videoUri);

//                try{
//                    InputStream inputStream  = getContentResolver().openInputStream(videoUri);
//                    int bytesRead;
//                    ByteArrayOutputStream os = new ByteArrayOutputStream();
//                    byte[] buf = new byte[1024];
//                   while((bytesRead = inputStream.read(buf)) != -1){
//                        os.write(buf, 0, bytesRead);
//                   }
//                   sendCapturedVideoToServer(os.toByteArray());
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }


    private void sendCapturedVideoToServer(Uri fileUri){
        File videoFile = null;
        try {
            videoFile = File.createTempFile("video", ".mp4");
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            FileOutputStream os = new FileOutputStream(videoFile);
            byte[] buf = new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0){
                os.write(buf,0,len);
            }
            os.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), videoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", videoFile.getName(), requestFile);

        IndentificationAPI indentificationAPI = ServiceGenerator.createService(IndentificationAPI.class);
        Call<ArrayList<Student>> call = indentificationAPI.identifyPersonByCapturedVideo(body);
        Dialog dialog = getProgressBarDialog();
        dialog.show();
        call.enqueue(new Callback<ArrayList<Student>>() {
            @Override
            public void onResponse(Call<ArrayList<Student>> call, Response<ArrayList<Student>> response) {
                Intent intent = new Intent(AttendanceActivity.this, AttendedStudent.class);
                ArrayList<Student> attendedStudentList = response.body();
                if(attendanceArrayList != null){
                    intent.putExtra("attendanceArrayList", (Serializable)attendanceArrayList);
                    intent.putExtra("event", event);
                }
                if(attendedStudentList!= null)
                    intent.putExtra("attendedStudent", (Serializable) attendedStudentList);
                startActivity(intent);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<Student>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.e("Upload error:", t.getMessage());
                dialog.dismiss();
            }
        });
    }
    private Dialog getProgressBarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this);
        builder.setView(R.layout.custom_progress_dialog);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        return dialog;
    }

    private void sendImageToServer(File imageFile) {
        Bundle extras = getIntent().getExtras();
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), imageFile);
        MultipartBody.Part fbody =  MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);
        IndentificationAPI indentificationAPI = ServiceGenerator.createService(IndentificationAPI.class);
        Call<ArrayList<Student>> call = indentificationAPI.identifyPerson(fbody);
        Dialog dialog = getProgressBarDialog();
        dialog.show();

        call.enqueue(new Callback<ArrayList<Student>>() {
            @Override
            public void onResponse(Call<ArrayList<Student>> call, Response<ArrayList<Student>> response) {
                Intent intent = new Intent(AttendanceActivity.this, AttendedStudent.class);
                ArrayList<Student> attendedStudentList = response.body();
                if(attendanceArrayList != null){
                    intent.putExtra("attendanceArrayList", (Serializable)attendanceArrayList);
                    intent.putExtra("event", event);
                }
                if(attendedStudentList!= null)
                    intent.putExtra("attendedStudent", (Serializable) attendedStudentList);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ArrayList<Student>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private byte[] convertBitmapToByteArr(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
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