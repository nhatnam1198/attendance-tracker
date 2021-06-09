package com.example.facerecogapp.Activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.view.Menu;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facerecogapp.API.EventAPI;
import com.example.facerecogapp.API.ShiftAPI;
import com.example.facerecogapp.API.StatisticsAPI;
import com.example.facerecogapp.API.SubjectAPI;
import com.example.facerecogapp.Adapter.EventAdapter;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.CallBack.ShiftCallBack;
import com.example.facerecogapp.CallBack.SubjectCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Dialog.AddScheduleDialog;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.Model.SubjectClass;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.example.facerecogapp.ui.statistics.StatisticsFragment;
import com.example.facerecogapp.ui.statisticsChart.StatisticsChartFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AddScheduleDialog.AddScheduleDialogListener {
    private static final String CHANNEL_ID = "1";
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView imageView;
    private static final  int REQUEST_IMAGE_CAPTURE = 101;
    private Button button;
    private TextView textView;
    private FloatingActionButton fabButton;
    File tempFile;
    private CalendarView calendarView;
    private FloatingActionButton addScheduleBtn;
    private TextView addScheduleBtnText;
    final Calendar myCalendar = Calendar.getInstance();
    private boolean isFabsVisible = false;
    boolean isLargeLayout;
    private EditText editText;
    private List<Shift> shiftList;
    private List<Subject> subjectList;
    AutoCompleteTextView autoCompleteTextView;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_statistic, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        RecyclerView recyclerViewClasses = (RecyclerView)findViewById(R.id.content_main_recycle_view);
//
//
        fetchShiftList(new ShiftCallBack() {
            @Override
            public void onSuccess(ArrayList<Shift> value) {
                shiftList = value;
            }

            @Override
            public void onFailure() {

            }
        });

        fetchSubjectList(new SubjectCallBack() {
            @Override
            public void onSuccess(ArrayList<Subject> value) {
                subjectList = value;
            }

            @Override
            public void onFailure() {

            }
        });
//
//        calendarView = (CalendarView)findViewById(R.id.calendar_view);
//        calendarView.setVisibility(View.INVISIBLE);
//

//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 110);
//            takingPhoto();
//        } else {
//            takingPhoto();
//        }
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                month = month + 1;
//                String newDate = year+"-"+month + "-" + dayOfMonth;
//
//                getEventByDate(new ResultCallBack<Event>() {
//                    @Override
//                    public void onSuccess(ArrayList<Event> eventArrayList) {
//                        EventAdapter eventAdapter = new EventAdapter(eventArrayList, MainActivity.this);
//                        recyclerViewClasses.setAdapter(eventAdapter);
//                        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT);
//                    }
//                }, newDate);
//
//            }
//        });

    }


//    public void takingPhoto(){
////        button.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
////            }
////        });
//    }
//
//
//
//    private byte[] convertBitmapToByteArr(Bitmap bitmap){
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        return byteArray;
//    }
//
//    public void takePicture(View view){
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        } catch (ActivityNotFoundException e) {
//            // display error state to the user
//        }
//    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.calendar_btn:
//                calendarView.setVisibility(View.VISIBLE);
                return true;
            case R.id.export_csv_btn:
                StatisticsFragment statisticsFragment = (StatisticsFragment)getCurrentFragment();
                ArrayList<Event> eventArrayList = statisticsFragment.getEventList();
                if(eventArrayList == null){
                    Toast.makeText(this, "Chọn lớp học phần bên dưới", Toast.LENGTH_SHORT).show();
                }else{
                    if(eventArrayList.size() == 0){
                        Toast.makeText(this, "Không có dữ liệu cho lớp học phần này", Toast.LENGTH_SHORT).show();
                    }else{
                        if (ContextCompat.checkSelfPermission(
                                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_GRANTED) {
                            SubjectClass subjectClass = eventArrayList.get(0).getSubjectClass();
                            StatisticsAPI statisticsAPI = ServiceGenerator.createService(StatisticsAPI.class);
                            Call<ResponseBody> call = statisticsAPI.getStatisticsSheet(subjectClass.getId());
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()){
                                        boolean isSuccess = writeResponseBodyToDisk(response.body(), subjectClass.getName());
                                        if(isSuccess){
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.outline_description_24)
                                                    .setContentTitle("Tải file thành công")
                                                    .setContentText("Đã lưu file vào " + fileName)
                                                    .setStyle(new NotificationCompat.BigTextStyle()
                                                            .bigText("Đã lưu file vào " + fileName))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                            notificationManager.notify(1, builder.build());
                                            Toast.makeText(MainActivity.this, "Đã lưu file vào " + fileName, Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(MainActivity.this, "Tải file thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(MainActivity.this, "Tải file thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Tải file thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            // You can directly ask for the permission.
                            // The registered ActivityResultCallback gets the result of this request.
                            requestPermissionLauncher.launch(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                    }
                }
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    public Fragment getCurrentFragment(){
        Fragment navHostFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String subjectClassName) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            String directoryPath = root +  "/"+ Const.APP_NAME;
            File dir = new File(directoryPath);
            if (!dir.exists()) dir.mkdirs();
            StringBuilder sb = new StringBuilder();
            sb.append(directoryPath);
            sb.append("/");
            sb.append("BaoCao_");
            sb.append(subjectClassName.replaceAll("\\s+",""));
            sb.append(new SimpleDateFormat("dd_MM_yy").format(new Date()));
            sb.append(".xlsx");
            fileName = sb.toString();
            File futureStudioIconFile = new File(fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
    private void fetchShiftList(ShiftCallBack shiftCallBack){
        try{
            ShiftAPI shiftAPI = ServiceGenerator.createService(ShiftAPI.class);
            Call<ArrayList<Shift>> call = shiftAPI.getShiftList();
            call.enqueue(new Callback<ArrayList<Shift>>() {
                @Override
                public void onResponse(Call<ArrayList<Shift>> call, Response<ArrayList<Shift>> response) {
                    if(!response.isSuccessful()){
                        try {
                            Log.d(getString(R.string.GET_SHIFT_LIST_ERROR), response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    ArrayList<Shift> shiftList = new ArrayList<>();
                    shiftList = response.body();
                    shiftCallBack.onSuccess(shiftList);
                }
                @Override
                public void onFailure(Call<ArrayList<Shift>> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    private void getEventByDate(ResultCallBack<Event> eventCallBack, String eventDate){
//        ServiceGenerator serviceGenerator = new ServiceGenerator();
//        EventAPI eventAPI = serviceGenerator.createService(EventAPI.class);
//        Call<ArrayList<Event>> call = eventAPI.getEventByDate(eventDate);
//        call.enqueue(new Callback<ArrayList<Event>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
//                if(!response.isSuccessful()){
//                    Toast.makeText(MainActivity.this, R.string.HTTP500, Toast.LENGTH_SHORT);
//                }
//                ArrayList<Event> eventArrayList = response.body();
//                eventCallBack.onSuccess(eventArrayList);
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
//                eventCallBack.onFailure(t);
//
//            }
//        });
//    }
    private void fetchSubjectList(SubjectCallBack subjectCallBack){
        try{
            SubjectAPI subjectAPI = ServiceGenerator.createService(SubjectAPI.class);
            Call<ArrayList<com.example.facerecogapp.Model.Subject>> call = subjectAPI.getShiftList();
            call.enqueue(new Callback<ArrayList<Subject>>() {
                @Override
                public void onResponse(Call<ArrayList<Subject>> call, Response<ArrayList<Subject>> response) {
                    if(!response.isSuccessful()){
                        try {
                            Log.d(getString(R.string.GET_SHIFT_LIST_ERROR), response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    ArrayList<Subject> subjectList = new ArrayList<>();
                    subjectList = response.body();
                    subjectCallBack.onSuccess(subjectList);
                }
                @Override
                public void onFailure(Call<ArrayList<Subject>> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}