package com.example.yownner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
    private List<Item_timeTable> timeList;

    public TimeAdapter(List<Item_timeTable> timeList) {
        this.timeList = timeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_timeTable timeItem = timeList.get(position);
        holder.timeTextView.setText(timeItem.getTime());
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }


}
