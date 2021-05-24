package com.example.facerecogapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Activity.AttendanceActivity;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {
    private List<Attendance> attendanceList;
    private Context context;

    public StudentListAdapter(Context context, ArrayList<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
        this.context = context;

    }

    @NonNull
    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View studentListView = inflater.inflate(R.layout.item_student_list, parent, false);
        StudentListAdapter.ViewHolder viewHolder = new StudentListAdapter.ViewHolder(studentListView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.ViewHolder holder, int position) {

        TextView studentNameTextView = holder.item_student_name;
        studentNameTextView.setText(attendanceList.get(position).getStudent().getName());
        TextView studentCodeTextView = holder.item_student_code;
        studentCodeTextView.setText(attendanceList.get(position).getStudent().getStudentCode());
        TextView studentEmailTextView = holder.item_student_email;
        studentEmailTextView.setText(attendanceList.get(position).getStudent().getEmail());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, AttendanceActivity.class);
////                String studentName = attendanceList.get(position).getStudent().getName();
////                Integer studentCode = attendanceList.get(position).getStudent().getStudentId();
////                String studentEmail = attendanceList.get(position).getStudent().getEmail();
////                Student student = new Student();
////                student.setName(studentName);
////                student.setEmail(studentEmail);
////                student.setStudentCode(String.valueOf(studentCode));
////                intent.putExtra("student", student);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView item_student_name;
        public TextView item_student_code;
        public TextView item_student_email;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            item_student_name = (TextView) itemView.findViewById(R.id.item_student_name);
            item_student_code = (TextView) itemView.findViewById(R.id.item_student_code);
            item_student_email = (TextView) itemView.findViewById(R.id.item_student_email);

        }
    }
}
