package com.example.facerecogapp.Dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.facerecogapp.API.EventAPI;
import com.example.facerecogapp.API.SubjectClassAPI;
import com.example.facerecogapp.Activity.MainActivity;
import com.example.facerecogapp.CallBack.SubjectClassCallBack;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Shift;
import com.example.facerecogapp.Model.Subject;
import com.example.facerecogapp.Model.SubjectClass;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditScheduleDialog extends DialogFragment{
    private static final int EVENT_UNCHECKED = 0;
    final Calendar myCalendar = Calendar.getInstance();
    private Event event;
    private EditText dateEditText;
    private Button okButton;
    private Button cancelButton;
    ArrayList<SubjectClass> subjectClassArrayList = new ArrayList<>();
    private Integer shiftId;
    private Integer subjectClassId;
    private SubjectClass chosenSubjectClass;
    private Integer eventId;
    private AutoCompleteTextView subjectAutoCompleteTextView;
    private AutoCompleteTextView shiftAutoCompleteTextView;
    private AutoCompleteTextView subjectClassAutoCompleteTextView;
    private Context context;
    private ArrayList<Subject> subjectList;
    private ArrayList<Shift> shiftList;
    private boolean isUserSelectionChanged = false;
    public interface EditScheduleDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    EditScheduleDialog.EditScheduleDialogListener listener;
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (EditScheduleDialog.EditScheduleDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(EditScheduleDialog.this.toString()
                    + " must implement EditScheduleDialogListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.dialog_edit_schedule, container, false);
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_schedule, null);
        builder.setView(view);
        Bundle bundle = getArguments();

        this.event = (Event)bundle.getSerializable("event");
        shiftList = (ArrayList<Shift>)bundle.getSerializable("shiftList");
        subjectList = (ArrayList<Subject>)bundle.getSerializable("subjectList");

        subjectAutoCompleteTextView = view.findViewById(R.id.dialog_edit_schedule_subject_autocomplete);
        shiftAutoCompleteTextView = view.findViewById(R.id.dialog_edit_shift_autocomplete);
        subjectClassAutoCompleteTextView = view.findViewById(R.id.dialog_edit_schedule_subject_class_autocomplete);
        dateEditText = (EditText)view.findViewById(R.id.dialog_edit_schedule_date_edit_text);
        setDefaultValueForDialog();

        ArrayList<String> shiftName = new ArrayList<>();
        for(int i = 0; i< shiftList.size(); i++){
            shiftName.add(shiftList.get(i).getName());
        }

        ArrayList<String> subjectNames = new ArrayList<>();
        for(int i = 0; i< subjectList.size(); i++){
            subjectNames.add(subjectList.get(i).getName());
        }

        ArrayAdapter<String> subjectArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, subjectNames);
//        subjectAutoCompleteTextView.setText("Chọn môn học", false);

        subjectAutoCompleteTextView.setAdapter(subjectArrayAdapter);
        subjectAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isUserSelectionChanged = true;
                Integer subjectId = subjectList.get(position).getId();
                getSubjectClassBySubjectClassIdAndTeacherId(new SubjectClassCallBack() {
                    @Override
                    public void onSuccess(ArrayList<SubjectClass> subjectClassArrayList) {
                        subjectClassAutoCompleteTextView.setText("Chọn lớp học phần", false);
                        if(subjectClassArrayList.size() == 0){
                            Toast.makeText(getContext(), "Không có lớp học phần cho môn học này", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<String> subjectClassNames = new ArrayList<>();
                        for(int i = 0; i< subjectClassArrayList.size(); i++){
                            subjectClassNames.add(subjectClassArrayList.get(i).getName());
                        }
                        ArrayAdapter subjectClassArrayAdapter = new ArrayAdapter(getContext(), R.layout.list_item, subjectClassNames);

                        subjectClassAutoCompleteTextView.setAdapter(subjectClassArrayAdapter);
                        subjectClassAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                isUserSelectionChanged = true;
                                subjectClassId = subjectClassArrayList.get(position).getId();
                                chosenSubjectClass = subjectClassArrayList.get(position);
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT);
                    }
                }, subjectId, 73);
            }
        });
        ArrayAdapter shiftArrayAdapter = new ArrayAdapter(getContext(), R.layout.list_item, shiftName);
//        shiftAutoCompleteTextView.setText("Chọn ca học", false);
        shiftAutoCompleteTextView.setAdapter(shiftArrayAdapter);
        shiftAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isUserSelectionChanged = true;
                shiftId = shiftList.get(position).getId();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserSelectionChanged = true;
                new DatePickerDialog(v.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        okButton = view.findViewById(R.id.dialog_edit_ok_btn);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.onDialogPositiveClick(AddScheduleDialog.this);
                if(!isUserSelectionChanged){
                    dismiss();
                    return;
                }
                if(!isDateValid(dateEditText.getText().toString())){
                    Toast.makeText(getContext(), "Ngày bạn chọn không hợp lệ xin mời chọn ngày nằm trong thời gian học đã lên trước của lớp học phần", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(shiftId != 0 && subjectClassId != 0){
                    Event event = new Event();
                    event.setId(eventId);
                    event.setShiftId(shiftId);
                    event.setSubjectClassId(subjectClassId);
                    event.setStatus(EVENT_UNCHECKED);
                    event.setName("Đã chỉnh sửa");
                    String datePattern = "yyyy/MM/dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                    try {
                        Date date = simpleDateFormat.parse(dateEditText.getText().toString());
                        String[] dateString = dateEditText.getText().toString().split("/");
                        String year = dateString[0];
                        String month = dateString[1];
                        String day = dateString[2];
                        event.setDateTime(month+"/"+day+"/"+year);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try{
                        EventAPI eventAPI = ServiceGenerator.createService(EventAPI.class);
                        Call<Void> call = eventAPI.updateEvent(event);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(!response.isSuccessful()){
                                    if(response.code() == Const.HttpStatusCode.INTERNAL_SERVER_ERROR.getValue()){
                                        Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if(response.code() == Const.HttpStatusCode.CONFLICT.getValue()){
                                        Toast.makeText(getContext(), "Sự kiện này đã tồn tại", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    try {
                                        Log.d(getString(R.string.EDIT_EVENT_ERROR), response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }else {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    Toast.makeText(getContext(), "Chỉnh sửa sự kiện thành công", Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getContext(), "Hãy chọn các lựa chọn trên.", Toast.LENGTH_SHORT);
                    return;
                }
            }
        });
        cancelButton = view.findViewById(R.id.dialog_edit_cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.onDialogNegativeClick(EditScheduleDialog.this);
            }
        });
        return builder.create();
    }

    private void setDefaultValueForDialog() {
        eventId = event.getId();
        String subjectName = event.getSubjectClass().getSubject().getName();
        String shiftName = event.getShift().getName();
        String subjectClassName = event.getSubjectClass().getName();
        String eventDate = event.getDateTime();
        chosenSubjectClass = event.getSubjectClass();
        subjectAutoCompleteTextView.setText(subjectName);
        shiftAutoCompleteTextView.setText(shiftName);
        subjectClassAutoCompleteTextView.setText(subjectClassName);

        shiftId = event.getShift().getId();
        subjectClassId = event.getSubjectClass().getId();

        String[] dateString = event.getDateTime().split("-");
        String year = dateString[0];
        String month = dateString[1];
        String day = dateString[2];
        dateEditText.setText(year+"/"+month+"/"+day);
    }

    public void getSubjectClassBySubjectClassIdAndTeacherId(SubjectClassCallBack subjectCallBack, Integer subjectId, Integer teacherId){
        try{
            SubjectClassAPI subjectClassAPI = ServiceGenerator.createService(SubjectClassAPI.class);
            Call<ArrayList<SubjectClass>> call = subjectClassAPI.getSubjectClassListBySubjectIdAndTeacherId(subjectId, teacherId);
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
                    Toast.makeText(getContext(), R.string.HTTP500, Toast.LENGTH_SHORT);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean isDateValid(String date){
        try {
            Date eventDate = new SimpleDateFormat("yyyy/MM/dd").parse(date);
            if(eventDate.before(chosenSubjectClass.getStartDateTime()) || eventDate.after(chosenSubjectClass.getEndDateTime())){
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        this.dateEditText.setText(sdf.format(myCalendar.getTime()));
    }
}
