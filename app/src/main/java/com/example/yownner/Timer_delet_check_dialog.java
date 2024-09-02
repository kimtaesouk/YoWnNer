package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Timer_delet_check_dialog extends Dialog {
    private Button cancle, ok;
    private View.OnClickListener btn_timer_delet_cancle, btn_timer_delet_ok;


    public Timer_delet_check_dialog(@NonNull Context context, View.OnClickListener btn_timer_delet_ok, View.OnClickListener btn_timer_delet_cancle) {
        super(context);
        this.btn_timer_delet_cancle = btn_timer_delet_cancle;
        this.btn_timer_delet_ok = btn_timer_delet_ok;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_delet_check_dialog);

        cancle = findViewById(R.id.btn_timer_delet_cancle);
        cancle.setOnClickListener(btn_timer_delet_cancle);
        ok = findViewById(R.id.btn_timer_delet_ok);
        ok.setOnClickListener(btn_timer_delet_ok);

    }
}