package com.example.yownner;

import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.Fragment.fragmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MdayAdapter extends RecyclerView.Adapter<MdayAdapter.DayView> {
    private int tempMonth;
    private List<Date> dayList;
    private static final int ROW = 6;
    private int selectedPosition = RecyclerView.NO_POSITION; // 이전에 선택한 아이템의 위치
    private Date today; // 현재 날짜
    private String pid;
    private boolean isDay, isWeek;
    private List<Integer> selectedPositions = new ArrayList<>(); // 선택한 아이템의 위치 목록
    private List<Item_timeDate> timeDate = new ArrayList<>();

    TextView tv_mannager_time;
    public MdayAdapter(List<Date> dayList, int tempMonth, Date today, boolean isDay, boolean isWeek, List<Item_timeDate> timeDate, TextView tv_mannager_time) {
        this.dayList = dayList;
        this.tempMonth = tempMonth;
        this.today = today; // 초기에 선택된 날짜 설정
        this.isDay = isDay;
        this.isWeek = isWeek;
        this.timeDate = timeDate;
        this.tv_mannager_time = tv_mannager_time;
    }
    public class DayView extends RecyclerView.ViewHolder {
        View layout;
        TextView dayText, tv_time_day;
        LinearLayout item_day_layout;
        public DayView(View itemView) {
            super(itemView);
            layout = itemView;
            dayText = itemView.findViewById(R.id.item_day_text);
            item_day_layout = itemView.findViewById(R.id.item_day_layout);
            tv_time_day = itemView.findViewById(R.id.tv_time_day);
        }
    }
    @Override
    public DayView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mday_item, parent, false);
        return new DayView(view);
    }
    @Override
    public void onBindViewHolder(DayView holder, int position) {
        Date date = dayList.get(position);
        boolean isCurrentMonth = isCurrentMonth(date);
        holder.dayText.setText(String.valueOf(date.getDate()));

        // 현재 날짜의 총 시간을 계산
        int totalSeconds = calculateTotalSecondsForDate(date);

        if (totalSeconds > 0) {
            // 총 시간을 HH:mm:ss 형식으로 포맷
            String formattedTime = formatSeconds(totalSeconds);
            holder.tv_time_day.setText(formattedTime);
            holder.tv_time_day.setVisibility(View.VISIBLE);
        } else {
            holder.tv_time_day.setVisibility(View.GONE);
        }
// for 루프 종료 후에 시간을 설정
        // timeDate 목록에 현재 날짜가 없는 경우에만 UI 요소를 숨깁니다.
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

        if (selectedPositions.contains(position)) {
            holder.item_day_layout.setBackgroundResource(R.drawable.blue_border);
        } else {
            holder.item_day_layout.setBackground(null);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Date selectedDate = dayList.get(adapterPosition);
                    String formattedDate = formatDate(selectedDate);
                    List<String> a = new ArrayList();
                    // 여기서 isDay 및 isWeek에 따라 처리
                    if (isDay) {
                        a.add(formattedDate);
                        // isDay가 true인 경우 선택한 날만 선택
                        handleDaySelection(selectedDate);
                        fragmanager fragment = new fragmanager();
                        fragment.Set_time(timeDate,a,isDay,isWeek, tv_mannager_time);

                    }
                    if (isWeek) {
                        // isWeek가 true인 경우 선택한 날이 포함된 주를 선택
                        handleWeekSelection(selectedDate);

                    }
                }
            }
        });
    }
    // 날짜에 대한 총 시간을 계산하는 메서드
    private int calculateTotalSecondsForDate(Date date) {
        String formattedDate = formatDate(date);

        // 해당 월에 해당하는 timeDate 목록 필터링
        List<Item_timeDate> filteredList = new ArrayList<>();
        for (Item_timeDate item : timeDate) {
            if (formattedDate.equals(item.getDate())) {
                filteredList.add(item);
            }
        }

        // 필터링된 목록에서 totalSeconds를 합산
        int totalSeconds = 0;
        for (Item_timeDate item : filteredList) {
            totalSeconds += item.getSeconds();
        }

        return totalSeconds;
    }

    // 초를 HH:mm:ss 형식으로 포맷하는 메서드
    private String formatSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
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

    private void handleDaySelection(Date selectedDate) {
        selectedPositions.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateString = sdf.format(selectedDate);
        List<String> toDates = new ArrayList<>();
        toDates.add(selectedDateString);
        fragmanager fragment = new fragmanager();
        fragment.Set_time(timeDate,toDates,isDay,isWeek, tv_mannager_time);
        int selectedPosition = dayList.indexOf(selectedDate);
        Log.d("DaySelection", "Selected Date: " + selectedPosition);
        selectedPositions.add(selectedPosition);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged(); // 변경된 위치의 아이템에 대한 업데이트를 알림
            }
        });
        // 여기에 선택한 날짜에 대한 추가적인 작업을 수행할 수 있습니다.
    }

    private void handleWeekSelection(Date selectedDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

        Date startOfWeek = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endOfWeek = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startOfWeekString = sdf.format(startOfWeek);
        String endOfWeekString = sdf.format(endOfWeek);

        Log.d("WeekSelection", "Selected Week: " + startOfWeekString + " to " + endOfWeekString);

        // 주의 시작 및 끝 위치를 계산
        int startOfWeekPosition = dayList.indexOf(startOfWeek);
        int endOfWeekPosition = dayList.indexOf(endOfWeek);

        List<String> a = new ArrayList<>();

        // 리스트에 startOfWeekString부터 endOfWeekString까지의 날짜를 추가
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(startOfWeek);

        while (!tempCalendar.getTime().after(endOfWeek)) {
            a.add(sdf.format(tempCalendar.getTime()));
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        fragmanager fragment = new fragmanager();
        fragment.Set_time(timeDate, a, isDay, isWeek, tv_mannager_time);
        a.add(startOfWeekString);

        // 전체 주의 범위에 대해 테두리 설정을 위해 selectedPositions 초기화
        selectedPositions.clear();

        // 전체 주의 범위에 대해 테두리 설정
        for (int i = startOfWeekPosition; i <= endOfWeekPosition; i++) {
            selectedPositions.add(i);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged(); // 변경된 위치의 아이템에 대한 업데이트를 알림
            }
        });
    }
}
