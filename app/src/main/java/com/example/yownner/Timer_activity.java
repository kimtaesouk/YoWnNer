package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Timer_activity extends AppCompatActivity {

    private LinearLayout lilay_date_timer;
    private TextView tv_date_timer, tv_total_time;
    private ImageView back_timer_to_main, iv_add_timer, iv_subtract_day, iv_add_day;
    private Timer_Dialog Dialog_Listener;
    private Timer_Add_Dialog timer_add_dialog;
    private RecyclerView rv_timer_list, rv_timetable_time, rv_timetable;
    private String timername, upid;
    private int color ;
    private String colorhex ;
    private String tpid;
    private GetDataService service;
    private TimerListAdapter adapter;
    private int totaltime = 0;
    String selectedDate;
    private int selectedColor = 0; // 클래스 변수로 색상을 저장할 변수를 선언합니다.
    private SwipeRefreshLayout timer_swiperefreshlayout;
    private TimeAdapter timeAdapter;
    private List<Item_timeTable> timeList;
    List<Item_table> startAndEndPositionsList;

    private float startY; // RecyclerView 스크롤 동기화를 위한 시작 Y 좌표

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Intent intent = getIntent();
        upid = intent.getStringExtra("pid");
        selectedDate = intent.getStringExtra("selectedDate");
        tv_date_timer = findViewById(R.id.tv_date_timer);
        lilay_date_timer = findViewById(R.id.lilay_date_timer);
        back_timer_to_main = findViewById(R.id.back_timer_to_main);
        iv_add_timer = findViewById(R.id.iv_add_timer);
        iv_subtract_day = findViewById(R.id.iv_subtract_day);
        iv_add_day = findViewById(R.id.iv_add_day);
        rv_timer_list = findViewById(R.id.rv_timer_list);
        rv_timetable_time = findViewById(R.id.rv_timetable_time);
        rv_timetable = findViewById(R.id.rv_timetable);
        tv_total_time = findViewById(R.id.tv_total_time);
        timer_swiperefreshlayout = findViewById(R.id.timer_swiperefreshlayout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_timer_list.setLayoutManager(layoutManager);
        timeList = generateTimeList(); // 15분 간격의 시간 목록 생성
        timeAdapter = new TimeAdapter(timeList);
        rv_timetable_time.setLayoutManager(new LinearLayoutManager(this));
        rv_timetable_time.setAdapter(timeAdapter);
        System.out.println(totaltime);
        back_timer_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_subtract_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddDayClick(false);
            }
        });
        iv_add_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddDayClick(true);
            }
        });

        tv_date_timer.setText(selectedDate);
        lilay_date_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        iv_add_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceDialog();
            }
        });
        timer_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTimerService();
                getTimertable_time();
                /* 업데이트가 끝났음을 알림 */
                timer_swiperefreshlayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    private void showChoiceDialog() {
        Dialog_Listener = new Timer_Dialog(this , addTimer,editTimer,editRecord);
        Dialog_Listener.show();
    }

    private void handleAddDayClick(boolean isDays) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = dateFormat.parse(selectedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return; // 날짜 파싱에 실패하면 아무것도 하지 않음
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (isDays) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        Date newDate = calendar.getTime();
        selectedDate = dateFormat.format(newDate);
        tv_date_timer.setText(selectedDate);

        getTimerService();
        getTimertable_time();
    }

    private View.OnClickListener addTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Dialog_Listener.dismiss();
            timer_add_dialog = new Timer_Add_Dialog(Timer_activity.this, Write , Cancel);
            timer_add_dialog.show();
        }
    };

    private View.OnClickListener editTimer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Timer_activity.this, Edit_Timer_activity.class);
            intent.putExtra("upid" , upid);
            startActivity(intent);
            Dialog_Listener.dismiss();
        }
    };
    private View.OnClickListener editRecord = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Timer_activity.this, Edit_Record_activity.class);
            intent.putExtra("upid" , upid);
            intent.putExtra("selecteddate" , selectedDate);
            startActivity(intent);
            Dialog_Listener.dismiss();
        }
    };
    private View.OnClickListener Cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_add_dialog.dismiss();
        }
    };

    @Override
    protected void onResume() {
        getTimerService();
        getTimertable_time();
        super.onResume();
    }

    private List<Item_timeTable> generateTimeList() {
        List<Item_timeTable> times = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i <= 23 ; i++) { // 60분 간격으로 24:00까지 표시
            times.add(new Item_timeTable(sdf.format(calendar.getTime())));
            calendar.add(Calendar.MINUTE, 60);
        }

        return times;
    }

    private void getTimerService(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://49.247.32.169/YoWnNer/get_timerList.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("upid", upid) //사용자 고유번호
                .add("date", selectedDate) //사용자 고유번호
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
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 서버에서의 응답을 처리합니다 (필요한 경우)
                String responseBody = response.body().string();
                Log.d("respons", responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        List<Item_timer> timerList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String pid = jsonObject.getString("pid");
                            String name = jsonObject.getString("name");
                            String color = jsonObject.getString("color");
                            String colorhex = jsonObject.getString("colorhex");
                            String date = jsonObject.getString("date");
                            int status = Integer.parseInt(jsonObject.getString("status"));
                            String totalSeconds = jsonObject.getString("totalSeconds");
                            // 여기에서 필요한 작업 수행
                            // Item_timer 객체 생성
                            Item_timer timerItem = new Item_timer( name,pid,upid, color, colorhex, date, status,totalSeconds);
                            // timerList에 추가
                            timerList.add(timerItem);
                        }
                        generateDataList(timerList);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    private void getTimertable_time(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://49.247.32.169/YoWnNer/get_timetable_Data.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("upid", upid) //사용자 고유번호
                .add("date", selectedDate) //사용자 고유번호
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
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 서버에서의 응답을 처리합니다 (필요한 경우)
                String responseBody = response.body().string();
                Log.d("respons", responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        List<Item_table> time = new ArrayList<>();
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String pid = jsonObject.getString("pid");
                            String starttime = jsonObject.getString("starttime");
                            String Color = jsonObject.getString("color");
                            String stoptime = jsonObject.getString("stoptime");
                            // 여기에서 필요한 작업 수행
                            // Item_timer 객체 생성
                            // timerList에 추가
                            int color = Integer.parseInt(Color);
                            if(starttime == "null" && stoptime == "null"){
                                starttime = "00:00:00";
                                stoptime = "00:00:00";
                                Item_table item_table = new Item_table(starttime , stoptime , color);
                                time.add(item_table);
                            }else {
                                // 공백을 기준으로 문자열을 분할
                                String[] startparts = starttime.split(" ");
                                String start = startparts[1]; // 두 번째 부분 (시간 부분)을 가져옴
                                String[] stopparts = stoptime.split(" ");
                                String stop = stopparts[1]; // 두 번째 부분 (시간 부분)을 가져옴
                                    // StringTokenizer에서 시간 및 분 부분 가져오기
                                System.out.println(start + stop);
                                Item_table item_table = new Item_table(start , stop , color);
                                time.add(item_table);
                            }
                            timeTableDataList(time);
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    private void timeTableDataList(final List<Item_table> item_tables) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    rv_timetable = findViewById(R.id.rv_timetable);
                    TimetableAdapter gridAdapter = new TimetableAdapter(item_tables);
                    rv_timetable.setAdapter(gridAdapter);
                    GridLayoutManager layoutManager = new GridLayoutManager(Timer_activity.this, 24, GridLayoutManager.VERTICAL, false);
                    layoutManager.setSpanCount(60); // 48개의 행(세로 칸)
                    rv_timetable.setLayoutManager(layoutManager);
            }
        });
    }
    private void generateDataList(final List<Item_timer> timerList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv_timer_list = findViewById(R.id.rv_timer_list);
                List<Item_timer> filteredList = new ArrayList<>();
                // 오늘 날짜 가져오기 (yyyy-MM-dd 형식)
                SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = today.format(new Date());
                int totaltime = 0; // 총 시간을 0으로 초기화

                // 아이템의 날짜와 오늘 날짜를 비교하여 필터링
                for (Item_timer timerItem : timerList) {
                    String itemDate = timerItem.getDate();
                    System.out.println(itemDate);
                    String measurementtime = timerItem.getTotalSeconds();
                    int time = 0; // 기본값 0 설정
                    if (measurementtime != null && !measurementtime.equals("null") && !measurementtime.isEmpty()) {
                        time = Integer.parseInt(measurementtime);
                    }
                    totaltime += time;
                    if (itemDate.compareTo(selectedDate) <= 0) {
                        filteredList.add(timerItem);
                    }
                }
                int hours = totaltime / 3600;
                int minutes = (totaltime % 3600) / 60;
                int seconds = totaltime % 60;
                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                tv_total_time.setText(formattedTime);
                adapter = new TimerListAdapter(getBaseContext(), filteredList, upid,selectedDate);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Timer_activity.this);
                rv_timer_list.setLayoutManager(layoutManager);
                rv_timer_list.setAdapter(adapter);
                adapter.setSelectedDate(selectedDate);
            }
        });
    }

    private View.OnClickListener Write = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timername = timer_add_dialog.getTextContent();
            color = timer_add_dialog.getColor();
            colorhex = timer_add_dialog.getColorHex();
            if (!timername.isEmpty() && timername != null &&colorhex != null && !colorhex.isEmpty()) {
                // OkHttpClient 인스턴스 생성
                OkHttpClient client = new OkHttpClient();
                // PHP 스크립트의 URL을 정의합니다.
                String url = "http://49.247.32.169/YoWnNer/timer_add.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                RequestBody requestBody = new FormBody.Builder()
                        .add("name", timername) // 작성한 내용
                        .add("date", selectedDate) //작성할때 선택되있던 날짜
                        .add("color", String.valueOf(color)) //작성할때 선택되있던 날짜
                        .add("colorhex", colorhex) //작성할때 선택되있던 날짜
                        .add("upid", upid) //사용자 고유번호
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

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 서버에서의 응답을 처리합니다 (필요한 경우)
                        String responseBody = response.body().string();
                        // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
                    }
                });
                // 다이얼로그를 닫습니다.
                timer_add_dialog.dismiss();
                getTimerService();
            } else {
                timer_add_dialog.dismiss();
            }

        }
    };
    private void showDatePickerDialog() {
        // 선택된 날짜를 초기값으로 설정
        String[] dateParts = selectedDate.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // 월은 0부터 시작하므로 -1
        int day = Integer.parseInt(dateParts[2]);
        // DatePickerDialog를 생성하고 표시
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // 사용자가 선택한 날짜를 문자열로 가공하여 스피너 아이템으로 추가하고 갱신
                selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay); // YYYY-MM-DD 형식
                tv_date_timer.setText(selectedDate);
                System.out.println(selectedDate);
                adapter.setSelectedDate(selectedDate);
                getTimerService();
            }
        }, year, month, day);
        datePickerDialog.show(); // 다이얼로그를 생성하고 표시
    }
}