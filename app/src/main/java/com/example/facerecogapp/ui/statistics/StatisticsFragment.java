package com.example.facerecogapp.ui.statistics;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.facerecogapp.API.EventAPI;
import com.example.facerecogapp.API.SubjectClassAPI;
import com.example.facerecogapp.CallBack.ResultCallBack;
import com.example.facerecogapp.CallBack.SubjectClassCallBack;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.SubjectClass;
import com.example.facerecogapp.Other.DayAxisValueFormatter;
import com.example.facerecogapp.Other.Fill;
import com.example.facerecogapp.Other.MyAxisValueFormatter;
import com.example.facerecogapp.Other.XYMarkerView;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.example.facerecogapp.ui.home.HomeViewModel;
import com.example.facerecogapp.ui.statisticsChart.StatisticsChartFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {
    private StatisticsViewModel mViewModel;
    private PieChart pieChart;
    private BarChart barChart;
    private Integer subjetClassId;
    ArrayList<SubjectClass> subjectClasses = new ArrayList<>();
    ArrayList<Event> eventList = new ArrayList<>();
    private AutoCompleteTextView subjectClassAutoCompleteTextView;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.statistics_fragment, container, false);
//        pieChart = root.findViewById(R.id.pie_chart);
//        barChart = root.findViewById(R.id.bar_chart);
//        initBarChart();
//        initPieChart();
        subjectClassAutoCompleteTextView = root.findViewById(R.id._statistics_fragment_subject_class_autocomplete);
        getSubjectClassByTeacherId(new SubjectClassCallBack() {
            @Override
            public void onSuccess(ArrayList<SubjectClass> value) {
                subjectClasses = value;
                if(subjectClasses.size() == 0){
                    Toast.makeText(getContext(), "Không có lớp học phần cho môn học này", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> subjectClassNames = new ArrayList<>();
                for(int i = 0; i< subjectClasses.size(); i++){
                    subjectClassNames.add(subjectClasses.get(i).getName());
                }
                ArrayAdapter subjectClassArrayAdapter = new ArrayAdapter(getContext(), R.layout.list_item, subjectClassNames);
                subjectClassAutoCompleteTextView.setText("Chọn lớp học phần", false);
                subjectClassAutoCompleteTextView.setAdapter(subjectClassArrayAdapter);
                subjectClassAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        subjetClassId = subjectClasses.get(position).getId();
                        getEventListBySubjectClassId(new ResultCallBack<Event>() {
                            @Override
                            public void onSuccess(ArrayList<Event> value) {
                                eventList = value;
                                StatisticsChartFragment statisticsChartFragment = (StatisticsChartFragment)getChildFragmentManager().findFragmentById(R.id.child_fragment_container);
                                statisticsChartFragment.getEventList(eventList);
                                StatisticsViewModel model =  new ViewModelProvider(getActivity()).get(StatisticsViewModel.class);
                                model.setEventArrayList(eventList);
                            }
                            @Override
                            public void onFailure(Throwable t) {

                            }
                        }, subjetClassId);
                    }
                });
            }
            @Override
            public void onFailure() {

            }
        }, 73);
        return root;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new StatisticsChartFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_container, childFragment).commit();
    }





    public void getEventListBySubjectClassId(ResultCallBack<Event> eventResultCallBack, Integer subjetClassId){
        try{
            EventAPI eventAPI = ServiceGenerator.createService(EventAPI.class);
            Call<ArrayList<Event>> call = eventAPI.getEventListBySubjectClassId(subjetClassId);
            call.enqueue(new Callback<ArrayList<Event>>() {
                @Override
                public void onResponse(Call<ArrayList<Event>> call, Response<ArrayList<Event>> response) {
                    if(!response.isSuccessful()){
                        try {
                            Log.d(getString(R.string.GET_SUBJECT_CLASS_ERROR), response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    eventResultCallBack.onSuccess(response.body());
                }
                @Override
                public void onFailure(Call<ArrayList<Event>> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getSubjectClassByTeacherId(SubjectClassCallBack subjectCallBack, Integer teacherId){
        try{
            SubjectClassAPI subjectClassAPI = ServiceGenerator.createService(SubjectClassAPI.class);
            Call<ArrayList<SubjectClass>> call = subjectClassAPI.getSubjectClassByTeacherId(teacherId);
            call.enqueue(new Callback<ArrayList<SubjectClass>>() {
                @Override
                public void onResponse(Call<ArrayList<SubjectClass>> call, Response<ArrayList<SubjectClass>> response) {
                    if(!response.isSuccessful()){
                        try {
                            Log.d(getString(R.string.GET_SUBJECT_CLASS_ERROR), response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    subjectCallBack.onSuccess(response.body());
                }
                @Override
                public void onFailure(Call<ArrayList<SubjectClass>> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        // TODO: Use the ViewModel
    }

}