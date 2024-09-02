package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddStudy_activity extends AppCompatActivity {
    ImageView iv_back;
    EditText et_name_study;
    Button btn_add_study;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study);
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        et_name_study = findViewById(R.id.et_name_study);
        iv_back = findViewById(R.id.iv_back);
        btn_add_study = findViewById(R.id.btn_add_study);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_add_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String study_name = et_name_study.getText().toString();
                save_study(pid , study_name);
            }
        });
    }

    private void save_study(String pid , String name){
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        // PHP 스크립트의 URL을 정의합니다.
        String url = "http://49.247.32.169/YoWnNer/set_study_Data.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
                .add("upid", pid) //사용자 고유번호
                .add("name", name) //사용자 고유번호
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}