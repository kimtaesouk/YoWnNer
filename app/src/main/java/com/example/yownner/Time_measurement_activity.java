package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Time_measurement_activity extends AppCompatActivity {
    private String tPid;
    private String uPid;
    private String selectedDate;
    private TextView time;
    private ImageView stop;
    private String startTimer;
    private String stopTimer;
    private String measuretimer;
    private String measuretime;
    private RecyclerView rv_measure_timer_list;
    private String ip = "192.168.0.5";
    private int port = 5002;
    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    List<String> messages = new ArrayList<>();  // 메시지 목록
    List<String> clients = new ArrayList<>();
    MessageAdapter messageAdapter;  // 메시지 어댑터 추가
    String read;
    private int elapsedSeconds = 0;
    private long startTime;
    private volatile boolean isThreadRunning = true; // 스레드 실행 여부를 제어할 변수
    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals("TIMER_UPDATE")) {
                    int hours = intent.getIntExtra("hours", 0);
                    int minutes = intent.getIntExtra("minutes", 0);
                    int seconds = intent.getIntExtra("seconds", 0);
                    // 시간 데이터를 TextView에 설정
                    time.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_measurement);
        Intent intent = getIntent();
        tPid = intent.getStringExtra("pid");
        uPid = intent.getStringExtra("upid");
        selectedDate = intent.getStringExtra("selectedDate");
        measuretime = intent.getStringExtra("measuretime");

        System.out.println(tPid +"/"+ uPid +"/"+ measuretime +"/"+ selectedDate);
        rv_measure_timer_list = findViewById(R.id.rv_measure_timer_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        rv_measure_timer_list.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(this, messages, clients);
        rv_measure_timer_list.setAdapter(messageAdapter);  // 어댑터 설정
        time = findViewById(R.id.time); // 시간을 표시할 TextView
        stop = findViewById(R.id.stop); // 타이머 중지 버튼
        // mHandler를 초기화
        mHandler = new Handler();
        // 두 번째 스레드 시작
        new Thread() {
            public void run() {
                connectToSocketServer(); // 초기 연결
            }
        }.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadRunning) {
                    startTime = SystemClock.elapsedRealtime();
                    System.out.println("1111 :"+startTime);
                    String timeString = time.getText().toString();
                    String[] timeParts = timeString.split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);
                    elapsedSeconds = hours * 3600 + minutes * 60 + seconds;
                    sendToServer(String.valueOf(elapsedSeconds));
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        // BroadcastReceiver 등록
        IntentFilter filter = new IntentFilter("TIMER_UPDATE");
        registerReceiver(timerReceiver, filter);
        // 디버그 로그로 타이머 서비스 시작 확인
        Log.d("Time_measurement_activity", "Timer service started");
        // 타이머 서비스 시작 코드 추가
        // Calendar 객체를 생성하여 현재 시간 정보를 가져옵니다.
        Calendar calendar = Calendar.getInstance();
        // 시간 정보를 형식에 맞게 출력하기 위해 SimpleDateFormat을 사용합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // SimpleDateFormat을 사용하여 현재 시간을 문자열로 변환합니다.
        startTimer = sdf.format(calendar.getTime());
        startTimerService();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThreadRunning = false;
                if (sendWriter != null) {
                    sendWriter.close();
                }
                stopThreadAndSocket();

                Intent stopServiceIntent = new Intent(Time_measurement_activity.this, TimerService.class);
                stopServiceIntent.setAction("STOP_TIMER"); // "STOP_TIMER" 액션 추가
                stopServiceIntent.putExtra("tpid", tPid);
                stopServiceIntent.putExtra("upid", uPid);
                // 네트워크 작업을 백그라운드 스레드에서 수행
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveData(); // 네트워크 작업 수행
                    }
                }).start();
                startService(stopServiceIntent); // 타이머 서비스를 중지
                finish();
            }
        });
    }

    private void saveData(){
        // Calendar 객체를 생성하여 현재 시간 정보를 가져옵니다.
        Calendar calendar = Calendar.getInstance();
        // 시간 정보를 형식에 맞게 출력하기 위해 SimpleDateFormat을 사용합니다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // SimpleDateFormat을 사용하여 현재 시간을 문자열로 변환합니다.
        stopTimer = sdf.format(calendar.getTime());
        System.out.println(stopTimer);
        measuretimer = time.getText().toString();
        String[] parts = measuretimer.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        if(measuretime != null && !measuretime.isEmpty()){
            int measure = Integer.parseInt(measuretime);
            int measure_hours = measure / 3600; // 1 시간은 3600 초입니다.
            int measure_minutes = (measure % 3600) / 60;
            int measure_seconds = measure % 60;
            seconds -= measure_seconds;
            minutes -= measure_minutes;
            hours -= measure_hours;
        }
// 시간, 분, 초를 초로 변환
        int totalSeconds = hours * 3600 + minutes * 60 + seconds;
        System.out.println(uPid +"/"+ tPid +"/" + selectedDate + "/" + startTimer + "/" + stopTimer + "/" + totalSeconds);
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        // PHP 스크립트의 URL을 정의합니다.
        String url = "http://49.247.32.169/YoWnNer/time_measurement.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("upid", uPid) //사용자 고유번호
                .add("tpid", tPid)
                .add("selectedDate", selectedDate)
                .add("starttime", startTimer)
                .add("stoptime", stopTimer)
                .add("totalSeconds", String.valueOf(totalSeconds))
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
                finish();
                // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
            }
        });
    }
    private void startTimerService() {
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.setAction("START_TIMER"); // "START_TIMER" 액션을 추가
        serviceIntent.putExtra("measuretime", measuretime);
        startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isThreadRunning = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(timerReceiver);
    }
    // onCreate 메소드 외부에 스레드와 소켓을 닫는 메소드 추가
    private void stopThreadAndSocket() {
        isThreadRunning = false; // 스레드 종료 플래그 설정

        if (sendWriter != null) {
            sendWriter.close();
            sendWriter = null; // sendWriter를 닫고 null로 설정
        }
            try {
                socket.close();
                System.out.println("소켓 닫음");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 버튼을 눌러도 아무런 동작을 하지 않습니다.
    }

    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            if (msg != null) {
                String[] parts = msg.split(":");
                if (parts.length == 3) {
                    String firstPart = parts[0].trim();
                    String secondPart = parts[1].trim();
                    String thirdPart = parts[2].trim();
                    System.out.println(msg);
                    int clientNum = Integer.parseInt(firstPart);
                    int userpid = Integer.parseInt(secondPart);
                    int seconds = Integer.parseInt(thirdPart);
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int remainingSeconds = seconds % 60;
                    int upid = Integer.parseInt(uPid);

                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
                        OkHttpClient client = new OkHttpClient();

                        String url = "http://49.247.32.169/YoWnNer/get_clients_data.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                        RequestBody requestBody = new FormBody.Builder()
                                .add("upid", secondPart) //사용자 고유번호
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
                                if (responseBody != null && !responseBody.isEmpty()) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(responseBody);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                                            if (dataArray.length() > 0) {
                                                JSONObject dataObject = dataArray.getJSONObject(0);
                                                String name = dataObject.optString("name", ""); // "name" 필드가 없거나 빈 값일 경우 빈 문자열로 설정
                                                String identity = dataObject.optString("identity", "");

                                                int emptyNameCount = 0; // 빈 문자열의 개수를 저장할 변수
                                                if (name.isEmpty()) {
                                                    // "name" 필드가 빈 문자열일 경우 emptyNameCount를 증가
                                                    emptyNameCount++;
                                                }

                                                clients.set(clientNum - emptyNameCount, name); // 빈 문자열의 개수만큼 clientNum에서 뺄셈
                                                System.out.println(clients);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    // 응답이 비어있는 경우에 대한 처리를 수행
                                    Log.e("JSON Parsing", "Empty response");
                                }

                                // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
                            }
                        });
                        // userpid와 uPid가 같지 않을 때만 메시지를 처리
                        // 클라이언트 번호가 messages 리스트의 크기를 초과하지 않는지 확인
                        while (clientNum >= messages.size()) {
                            messages.add(""); // 클라이언트 번호에 해당하는 인덱스가 없으면 빈 문자열 추가
                        }
                        while (clientNum >= clients.size()) {
                            clients.add(""); // Add an empty client for the client
                        }
                        if(userpid != upid){
                            messages.set(clientNum, timeString); // 해당 인덱스의 값을 업데이트
                            messageAdapter.notifyDataSetChanged();
                        }else{
                            String a = time.getText().toString();
                            messages.set(clientNum,a);
                            messageAdapter.notifyDataSetChanged();
                        }

                        System.out.println(messages);
                        System.out.println(clients);
                } else {
                    System.out.println(msg);
                }
            }
        }
    }
    // 인터넷 연결이 끊겼을 때 다시 연결하는 메서드
    private void connectToSocketServer() {
        try {
            InetAddress serverAddr = InetAddress.getByName(ip);
            socket = new Socket(serverAddr, port);
            sendWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            while (isThreadRunning) {
                read = input.readLine();
                if (read != null) {
                    mHandler.post(new msgUpdate(read));
                    System.out.println(read);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 연결 오류를 처리하고 다시 연결을 시도합니다.
            // 연결 재시도 간격을 추가할 수 있습니다.
        }
    }
    private void sendToServer(String message) {
        if (sendWriter != null) {
            sendWriter.println( uPid + ":" + message);
            sendWriter.flush();
        }
    }
}