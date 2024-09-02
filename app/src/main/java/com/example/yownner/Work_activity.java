package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Work_activity extends AppCompatActivity {
    private String pid ;

    private ImageView back_work_to_main;
    private Button btn_all_day, btn_contect_day,btn_complet_list, btn_Incomplete_list;
    private TextView tv_deadline_text, tv_check_text, tv_text_date;
    private String Day = "기한 전체";
    private String List = "전체" ;

    private String selectedDate ;

    private RecyclerView rv_work_list;
    private CustomAdapter adapter;
    private InCompletAdapter inCompletAdapter;
    private CompletAdapter comPletAdapter;
    private  GetDataService service;
    String Complet = null;
    private Item_memo memo;


    // 1페이지에 10개씩 데이터를 불러온다
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear);
            Day = String.valueOf(monthOfYear);
            selectedDate = year + "-" + monthOfYear; // monthOfYear는 0부터 시작하므로 +1 해줍니다.
            System.out.println(selectedDate);
            tv_deadline_text.setText(Day + "월");
            if (Complet != null){
                getMemoService(Complet);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        back_work_to_main = findViewById(R.id.back_work_to_main);
        btn_all_day = findViewById(R.id.btn_all_day);
        btn_contect_day = findViewById(R.id.btn_contect_day);
        btn_complet_list = findViewById(R.id.btn_complet_list);
        btn_Incomplete_list = findViewById(R.id.btn_Incomplete_list);
        tv_deadline_text = findViewById(R.id.tv_deadline_text);
        tv_deadline_text.setText(Day);
        tv_check_text = findViewById(R.id.tv_check_text);
        tv_check_text.setText(List);



        back_work_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_all_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Day = btn_all_day.getText().toString();
                tv_deadline_text.setText(Day);
                selectedDate = "All";
                btn_all_day.setBackgroundResource(R.drawable.radius2_click);
                btn_contect_day.setBackgroundResource(R.drawable.radius2);
                if(Complet != null){
                    getMemoService(Complet);
                }
            }
        });

        btn_contect_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
                btn_all_day.setBackgroundResource(R.drawable.radius2);
                btn_contect_day.setBackgroundResource(R.drawable.radius2_click);
                if (Complet != null && selectedDate != null && !selectedDate.isEmpty()) {
                    getMemoService(Complet);
                }
            }
        });
        btn_complet_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List = btn_complet_list.getText().toString();
                tv_check_text.setText(List);
                Complet = "Complet";
                if(selectedDate != null && !selectedDate.isEmpty()){
                    getMemoService(Complet);
                }
                clickButton();
            }
        });
        btn_Incomplete_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List = btn_Incomplete_list.getText().toString();
                tv_check_text.setText(List);
                Complet = "inComplet";
                if(selectedDate != null && !selectedDate.isEmpty()){
                    getMemoService(Complet);
                }
                clickButton();
            }
        });


    }
    public void setMemo(Item_memo memoItem){
        this.memo = memoItem;
        String c = memo.getMemo();
        System.out.println(c);
    }
    private void clickButton() {
        if (Complet.equals("Complet")) {
            btn_complet_list.setBackgroundResource(R.drawable.radius2_click);
            btn_Incomplete_list.setBackgroundResource(R.drawable.radius2);
        } else if (Complet.equals("inComplet")) {
            btn_Incomplete_list.setBackgroundResource(R.drawable.radius2_click);
            btn_complet_list.setBackgroundResource(R.drawable.radius2);
        } else {
            btn_Incomplete_list.setBackgroundResource(R.drawable.radius2);
            btn_complet_list.setBackgroundResource(R.drawable.radius2);
        }
    }
    private void getMemoService(String Complet){
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        if(!selectedDate.equals("All")) {
            String[] dateParts = selectedDate.split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            System.out.println(Complet);
            retrofit2.Call<List<Item_memo>> call = service.getItemList(year, month, Complet, pid);
            //통신완료후 이벤트 처리를 위한 콜백 리스너 등록
            call.enqueue(new retrofit2.Callback<List<Item_memo>>() {
                @Override
                public void onResponse(retrofit2.Call<List<Item_memo>> call, retrofit2.Response<List<Item_memo>> response) {
                    generateDataList(response.body());
                }

                @Override
                public void onFailure(retrofit2.Call<List<Item_memo>> call, Throwable t) {
                    Log.e("Retrofit", "Network request failed: " + call.request().url().toString());
                    t.printStackTrace(); // Print the stack trace
                }
                // 정상으로 통신 성공시
            });
        } else {
            System.out.println(selectedDate);
            retrofit2.Call<List<Item_memo>> call = service.getItemLists(pid, selectedDate);
            //통신완료후 이벤트 처리를 위한 콜백 리스너 등록
            call.enqueue(new retrofit2.Callback<List<Item_memo>>() {
                @Override
                public void onResponse(retrofit2.Call<List<Item_memo>> call, retrofit2.Response<List<Item_memo>> response) {
                    generateDataList(response.body());
                }

                @Override
                public void onFailure(retrofit2.Call<List<Item_memo>> call, Throwable t) {
                    Toast.makeText(Work_activity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
                // 정상으로 통신 성공시
            });
        }

    }
    public void generateDataList(List<Item_memo> memoList) {

        String month;
        String day ;
        List<Item_memo> filteredIncompletList = new ArrayList<>();
        // 'status' 값이 1인 아이템만 필터링하여 새 리스트 생성
        for (Item_memo item : memoList) {
            if (item.getStatus() == 1) {
                filteredIncompletList.add(item);
                Log.d("filteredIncompletList", "123Memo: " + item.getMemo());
            }
        }
        List<Item_memo> filteredCompletList = new ArrayList<>();
        for (Item_memo item : memoList) {
            if (item.getStatus() == 2) {
                filteredCompletList.add(item);
            }
        }
        rv_work_list = findViewById(R.id.rv_work_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_work_list.setLayoutManager(layoutManager);

        if (Complet.equals("inComplet")) {
            inCompletAdapter = new InCompletAdapter(this, filteredIncompletList);
            rv_work_list.setAdapter(inCompletAdapter);
        } else if (Complet.equals("Complet")) {
            comPletAdapter = new CompletAdapter(this, filteredCompletList);
            rv_work_list.setAdapter(comPletAdapter);
        }
    }
}