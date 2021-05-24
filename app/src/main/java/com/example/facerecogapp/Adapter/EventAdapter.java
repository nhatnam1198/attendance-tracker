package com.example.facerecogapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facerecogapp.Activity.AttendanceActivity;
import com.example.facerecogapp.Activity.AttendedResultActivity;
import com.example.facerecogapp.Model.Event;
import com.example.facerecogapp.R;

import java.io.Serializable;
import java.util.List;

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
        }
    }
}
