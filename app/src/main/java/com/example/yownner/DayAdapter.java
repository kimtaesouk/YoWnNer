package com.example.yownner;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.Fragment.fraghome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayView> {
    private int tempMonth;
    private List<Date> dayList;
    private List<String> memoDate = new ArrayList<>();
    private List<Item_timeDate> timeDate = new ArrayList<>();
    private static final int ROW = 6;
    private int selectedPosition = RecyclerView.NO_POSITION; // 이전에 선택한 아이템의 위치
    private Date today; // 현재 날짜
    private String pid;


    public DayAdapter(List<Date> dayList, List<String> memoDate, List<Item_timeDate> timeDate, int tempMonth, String pid) {
        this.memoDate = memoDate;
        this.timeDate = timeDate;
        this.dayList = dayList;
        this.tempMonth = tempMonth; // tempMonth 초기화
        this.today = new Date(System.currentTimeMillis()); // 현재 날짜 가져오기
        this.pid = pid; // tempMonth 초기화
    }


    public class DayView extends RecyclerView.ViewHolder {
        View layout;
        TextView dayText, tv_time_day;
        ImageView iv_isWrite, iv_istime;
        LinearLayout item_day_layout;


        public DayView(View itemView) {
            super(itemView);
            layout = itemView;
            dayText = itemView.findViewById(R.id.item_day_text);
            iv_isWrite = itemView.findViewById(R.id.iv_isWrite);
            iv_istime = itemView.findViewById(R.id.iv_istime);
            item_day_layout = itemView.findViewById(R.id.item_day_layout);
            tv_time_day = itemView.findViewById(R.id.tv_time_day);
        }
    }

    @Override
    public DayView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        return new DayView(view);
    }

    @Override
    public void onBindViewHolder(DayView holder, int position) {
        Date date = dayList.get(position);
        boolean isCurrentMonth = isCurrentMonth(date);
        holder.dayText.setText(String.valueOf(date.getDate()));
        // memoDate에 있는 날짜에 대한 처리
        String formattedDate = formatDate(date); // date를 문자열로 변환하여 비교
        // timeDate 목록을 순회하면서 현재 날짜와 일치하는지 확인
        boolean hasTime = false;
        for (Item_timeDate item : timeDate) {
            if (formattedDate.equals(item.getDate())) {
                String a = item.getDate();
                System.out.println(a);
                hasTime = true;
                int totalSeconds = item.getSeconds();
                System.out.println("totalSeconds : " + totalSeconds);
                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;
                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                // 해당 날짜에 대한 텍스트로 총 측정 시간을 표시
                holder.tv_time_day.setText(String.valueOf(formattedTime));
                break;
            }
        }

        // timeDate 목록에 현재 날짜가 없는 경우에만 UI 요소를 숨깁니다.
        if (!hasTime) {
            holder.iv_istime.setVisibility(View.GONE);
            holder.tv_time_day.setVisibility(View.GONE);
        }

        if (!memoDate.contains(formattedDate)) {
            holder.iv_isWrite.setVisibility(View.GONE); // iv_isWrite를 보이도록 설정
        }
        if (isSameDay(date, today)) {
            // 현재 날짜인 경우 글씨를 하늘색으로 설정
            holder.dayText.setBackgroundResource(R.drawable.baseline_brightness_2_24);
        } else {
            if (!isCurrentMonth) {
                holder.dayText.setAlpha(0.4f);
            } else {
                holder.dayText.setAlpha(1.0f);
            }

            if ((position + 1) % 7 == 0) {
                holder.dayText.setTextColor(ContextCompat.getColor(holder.layout.getContext(), R.color.blue));
            } else if (position % 7 == 0) {
                holder.dayText.setTextColor(ContextCompat.getColor(holder.layout.getContext(), R.color.red));
            } else {
                holder.dayText.setTextColor(ContextCompat.getColor(holder.layout.getContext(), R.color.black));
            }
        }
    }



    // Date를 yyyy-MM-dd 형식의 문자열로 변환하는 메서드
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date1).equals(sdf.format(date2));
    }


    @Override
    public int getItemCount() {
        return ROW * 7;
    }

    private boolean isCurrentMonth(Date date) {
        int month = date.getMonth() + 1;
        return tempMonth == month;
    }
}



