package com.example.yownner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Singup_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Button btn_signup_email_confire, btn_email_certification_check, btn_Singup;

    private ImageView back_sign_so_login, Iv_profile_image_singup;

    private EditText et_email_signup, et_email_certification,et_pw_signup, et_pwcheck_signup, et_name_signup, et_birth_signup;

    private TextView tv_check_email, tv_certification_timer, tv_check_pw;

    private Spinner sn_sex_signup, sn_identity_signup;

    private CountDownTimer timer;

    private MyDialog Dialog_Listener;

    boolean emailconfig = false;

    boolean certification = false;

    String emailCode = null ;

    String profile_image = null ;

    private static final int GALLERY_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        Iv_profile_image_singup = findViewById(R.id.Iv_profile_image_singup);
        Iv_profile_image_singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceProfileDialog();
            }
        });

        et_email_signup = findViewById(R.id.et_email_signup);
        et_email_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력 전 동작 (사용하지 않음)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력 중 동작 (사용하지 않음)
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력 후 동작
                checkEmailValidity();
            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        btn_signup_email_confire = findViewById(R.id.btn_signup_email_confire);
        btn_signup_email_confire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email_signup.getText().toString();
                if(!email.isEmpty()){
                    et_email_signup.setError(null); // 테두리 초기화
                    CheckEmailTask task = new CheckEmailTask(getApplicationContext());
                    boolean isEmailValid = false;

                    try {
                        String response = task.execute(email).get(); // 실행 결과를 가져옴

                        // response 값을 이용하여 중복 확인 결과를 처리
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        isEmailValid = success; // success 값을 isEmailValid로 설정

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isEmailValid) {
                        emailconfig = true;
                        showConfirmationDialog();
                    } else {
                        emailconfig = false;
                    }
                }else{
                        et_email_signup.setError("이메일을 입력하세요");
                }

            }
        });
        tv_certification_timer = findViewById(R.id.tv_certification_timer);
        tv_check_email = findViewById(R.id.tv_check_email);
        et_email_certification = findViewById(R.id. et_email_certification);

        btn_email_certification_check = findViewById(R.id.btn_email_certification_check);
        btn_email_certification_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String certification_code = et_email_certification.getText().toString();
                if(!emailconfig){
                    btn_signup_email_confire.setError("중복검사를 해주세요");
                }else{
                    btn_signup_email_confire.setError(null); // 테두리 초기화
                }
                if(certification_code.isEmpty()){
                    et_email_certification.setError("인증번호 입력하세요");
                }else if(!certification_code.isEmpty()){
                    if(certification_code.equals(emailCode)){
                        Toast.makeText(Singup_activity.this , "인증 완료되었습니다" , Toast.LENGTH_SHORT).show();
                        certification = true;
                        stopTimer(); // 인증 완료 시 타이머 중지
                    }else if(!certification_code.equals(emailCode)){
                        et_email_certification.setError("인증번호가 다릅니다");
                    }
                }


            }
        });

        tv_check_pw = findViewById(R.id.tv_check_pw);
        et_pw_signup = findViewById(R.id.et_pw_signup);
        et_pw_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPwValidity();
            }
        });


        back_sign_so_login = findViewById(R.id.back_sign_so_login);
        back_sign_so_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(getApplicationContext() , Login_activity.class);
                startActivity(back_intent);
                finish();
            }
        });

        et_pwcheck_signup = findViewById(R.id.et_pwcheck_signup);
        et_name_signup = findViewById(R.id.et_name_signup);
        et_birth_signup = findViewById(R.id.et_birth_signup);

        sn_sex_signup = findViewById(R.id.sn_sex_signup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sn_sex_signup.setAdapter(adapter);
        sn_sex_signup.setOnItemSelectedListener(this);

        sn_identity_signup = findViewById(R.id.sn_identity_signup);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this , R.array.identity, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sn_identity_signup.setAdapter(adapter1);
        sn_identity_signup.setOnItemSelectedListener(this);
        btn_Singup = findViewById(R.id.btn_Singup);
        btn_Singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email_signup.getText().toString();
                String pw = et_pw_signup.getText().toString();
                String pwcheck = et_pwcheck_signup.getText().toString();
                String name = et_name_signup.getText().toString();
                String birth = et_birth_signup.getText().toString();
                String sex = sn_sex_signup.getSelectedItem().toString();
                String identity = sn_identity_signup.getSelectedItem().toString();
                boolean hasError = false;
//                System.out.println("이메일 : " + email + "비밀번호" + pw + "비밀번호 확인" + pwcheck + "이름" + name + "생년월일" + birth + "성별" + sex + "신분" + identity
//                        + "이메일 중복검사 여부" + emailconfig + "이메일 인증 여부" + certification);

                    if(email.isEmpty()){
                        et_email_signup.setError("이메일을 입력하세요");
                        hasError = true;
                    } else{
                        et_email_signup.setError(null); // 테두리 초기화
                    }
                if(pw.isEmpty()){
                    et_pw_signup.setError("비밀번호를 입력하세요");
                    hasError = true;
                } else{
                    et_pw_signup.setError(null); // 테두리 초기화
                }
                if(pwcheck.isEmpty()){
                    et_pwcheck_signup.setError("비밀번호 확인을 입력하세요");
                    hasError = true;
                } else{
                    et_pwcheck_signup.setError(null); // 테두리 초기화
                }
                if(name.isEmpty()){
                    et_name_signup.setError("이름을 입력하세요");
                    hasError = true;
                } else{
                    et_name_signup.setError(null); // 테두리 초기화
                }
                if(birth.isEmpty()){
                    et_birth_signup.setError("생년월일을 입력하세요");
                    hasError = true;
                } else {
                    et_birth_signup.setError(null); // 테두리 초기화
                }
                if (sex.isEmpty() || sex.equals("성별을 선택해주세요")) {
                    // "성별 선택"이라는 값이 스피너에서 선택되지 않았을 경우 에러 표시
                    TextView errorText = (TextView) sn_sex_signup.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED); // 에러 텍스트 색상 변경
                    hasError = true;
                } else {
                    // 선택된 경우 에러 텍스트 초기화
                    TextView errorText = (TextView) sn_sex_signup.getSelectedView();
                    errorText.setError(null);
                    errorText.setTextColor(Color.BLACK); // 텍스트 색상 원래대로 변경
                }
                if (identity.isEmpty() || identity.equals("신분을 선택해주세요")) {
                    // "성별 선택"이라는 값이 스피너에서 선택되지 않았을 경우 에러 표시
                    TextView errorText = (TextView) sn_identity_signup.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED); // 에러 텍스트 색상 변경
                    hasError = true;
                } else {
                    // 선택된 경우 에러 텍스트 초기화
                    TextView errorText = (TextView) sn_identity_signup.getSelectedView();
                    errorText.setError(null);
                    errorText.setTextColor(Color.BLACK); // 텍스트 색상 원래대로 변경
                }
                if(!emailconfig){
                    btn_signup_email_confire.setError("중복검사를 해주세요");
                    hasError = true;
                }else{
                    btn_signup_email_confire.setError(null);
                }
                if(!certification){
                    btn_email_certification_check.setError("중복검사를 해주세요");
                    hasError = true;
                }else{
                    btn_email_certification_check.setError(null);
                }
                if (profile_image == null || profile_image.isEmpty()) {
                    profile_image = "null";
                }

                if(!hasError){
                    InsertData task = new InsertData();
                    task.execute("http://49.247.32.169/YoWnNer/sign_up.php",email,pw,name,birth,sex,identity,profile_image);
                    Toast.makeText(Singup_activity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // 이메일 유효성 검사 함수
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.(com|net)";
        return email.matches(emailPattern);
    }

    private boolean isValidPw(String password) {
        String PwPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return password.matches(PwPattern);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("인증번호 발송");
        builder.setMessage("인증번호를 발송하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 인증번호 발송 메소드를 실행하는 부분
                if(emailconfig) {
                    SendEmail mailServer = new SendEmail();
                    mailServer.sendSecurityCode(getApplicationContext(), et_email_signup.getText().toString());
                    emailCode = mailServer.emailCode;
                    System.out.println(emailCode);

                    // 3분 타이머 실행
                    startTimer(3 * 60 * 1000); // 3분을 밀리초로 변환하여 전달
                }else{
                    Toast.makeText(Singup_activity.this, "이메일 중복검사를 진행해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼 클릭 시 아무 동작 없음
            }
        });
        builder.show();
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
            profile_image = "null";
            Dialog_Listener.dismiss(); // 다이얼로그를 종료합니다.
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

                profile_image = saveBitmapAsJPEG(circularBitmap);
                System.out.println(profile_image);
                Iv_profile_image_singup.setImageBitmap(circularBitmap);
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

            profile_image = saveBitmapAsJPEG(circularBitmap);

            // 이미지뷰에 Bitmap으로 이미지를 입력
            Iv_profile_image_singup.setImageBitmap(circularBitmap);
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
                emailCode = null;
                tv_certification_timer.setText("");
            }
        }.start();
    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            tv_certification_timer.setText("인증 완료"); // 타이머 중지 후 텍스트뷰 초기화
            tv_certification_timer.setTextColor(Color.parseColor("#00FF00"));
        }
    }

    private void checkEmailValidity() {
        String email = et_email_signup.getText().toString().trim();

        if (isValidEmail(email)) {
            tv_check_email.setVisibility(View.VISIBLE);
            tv_check_email.setText("사용가능한 이메일");
            tv_check_email.setTextColor(Color.parseColor("#00FF00"));
        } else {
            tv_check_email.setVisibility(View.VISIBLE);
            tv_check_email.setText("이메일 형식이 다릅니다.");
            tv_check_email.setTextColor(Color.parseColor("#FB0000"));
        }

    }

    private void checkPwValidity() {
        String password = et_pw_signup.getText().toString().trim();
        if (isValidPw(password)) {
            tv_check_pw.setVisibility(View.VISIBLE);
            tv_check_pw.setText("사용가능한 비밀번호");
            tv_check_pw.setTextColor(Color.parseColor("#00FF00"));
        } else {
            tv_check_pw.setVisibility(View.VISIBLE);
            tv_check_pw.setText("비밀번호 형식이 다릅니다.");
            tv_check_pw.setTextColor(Color.parseColor("#FB0000"));
        }
    }

}