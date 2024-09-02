package com.example.yownner.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.Change_Work_Dialog;
import com.example.yownner.CustomAdapter;
import com.example.yownner.GetDataService;
import com.example.yownner.ItemTouchHelperAdapter;
import com.example.yownner.ItemTouchHelperCallback;
import com.example.yownner.Item_memo;
import com.example.yownner.Item_time;
import com.example.yownner.Item_timer;
import com.example.yownner.MyDialog;
import com.example.yownner.R;
import com.example.yownner.RetrofitClientInstance;
import com.example.yownner.TaskAdapter;
import com.example.yownner.Timer_activity;
import com.example.yownner.WorkDialog;
import com.example.yownner.Work_activity;

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

public class fraghome extends Fragment  {
    private View view;
    private String TAG = "프래그먼트";
    private LinearLayout lilay_date_main;
    private TextView tv_date_main, tv_home_total_time;
    private ImageView iv_add_work, iv_subtract_day_main, iv_add_day_main;
    private ImageView iv_list_work;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> dateList = new ArrayList<>(); // 스피너 아이템으로 사용될 날짜 목록
    private String pid; // pid 값을 저장하기 위한 멤버 변수
    String selectedDate;
    private WorkDialog Dialog_Listener;
    private LinearLayout ll_timer;

    private RecyclerView rv_work;

    private static final String RATAG = "TestActivity-레트로핏";
    // 어답터
    private CustomAdapter adapter;

    private Change_Work_Dialog Dialog;
    private  GetDataService service;
    private ItemTouchHelper helper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        lilay_date_main = view.findViewById(R.id.lilay_date_main);
        tv_date_main = view.findViewById(R.id.tv_date_main);
        iv_add_work = view.findViewById(R.id.iv_add_work);
        iv_list_work = view.findViewById(R.id.iv_list_work);
        ll_timer = view.findViewById(R.id.ll_timer);
        iv_subtract_day_main = view.findViewById(R.id.iv_subtract_day_main);
        iv_add_day_main = view.findViewById(R.id.iv_add_day_main);
        tv_home_total_time = view.findViewById(R.id.tv_home_total_time);
        Bundle arguments = getArguments();
        if (arguments != null) {
            pid = arguments.getString("pid");
            selectedDate = arguments.getString("selectedDate");
        }
        // 오늘 날짜를 기본값으로 설정
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day); // YYYY-MM-DD 형식
        System.out.println(pid);
        System.out.println(selectedDate);
        ll_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , Timer_activity.class);
                intent.putExtra("pid" , pid);
                intent.putExtra("selectedDate" , selectedDate);
                System.out.println(" 프레그 홈" + selectedDate);
                startActivity(intent);
            }
        });
        iv_subtract_day_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddDayClick(false);
            }
        });
        iv_add_day_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddDayClick(true);
            }
        });
        // 레트로핏 인스턴스 생성을 해줍니다.
        // enqueue로 비동기 통신을 싱행합니다.
        // 레트로핏 인스턴스 생성을 해줍니다.
        // enqueue로 비동기 통신을 싱행합니다.

        // 스피너 클릭 시 날짜 선택 다이얼로그 표시
        lilay_date_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        iv_add_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceProfileDialog();
            }
        });
        iv_list_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , Work_activity.class);
                intent.putExtra("pid" , pid);
                startActivity(intent);
            }
        });
        System.out.println("create 끝");
        // 날짜 선택 다이얼로그에서 선택된 날짜를 스피너 아이템으로 추가
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("start 시작");
        tv_date_main.setText(selectedDate);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMemoService();
        getTimeService();
    }

    public void setPid(String pid) {
        this.pid = pid;
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
        if(isDays){
            calendar.add(Calendar.DAY_OF_MONTH, +1);
        }else{
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        selectedDate = dateFormat.format(calendar.getTime());

        tv_date_main.setText(selectedDate);
        getMemoService();
        getTimeService();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    public void generateDataList(List<Item_memo> memoList) {
        rv_work = view.findViewById(R.id.rv_work);
        adapter = new CustomAdapter(getContext(), memoList, helper);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_work.setLayoutManager(layoutManager);
        rv_work.setAdapter(adapter);
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(rv_work);
    }
    private void getMemoService(){
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        // 날짜를 년, 월, 일로 분리
        String[] dateParts = selectedDate.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        retrofit2.Call<List<Item_memo>> call = service.getItems(selectedDate,year,month,day, pid);
        //통신완료후 이벤트 처리를 위한 콜백 리스너 등록
        call.enqueue(new retrofit2.Callback<List<Item_memo>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Item_memo>> call, retrofit2.Response<List<Item_memo>> response) {
                generateDataList(response.body());
            }
            @Override
            public void onFailure(retrofit2.Call<List<Item_memo>> call, Throwable t) {
                // Network request failed
                Log.e("Retrofit", "Network request failed: " + call.request().url().toString());
                t.printStackTrace(); // Print the stack trace
            }
            // 정상으로 통신 성공시
        });
    }
    private void getTimeService() {
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        System.out.println(pid);

        // 서버의 URL을 정의합니다. 아래 URL을 실제 서버의 주소로 변경하세요.
        String url = "http://49.247.32.169/YoWnNer/get_timeList.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", pid) // 작성한 내용
                .add("selectedDate", selectedDate) //작성할때 선택되있던 날짜
                .build();
        // 요청을 생성합니다.
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 요청을 비동기적으로 실행하고 응답을 처리합니다.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청이 실패한 경우 처리합니다. (예: 네트워크 오류)
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 서버 응답을 처리합니다.
                if (response.isSuccessful()) {
                    // 응답이 성공한 경우 처리
                    String responseBody = response.body().string();

                    System.out.println(responseBody);

                    // responseBody에 서버로부터 받은 데이터가 포함되어 있습니다.
                    // 이 데이터를 파싱하여 사용하세요.

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 서버 응답을 JSON 배열로 파싱
                            try {
                                    JSONObject jsonResponse = new JSONObject(responseBody);
                                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                                if (dataArray.length() > 0) {
                                    // JSON 배열의 첫 번째 항목에서 "totalSeconds" 값을 추출
                                    JSONObject jsonObject = dataArray.getJSONObject(0);
                                    String totalSeconds = jsonObject.getString("Seconds");

                                    // "totalSeconds" 값이 "null" 또는 빈 문자열인 경우 0으로 처리
                                    int totalSecondsInt = (totalSeconds != null && !totalSeconds.equals("null")) ? Integer.parseInt(totalSeconds) : 0;

                                    // 시, 분, 초 계산
                                    int hours = totalSecondsInt / 3600;
                                    int minutes = (totalSecondsInt % 3600) / 60;
                                    int seconds = totalSecondsInt % 60;

                                    // hh:mm:ss 형식으로 변환
                                    String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                                    // TextView에 설정
                                    tv_home_total_time.setText(formattedTime);
                                } else {
                                    // JSON 배열이 비어있는 경우 처리
                                    // 예를 들어 기본값 0을 설정할 수 있습니다.
                                    tv_home_total_time.setText("00:00:00");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    // 응답이 실패한 경우 처리
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Response is not successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    // setEmail 메소드를 추가하여 email 값을 설정

    private void showDatePickerDialog() {
        // 선택된 날짜를 초기값으로 설정
        String[] dateParts = selectedDate.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // 월은 0부터 시작하므로 -1
        int day = Integer.parseInt(dateParts[2]);

        // DatePickerDialog를 생성하고 표시
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // 사용자가 선택한 날짜를 문자열로 가공하여 스피너 아이템으로 추가하고 갱신
                selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay); // YYYY-MM-DD 형식
                tv_date_main.setText(selectedDate);
                getMemoService();
                getTimeService();
                System.out.println(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show(); // 다이얼로그를 생성하고 표시
    }

    private void showChoiceProfileDialog() {
        Dialog_Listener = new WorkDialog(getContext(), cancle, write);
        Dialog_Listener.show();
    }
    private View.OnClickListener cancle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 다이얼로그 취소 버튼 클릭 시 처리
            Dialog_Listener.dismiss();

        }
    };
    public void refreshRecyclerView() {
        getMemoService();
    }
    private View.OnClickListener write = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 다이얼로그 작성 버튼 클릭 시 처리
            String editTextContent = Dialog_Listener.getEditTextContent(); // 에딧 텍스트 내용 가져오기
            // editTextContent를 사용하여 필요한 작업을 수행
            System.out.println("123"+editTextContent);
            System.out.println(selectedDate);
            System.out.println(pid);

            // 날짜를 년, 월, 일로 분리
            String[] dateParts = selectedDate.split("-");
            String year = dateParts[0];
            String month = dateParts[1];
            String day = dateParts[2];

            // OkHttpClient 인스턴스 생성
            OkHttpClient client = new OkHttpClient();

            // PHP 스크립트의 URL을 정의합니다.
            String url = "http://49.247.32.169/YoWnNer/memo_add.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.

            RequestBody requestBody = new FormBody.Builder()
                    .add("memo", editTextContent) // 작성한 내용
                    .add("date", selectedDate) //작성할때 선택되있던 날짜
                    .add("year", year) //작성할때 선택되있던 날짜
                    .add("month", month) //작성할때 선택되있던 날짜
                    .add("day", day) //작성할때 선택되있던 날짜
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

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // 서버에서의 응답을 처리합니다 (필요한 경우)
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
                    getMemoService();
                }
            });
            // 다이얼로그를 닫습니다.
            Dialog_Listener.dismiss();

        }
    };


}
