package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.vcn.VcnUnderlyingNetworkTemplate;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FindPassword_activity extends AppCompatActivity {

    private EditText et_email_findpw, et_email_certification;

    private Button btn_findpw_email_confire, btn_email_certification_check, btn_generation_password;

    private TextView tv_certification_timer;

    boolean certification = false;

    String emailCode = null;

    String email_certification = null;

    String tempPassword = null;

    private CountDownTimer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        et_email_findpw = findViewById(R.id.et_email_findpw);

        btn_findpw_email_confire = findViewById(R.id.btn_findpw_email_confire);
        btn_findpw_email_confire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendEmail mailServer = new SendEmail();
                mailServer.sendSecurityCode(getApplicationContext(), et_email_findpw.getText().toString());
                emailCode = mailServer.emailCode;
                System.out.println(emailCode);

                startTimer(1 * 60 * 1000);

            }
        });

        tv_certification_timer = findViewById(R.id.tv_certification_timer);
        et_email_certification = findViewById(R.id.et_email_certification);

        btn_email_certification_check = findViewById(R.id.btn_email_certification_check);
        btn_email_certification_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_certification = et_email_certification.getText().toString();
                if(email_certification.equals(emailCode)){
                    stopTimer();
                    certification = true;
                    boolean haserror = false;
                    String email = et_email_findpw.getText().toString();
                    email_certification = et_email_certification.getText().toString();
                    if(email.isEmpty()){

                        et_email_findpw.setError("이메일을 입력하세요");
                        haserror = true;
                    }else {
                        et_email_findpw.setError(null);
                    }
                    if(email_certification.isEmpty()){
                        et_email_certification.setError("인증번호를 입력하세요");
                        haserror = true;
                    }else{
                        et_email_certification.setError(null);
                    }
                    if(!certification){
                        btn_email_certification_check.setError("이메일 인증을 진행하세요");
                        haserror = true;
                    }else{
                        btn_email_certification_check.setError(null);
                    }
                    if(!haserror){
                        Intent intent = new Intent(getApplicationContext() , ChangePassword_activity.class);
                        intent.putExtra("email" , email);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

//



    }

    private void startTimer(long durationMillis) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                String timeLeft = String.format("%02d:%02d", minutes, seconds);
                tv_certification_timer.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                emailCode = null; // emailCode 값을 null로 설정
                tv_certification_timer.setText("인증 실패");
                tv_certification_timer.setTextColor(Color.parseColor("#00FF00"));
                tv_certification_timer.setTextColor(Color.parseColor("#FB0000"));

                // email_certification 값과 emailCode 값이 같은지 비교하여 certification 값을 변경
                if (email_certification != null && email_certification.equals(emailCode)) {
                    certification = true;
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            tv_certification_timer.setText("인증 완료");
            tv_certification_timer.setTextColor(Color.parseColor("#00FF00"));
        }
    }


}