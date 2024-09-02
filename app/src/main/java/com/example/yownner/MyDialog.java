package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MyDialog extends Dialog {

    private Button Cammara;
    private Button gallery;
    private Button nomal;

    private View.OnClickListener btn_camara;
    private View.OnClickListener btn_gallery;
    private View.OnClickListener btn_nomal;

    public TextView tv_dialog_title;

    public String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.activity_my_dialog);

        Cammara = (Button)findViewById(R.id.btn_camara);
        gallery = (Button)findViewById(R.id.btn_gallery);
        nomal = (Button)findViewById(R.id.btn_nomal);

        tv_dialog_title = findViewById(R.id.tv_dialog_title);

        Cammara.setOnClickListener(btn_camara);
        gallery.setOnClickListener(btn_gallery);
        nomal.setOnClickListener(btn_nomal);

        //타이틀과 바디의 글씨 재셋팅
        tv_dialog_title.setText(this.title);
    }

    public MyDialog(@NonNull Context context, View.OnClickListener btn_camara, View.OnClickListener btn_gallery,View.OnClickListener btn_nomal, String title) {
        super(context);
        //생성자에서 리스너 및 텍스트 초기화
        this.btn_camara = btn_camara;
        this.btn_gallery = btn_gallery;
        this.btn_nomal = btn_nomal;
        this.title = title;
    }
}