package com.example.yownner;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class McalendarAdapter extends RecyclerView.Adapter<McalendarAdapter.CalendarViewHolder>{

    private Calendar calendar = Calendar.getInstance();
    private ItemTouchHelper itemTouchHelper;
    private boolean isDay, isWeek;

    private List<Item_timeDate> timeDate = new ArrayList<>();
    TextView tv_mannager_time;


    public McalendarAdapter(boolean isDay, boolean isWeek, List<Item_timeDate> timeDate, TextView tv_mannager_time) {
        this.isDay = isDay;
        this.isWeek = isWeek;
        this.timeDate = timeDate;
        this.tv_mannager_time = tv_mannager_time;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃을 인플레이트하여 뷰홀더를 생성
        View view;
        if(!isWeek && !isDay) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar2, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
        }

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        // 캘린더를 현재 월로 설정합니다.
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 해당 월의 첫 날로 설정
        calendar.add(Calendar.MONTH, position); // 포지션에 따라 해당 월로 이동
        TextView titleText = holder.view.findViewById(R.id.title);
        // MdayAdapter에 대한 OnItemClickListener 설정
        if(!isWeek && !isDay){
            calendar.add(Calendar.YEAR, position); // 포지션에 따라 해당 월로 이동
            titleText.setText(calendar.get(Calendar.YEAR) + "년 ");
            List<Date> dayList = new ArrayList<>(12); // 각 월에 해당하는 날짜만 담을 리스트
            int tempMonth = calendar.get(Calendar.MONTH) + 1;

            // 각 월에 대한 날짜를 리스트에 추가
            for (int month = 0; month < 12; month++) {
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                // 각 월의 첫날을 리스트에 추가
                dayList.add(calendar.getTime());
            }
            System.out.println(dayList);
            RecyclerView listLayout = holder.view.findViewById(R.id.month_recycler);
            listLayout.setLayoutManager(new GridLayoutManager(holder.view.getContext(), 4));
            MonthAdapter monthAdapter = new MonthAdapter(dayList , tempMonth , timeDate, tv_mannager_time);
            listLayout.setAdapter(monthAdapter);
            // 스와이프 동작을 처리하는 ItemTouchHelper 설정
            setUpItemTouchHelper(listLayout, position);
        }else{
            titleText.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");
            // 현재 날짜 표시
            int tempMonth = calendar.get(Calendar.MONTH) + 1;
            // 날짜 가져오기
            List<Date> dayList = new ArrayList<>(6 * 7);
            for (int i = 0; i < 6; i++) { // 주
                for (int k = 0; k < 7; k++) { // 일
                    // 현재 월의 일만 표시
                    // 요일 표시
                    calendar.add(Calendar.DAY_OF_MONTH, (1 - calendar.get(Calendar.DAY_OF_WEEK)) + k);
                    dayList.add(calendar.getTime()); // 현재 인덱스에 날짜 저장
                }
                // 다음 주로 이동
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
            }
            // 캘린더를 현재 월의 시작으로 재설정
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            RecyclerView listLayout = holder.view.findViewById(R.id.month_recycler);
            listLayout.setLayoutManager(new GridLayoutManager(holder.view.getContext(), 7));
            // DayAdapter를 생성하고 현재 날짜를 전달합니다
            MdayAdapter dayAdapter = new MdayAdapter(dayList, tempMonth, new Date(System.currentTimeMillis()), isDay , isWeek, timeDate,tv_mannager_time);
            listLayout.setAdapter(dayAdapter);
            // 스와이프 동작을 처리하는 ItemTouchHelper 설정
            setUpItemTouchHelper(listLayout, position);
        }

    }
    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }



    public interface DateItemClickListener {
        void onDateItemClick(String selectedDate);
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        View view;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }
    }

    private void setUpItemTouchHelper(RecyclerView recyclerView, int position) {
        // 스와이프 동작을 처리하는 ItemTouchHelper 설정
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 스와이프 동작 처리 (예: 이전 달로 이동)
                if (direction == ItemTouchHelper.LEFT) {
                    // 이전 달로 이동
                    calendar.add(Calendar.MONTH, -1);

                    // 어댑터에 데이터 변경을 알림
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                // 필요한 경우 여기에 사용자 정의 그림 처리 추가 (예: 배경색 또는 아이콘)
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
