package com.example.facerecogapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.API.AttendanceDetailAPI;
import com.example.facerecogapp.API.EventAPI;
import com.example.facerecogapp.Activity.AbsentStudentList;
import com.example.facerecogapp.Activity.AttendanceActivity;
import com.example.facerecogapp.Activity.AttendedResultActivity;
import com.example.facerecogapp.Activity.MainActivity;
import com.example.facerecogapp.Activity.SuccessActivity;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;

import java.io.Serializable;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    public TextView nameTextView;
    private final int EVENT_CHECKED = 1;
    private List<Event> eventList;
    Context context;
    // Pass in the contact array into the constructor
    public EventAdapter(List<Event> eventList, Context context) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View classesView = inflater.inflate(R.layout.item_classes, parent, false);
        ViewHolder viewHolder = new ViewHolder(classesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        TextView eventNameTextView = holder.eventNameTextView;
        eventNameTextView.setText(event.getName());
        TextView shiftTextView = holder.shiftTextView;
        shiftTextView.setText(event.getShift().getName());
        TextView subjectTextView = holder.subjectTextView;
        subjectTextView.setText(event.getSubjectClass().getSubject().getName());
        TextView classTextView = holder.classTextView;
        classTextView.setText(event.getSubjectClass().getName());
        if(event.getStatus() != EVENT_CHECKED){
            holder.constraintLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.layout_bg_unchecked));
        }
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);

                popup.inflate(R.menu.event_item_options);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Xóa sự kiện!")
                                        .setMessage("Bạn có chắc muốn xóa sự kiện này?")
                                        .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                EventAPI eventAPI = ServiceGenerator.createService(EventAPI.class);
                                                Call<ResponseBody> call = eventAPI.deleteEvent(event.getId());
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code() != 200) {
                                                            Toast.makeText(context, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Toast.makeText(context, "Xóa buổi học thành công", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(context, MainActivity.class);
                                                            notifyItemRemoved(position);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                break;
                            case R.id.edit:
                                //handle menu2 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event.getStatus() == EVENT_CHECKED){
                    Intent intent = new Intent(context, AttendedResultActivity.class);
                    intent.putExtra("event", event);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, AttendanceActivity.class);
                    Integer eventId = eventList.get(position).getId();
                    Integer subjectClassId = eventList.get(position).getSubjectClass().getId();
                    intent.putExtra("event", (Serializable)event);
//                intent.putExtra("eventId", eventId);
                    intent.putExtra("subjectClassId", subjectClassId);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView buttonViewOption;
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView shiftTextView;
        public TextView subjectTextView;
        public ConstraintLayout constraintLayout;
        public TextView classTextView;
        public TextView eventNameTextView;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            eventNameTextView = (TextView) itemView.findViewById(R.id.item_event_name);
            shiftTextView = (TextView) itemView.findViewById(R.id.item_shift);
            subjectTextView = (TextView) itemView.findViewById(R.id.item_subject);
            classTextView = (TextView) itemView.findViewById(R.id.item_class);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        }
    }
}
