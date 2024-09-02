package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login_activity extends AppCompatActivity {

    private EditText et_id, et_pw;

    private Button btn_login;

    private TextView tv_signup, tv_findpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);

        btn_login = findViewById(R.id.btn_login);

        tv_signup = findViewById(R.id.tv_signup);
        tv_findpw = findViewById(R.id.tv_findpw);

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Singup_intent = new Intent(getApplicationContext(), Singup_activity.class);
                startActivity(Singup_intent);
            }
        });
        tv_findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 찾기 버튼 클릭시 작동
                Intent Findpw_intent = new Intent(getApplicationContext(), FindPassword_activity.class);
                startActivity(Findpw_intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_id.getText().toString();
                String pw = et_pw.getText().toString();
                attemptLogin(email , pw);
            }
        });



    }

    private void attemptLogin(String email, String pw){
        Response.Listener<String> responseListenert = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);  // 서버 응답을 JSON 객체로 변환
                    boolean success = jsonObject.getBoolean("success");
                    System.out.println(success);
                    if (success) {
                        String pid = jsonObject.getString("pid");
                        System.out.println(pid);
                        String email = jsonObject.getString("email");
                        System.out.println(email);
                        String profile_image = jsonObject.getString("profile_image");
                        String username = jsonObject.getString("name");
                        String sex = jsonObject.getString("sex");
                        String identity = jsonObject.getString("identity");
                        String date = jsonObject.getString("birth");
                        String name = username.replaceAll("\\s", "");
                        // 로그인 성공 처리
                        Intent intent = new Intent(getApplicationContext() , Main_activity.class);
                        intent.putExtra("pid" , pid);
                        intent.putExtra("email" , email);
                        intent.putExtra("profile_image", profile_image);
                        intent.putExtra("name", name);
                        intent.putExtra("sex", sex);
                        intent.putExtra("identity", identity);
                        intent.putExtra("date", date);
                        startActivity(intent);
                        finish();
                    } else {
                        // 로그인 실패 처리
                        System.out.println("로그인 실패");
                        Toast.makeText(Login_activity.this , "아이디 비밀번호가 다릅니다 확인하세요.", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };
        LoginRequest loginRequest = new LoginRequest(email , pw, responseListenert);
        RequestQueue queue = Volley.newRequestQueue(Login_activity.this);
        queue.add(loginRequest);
    }


}

