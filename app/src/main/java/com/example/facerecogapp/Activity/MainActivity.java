package com.example.facerecogapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.facerecogapp.API.TeacherAPI;
import com.example.facerecogapp.Adapter.EventAdapter;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.CallBack.ShiftCallBack;
import com.example.facerecogapp.CallBack.SubjectCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Dialog.AddScheduleDialog;
import com.example.facerecogapp.Dialog.EditScheduleDialog;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.Model.SubjectClass;
import com.example.facerecogapp.Model.Teacher;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.example.facerecogapp.ui.statistics.StatisticsFragment;
import com.example.facerecogapp.ui.statisticsChart.StatisticsChartFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MainActivity extends AppCompatActivity implements AddScheduleDialog.AddScheduleDialogListener, EditScheduleDialog.EditScheduleDialogListener {
    private final static String[] SCOPES = {"api://e2da588d-2e4b-4020-80f6-b97c0fe62adf/.default"};
    SharedPreferences sharedPref;
    final static String AUTHORITY = "https://login.microsoftonline.com/b3b9cfc4-0719-45ae-9178-946ebe5ac23f";
    private static final String CHANNEL_ID = "1";
    private AppBarConfiguration mAppBarConfiguration;
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private ImageView imageView;
    private static final  int REQUEST_IMAGE_CAPTURE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button button;
    private TextView textView;
    private FloatingActionButton fabButton;
    File tempFile;
    private CalendarView calendarView;
    private FloatingActionButton addScheduleBtn;
    private TextView addScheduleBtnText;
    final Calendar myCalendar = Calendar.getInstance();
    private Teacher teacher;
    private boolean isFabsVisible = false;
    boolean isLargeLayout;
    private EditText editText;
    private List<Shift> shiftList;
    private List<Subject> subjectList;
    AutoCompleteTextView autoCompleteTextView;
    private String fileName;
    // key for storing email.
    public static final String EMAIL_KEY = "email_key";
    private String authorizationHeader;
    private TextView nav_header_name;
    private TextView nav_header_email;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_header_name = (TextView)headerView.findViewById(R.id.nav_header_name);
        nav_header_email = (TextView)headerView.findViewById(R.id.nav_header_email);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_statistic, R.id.nav_manage_account, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {logoutAccount();
                return true;});
        initMsalInstance();
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString(getString(R.string.email), "0");
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
    private Dialog getProgressBarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.custom_progress_dialog);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        return dialog;
    }

    private void getTeacherInfo(ResultCallBack callBack, String email) {
        try{
            TeacherAPI teacherAPI = ServiceGenerator.createService(TeacherAPI.class);
            Call<Teacher> call = teacherAPI.getTeacher(email);
            call.enqueue(new Callback<Teacher>() {
                @Override
                public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                    if(response.code() == 401){
                        Toast.makeText(MainActivity.this, "Hết phiên đăng nhập", Toast.LENGTH_SHORT).show();
                    }else{
                        teacher = response.body();
                        ArrayList<Teacher> teacherArrayList = new ArrayList<Teacher>();
                        teacherArrayList.add(teacher);
                        callBack.onSuccess(teacherArrayList);
                    }
                }

                @Override
                public void onFailure(Call<Teacher> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initMsalInstance() {
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        loadAccount();

                    }
                    @Override
                    public void onError(MsalException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void logoutAccount() {
        mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            @Override
            public void onError(@NonNull MsalException exception){
//                    displayError(exception);
            }
        });
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
    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                if(activeAccount != null){
                    getTeacherInfo(new ResultCallBack<Teacher>() {
                        @Override
                        public void onSuccess(ArrayList<Teacher> teacherArrayList) {
                            Teacher teacher = teacherArrayList.get(0);
                            if(teacher != null){
                                nav_header_name.setText(teacher.getName());
                                nav_header_email.setText(teacher.getEmail());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    }, activeAccount.getUsername());
                }
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
//                    performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        displayError(exception);
                    }
                });
            }
        });
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.nav_logout){
//            mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
//                @Override
//                public void onSignOut() {
////                    updateUI(null);
////                    performOperationOnSignOut();
//                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
//                @Override
//                public void onError(@NonNull MsalException exception){
////                    displayError(exception);
//                }
//            });
//        }
//        return true;
//    }

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
                            Call<ResponseBody> call = statisticsAPI.getStatisticsSheet(subjectClass.getId(), userEmail);
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