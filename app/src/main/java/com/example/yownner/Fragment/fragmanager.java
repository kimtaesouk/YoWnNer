package com.example.yownner.Fragment;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.GetDataService;
import com.example.yownner.Item_time;
import com.example.yownner.Item_timeDate;
import com.example.yownner.McalendarAdapter;
import com.example.yownner.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragmanager extends Fragment {
    private View view;
    private String TAG = "프래그먼트";
    private boolean isDay ;
    private boolean isWeek ;
    String pid ;
    Button btn_fragmanager_day, btn_fragmanager_week,btn_fragmanager_month;
    String today, toweek, tomonth;
    private  GetDataService service;
    List<Item_time> timeList = new ArrayList<>();
    List<Item_timeDate> timeDate = new ArrayList<>();
    List<String> toDates = new ArrayList<>();
    TextView tv_mannager_time;
    private RecyclerView rv_frag_manager_cal;
    LinearLayoutManager layoutManager;
    int Seconds;
    private BarChart manager_chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_manager, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            pid = arguments.getString("pid");
        }
        isDay = true;
        isWeek = false;
        timeDate.clear();
        btn_fragmanager_day = view.findViewById(R.id.btn_fragmanager_day);
        btn_fragmanager_week = view.findViewById(R.id.btn_fragmanager_week);
        btn_fragmanager_month = view.findViewById(R.id.btn_fragmanager_month);
        tv_mannager_time = view.findViewById(R.id.tv_mannager_time);
        Calendar calendar = Calendar.getInstance();
        today = getCurrentDate(calendar);
        toDates.add(today);
        btn_fragmanager_day.setBackgroundResource(R.drawable.radius2);
        btn_fragmanager_week.setBackgroundResource(R.drawable.radius2);
        btn_fragmanager_month.setBackgroundResource(R.drawable.radius2);


        manager_chart = (BarChart) view.findViewById(R.id.manager_chart);
        btn_fragmanager_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                today = getCurrentDate(calendar);
                isDay = true;
                isWeek = false;
                rv_frag_manager_cal.setAdapter(new McalendarAdapter(isDay , isWeek, timeDate, tv_mannager_time));
                // timeList 중에서 현재 날짜와 일치하는 정보를 따로 리스트화
                // todayList를 활용하여 필요한 작업 수행
                // 예: 해당 날짜의 정보를 출력하거나 처리하는 등의 작업 수행
                toDates.add(today);
                btn_fragmanager_day.setBackgroundResource(R.drawable.radius2_click);
                btn_fragmanager_week.setBackgroundResource(R.drawable.radius2);
                btn_fragmanager_month.setBackgroundResource(R.drawable.radius2);
                int totalSecondsForWeek = filterTimeListByDate(timeDate, toDates);
                System.out.println("오늘 총 초: " + totalSecondsForWeek);
                setupBarChart(timeDate);
            }
        });
        // btn_fragmanager_week의 onClick 리스너 업데이트
        btn_fragmanager_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWeek = true;
                isDay = false;
                rv_frag_manager_cal.setAdapter(new McalendarAdapter(isDay , isWeek, timeDate, tv_mannager_time));
                Calendar calendar = Calendar.getInstance();
                int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                btn_fragmanager_week.setBackgroundResource(R.drawable.radius2_click);
                btn_fragmanager_day.setBackgroundResource(R.drawable.radius2);
                btn_fragmanager_month.setBackgroundResource(R.drawable.radius2);
                // 현재 주의 시작 날짜 가져오기
                calendar.set(Calendar.WEEK_OF_YEAR, currentWeek);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // 주의 첫날로 설정 (일반적으로 일요일)
                String startOfWeek = getCurrentDate(calendar);
                // 현재 주의 모든 날짜 수집
                List<String> weekDates = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    weekDates.add(getCurrentDate(calendar));
                    calendar.add(Calendar.DAY_OF_WEEK, 1); // 다음 날짜로 이동
                }
                // 주의 총 초 계산 및 TextView 업데이트
                int totalSecondsForWeek = filterTimeListByDate(timeDate, weekDates);
                setupBarChart(timeDate);
                System.out.println("이번 주의 총 초: " + totalSecondsForWeek);
            }
        });

        btn_fragmanager_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWeek = false;
                isDay = false;
                rv_frag_manager_cal.setAdapter(new McalendarAdapter(isDay , isWeek, timeDate, tv_mannager_time));
                btn_fragmanager_month.setBackgroundResource(R.drawable.radius2_click);
                btn_fragmanager_day.setBackgroundResource(R.drawable.radius2);
                btn_fragmanager_week.setBackgroundResource(R.drawable.radius2);
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더합니다.

                // 이번 달의 시작 날짜 가져오기
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                String startOfMonth = getCurrentDate(calendar);

                // 이번 달의 마지막 날짜 가져오기
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String endOfMonth = getCurrentDate(calendar);

                // 이번 달의 모든 날짜 수집
                List<String> monthDates = new ArrayList<>();
                calendar.set(Calendar.DAY_OF_MONTH, 1); // 다시 월의 첫날로 이동
                while (getCurrentDate(calendar).compareTo(endOfMonth) <= 0) {
                    monthDates.add(getCurrentDate(calendar));
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날짜로 이동
                }
                System.out.println(monthDates);
                monthDates.add(endOfMonth); // 마지막 날짜 추가
                // 이번 달의 총 초 계산 및 TextView 업데이트
                int totalSecondsForMonth = filterTimeListByDate(timeDate, monthDates);
                setupBarChart(timeDate);
                System.out.println("이번 달의 총 초: " + totalSecondsForMonth);
            }
        });
        return view;
    }
    public void Set_time(List<Item_timeDate> timeDate, List<String> selected_Date, boolean isDay , boolean isWeek, TextView tv_mannager_time){
        this.timeDate = timeDate;
        this.tv_mannager_time = tv_mannager_time;
        if(isDay){
            int totalSecondsForWeek = filterTimeListByDate(timeDate, selected_Date);
            System.out.println("set_time : " + totalSecondsForWeek);
        } else if (isWeek) {
            int totalSecondsForWeek = filterTimeListByDate(timeDate, selected_Date);
            System.out.println("set_time : " + totalSecondsForWeek);
        }else if (!isDay && !isWeek){
            int totalSecondsForWeek = filterTimeListByDate(timeDate, selected_Date);
            System.out.println("set_time : " + totalSecondsForWeek);
        }
    }
    // BarChart 초기화 및 데이터 설정
    private void setupBarChart(List<Item_timeDate> timeDateList) {
        BarChart barChart = view.findViewById(R.id.manager_chart);
        if (isDay) {
            List<String> xValues = getCurrentWeekDates(); // 이번 주의 날짜 리스트
            Map<String, Float> dateMinutesMap = new HashMap<>(); // 날짜와 해당 날짜의 totalMinutes를 저장할 Map
            for (Item_timeDate item : timeDateList) {
                String currentDate = item.getDate();

                // 이미 맵에 해당 날짜가 있다면 더하기
                if (dateMinutesMap.containsKey(currentDate)) {
                    float currentMinutes = dateMinutesMap.get(currentDate);
                    dateMinutesMap.put(currentDate, currentMinutes + item.getSeconds() / 60.0f);
                } else {
                    // 맵에 해당 날짜가 없다면 추가
                    dateMinutesMap.put(currentDate, item.getSeconds() / 60.0f);
                }
            }

            // 맵에서 값을 꺼내어 yValues에 추가
            List<Float> yValues = new ArrayList<>();
            for (String date : xValues) {
                if (dateMinutesMap.containsKey(date)) {
                    yValues.add(dateMinutesMap.get(date));
                } else {
                    // 해당 날짜에 값이 없는 경우, 적절한 기본값을 사용하거나 0으로 설정
                    yValues.add(0.0f);
                }
            }
            BarDataSet barDataSet = new BarDataSet(generateBarEntries(yValues), "시간 (분)");
            barDataSet.setColor(Color.BLUE);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);

            xValues.clear();
            xValues = WeekDates(); // 이번 주의 날짜 리스트
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(0.5f);

            barChart.getDescription().setEnabled(false);
            barChart.setDrawValueAboveBar(false);

            barChart.invalidate();
        }else if (isWeek) {
            // TreeMap을 사용하여 key를 정렬된 순서로 관리
            Map<String, Float> weekMinutesMap = new TreeMap<>();

            // Assuming timeDateList contains the data for the desired weeks
            for (Item_timeDate item : timeDateList) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    calendar.setTime(dateFormat.parse(item.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    // 파싱 예외 처리, 필요한 경우
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);

                // 해당 주가 해당 월의 몇 번째 주인지 구하기
                int weekNumber = calendar.get(Calendar.WEEK_OF_MONTH);

                String currentWeek = year + "-" + (month + 1) + "-" + weekNumber;

                // TreeMap에 자동으로 정렬된 순서로 추가됨
                weekMinutesMap.put(currentWeek, weekMinutesMap.getOrDefault(currentWeek, 0.0f) + item.getSeconds() / 60.0f);

                System.out.println(weekMinutesMap);
            }

            // 맵에서 값을 꺼내어 xValues와 yValues에 추가
            List<String> xValues = new ArrayList<>();
            List<Float> yValues = new ArrayList<>();

            for (Map.Entry<String, Float> entry : weekMinutesMap.entrySet()) {
                String week = entry.getKey();
                float minutes = entry.getValue();

                xValues.add(week);
                yValues.add(minutes);
            }

            BarDataSet barDataSet = new BarDataSet(generateBarEntries(yValues), "시간 (분)");
            barDataSet.setColor(Color.BLUE);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // 1 주차 간격으로 설정

            barChart.getDescription().setEnabled(false);
            barChart.setDrawValueAboveBar(false);

            barChart.invalidate();
        }else if (!isDay && !isWeek) {
            Map<String, Float> monthMinutesMap = new TreeMap<>();

            for (Item_timeDate item : timeDateList) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    calendar.setTime(dateFormat.parse(item.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    // 파싱 예외 처리, 필요한 경우
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);

                String currentMonth = year + "-" + (month + 1);

                // TreeMap에 자동으로 정렬된 순서로 추가됨
                monthMinutesMap.put(currentMonth, monthMinutesMap.getOrDefault(currentMonth, 0.0f) + item.getSeconds() / 60.0f);
            }

            // 맵에서 값을 꺼내어 xValues와 yValues에 추가
            List<String> xValues = new ArrayList<>();
            List<Float> yValues = new ArrayList<>();

            for (Map.Entry<String, Float> entry : monthMinutesMap.entrySet()) {
                String month = entry.getKey();
                float minutes = entry.getValue();

                xValues.add(month);
                yValues.add(minutes);
            }

            BarDataSet barDataSet = new BarDataSet(generateBarEntries(yValues), "시간 (분)");
            barDataSet.setColor(Color.BLUE);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(12f);

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f); // 1 월 간격으로 설정

            barChart.getDescription().setEnabled(false);
            barChart.setDrawValueAboveBar(false);

            barChart.invalidate();
        }


    }
    private List<String> WeekDates() {
        List<String> weekDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // 현재 주의 시작 날짜 가져오기
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // 주의 첫날로 설정 (보통 일요일)

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월dd일", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            weekDates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_WEEK, 1); // 다음 날짜로 이동
        }

        return weekDates;
    }





    private List<String> getCurrentWeekDates() {
        List<String> weekDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // 현재 주의 시작 날짜 가져오기
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // 주의 첫날로 설정 (일반적으로 일요일)

        for (int i = 0; i < 7; i++) {
            weekDates.add(getCurrentDate(calendar));
            calendar.add(Calendar.DAY_OF_WEEK, 1); // 다음 날짜로 이동
        }

        return weekDates;
    }

    // BarEntry 생성
    private List<BarEntry> generateBarEntries(List<Float> yValues) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> weekDates = WeekDates(); // 주의 날짜 리스트 가져오기

        for (int i = 0; i < weekDates.size(); i++) {
            float yValue = (i < yValues.size()) ? yValues.get(i) : 0.0f; // 인덱스가 범위를 벗어나면 0.0f를 사용합니다.
            entries.add(new BarEntry(i, yValue));
        }
        return entries;
    }


    private void initView() {
        rv_frag_manager_cal = view.findViewById(R.id.rv_frag_manager_cal);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_frag_manager_cal.setLayoutManager(layoutManager);
        McalendarAdapter mcalendarAdapter = new McalendarAdapter(isDay , isWeek, timeDate, tv_mannager_time);
        rv_frag_manager_cal.setAdapter(mcalendarAdapter);
        // 항목의 위치 지정
        // 항목씩 스크롤 지정
        setupBarChart(timeDate);
        PagerSnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(rv_frag_manager_cal);
    }
    private String formatSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // filterTimeListByDate 메서드 내부
    private int filterTimeListByDate(List<Item_timeDate> timeList, List<String> targetDates) {
        int totalSeconds = 0;
        for (Item_timeDate item : timeList) {
            if (targetDates.contains(item.getDate())) {
                totalSeconds += item.getSeconds();
            }
        }

        String formattedTime = formatSeconds(totalSeconds);
        if (tv_mannager_time != null) {
            tv_mannager_time.setText(formattedTime);
        } else {
            Log.e(TAG, "tv_mannager_time is null");
        }

        return totalSeconds;
    }
    private String getCurrentDate2(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월dd일", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
    private String getCurrentDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onResume() {
        get_Data(pid);
        super.onResume();
    }

    private void get_Data(String pid) {
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        // PHP 스크립트의 URL을 정의합니다.
        String url = "http://49.247.32.169/YoWnNer/get_record_timelist2.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("upid", pid) //사용자 고유번호
                .build();
        // URL 및 요청 바디를 사용하여 POST 요청을 생성합니다.
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        // 요청을 비동기적으로 실행합니다.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 실패를 처리합니다 (예: 네트워크 오류)
                e.printStackTrace();
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 서버에서의 응답을 처리합니다 (필요한 경우)
                String responseBody = response.body().string();
                Log.d("respons", responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String Seconds = jsonObject.getString("totalSeconds");
                            String date = jsonObject.getString("date");
                            // 여기에서 필요한 작업 수행
                            // Item_timer 객체 생성
                            int totalSeconds = Integer.parseInt(Seconds);
                            timeDate.add(new Item_timeDate(date, totalSeconds));
//                            getTimeService(pid);
                        }
                        if (!timeDate.isEmpty()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initView();
                                }
                            });
                        }
                    } else {
                        // 처리에 실패한 경우
                        // Handle failure...
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
