package com.example.yownner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yownner.Fragment.fragcallender;
import com.example.yownner.Fragment.fraghome;
import com.example.yownner.Fragment.fragmanager;
import com.example.yownner.Fragment.fragstudy;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class Main_activity extends AppCompatActivity {
    private ImageView Iv_profile_image_main;
    BottomNavigationView bottomNavigationView;
    private String TAG = "메인";
    Fragment fragment_home, fragment_callender, fragment_manager, fragment_study;
    String pid, email, name,sex, identity, date, profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment_home = new fraghome();
        // fraghome 프래그먼트를 찾아 setEmail 메소드로 email 값을 전달
        fragment_callender = new fragcallender();
        fragment_manager = new fragmanager();
        fragment_study = new fragstudy();
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        email = intent.getStringExtra("email");
        profile_image = intent.getStringExtra("profile_image");
        name = intent.getStringExtra("name");
        sex = intent.getStringExtra("sex");
        identity = intent.getStringExtra("identity");
        date = intent.getStringExtra("date");
        attemptLogin(pid);
        fraghome fragmentHome = (fraghome) getSupportFragmentManager().findFragmentById(R.id.main_layout);
        Bundle bundle = new Bundle();
        bundle.putString("pid", pid);
        fragment_home.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitNowAllowingStateLoss();
        if (fragmentHome != null) {
            fragmentHome.setPid(pid);
        }
        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Log.i(TAG, "home 들어옴");
                        bundle.putString("pid" , pid);
                        fragment_home.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitNowAllowingStateLoss();
                        return true;

                    case R.id.calendar:
                        Log.i(TAG, "calender 들어옴");
                        bundle.putString("pid" , pid);
                        fragment_callender.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_callender).commitNowAllowingStateLoss();
                        return true;
                    case R.id.stady:
                        Log.i(TAG, "staudy 들어옴");
                        bundle.putString("pid" , pid);
                        fragment_study.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_study).commitNowAllowingStateLoss();
                        return true;

                    case R.id.manager:
                        Log.i(TAG, "manager 들어옴");
                        bundle.putString("pid" , pid);
                        fragment_manager.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_manager).commitNowAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });

        Iv_profile_image_main = findViewById(R.id.Iv_profile_image_main);
        Iv_profile_image_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , Profile_activity.class);
                intent.putExtra("pid" , pid);
                intent.putExtra("email" , email);
                intent.putExtra("profile_image" , profile_image);
                intent.putExtra("name" , name);
                intent.putExtra("sex" , sex);
                intent.putExtra("identity" , identity);
                intent.putExtra("date" , date);
                startActivity(intent);

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        sex = intent.getStringExtra("sex");
        identity = intent.getStringExtra("identity");
        profile_image = intent.getStringExtra("profile_image");
        attemptLogin(pid);
    }

    private void attemptLogin(String pid){
        Response.Listener<String> responseListenert = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);  // 서버 응답을 JSON 객체로 변환
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String profile_image = jsonObject.getString("profile_image");
                        String username = jsonObject.getString("name");
                        String sex = jsonObject.getString("sex");
                        String identity = jsonObject.getString("identity");
                        String birth = jsonObject.getString("birth");
                        String name = username.replaceAll("\\s", "");

                        // 리소스 이미지의 경로를 Uri로 생성
                        if (profile_image == null || profile_image.isEmpty()) {
                            // 기본 이미지를 설정해줌
                            Iv_profile_image_main.setImageResource(R.drawable.icon_nomalprofile);
                        } else {
                            Bitmap bm = BitmapFactory.decodeFile(profile_image);

                            if (bm != null) {
                                int diameterInDp = 35;
                                int diameterInPx = (int) (diameterInDp * getResources().getDisplayMetrics().density);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, diameterInPx, diameterInPx, true);

                                Bitmap circularBitmap = getCircularBitmap(scaledBitmap);

                                Iv_profile_image_main.setImageBitmap(circularBitmap);
                            } else {
                                // 이미지 로딩에 실패한 경우 기본 이미지 설정
                                Iv_profile_image_main.setImageResource(R.drawable.icon_nomalprofile);
                            }
                        }


                    } else {
                        // 로그인 실패 처리
                        System.out.println("프로필 조회 실패");
                        Toast.makeText(Main_activity.this , "존재하지 않는 회원입니다", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };
        ProfileRequest profileRequest = new ProfileRequest(pid, responseListenert);
        RequestQueue queue = Volley.newRequestQueue(Main_activity.this);
        queue.add(profileRequest);
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int diameter = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 원형 이미지 그리기
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}