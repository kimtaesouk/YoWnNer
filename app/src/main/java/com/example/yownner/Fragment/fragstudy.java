package com.example.yownner.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yownner.AddStudy_activity;
import com.example.yownner.R;
import com.example.yownner.Work_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class fragstudy extends Fragment  {
    private View view;
    private String TAG = "프래그먼트";
    String pid ;

    ImageView iv_add_study;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_study, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            pid = arguments.getString("pid");
        }
        iv_add_study = view.findViewById(R.id.iv_add_study);
        iv_add_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext() , AddStudy_activity.class);
                intent.putExtra("pid" , pid);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onstart");
        get_study_data();
    }

    private void get_study_data(){
        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();
        // PHP 스크립트의 URL을 정의합니다.
        String url = "http://49.247.32.169/YoWnNer/get_study_Date.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
        RequestBody requestBody = new FormBody.Builder()
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
                    if(success){
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject studyData = dataArray.getJSONObject(i);
                            String pid = studyData.getString("pid");
                            String name = studyData.getString("name");
                            String master = studyData.getString("master");
                            String classification = studyData.getString("classification");
                            String personnel = studyData.getString("personnel");
                            String Participant = studyData.getString("Participant");
                            String profile_image = studyData.getString("profile_image");
                            String created = studyData.getString("created");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
