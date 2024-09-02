package com.example.yownner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

public class Timer_hide_check_dialog extends Dialog {
    private Button cancle, ok;
    private View.OnClickListener btn_timer_hide_cancle, btn_timer_hide_ok;
    public Timer_hide_check_dialog(@NonNull Context context, View.OnClickListener btn_timer_hide_ok, View.OnClickListener btn_timer_hide_cancle) {
        super(context);
        this.btn_timer_hide_cancle = btn_timer_hide_cancle;
        this.btn_timer_hide_ok = btn_timer_hide_ok;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_hide_check_dialog);

        cancle = findViewById(R.id.btn_timer_hide_cancle);
        cancle.setOnClickListener(btn_timer_hide_cancle);
        ok = findViewById(R.id.btn_timer_hide_ok);
        ok.setOnClickListener(btn_timer_hide_ok);

    }
}
