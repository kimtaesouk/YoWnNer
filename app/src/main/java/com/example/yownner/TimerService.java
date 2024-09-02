package com.example.yownner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class TimerService extends Service {
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private boolean isRunning = true;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                seconds++;
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                    if (minutes == 60) {
                        minutes = 0;
                        hours++;
                    }
                }
                Log.d("TimerService", String.format("%02d:%02d:%02d", hours, minutes, seconds));

                // 시간 업데이트를 액티비티에 브로드캐스트
                Intent intent = new Intent("TIMER_UPDATE");
                intent.putExtra("hours", hours);
                intent.putExtra("minutes", minutes);
                intent.putExtra("seconds", seconds);
                sendBroadcast(intent);
                // 업데이트 노티피케이션 메소드 호출
                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                updateNotification(formattedTime);

                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    private static final String CHANNEL_ID = "timer_channel";
    private static final CharSequence CHANNEL_NAME = "Timer Service Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void updateNotification(String formattedTime) {
        Intent timerActivityIntent = new Intent(this, Time_measurement_activity.class);
        PendingIntent timerActivityPendingIntent = PendingIntent.getActivity(this, 0, timerActivityIntent, 0);
        // 노티피케이션을 업데이트합니다.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icons) // 작은 아이콘을 설정
                .setContentTitle("타이머 실행 중")
                .setContentText("측정 중인 시간: " + formattedTime)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(timerActivityPendingIntent); // 클릭 시 엑티비티로 이동



        // 노티피케이션을 업데이트
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("START_TIMER")) {
                    String measuretime = intent.getStringExtra("measuretime");
                    if (measuretime != null) {
                        int measurement = Integer.parseInt(measuretime);

                        int measurementhours = measurement / 3600;
                        int measuremenminutes = (measurement % 3600) / 60;
                        int measuremenseconds = measurement % 60;

                        hours += measurementhours;
                        minutes += measuremenminutes;
                        seconds += measuremenseconds;
                    }
                    startTimer();
                } else if (action.equals("STOP_TIMER")) {
                    stopTimer();
                    stopSelf();
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void startTimer() {
        isRunning = true;
        timerHandler.postDelayed(timerRunnable, 1000);

        // 타이머를 시작할 때 노티피케이션을 업데이트합니다.
        updateNotification("00:00:00");
    }

    private void stopTimer() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
