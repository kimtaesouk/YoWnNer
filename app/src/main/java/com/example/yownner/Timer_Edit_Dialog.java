package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

public class Timer_Edit_Dialog extends Dialog {
    private EditText et_name_edit_timer;
    private ImageButton ib_timer_edit_color;
    private ImageView iv_timer_edit_color;
    private Button cancel, write;
    private View.OnClickListener btn_edit_timer_cancel, btn_edit_timer_write;
    private int color;
    private String colorHex;
    private String timerName;


    public Timer_Edit_Dialog(@NonNull Context context,View.OnClickListener btn_edit_timer_write, View.OnClickListener btn_edit_timer_cancel, String timername, int color,String colorhex) {
        super(context);
        this.btn_edit_timer_cancel = btn_edit_timer_cancel;
        this.btn_edit_timer_write = btn_edit_timer_write;
        this.color = color;
        this.timerName = timername;
        this.colorHex = colorhex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_edit_dialog);
        et_name_edit_timer = findViewById(R.id.et_name_edit_timer);
        ib_timer_edit_color = findViewById(R.id.ib_timer_edit_color);
        iv_timer_edit_color = findViewById(R.id.iv_timer_edit_color);
        cancel = findViewById(R.id.btn_edit_timer_cancel);
        cancel.setOnClickListener(btn_edit_timer_cancel);
        write = findViewById(R.id.btn_edit_timer_write);
        write.setOnClickListener(btn_edit_timer_write);
        iv_timer_edit_color.setColorFilter(color);
        et_name_edit_timer.setText(timerName);
        ib_timer_edit_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(getContext())
                        .setTitle("Pick Theme")         // Default "Choose Color"
                        .setColorShape(ColorShape.SQAURE) // Default ColorShape.CIRCLE
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int chcolor, String chcolorHex) {
                                iv_timer_edit_color.setColorFilter(chcolor); // 배경색 설정
                                color = chcolor;
                                colorHex = chcolorHex;
                            }
                        })
                        .show();
            }
        });


    }
    public int getEditColor(){
        return color;
    }
    public String getEditColorHex(){
        return colorHex;
    }
    public String getEditTextContent(){
        return et_name_edit_timer.getText().toString();
    }
}