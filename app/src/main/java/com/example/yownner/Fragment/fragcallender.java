package com.example.yownner.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yownner.CalendarAdapter;
import com.example.yownner.Item_timeDate;
import com.example.yownner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragcallender extends Fragment {
    private View view;

    private RecyclerView recyclerView;

    private String TAG = "프래그먼트";
    String pid ;
    List<String> memoDate = new ArrayList<>();
    List<Item_timeDate> timeDate = new ArrayList<>();
    private Set<String> memoDateSet = new HashSet<>(); // Set을 사용하여 중복 제거
    private Set<String> timeDateSet = new HashSet<>(); // Set을 사용하여 중복 제거
    private CalendarAdapter calendarAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_callender, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            pid = arguments.getString("pid");
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        get_time_Date(pid);
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.cal_recycler);
        int position = Integer.MAX_VALUE / 2;
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CalendarAdapter(memoDate, timeDate, pid));
        // 항목의 위치 지정
        // 항목씩 스크롤 지정
        PagerSnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(recyclerView);
    }


    public void get_memo_Date(String pid) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(pid);
        // 서버의 URL을 정의합니다. 아래 URL을 실제 서버의 주소로 변경하세요.
        String url = "http://49.247.32.169/YoWnNer/get_memo_Date.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", pid)
                .build();
        // 요청을 생성합니다.
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 서버 응답을 JSON 배열로 파싱
                            JSONArray jsonArray = new JSONArray(responseBody);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String memo = jsonObject.getString("memo");
                                String year = jsonObject.getString("year");
                                String month = jsonObject.getString("month");
                                String day = jsonObject.getString("day");
                                // 날짜를 두 자리수로 포맷팅
                                String formattedDay = String.format(Locale.getDefault(), "%02d", Integer.parseInt(day));
                                String date = year + "-" + month + "-" + formattedDay;
                                // 중복 검사 후 추가
                                if (memoDateSet.add(date)) {
                                    // 중복이 없는 경우에만 memoDate 리스트에 추가
                                    memoDate.add(date);
                                }
                            }
                            initView();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void get_time_Date(String pid) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(pid);
        // 서버의 URL을 정의합니다. 아래 URL을 실제 서버의 주소로 변경하세요.
        String url = "http://49.247.32.169/YoWnNer/get_time_Date.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("pid", pid)
                .build();
        // 요청을 생성합니다.
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 서버 응답을 JSON 배열로 파싱
                            JSONArray jsonArray = new JSONArray(responseBody);
                            Map<String, Integer> dateTotalSecondsMap = new HashMap<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String tpid = jsonObject.getString("tpid");
                                String date = jsonObject.getString("date");
                                String totalSeconds = jsonObject.getString("totalSeconds");
                                int seconds = Integer.parseInt(totalSeconds);

                                // 날짜에 대한 총 초를 업데이트
                                if (dateTotalSecondsMap.containsKey(date)) {
                                    int currentTotalSeconds = dateTotalSecondsMap.get(date);
                                    dateTotalSecondsMap.put(date, currentTotalSeconds + seconds);
                                } else {
                                    dateTotalSecondsMap.put(date, seconds);
                                }
                            }

                            // 맵 항목을 Item_timeDate 객체로 변환하고 timeDate 리스트에 추가
                            timeDate.clear(); // 기존 데이터를 모두 지우고 새로 추가
                            for (Map.Entry<String, Integer> entry : dateTotalSecondsMap.entrySet()) {
                                String date = entry.getKey();
                                int totalSeconds = entry.getValue();
                                System.out.println(date + "/" + totalSeconds);
                                timeDate.add(new Item_timeDate(date, totalSeconds));
                            }

                            // 여기서 initView 호출하면 기존 데이터가 아닌 새로운 데이터를 이용하여 RecyclerView를 갱신
                            get_memo_Date(pid);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }



}

