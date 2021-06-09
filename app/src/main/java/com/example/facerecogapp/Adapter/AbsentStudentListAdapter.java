package com.example.facerecogapp.Adapter;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AbsentStudentListAdapter extends RecyclerView.Adapter<AbsentStudentListAdapter.ViewHolder> {
    private List<Student> absentStudentList;
    SparseBooleanArray checkboxSparseBooleanArray = new SparseBooleanArray();
    private Context context;

    public AbsentStudentListAdapter(Context context, ArrayList<Student> absentStudentList) {
        this.absentStudentList = absentStudentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AbsentStudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View studentListView = inflater.inflate(R.layout.item_absent_student_list, parent, false);
        AbsentStudentListAdapter.ViewHolder viewHolder = new AbsentStudentListAdapter.ViewHolder(studentListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AbsentStudentListAdapter.ViewHolder holder, int position) {
        Student absentStudent = absentStudentList.get(position);
        TextView studentNameTextView = holder.item_student_name;
        studentNameTextView.setText(absentStudent.getName());
        TextView studentCodeTextView = holder.item_student_code;
        studentCodeTextView.setText(absentStudent.getStudentCode());
        TextView studentEmailTextView = holder.item_student_email;
        studentEmailTextView.setText(absentStudent.getEmail());
        if(absentStudent.getProfileImage() != null){
            byte[] bytes = Base64.decode(absentStudent.getProfileImage(), Base64.DEFAULT);
            Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.circle_profile_image_view.setImageBitmap(profileImageBitmap);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = holder.checkBox;

                if(checkBox.isChecked()){
                    checkboxSparseBooleanArray.put(position, false);
                    checkBox.setChecked(false);
                }else{
                    checkBox.setChecked(true);
                    checkboxSparseBooleanArray.put(position, true);
                }
            }
        });
    }
    public SparseBooleanArray getcheckboxSparseBooleanArray(){
        return checkboxSparseBooleanArray;
    }
    @Override
    public int getItemCount() {
        return absentStudentList.size();
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
            circle_profile_image_view = itemView.findViewById(R.id.profile_image);
        }
    }


}