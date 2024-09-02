package com.example.yownner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.GridViewHolder> {
    private List<Item_table> startAndEndPositionsList;
    private int backgroundColor; // 외부 배경색을 저장하는 변수
    public TimetableAdapter(List<Item_table> startAndEndPositionsList) {
        this.startAndEndPositionsList = startAndEndPositionsList;
    }
    @NonNull
    @Override
    public TimetableAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new GridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableAdapter.GridViewHolder holder, int position) {

        int color; // 기본 배경색 설정

        for (Item_table item : startAndEndPositionsList) {
            String starttime = item.getStartTime();
            String stoptime = item.getStopTime();

            String[] startTimeParts = starttime.split(":");
            String[] stopTimeParts = stoptime.split(":");

            int startHour = Integer.parseInt(startTimeParts[0]) * 3600;
            int startMinute = Integer.parseInt(startTimeParts[1]) * 60;
            int startSecond = Integer.parseInt(startTimeParts[2]);

            int stopHour = Integer.parseInt(stopTimeParts[0]) * 3600;
            int stopMinute = Integer.parseInt(stopTimeParts[1]) * 60;
            int stopSecond = Integer.parseInt(stopTimeParts[2]);

            int startTime = startHour + startMinute + startSecond;
            int stopTime = stopHour + stopMinute + stopSecond;

            int start = startTime / 60;
            int stop = stopTime / 60;

            if (position >= start && position <= stop &&stop != 0) {
                // 아이템별로 다른 색상을 사용하려면 해당 아이템의 색상을 가져옴
                color = item.getColor();
                holder.itemView.setBackgroundColor(color);
            }
        }
        // 배경색 설정


    }



    @Override
    public int getItemCount() {
        return 60 * 24; // 가로 90, 세로 98
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
