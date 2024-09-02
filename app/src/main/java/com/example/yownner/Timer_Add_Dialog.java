package com.example.yownner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

public class Timer_Add_Dialog extends Dialog {

    private ImageButton timer_color;
    private Button add_timer_cancel, add_timer_write;
    private ImageView iv_timer_color;
    private EditText et_name_timer;

    private View.OnClickListener ib_timer_color;
    private View.OnClickListener btn_add_timer_cancel;
    private View.OnClickListener btn_add_timer_write;
    private int color;
    private String colorHex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_add_dialog);
        timer_color = findViewById(R.id.ib_timer_color);
        timer_color.setOnClickListener(ib_timer_color);
        et_name_timer = findViewById(R.id.et_name_timer);
        iv_timer_color = findViewById(R.id.iv_timer_color);
        add_timer_cancel = findViewById(R.id.btn_add_timer_cancel);
        add_timer_write = findViewById(R.id.btn_add_timer_write);
        add_timer_cancel.setOnClickListener(btn_add_timer_cancel);
        add_timer_write.setOnClickListener(btn_add_timer_write);

        timer_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(getContext())
                        .setTitle("Pick Theme")         // Default "Choose Color"
                        .setColorShape(ColorShape.SQAURE) // Default ColorShape.CIRCLE
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int chcolor, String chcolorHex) {
                                iv_timer_color.setColorFilter(chcolor); // 배경색 설정
                                color = chcolor;
                                colorHex = chcolorHex;
                            }
                        })
                        .show();
            }
        });
    }

    public Timer_Add_Dialog(@NonNull Context context, View.OnClickListener btn_add_timer_write, View.OnClickListener btn_add_timer_cancel) {
        super(context);
        this.btn_add_timer_cancel = btn_add_timer_cancel;
        this.btn_add_timer_write = btn_add_timer_write;
    }

    public int getColor(){
        return color;
    }
    public String getColorHex(){
        return colorHex;
    }
    public String getTextContent(){
        return et_name_timer.getText().toString();
    }
}
