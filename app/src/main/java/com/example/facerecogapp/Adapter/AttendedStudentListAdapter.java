package com.example.facerecogapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendedStudentListAdapter extends RecyclerView.Adapter<AttendedStudentListAdapter.ViewHolder> {
    private List<Student> attendedStudentList;
    private Context context;

    public AttendedStudentListAdapter(Context context, ArrayList<Student> attendedStudentList) {
        this.attendedStudentList = attendedStudentList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendedStudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View studentListView = inflater.inflate(R.layout.item_student_list, parent, false);
        AttendedStudentListAdapter.ViewHolder viewHolder = new AttendedStudentListAdapter.ViewHolder(studentListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendedStudentListAdapter.ViewHolder holder, int position) {
        TextView studentNameTextView = holder.item_student_name;
        studentNameTextView.setText(attendedStudentList.get(position).getName());
        TextView studentCodeTextView = holder.item_student_code;
        studentCodeTextView.setText(attendedStudentList.get(position).getStudentCode());
        TextView studentEmailTextView = holder.item_student_email;
        studentEmailTextView.setText(attendedStudentList.get(position).getEmail());
        if(attendedStudentList.get(position).getProfileImage() != null){
            byte[] bytes = Base64.decode(attendedStudentList.get(position).getProfileImage(), Base64.DEFAULT);
            Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.circle_profile_image_view.setImageBitmap(profileImageBitmap);
        }
    }

@Override
public int getItemCount() {
        return attendedStudentList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView item_student_name;
        public TextView item_student_code;
        public TextView item_student_email;
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
            circle_profile_image_view = itemView.findViewById(R.id.profile_image);
        }
    }
}
