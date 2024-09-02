package com.example.yownner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class WorkDialog extends Dialog {
    private Button cancle;
    private Button write;

    private EditText et_work; // 에딧 텍스트 추가

    private View.OnClickListener btn_wd_cancle;
    private View.OnClickListener btn_wd_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_dialog);


        cancle = findViewById(R.id.btn_wd_cancle);
        write = findViewById(R.id.btn_wd_write);

        et_work = findViewById(R.id.et_id);

        cancle.setOnClickListener(btn_wd_cancle);
        write.setOnClickListener(btn_wd_write);
    }

    public WorkDialog(@NonNull Context context, View.OnClickListener btn_wd_cancle, View.OnClickListener btn_wd_write) {
        super(context);

        this.btn_wd_cancle = btn_wd_cancle;
        this.btn_wd_write = btn_wd_write;
    }

    // 에딧 텍스트의 내용을 얻는 메소드 추가
    public String getEditTextContent() {
        return et_work.getText().toString();
    }
}
