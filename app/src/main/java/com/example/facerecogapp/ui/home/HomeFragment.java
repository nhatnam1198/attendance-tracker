package com.example.facerecogapp.ui.home;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.API.EventAPI;
import com.example.facerecogapp.API.ShiftAPI;
import com.example.facerecogapp.API.SubjectAPI;
import com.example.facerecogapp.Activity.MainActivity;
import com.example.facerecogapp.Adapter.EventAdapter;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.CallBack.ShiftCallBack;
import com.example.facerecogapp.CallBack.SubjectCallBack;
import com.example.facerecogapp.Dialog.AddScheduleDialog;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final  int REQUEST_IMAGE_CAPTURE = 101;
    private List<Shift> shiftList;
    private List<Subject> subjectList;
    private CalendarView calendarView;
    private RecyclerView recyclerViewClasses;
    private FloatingActionButton addScheduleBtn;
    private TextView addScheduleBtnText;
    private FloatingActionButton fabButton;
    private boolean isFabsVisible = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        recyclerViewClasses = (RecyclerView) root.findViewById(R.id.home_fragment_recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewClasses.setLayoutManager(layoutManager);
        addScheduleBtn = (FloatingActionButton)root.findViewById(R.id.add_schedule_btn);
        addScheduleBtnText = root.findViewById(R.id.add_schedule_btn_text);
        addScheduleBtn.setVisibility(View.GONE);
        addScheduleBtnText.setVisibility(View.GONE);
        addScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                AddScheduleDialog fragment = new AddScheduleDialog();
                Bundle bundle = new Bundle();
                if(shiftList != null){
                    bundle.putSerializable("shiftList", (ArrayList<Shift>) shiftList);
                    bundle.putSerializable("subjectList", (ArrayList<Subject>) subjectList);
                    fragment.setArguments(bundle);
                    fragment.show(fragmentManager, "dialog");

                }
            }
        });
        fabButton = (FloatingActionButton)root.findViewById(R.id.fab);
        isFabsVisible = false;
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFabsVisible){
                    fabButton.show();
                    addScheduleBtn.show();
                    addScheduleBtn.setVisibility(View.VISIBLE);
                    addScheduleBtnText.setVisibility(View.VISIBLE);
                    isFabsVisible = true;
                }else{
                    addScheduleBtn.hide();
                    addScheduleBtn.setVisibility(View.GONE);
                    addScheduleBtnText.setVisibility(View.GONE);
                    isFabsVisible = false;
                }
            }
        });

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

        calendarView = (CalendarView)root.findViewById(R.id.home_fragment_calendar_view);



        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 110);
            takingPhoto();
        } else {
            takingPhoto();
        }
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String newDate = year+"-"+ month + "-" + dayOfMonth;

                getEventByDate(new ResultCallBack<Event>() {
                    @Override
                    public void onSuccess(ArrayList<Event> eventArrayList) {
                        EventAdapter eventAdapter = new EventAdapter(eventArrayList, getContext());
                        recyclerViewClasses.setAdapter(eventAdapter);
                        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT);
                    }
                }, newDate);

            }
        });
        return root;

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.export_csv_btn).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    public void takingPhoto(){
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
//            }
//        });
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
    private void getEventByDate(ResultCallBack<Event> eventCallBack, String eventDate){
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        EventAPI eventAPI = serviceGenerator.createService(EventAPI.class);
        Call<ArrayList<Event>> call = eventAPI.getEventByDate(eventDate);
        call.enqueue(new Callback<ArrayList<Event>>() {
            @Override
            public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT);
                }
                ArrayList<Event> eventArrayList = response.body();
                eventCallBack.onSuccess(eventArrayList);
            }

            @Override
            public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                eventCallBack.onFailure(t);

            }
        });
    }
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
}