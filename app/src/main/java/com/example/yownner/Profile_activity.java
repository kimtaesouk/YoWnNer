package com.example.yownner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class Profile_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ImageView Iv_profile_image_profile, back_profile_so_main;

    EditText et_name_profile, et_birth_profile;

    Spinner sn_sex_profile, sn_identity_profile;
    Button btn_change_profile;

    private MyDialog Dialog_Listener;

    String ch_profile_image = null ;

    private static final int GALLERY_REQUEST_CODE = 1;
    boolean isProfileInfoChanged = false; // 추가: 프로필 정보 변경 여부

    String profile_image = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String pid = intent.getStringExtra("pid");
        String email = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String sex = intent.getStringExtra("sex");
        String identity = intent.getStringExtra("identity");
        profile_image = intent.getStringExtra("profile_image");
        attemptLogin(pid);
        ch_profile_image = profile_image;
        btn_change_profile = findViewById(R.id.btn_change_profile);
        btn_change_profile.setEnabled(false); // 초기에 버튼 비활성화
        back_profile_so_main = findViewById(R.id.back_profile_so_main);
        back_profile_so_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateProfileInfoChanged();

        Iv_profile_image_profile = findViewById(R.id.Iv_profile_image_profile);

        Iv_profile_image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceProfileDialog();
            }
        });
        et_name_profile = findViewById(R.id.et_name_profile);
        et_name_profile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newName = et_name_profile.getText().toString();
                boolean nameChanged = !name.equals(newName);
                System.out.println(name +"/"+newName);
                System.out.println(nameChanged);
                if (nameChanged) {
                    isProfileInfoChanged = true;
                    btn_change_profile.setEnabled(true); // 버튼 활성화

                } else {
                    isProfileInfoChanged = false;
                    btn_change_profile.setEnabled(false); // 버튼 비활성화
                }
            }
        });
        et_birth_profile = findViewById(R.id.et_birth_profile);
        et_birth_profile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newBirth = et_birth_profile.getText().toString();
                // 변경 여부 확인
                boolean birthChanged = !date.equals(newBirth);
                System.out.println(date +"/"+newBirth);
                System.out.println(birthChanged);
                if (birthChanged) {
                    isProfileInfoChanged = true;
                    btn_change_profile.setEnabled(true); // 버튼 활성화

                } else {
                    isProfileInfoChanged = false;
                    btn_change_profile.setEnabled(false); // 버튼 비활성화
                }
            }
        });
        sn_identity_profile = findViewById(R.id.sn_identity_profile);
        if (isProfileInfoChanged) {
            btn_change_profile.setEnabled(true); // 프로필 정보가 변경되면 버튼 활성화
        }
        sn_sex_profile = findViewById(R.id.sn_sex_profile);
        sn_sex_profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String newSex = sn_sex_profile != null ? sn_sex_profile.getSelectedItem().toString() : "";
                boolean sexChanged = !sex.equals(newSex);
                System.out.println(sex +"/"+newSex);
                System.out.println(sexChanged);
                if(sexChanged) {
                    isProfileInfoChanged = true;
                    btn_change_profile.setEnabled(true); // 버튼 활성화
                }else{
                    isProfileInfoChanged = false;
                    btn_change_profile.setEnabled(false); // 버튼 비활성화
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        sn_identity_profile = findViewById(R.id.sn_identity_profile);
        sn_identity_profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String newIdentity = sn_identity_profile != null ? sn_identity_profile.getSelectedItem().toString() : "";
                boolean identityChanged = !identity.equals(newIdentity);
                System.out.println(identity +"/"+newIdentity);
                System.out.println(identityChanged);
                if(identityChanged) {
                    isProfileInfoChanged = true;
                    btn_change_profile.setEnabled(true); // 버튼 활성화
                }else{
                    isProfileInfoChanged = false;
                    btn_change_profile.setEnabled(false); // 버튼 비활성화
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeData task = new ChangeData();
                String name = et_name_profile.getText().toString();
                String birth = et_birth_profile.getText().toString();
                String sex = sn_sex_profile.getSelectedItem().toString();
                String identity = sn_identity_profile.getSelectedItem().toString();
                if(ch_profile_image.isEmpty()){
                    ch_profile_image = "null";
                }
                System.out.println(name + "/" + date + "/" +sex + "/" +identity + "/" +ch_profile_image + "/" + birth);
                task.execute("http://49.247.32.169/YoWnNer/profile_change.php",pid,name,birth,sex,identity,ch_profile_image);
                Toast.makeText(Profile_activity.this, "프로필 변경 성공", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void attemptLogin(String pid){
        Response.Listener<String> responseListenert = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);  // 서버 응답을 JSON 객체로 변환
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        profile_image = jsonObject.getString("profile_image");
                        String username = jsonObject.getString("name");
                        String sex = jsonObject.getString("sex");
                        String identity = jsonObject.getString("identity");
                        String birth = jsonObject.getString("birth");
                        String name = username.replaceAll("\\s", "");

                        // 리소스 이미지의 경로를 Uri로 생성
                        if (profile_image == null || profile_image.isEmpty()) {
                            // 기본 이미지를 설정해줌
                            Iv_profile_image_profile.setImageResource(R.drawable.icon_nomalprofile);
                        } else {
                            Bitmap bm = BitmapFactory.decodeFile(profile_image);

                            if (bm != null) {
                                int diameterInDp = 35;
                                int diameterInPx = (int) (diameterInDp * getResources().getDisplayMetrics().density);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, diameterInPx, diameterInPx, true);

                                Bitmap circularBitmap = getCircularBitmap(scaledBitmap);

                                Iv_profile_image_profile.setImageBitmap(circularBitmap);
                            } else {
                                // 이미지 로딩에 실패한 경우 기본 이미지 설정
                                Iv_profile_image_profile.setImageResource(R.drawable.icon_nomalprofile);
                            }
                        }

                        et_name_profile.setText(name);
                        et_birth_profile.setText(birth);
                        // 성별 (sex) 스피너 초기화 및 선택값 설정
                        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(Profile_activity.this, R.array.sex, android.R.layout.simple_spinner_item);
                        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sn_sex_profile.setAdapter(sexAdapter);
                        int selectedSexPosition = sexAdapter.getPosition(sex); // "성별" 값을 이용해 초기 선택값의 위치를 가져옴
                        sn_sex_profile.setSelection(selectedSexPosition); // 해당 위치를 선택하도록 설정
                        // 신분 (identity) 스피너 초기화 및 선택값 설정
                        ArrayAdapter<CharSequence> identityAdapter = ArrayAdapter.createFromResource(Profile_activity.this, R.array.identity, android.R.layout.simple_spinner_item);
                        identityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sn_identity_profile.setAdapter(identityAdapter);
                        int selectedIdentityPosition = identityAdapter.getPosition(identity); // "identity" 값을 이용해 초기 선택값의 위치를 가져옴
                        sn_identity_profile.setSelection(selectedIdentityPosition); // 해당 위치를 선택하도록 설정

                    } else {
                        // 로그인 실패 처리
                        System.out.println("프로필 조회 실패");
                        Toast.makeText(Profile_activity.this , "존재하지 않는 회원입니다", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };
        ProfileRequest profileRequest = new ProfileRequest(pid, responseListenert);
        RequestQueue queue = Volley.newRequestQueue(Profile_activity.this);
        queue.add(profileRequest);
    }
    private void updateProfileInfoChanged() {
        // 프로필 이미지 변경 여부 확인
        boolean profileImageChanged = (ch_profile_image != null);
        System.out.println(profileImageChanged);
        // 하나라도 변경되었으면 버튼을 활성화, 아니면 비활성화
        if (profileImageChanged) {
            isProfileInfoChanged = true;
            btn_change_profile.setEnabled(true); // 버튼 활성화

        } else {
            isProfileInfoChanged = false;
            btn_change_profile.setEnabled(false); // 버튼 비활성화
        }
    }

    // 프로필 정보 변경 여부 업데이트 메서드
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void showChoiceProfileDialog() {
        Dialog_Listener = new MyDialog(this,Cammara,gallery,nomal,"프로필 이미지");
        Dialog_Listener.show();
    }

    private View.OnClickListener Cammara = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);
        }
    };

    private View.OnClickListener gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    };
    private View.OnClickListener nomal = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Iv_profile_image_profile.setImageResource(R.drawable.icon_nomalprofile);
            ch_profile_image = "null"; // 이미지 경로를 빈 문자열로 설정
            Dialog_Listener.dismiss(); // 다이얼로그를 종료합니다.
            // 프로필 이미지가 변경되었으므로 버튼을 활성화
            updateProfileInfoChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                // 이미지를 원 모양으로 잘라서 적용
                Bitmap circularBitmap = getCircularBitmap(bitmap, 350);

                ch_profile_image = saveBitmapAsJPEG(circularBitmap);
                System.out.println(ch_profile_image);
                Iv_profile_image_profile.setImageBitmap(circularBitmap);

                // 프로필 이미지가 변경되었으므로 버튼을 활성화
                updateProfileInfoChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if(requestCode == 0 && resultCode == RESULT_OK) {
            // Bundle로 데이터를 입력
            Bundle extras = data.getExtras();

            // Bitmap으로 컨버전
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bitmap circularBitmap = getCircularBitmap(imageBitmap, 100);

            ch_profile_image = saveBitmapAsJPEG(circularBitmap);

            // 이미지뷰에 Bitmap으로 이미지를 입력
            Iv_profile_image_profile.setImageBitmap(circularBitmap);

            // 프로필 이미지가 변경되었으므로 버튼을 활성화
            updateProfileInfoChanged();
        }
    }


    private Bitmap getCircularBitmap(Bitmap bitmap, int diameter) {
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 이미지를 원형 중앙으로 자르기
        int left = (bitmap.getWidth() - diameter) / 2;
        int top = (bitmap.getHeight() - diameter) / 2;
        Rect srcRect = new Rect(left, top, left + diameter, top + diameter);

        canvas.drawBitmap(bitmap, srcRect, rect, paint);

        return output;
    }

    public String saveBitmapAsJPEG(Bitmap bitmap){
        File storageDir = getExternalFilesDir(null);

        // 현재 시간을 기반으로 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File imageFile;
        try {
            imageFile = File.createTempFile(
                    imageFileName,  // 파일 이름
                    ".jpg",         // 파일 확장자
                    storageDir      // 파일이 저장될 디렉토리
            );

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
