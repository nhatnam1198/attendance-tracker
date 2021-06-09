package com.example.facerecogapp.Other;

import android.renderscript.Sampler;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter extends ValueFormatter {

    private final String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private final BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }



    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        long emissionsMilliSince1970Time = TimeUnit.DAYS.toMillis((long)value);
        // Show time in local version
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(emissionsMilliSince1970Time);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM");

        return mDay+1+"/"+mMonth;
//        return dateTimeFormat.format(timeMilliseconds);
//        return new SimpleDateFormat("dd/MM").format(new Date((long)value));
    }
}
