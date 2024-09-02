package com.example.yownner;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Change_Work_Dialog extends Dialog {

    private Button write, delete;
    private EditText et_change_work;
    private String tpid;
    private String memo;
    private String getmemo;
    private CustomAdapter adapter;
    private OnMemoChangedListener memoChangedListener;
    public Change_Work_Dialog(Context context, CustomAdapter adapter) {
        super(context);
        this.adapter = adapter; // 어댑터 참조 초기화
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_work_dialog);

        write = findViewById(R.id.btn_rwd_write);
        delete = findViewById(R.id.btn_rwd_delete);

        et_change_work = findViewById(R.id.et_change_work);

        // 작성 버튼 클릭 이벤트
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo = getEditTextContent();
                getmemo = et_change_work.getText().toString();
                OkHttpClient okHttpClient = new OkHttpClient();
                // PHP 스크립트의 URL을 정의합니다.
                String url = "http://49.247.32.169/YoWnNer/update_memo.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                RequestBody requestBody = new FormBody.Builder()
                        .add("memo", memo) // 작성한 내용
                        .add("tpid", tpid) //사용자 고유번호
                        .build();
                // URL 및 요청 바디를 사용하여 POST 요청을 생성합니다.
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
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
                        // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다
                        adapter.onMemoChanged(getmemo);
                    }
                });
                dismiss(); // 다이얼로그 닫기
            }
        });

        // 삭제 버튼 클릭 이벤트
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient okHttpClient = new OkHttpClient();
                // PHP 스크립트의 URL을 정의합니다.
                String url = "http://49.247.32.169/YoWnNer/delete_memo.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                RequestBody requestBody = new FormBody.Builder()
                        .add("tpid", tpid) //사용자 고유번호
                        .build();
                // URL 및 요청 바디를 사용하여 POST 요청을 생성합니다.
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
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
                        // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다
                        // 리사이클러뷰를 새로고침합니다.
                        getmemo = "삭제";
                        adapter.onMemoChanged(getmemo);
                    }
                });
                dismiss(); // 다이얼로그 닫기

            }
        });
    }

    public String getEditTextContent() {
        return et_change_work.getText().toString();
    }

    public void SetEditTextContent(String memo , String pid) {
        et_change_work.setText(memo);
        tpid = pid;
    }

    public interface OnMemoChangedListener {
        boolean onItemMove(int fromPosition, int toPosition);

        void onMemoChanged(String memo);
    }

}
