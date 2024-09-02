package com.example.yownner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

public class Timer_Dialog extends Dialog {
    private Button add_timer;
    private Button edit_timer;
    private Button edit_record;
    private View.OnClickListener btn_add_timer;
    private View.OnClickListener btn_edit_timer;
    private View.OnClickListener btn_edit_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.activity_timer_dialog);

        add_timer = findViewById(R.id.btn_add_timer);
        edit_timer = findViewById(R.id.btn_edit_timer);
        edit_record = findViewById(R.id.btn_edit_record);

        add_timer.setOnClickListener(btn_add_timer);
        edit_timer.setOnClickListener(btn_edit_timer);
        edit_record.setOnClickListener(btn_edit_record);


    }

    public Timer_Dialog(@NonNull Context context, View.OnClickListener btn_add_timer, View.OnClickListener btn_edit_timer, View.OnClickListener btn_edit_record) {
        super(context);

        this.btn_add_timer = btn_add_timer;
        this.btn_edit_timer = btn_edit_timer;
        this.btn_edit_record = btn_edit_record;
    }
}
