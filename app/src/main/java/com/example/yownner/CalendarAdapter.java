package com.example.yownner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    private Calendar calendar = Calendar.getInstance();
    private List<String> memoDate = new ArrayList<>();
    private List<Item_timeDate> timeDate = new ArrayList<>();
    String pid;

    public CalendarAdapter(List<String> memoDate, List<Item_timeDate> timeDate, String pid) {
        this.memoDate = memoDate;
        this.timeDate = timeDate;
        this.pid = pid;
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        RecyclerView listLayout = holder.view.findViewById(R.id.month_recycler);
        for (Item_timeDate item : timeDate) {

            int a = item.getSeconds();
            System.out.println("calenderAdp" + a);
        }

        // 현재 월 가져오기
        calendar.setTime(new Date()); // 현재 날짜로 초기화
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 현재 월의 1일로 설정
        calendar.add(Calendar.MONTH, position); // 포지션에 따라 해당 월로 이동

        TextView titleText = holder.view.findViewById(R.id.title);

        // 현재 날짜 표시
        titleText.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");

        int tempMonth = calendar.get(Calendar.MONTH) + 1;

        // 날짜 가져오기
        List<Date> dayList = new ArrayList<>(6 * 7);

        for (int i = 0; i < 6; i++) { // 주
            for (int k = 0; k < 7; k++) { // 요일
                // 현재 월의 일만 표시
                // 요일 표시
                calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k);
                dayList.add(calendar.getTime()); // 현재 인덱스에 날짜 저장
            }
            // 다음 주로 이동
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
        }

        listLayout.setLayoutManager(new GridLayoutManager(holder.view.getContext(), 7));
        listLayout.setAdapter(new DayAdapter( dayList, memoDate, timeDate, tempMonth, pid));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
