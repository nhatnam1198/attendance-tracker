package com.example.facerecogapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendedResultAdapter extends RecyclerView.Adapter<AttendedResultAdapter.ViewHolder> {
    private ArrayList<AttendanceDetail>  attendanceDetailArrayList;
    boolean isEnabled = false;
    private Context context;
    SparseBooleanArray checkboxSparseBooleanArray = new SparseBooleanArray();
    public AttendedResultAdapter(Context context, ArrayList<AttendanceDetail> attendanceDetailArrayList) {
        this.attendanceDetailArrayList = attendanceDetailArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendedResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View studentListView = inflater.inflate(R.layout.item_absent_student_list, parent, false);
        AttendedResultAdapter.ViewHolder viewHolder = new AttendedResultAdapter.ViewHolder(studentListView);
        return viewHolder;
    }
    public SparseBooleanArray getCheckBoxSparseBooleanArray(){
        return checkboxSparseBooleanArray;
    }
    @Override
    public void onBindViewHolder(@NonNull AttendedResultAdapter.ViewHolder holder, int position) {
        Student student = attendanceDetailArrayList.get(position).getAttendance().getStudent();
        Integer status = attendanceDetailArrayList.get(position).getStatus();
        TextView studentNameTextView = holder.item_student_name;
        studentNameTextView.setText(student.getName());
        TextView studentCodeTextView = holder.item_student_code;
        studentCodeTextView.setText(student.getStudentCode());
        TextView studentEmailTextView = holder.item_student_email;
        studentEmailTextView.setText(student.getEmail());
        if(student.getProfileImage() != null){
            byte[] bytes = Base64.decode(student.getProfileImage(), Base64.DEFAULT);
            Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.circle_profile_image_view.setImageBitmap(profileImageBitmap);
        }
            if(status == Const.ATTENDED || status == Const.ALLOWED){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);


        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public int getItemCount() {
        return attendanceDetailArrayList.size();
    }
    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView item_student_name;
        public TextView item_student_code;
        public TextView item_student_email;
        public CheckBox checkBox;
        public CircleImageView circle_profile_image_view;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            item_student_name = (TextView) itemView.findViewById(R.id.item_student_name);
            item_student_code = (TextView) itemView.findViewById(R.id.item_student_code);
            item_student_email = (TextView) itemView.findViewById(R.id.item_student_email);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setEnabled(false);
            circle_profile_image_view = itemView.findViewById(R.id.profile_image);
        }
    }
}
