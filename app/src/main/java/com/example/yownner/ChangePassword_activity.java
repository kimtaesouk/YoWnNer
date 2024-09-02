package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassword_activity extends AppCompatActivity {

    private EditText et_pw_changepw;
    private EditText et_pwconf_changepw;
    private CheckBox cb_pw_conf1, cb_pw_conf2, cb_pw_conf3, cb_pw_conf4, cb_pw_conf5;
    private TextView tv_pw_conf1, tv_pw_conf2, tv_pw_conf3, tv_pw_conf4, tv_pw_conf5;
    private Button btn_change_password;
    boolean hasError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        System.out.println("3 = " + email);
        et_pw_changepw = findViewById(R.id.et_pw_changepw);
        et_pwconf_changepw = findViewById(R.id.et_pwconf_changepw);
        cb_pw_conf1 = findViewById(R.id.cb_pw_conf1);
        cb_pw_conf2 = findViewById(R.id.cb_pw_conf2);
        cb_pw_conf3 = findViewById(R.id.cb_pw_conf3);
        cb_pw_conf4 = findViewById(R.id.cb_pw_conf4);
        cb_pw_conf5 = findViewById(R.id.cb_pw_conf5);
        tv_pw_conf1 = findViewById(R.id.tv_pw_conf1);
        tv_pw_conf2 = findViewById(R.id.tv_pw_conf2);
        tv_pw_conf3 = findViewById(R.id.tv_pw_conf3);
        tv_pw_conf4 = findViewById(R.id.tv_pw_conf4);
        tv_pw_conf5 = findViewById(R.id.tv_pw_conf5);
        btn_change_password = findViewById(R.id.btn_change_password);

        et_pw_changepw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                validatePassword(s.toString());

            }
        });

        et_pwconf_changepw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 비밀번호 일치 검사를 수행하는 함수 호출
                validatePasswordMatch(editable.toString());
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasError){
                    String pw = et_pw_changepw.getText().toString();
                    ChangePassword task = new ChangePassword();
                    task.execute("http://49.247.32.169/YoWnNer/upload_pw.php",email,pw);
                    Toast.makeText(ChangePassword_activity.this, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        });
    }

//    btn_generation_password = findViewById(R.id.btn_generation_password);
    //        btn_generation_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean haserror = false;
//                String email = et_email_findpw.getText().toString();
//                email_certification = et_email_certification.getText().toString();
//                if(email.isEmpty()){
//
//                    et_email_findpw.setError("이메일을 입력하세요");
//                    haserror = true;
//                }else {
//                    et_email_findpw.setError(null);
//                }
//                if(email_certification.isEmpty()){
//                    et_email_certification.setError("인증번호를 입력하세요");
//                    haserror = true;
//                }else{
//                    et_email_certification.setError(null);
//                }
//                if(!certification){
//                    btn_email_certification_check.setError("이메일 인증을 진행하세요");
//                    haserror = true;
//                }else{
//                    btn_email_certification_check.setError(null);
//                }
//                if(!haserror){
//                    email = et_email_findpw.getText().toString();
//                    email_certification = et_email_certification.getText().toString();
//                    ChangePassword task = new ChangePassword();
//                    task.execute("http://43.200.179.127/YoWnNer/upload_pw.php",email,tempPassword);
//                    Toast.makeText(FindPassword_activity.this, "임시비밀번호 발송되었습니다.", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//
//
//
//            }
//        });
    private void validatePassword(String password) {
        // 여기에서 비밀번호 유효성 검사를 수행하고 조건에 따라 체크박스 체크 및 글자 색상 변경
        boolean isLengthValid = password.length() >= 8;
        boolean containsUpperCase = !password.equals(password.toLowerCase()) ;
        boolean containsLowerCase = !password.equals(password.toUpperCase());
        boolean containsDigit = password.matches(".*\\d.*");

        cb_pw_conf1.setChecked(containsUpperCase);
        cb_pw_conf5.setChecked(containsLowerCase);
        cb_pw_conf2.setChecked(containsDigit);
        cb_pw_conf3.setChecked(isLengthValid);
        cb_pw_conf4.setChecked(false); // 비밀번호 일치 여부는 아직 알 수 없음

        // 글자 색상 변경
        tv_pw_conf1.setTextColor(containsUpperCase ? Color.GREEN : Color.BLACK);
        tv_pw_conf5.setTextColor(containsLowerCase ? Color.GREEN : Color.BLACK);
        tv_pw_conf2.setTextColor(containsDigit ? Color.GREEN : Color.BLACK);
        tv_pw_conf3.setTextColor(isLengthValid ? Color.GREEN : Color.BLACK);
        hasError = !(containsUpperCase && containsLowerCase && containsDigit && isLengthValid);

    }

    private void validatePasswordMatch(String confirmPassword) {
        String password = et_pwconf_changepw.getText().toString();
        boolean passwordsMatch = password.equals(confirmPassword);

        cb_pw_conf4.setChecked(passwordsMatch);

        tv_pw_conf4.setTextColor(passwordsMatch ? Color.GREEN : Color.BLACK);

        hasError = !(passwordsMatch);
    }
}