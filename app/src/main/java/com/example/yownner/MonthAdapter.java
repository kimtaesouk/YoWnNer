package com.example.yownner;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.Fragment.fragmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.DayView> {
    private int tempMonth;
    private List<Date> dayList;

    private static final int ROW = 3;
    private int selectedPosition = RecyclerView.NO_POSITION; // 이전에 선택한 아이템의 위치
    private Date today; // 현재 날짜
    private String pid;
    private boolean isDay, isWeek;
    private List<Integer> selectedPositions = new ArrayList<>(); // 선택한 아이템의 위치 목록
    private List<Item_timeDate> timeDate = new ArrayList<>();
    TextView tv_mannager_time;

    public void setSelectedPositions(List<Integer> positions) {
        selectedPositions = positions;
    }

    public MonthAdapter(List<Date> dayList, int tempMonth, List<Item_timeDate> timeDate, TextView tv_mannager_time) {
        this.dayList = dayList;
        this.tempMonth = tempMonth;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, parent, false);
        return new DayView(view);
    }

    @Override
    public void onBindViewHolder(DayView holder, int position) {
        Date date = dayList.get(position);
        holder.dayText.setText(date.getMonth() + 1 + "월");
        String formattedDate = formatDate(date); // date를 문자열로 변환하여 비교
        // 필터링된 목록에서 totalSeconds를 합산
        int totalSeconds = calculateTotalSecondsForDate(formattedDate);

        if (totalSeconds > 0) {
            // 시간을 시:분 형식으로 변환하여 표시
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            String formattedTime = String.format("%02d:%02d", hours, minutes);
            holder.tv_time_day.setText(formattedTime);
        } else {
            // 해당 날짜에 측정된 시간이 없는 경우 빈 문자열로 설정
            holder.tv_time_day.setText("");
        }

        // 클릭 이벤트 처리
        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Date selectedDate = dayList.get(adapterPosition);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedDate); // Set calendar to the selected date

                    // 이번 달의 시작 날짜 가져오기
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    String startOfMonth = getCurrentDate(calendar);

                    // 이번 달의 마지막 날짜 가져오기
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String endOfMonth = getCurrentDate(calendar);

                    List<String> monthDates = new ArrayList<>();
                    calendar.setTime(selectedDate);
                    calendar.set(Calendar.DAY_OF_MONTH, 1); // 다시 월의 첫날로 이동
                    while (getCurrentDate(calendar).compareTo(endOfMonth) <= 0) {
                        monthDates.add(getCurrentDate(calendar));
                        calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날짜로 이동
                    }
                    System.out.println("123 : " + monthDates);

                    String formattedDate = formatDate(selectedDate);
                    System.out.println(formattedDate);
                    // 여기서 isDay 및 isWeek에 따라 처리

                    // 아이템을 클릭한 경우 해당 아이템의 테두리를 설정
                    selectedPositions.clear();
                    selectedPositions.add(adapterPosition);
                    fragmanager fragment = new fragmanager();
                    fragment.Set_time(timeDate,monthDates,isDay,isWeek, tv_mannager_time);
                    notifyDataSetChanged(); // 변경된 위치의 아이템에 대한 업데이트를 알림
                }
            }
        });



        // 아이템이 선택된 경우 테두리 설정
        if (selectedPositions.contains(position)) {
            holder.item_day_layout.setBackgroundResource(R.drawable.blue_border);
        } else {
            holder.item_day_layout.setBackground(null);
        }
    }
    private String getCurrentDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    private int calculateTotalSecondsForDate(String formattedDate) {
        // 해당 월에 해당하는 timeDate 목록 필터링
        List<Item_timeDate> filteredList = new ArrayList<>();
        for (Item_timeDate item : timeDate) {
            if (formattedDate.substring(0, 7).equals(item.getDate().substring(0, 7))) {
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

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return ROW * 4;
    }
}
