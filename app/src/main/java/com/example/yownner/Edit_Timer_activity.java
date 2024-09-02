package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Edit_Timer_activity extends AppCompatActivity {

    private RecyclerView rv_timer_edit_list;

    private Button btn_showtimer_list,btn_hidentimer_list;
    private GetDataService service;
    private TimerEditListAdapter adapter;
    private TimerHideListAdapter hideadapter;
    private ImageView back_edittimer_to_timer;
    private String upid;

    List<Item_timer> timersList;
    List<Item_timer> hidetimerList;

    private boolean ishide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);
        Intent intent = getIntent();
        upid = intent.getStringExtra("upid");
        getMemoService();
        back_edittimer_to_timer = findViewById(R.id.back_edittimer_to_timer);
        back_edittimer_to_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_showtimer_list = findViewById(R.id.btn_showtimer_list);
        btn_showtimer_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishide = false;
                clickButton();
                getMemoService();
            }
        });
        btn_hidentimer_list = findViewById(R.id.btn_hidentimer_list);
        btn_hidentimer_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishide = true;
                clickButton();
                getMemoService();
            }
        });
        clickButton();
    }
    private void clickButton() {
        if (!ishide) {
            btn_showtimer_list.setBackgroundResource(R.drawable.radius2_click);
            btn_hidentimer_list.setBackgroundResource(R.drawable.radius2);
        } else {
            btn_hidentimer_list.setBackgroundResource(R.drawable.radius2_click);
            btn_showtimer_list.setBackgroundResource(R.drawable.radius2);
        }
    }

    private void getMemoService(){
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        // 날짜를 년, 월, 일로 분리
        retrofit2.Call<List<Item_timer>> call = service.getItemAllTimer(upid);
        //통신완료후 이벤트 처리를 위한 콜백 리스너 등록
        call.enqueue(new retrofit2.Callback<List<Item_timer>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Item_timer>> call, retrofit2.Response<List<Item_timer>> response) {
                generateDataList(response.body());
            }
            @Override
            public void onFailure(retrofit2.Call<List<Item_timer>> call, Throwable t) {
                Toast.makeText(Edit_Timer_activity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
            // 정상으로 통신 성공시
        });
    }

    public void generateDataList(List<Item_timer> timerList) {
        rv_timer_edit_list = findViewById(R.id.rv_timer_edit_list);

        if (timerList == null) {
            timerList = new ArrayList<>(); // timerList가 null인 경우 빈 리스트로 초기화
        }

        // 나머지 코드는 그대로 유지
        timersList = timerList.stream()
                .filter(item -> item.getStatus() == 1)
                .collect(Collectors.toList());

        hidetimerList = timerList.stream()
                .filter(item -> item.getStatus() == 2)
                .collect(Collectors.toList());

        System.out.println(timerList.size());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_timer_edit_list.setLayoutManager(layoutManager);
        if (ishide) {
            hideadapter = new TimerHideListAdapter(getBaseContext(), hidetimerList);
            rv_timer_edit_list.setAdapter(hideadapter);
        } else {
            adapter = new TimerEditListAdapter(getBaseContext(), timersList);
            rv_timer_edit_list.setAdapter(adapter);
        }
    }
}