package com.example.facerecogapp.ui.statisticsChart;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Other.DayAxisValueFormatter;
import com.example.facerecogapp.Other.Fill;
import com.example.facerecogapp.Other.MyAxisValueFormatter;
import com.example.facerecogapp.Other.XYMarkerView;
import com.example.facerecogapp.R;
import com.example.facerecogapp.ui.statistics.StatisticsViewModel;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;

public class StatisticsChartFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private StatisticsChartViewModel mViewModel;
    private ArrayList<Event> eventList;
    private BarChart stackedBarChart;
    private PieChart pieChart;
    private TextView eventsCountTextView;
    private TextView trackedEventsCountTextView;
    private TextView overall_attendance_rate_textView;
    private int sumNumberOfAttendances;

    public static StatisticsChartFragment newInstance() {
        return new StatisticsChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.statistics_chart_fragment, container, false);
        eventsCountTextView= (TextView) root.findViewById(R.id.eventsCountTextView);
        trackedEventsCountTextView= (TextView) root.findViewById(R.id.tracked_event_count_text_view);
        overall_attendance_rate_textView= (TextView) root.findViewById(R.id.overall_attendance_rate_textView);

        pieChart = root.findViewById(R.id.pie_chart);
        setHasOptionsMenu(true);
        stackedBarChart = root.findViewById(R.id.bar_chart);
        return root;
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.calendar_btn).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        eventsCountTextView.setText(String.valueOf(this.eventList.size()));
        StatisticsViewModel model =  new ViewModelProvider(getActivity()).get(StatisticsViewModel.class);
        model.getEventArrayList().observe(getViewLifecycleOwner(), eventArrayList -> {this.eventList = eventArrayList;});
        mViewModel = new ViewModelProvider(this).get(StatisticsChartViewModel.class);
        // TODO: Use the ViewModel
    }

    public void getEventList(ArrayList<Event> events){
        this.eventList = events;
        setEventsCountTextView();
        setTrackedEventsCountTextView();
        setOverallAttendanceRate();
        initPieChart();
        initBarChart();
    }

    private void setOverallAttendanceRate() {
        sumNumberOfAttendances = countSumNumberOfAttendances();
        int sumNumberOfAttendedStudents = countSumOfAttendedAndAllowedStudents();
        Double overallAttendanceRate = (double)sumNumberOfAttendedStudents / sumNumberOfAttendances;
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        overall_attendance_rate_textView.setText(numberFormat.format(overallAttendanceRate) + "%");
    }

    private int countSumOfAttendedAndAllowedStudents() {
        int count = 0;
        for (Event event:
                eventList) {
            List<AttendanceDetail> attendanceDetailList = event.getAttendanceDetailsList();
            for(int i = 0; i <attendanceDetailList.size(); i++){
                int attendanceDetailStatus = attendanceDetailList.get(i).getStatus();
                if (attendanceDetailStatus == Const.ATTENDED || attendanceDetailStatus == Const.ALLOWED){
                    count += 1;
                }
            }
        }
        return count;
    }

    private int countSumNumberOfAttendances() {
        int count = 0;
        for (Event event:
             eventList) {
            count += event.getAttendanceDetailsList().size();
        }
        return count;
    }

    private void setTrackedEventsCountTextView() {
        int count = countTrackedEvents();
        trackedEventsCountTextView.setText(String.valueOf(count));

    }

    private int countTrackedEvents() {
        int count = 0;
        if(eventList != null)
        for(int i = 0; i< eventList.size(); i++){
            if(eventList.get(i).getStatus() == 1){
                count++;
            }
        }
        return count;
    }

    private void setEventsCountTextView() {
        if(this.eventList != null && this.eventList.size() != 0){
            eventsCountTextView.setText(String.valueOf(eventList.size()));
        }else{
            eventsCountTextView.setText(String.valueOf(eventList.size()));
        }

    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Điểm danh");
//        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
    private void initPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(Typeface.MONOSPACE);
        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        pieChart.setOnChartValueSelectedListener(this);

        // entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTypeface(Typeface.MONOSPACE);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        setPieChartData(3, 3);
    }
    private void setPieChartData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        int[] counter = getNumberOfAttendedStudent();

        entries.add(new PieEntry((float)counter[Const.ATTENDED]/sumNumberOfAttendances, "Đi học", getResources().getDrawable(R.drawable.star)));
        entries.add(new PieEntry((float)counter[Const.ALLOWED]/sumNumberOfAttendances, "Có phép", getResources().getDrawable(R.drawable.star)));
        entries.add(new PieEntry((float)counter[Const.ABSENT]/sumNumberOfAttendances, "Vắng", getResources().getDrawable(R.drawable.star)));
        entries.add(new PieEntry((float)counter[Const.LEAVE_OF_ABSENCE_REQUEST]/sumNumberOfAttendances, "Chưa được duyệt", getResources().getDrawable(R.drawable.star)));

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        for (int i = 0; i < count ; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
//                    "A",
//                    getResources().getDrawable(R.drawable.star)));
//        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(Typeface.MONOSPACE);
        pieChart.setData(data);

//        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private int[] getNumberOfAttendedStudent() {
        int[] counter = new int[4];
        int attendedCount = 0;
        int allowedCount = 0;
        int absentCount = 0;
        int leaveOfAbsenceRquestsCount = 0;
        for (Event event:
                eventList) {
            List<AttendanceDetail> attendanceDetailList = event.getAttendanceDetailsList();
            for(int i = 0; i <attendanceDetailList.size(); i++){
                int attendanceDetailStatus = attendanceDetailList.get(i).getStatus();
                if (attendanceDetailStatus == Const.ATTENDED){
                    attendedCount += 1;
                }else if (attendanceDetailStatus == Const.ALLOWED){
                    allowedCount += 1;
                }else if(attendanceDetailStatus == Const.LEAVE_OF_ABSENCE_REQUEST){
                    leaveOfAbsenceRquestsCount += 1;
                }else{
                    absentCount += 1;
                }
            }
        }
        counter[Const.ATTENDED] = attendedCount;
        counter[Const.ALLOWED] = allowedCount;
        counter[Const.LEAVE_OF_ABSENCE_REQUEST] = leaveOfAbsenceRquestsCount;
        counter[Const.ABSENT] = absentCount;
        return counter;
    }

    private void initBarChart() {
        stackedBarChart.setOnChartValueSelectedListener(this);

        stackedBarChart.setOnChartValueSelectedListener(this);

        stackedBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        stackedBarChart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        stackedBarChart.setPinchZoom(false);

        stackedBarChart.setDrawGridBackground(false);
        stackedBarChart.setDrawBarShadow(false);

        stackedBarChart.setDrawValueAboveBar(false);
        stackedBarChart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = stackedBarChart.getAxisLeft();
        leftAxis.setValueFormatter(new MyAxisValueFormatter());
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextColor(Color.BLACK);
        stackedBarChart.getAxisRight().setEnabled(false);


        DayAxisValueFormatter dayAxisValueFormatter = new DayAxisValueFormatter(stackedBarChart);

        XAxis xLabels = stackedBarChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);
        xLabels.setValueFormatter(dayAxisValueFormatter);
        // chart.setDrawXLabels(false);
        // chart.setDrawYLabels(false);



        Legend l = stackedBarChart.getLegend();
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        setBarChartData();
    }
    private void setBarChartData() {
        float start = 1f;
        ArrayList<BarEntry> values = new ArrayList<>();
        int yAxisColumns = 4;
        for (int i = 0; i< eventList.size();i++){
            int attendedCount = 0;
            int allowedCount = 0;
            int absentCount = 0;
            int leaveOfAbsenceRquestsCount = 0;
            Event event = eventList.get(i);
            String eventDateString = eventList.get(i).getDateTime();
            Date eventDate = null;
            try {
                eventDate = (new SimpleDateFormat("yyyy-MM-dd").parse(eventDateString));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int j = 0; j< event.getAttendanceDetailsList().size();j++) {
                int attendanceDetailStatus = event.getAttendanceDetailsList().get(j).getStatus();
                if (attendanceDetailStatus == Const.ATTENDED){
                    attendedCount += 1;
                }else if (attendanceDetailStatus == Const.ALLOWED){
                    allowedCount += 1;
                }else if(attendanceDetailStatus == Const.LEAVE_OF_ABSENCE_REQUEST){
                    leaveOfAbsenceRquestsCount += 1;
                }else{
                    absentCount += 1;
                }
            }
            // bar chart can't display value on the chart if the x axis values is milliseconds. The column becomes too thin to display so i convert it to days using TimeUnit
            float xAxisValue = TimeUnit.MILLISECONDS.toDays((long)eventDate.getTime());
            float[] yAxisValues = new float[] {attendedCount, absentCount, allowedCount, leaveOfAbsenceRquestsCount};
            values.add(new BarEntry(xAxisValue, yAxisValues, getResources().getDrawable(R.drawable.fade_red)));
        }
        BarDataSet set1;
        if (stackedBarChart.getData() != null &&
                stackedBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) stackedBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            stackedBarChart.getData().notifyDataChanged();
            stackedBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, eventList.get(0).getSubjectClass().getName());
            set1.setDrawIcons(false);
            set1.setColors(getColors(yAxisColumns));
            set1.setStackLabels(new String[]{"Đi học", "Nghỉ", "Có phép", "Chưa duyệt đơn"});

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new MyAxisValueFormatter());
            data.setValueTextColor(Color.BLACK);

            stackedBarChart.setData(data);
        }

        stackedBarChart.setFitBars(true);
        stackedBarChart.invalidate();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    private int[] getColors(int yAxisColumns) {

        // have as many colors as stack-values per entry
        int[] colors = new int[yAxisColumns];

        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, yAxisColumns);

        return colors;
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}