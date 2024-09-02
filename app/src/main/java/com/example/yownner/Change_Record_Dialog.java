package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Change_Record_Dialog extends Dialog {
    private RecordTimeAdapter adapter;
    private Context context;
    private Item_time item;
    private EditText et_starttime_hour, et_starttime_min,et_starttime_sec
            ,et_stoptime_hour,et_stoptime_min, et_stoptime_sec;
    private ImageView back_dialog_to_edittimer;
    private Button btn_record_change, btn_record_delete;
    private String nextstarttime, beforestoptime;


    public Change_Record_Dialog(@NonNull Context context, Item_time item) {
        super(context);
        this.context = context;
        this.item = item;
    }

    public void SetEditTextContent(String starttime , String stoptime , String tpid, String nextstarttime, String beforestoptime) {
        System.out.println(starttime+"/"+stoptime+"/"+tpid);
        this.nextstarttime = nextstarttime;
        this.beforestoptime = beforestoptime;
        String[] startTokens = starttime.split("\\s+");
        String[] stopTokens = stoptime.split("\\s+");

        String startTimeDay = startTokens[0]; // 두 번째 부분
        String startTimePart = startTokens[1]; // 두 번째 부분
        String stopTimeDay = stopTokens[0]; // 두 번째 부분
        String stopTimePart = stopTokens[1]; // 두 번째 부분

        String[] startTime = startTimePart.split(":");
        String[] stopTime = stopTimePart.split(":");

        String starttime_hour = startTime[0]; // 두 번째 부분
        String starttime_min = startTime[1]; // 두 번째 부분
        String starttime_sec = startTime[2]; // 두 번째 부분
        String stopTime_hour = stopTime[0]; // 두 번째 부분
        String stopTime_min = stopTime[1]; // 두 번째 부분
        String stopTime_sec = stopTime[2]; // 두 번째 부분

        et_starttime_hour.setText(starttime_hour);
        et_starttime_min.setText(starttime_min);
        et_starttime_sec.setText(starttime_sec);
        et_stoptime_hour.setText(stopTime_hour);
        et_stoptime_min.setText(stopTime_min);
        et_stoptime_sec.setText(stopTime_sec);

        back_dialog_to_edittimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_record_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 스타트 시간과 스톱 시간을 초로 변환할 변수를 초기화합니다.
                int startSeconds = 0;
                int stopSeconds = 0;

                // EditText에서 입력된 시, 분, 초 값을 가져옵니다.
                String startTimeHour = et_starttime_hour.getText().toString();
                String startTimeMin = et_starttime_min.getText().toString();
                String startTimeSec = et_starttime_sec.getText().toString();

                String stopTimeHour = et_stoptime_hour.getText().toString();
                String stopTimeMin = et_stoptime_min.getText().toString();
                String stopTimeSec = et_stoptime_sec.getText().toString();

                // 스타트 시간을 초로 변환하여 누적합니다.
                startSeconds += Integer.parseInt(startTimeHour) * 3600; // 시간을 초로 변환
                startSeconds += Integer.parseInt(startTimeMin) * 60;    // 분을 초로 변환
                startSeconds += Integer.parseInt(startTimeSec);         // 초를 그대로 더함

                // 스톱 시간을 초로 변환하여 누적합니다.
                stopSeconds += Integer.parseInt(stopTimeHour) * 3600; // 시간을 초로 변환
                stopSeconds += Integer.parseInt(stopTimeMin) * 60;    // 분을 초로 변환
                stopSeconds += Integer.parseInt(stopTimeSec);         // 초를 그대로 더함

                // ":"로 구분하여 문자열을 합칩니다.
                String startTimeCombined = startTimeHour + ":" + startTimeMin + ":" + startTimeSec;
                String stopTimeCombined = stopTimeHour + ":" + stopTimeMin + ":" + stopTimeSec;
                if (startTimeCombined.compareTo(beforestoptime) < 0) {
                    et_starttime_hour.setError(null);
                    et_starttime_min.setError(null);
                    et_starttime_sec.setError(null);
                    System.out.println(startTimeCombined);
                    System.out.println(beforestoptime);
                    System.out.println(stopTimeCombined);
                    System.out.println(nextstarttime);
                }
                if (stopTimeCombined.compareTo(nextstarttime) > 0) {
                    et_stoptime_hour.setError(null);
                    et_stoptime_min.setError(null);
                    et_stoptime_sec.setError(null);
                    System.out.println(startTimeCombined);
                    System.out.println(beforestoptime);
                    System.out.println(stopTimeCombined);
                    System.out.println(nextstarttime);
                }
                if(startTimeCombined.compareTo(beforestoptime) >= 0 && stopTimeCombined.compareTo(nextstarttime) <= 0){
                    String chstarttime = startTimeDay + " " + startTimeCombined;
                    String chstoptime = stopTimeDay + " " + stopTimeCombined;
                    String totalSeconds = String.valueOf(stopSeconds - startSeconds);
                    saveData(chstarttime,chstoptime,totalSeconds);
                    dismiss();
                    System.out.println(startTimeCombined);
                    System.out.println(beforestoptime);
                    System.out.println(stopTimeCombined);
                    System.out.println(nextstarttime);
                }
            }
        });

    }

    public void saveData(String chstarttime, String chstoptime, String totalSeconds){
        String timepid = item.getpid();
        String tpid =  String.valueOf(timepid);
        OkHttpClient client = new OkHttpClient();

        String url = "http://49.247.32.169/YoWnNer/update_record_time.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("tpid", tpid)
                .add("starttime", chstarttime)
                .add("stoptime", chstoptime)
                .add("totalSeconds", totalSeconds)
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
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_record_dialog);
        et_starttime_hour = findViewById(R.id.et_starttime_hour);
        et_starttime_min = findViewById(R.id.et_starttime_min);
        et_starttime_sec = findViewById(R.id.et_starttime_sec);
        et_stoptime_hour = findViewById(R.id.et_stoptime_hour);
        et_stoptime_min = findViewById(R.id.et_stoptime_min);
        et_stoptime_sec = findViewById(R.id.et_stoptime_sec);
        back_dialog_to_edittimer = findViewById(R.id.back_dialog_to_edittimer);
        btn_record_change = findViewById(R.id.btn_record_change);
        btn_record_delete = findViewById(R.id.btn_record_delete);
    }


}