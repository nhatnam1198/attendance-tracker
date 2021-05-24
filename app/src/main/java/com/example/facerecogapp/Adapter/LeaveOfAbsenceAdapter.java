package com.example.facerecogapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Activity.AbsenceRequestApprovalActivity;
import com.example.facerecogapp.Activity.LeaveOfAbsenceActivity;
import com.example.facerecogapp.Const;
import com.example.facerecogapp.Model.Attendance;
import com.example.facerecogapp.Model.AttendanceDetail;
import com.example.facerecogapp.Model.Student;
import com.example.facerecogapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LeaveOfAbsenceAdapter extends RecyclerView.Adapter<LeaveOfAbsenceAdapter.ViewHolder>{
    private List<AttendanceDetail> leaveOfAbsenceRequest;
    private Context context;

    public LeaveOfAbsenceAdapter(Context context, ArrayList<AttendanceDetail> leaveOfAbsenceRequest){
            this.leaveOfAbsenceRequest=leaveOfAbsenceRequest;
            this.context=context;
            }

    @NonNull
    @Override
    public LeaveOfAbsenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            Context context=parent.getContext();
            LayoutInflater inflater=LayoutInflater.from(context);
            View studentListView=inflater.inflate(R.layout.item_student_list_2,parent,false);
        LeaveOfAbsenceAdapter.ViewHolder viewHolder= new LeaveOfAbsenceAdapter.ViewHolder(studentListView);
            return viewHolder;
            }


    @Override
    public void onBindViewHolder(@NonNull LeaveOfAbsenceAdapter.ViewHolder holder,int position) {
        Attendance attendance = leaveOfAbsenceRequest.get(position).getAttendance();
        if(leaveOfAbsenceRequest.get(position).getStatus() == Const.ALLOWED){
            holder.approval_result.setText(Const.ALLOWED_TEXT);
            holder.approval_result.setTextColor(Color.parseColor("#7cfc00"));
        }else if(leaveOfAbsenceRequest.get(position).getStatus() == Const.LEAVE_OF_ABSENCE_REQUEST){
            holder.approval_result.setText(Const.LEAVE_OF_ABSENCE_REQUEST_TEXT);
            holder.approval_result.setTextColor(Color.parseColor("#ff0000"));
        }
        TextView studentNameTextView = holder.item_student_name;
        studentNameTextView.setText(attendance.getStudent().getName());
        TextView studentCodeTextView = holder.item_student_code;
        studentCodeTextView.setText(attendance.getStudent().getStudentCode());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(holder.context, AbsenceRequestApprovalActivity.class);
                intent.putExtra("attendance", (Serializable) attendance);
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount(){
            return leaveOfAbsenceRequest.size();
            }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public TextView item_student_name;
        public TextView item_student_code;
        public TextView approval_result;
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            context = itemView.getContext();
            approval_result = itemView.findViewById(R.id.approvalResultText);
            item_student_name = (TextView) itemView.findViewById(R.id.item_student_name);
            item_student_code = (TextView) itemView.findViewById(R.id.item_student_code);

        }
    }
}