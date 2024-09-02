package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Edit_Record_activity extends AppCompatActivity {
    private String upid;
    private String selectedDate;
    private ImageView back_record_to_timer;
    private RecyclerView rv_time_record_list;
    private TextView tv_date_recordtime, tv_total_recordtime;
    private RecordTimeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        back_record_to_timer = findViewById(R.id.back_record_to_timer);
        tv_date_recordtime = findViewById(R.id.tv_date_recordtime);
        tv_total_recordtime = findViewById(R.id.tv_total_recordtime);
        swipeRefreshLayout = findViewById(R.id.swiperefreshlayout);
        Intent intent = getIntent();
        upid = intent.getStringExtra("upid");
        selectedDate = intent.getStringExtra("selecteddate");
        tv_date_recordtime.setText(selectedDate);
        get_Date();
        back_record_to_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_Date();
                /* 업데이트가 끝났음을 알림 */
                swipeRefreshLayout.setRefreshing(false);
            }
        });



    }

    private void get_Date() {
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        // PHP 스크립트의 URL을 정의합니다.
        String url = "http://49.247.32.169/YoWnNer/get_record_timelist.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("date", selectedDate) //작성할때 선택되있던 날짜
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

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 서버에서의 응답을 처리합니다 (필요한 경우)
                String responseBody = response.body().string();
                Log.d("respons", responseBody);

                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        List<Item_time> timeList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String pid = jsonObject.getString("pid");
                            String starttime = jsonObject.getString("starttime");
                            String stoptime = jsonObject.getString("stoptime");
                            String totalSeconds = jsonObject.getString("totalSeconds");
                            String color = jsonObject.getString("color");
                            String colorhex = jsonObject.getString("colorhex");
                            String name = jsonObject.getString("name");
                            String date = jsonObject.getString("date");
                            // 여기에서 필요한 작업 수행
                            // Item_timer 객체 생성
                            Item_time Item = new Item_time( pid,totalSeconds,starttime, stoptime, name, color,colorhex, date);
                            // timerList에 추가
                            timeList.add(Item);
//                            getTimeService(pid);
                        }
                        generateDataList(timeList);
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

    public void generateDataList(final List<Item_time> timeList) {
        rv_time_record_list = findViewById(R.id.rv_time_record_list);

        int totaltime = 0;
        for (Item_time timeItem : timeList) {
            String stime = timeItem.getTotalSeconds();
            if (stime != null) { // null 체크
                int time = Integer.parseInt(stime);
                totaltime += time;
            }
        }
        final int hours = totaltime / 3600;
        final int minutes = (totaltime % 3600) / 60;
        final int seconds = totaltime % 60;
        final String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_total_recordtime.setText(formattedTime);

                adapter = new RecordTimeAdapter(Edit_Record_activity.this, timeList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Edit_Record_activity.this);
                rv_time_record_list.setLayoutManager(layoutManager);
                rv_time_record_list.setAdapter(adapter);
            }
        });
    }


}