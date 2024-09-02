package com.example.yownner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.CustomViewHolder> {
    private List<Item_timer> dataList;
    private Context context;
    private String selectedDate;
    private String upid;

    public TimerListAdapter(Context context, List<Item_timer> dataList, String upid, String selectedDate){
        this.context = context;
        this.dataList = dataList;
        this.upid = upid;
        this.selectedDate = selectedDate;
    }
    @NonNull
    @Override
    public TimerListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.timer_itemview, parent, false);
        return new TimerListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerListAdapter.CustomViewHolder holder, int position) {
        Item_timer timeritem = dataList.get(position);
        holder.tv_timer_name.setText(timeritem.getName());
        String measurementtime = timeritem.getTotalSeconds();
        int time = 0; // 기본값 0 설정
        if (measurementtime != null && !measurementtime.equals("null") && !measurementtime.isEmpty()) {
            time = Integer.parseInt(measurementtime);
        }
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        String colorCode = timeritem.getColorhex();
        String starttime = timeritem.getStarttime();
        String stoptime = timeritem.getStoptime();
        System.out.println(formattedTime);
        holder.tv_timer_measurementtime.setText(formattedTime);
        int color = Color.parseColor(colorCode);
        holder.iv_timerlist_color.setColorFilter(color);
        holder.iv_timer_play.setColorFilter(color);
        // 오늘 날짜 가져오기 (yyyy-MM-dd 형식)
        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = today.format(new Date());
        // 만약 현재 아이템의 날짜가 선택된 날짜(selectedDate)와 다르다면 iv_timer_play를 숨깁니다.
        if (!currentDate.equals(selectedDate)) {
            holder.iv_timer_play.setVisibility(View.GONE);
            holder.iv_timerlist_color.setVisibility(View.VISIBLE);
        } else {
            holder.iv_timer_play.setVisibility(View.VISIBLE);
            holder.iv_timerlist_color.setVisibility(View.GONE);
            holder.iv_timer_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Time_measurement_activity.class);
                    String pid = timeritem.getPid();
                    String measuretime = timeritem.getTotalSeconds();
                    System.out.println(pid + upid + measuretime);
                    System.out.println(measuretime);
                    intent.putExtra("pid" , pid);
                    intent.putExtra("upid" , upid);
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("measuretime", measuretime);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void setSelectedDate(String selectedDate){
        this.selectedDate = selectedDate;
        notifyDataSetChanged(); // 데이터 변경을 알립니다
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_timer_item;
        private TextView tv_timer_name, tv_timer_measurementtime;
        private ImageView iv_timerlist_color, iv_timer_play;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_timer_measurementtime = itemView.findViewById(R.id.tv_timer_measurementtime);
            ll_timer_item = itemView.findViewById(R.id.ll_timer_item);
            tv_timer_name = itemView.findViewById(R.id.tv_timer_name);
            iv_timerlist_color = itemView.findViewById(R.id.iv_timerlist_color);
            iv_timer_play = itemView.findViewById(R.id.iv_timer_play);
        }
    }
}
