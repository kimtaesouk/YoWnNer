package com.example.yownner;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimerEditListAdapter extends RecyclerView.Adapter<TimerEditListAdapter.CustomViewHolder> {
    private List<Item_timer> dataList;
    private Context context;
    private String pid;
    private String timereditname;
    private int editcolor;
    private String editcolorhex;
    private Timer_Edit_Dialog timer_edit_dialog;
    private Timer_hide_check_dialog timer_hide_check_dialog;
    int itemposition;
    private String timername;
    private String date;
    private String hidecolor;



    public TimerEditListAdapter(Context context, List<Item_timer> dataList){
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public TimerEditListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.timer_itemview_edit, parent, false);
        return new TimerEditListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerEditListAdapter.CustomViewHolder holder, int position) {
        Item_timer timeritem = dataList.get(position);
        holder.tv_timer_edit_name.setText(timeritem.getName());
        String colorCode = timeritem.getColorhex();
        int color = Color.parseColor(colorCode);
        holder.iv_timerlist_edit_color.setColorFilter(color);
        holder.tv_timer_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timername = timeritem.getName().toString();
                String colorCode = timeritem.getColorhex();
                String colothex = timeritem.getColorhex();
                pid = timeritem.getPid();
                int color = Color.parseColor(colorCode);
                timer_edit_dialog = new Timer_Edit_Dialog(v.getContext(), Write,Cancel,timername,color,colothex);
                timer_edit_dialog.show();
                timer_edit_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        timereditname = timer_edit_dialog.getEditTextContent();
                        editcolor = timer_edit_dialog.getEditColor();
                        String Editcolor = String.valueOf(editcolor);
                        editcolorhex = timer_edit_dialog.getEditColorHex();
                        timeritem.setName(timereditname);
                        timeritem.setColor(Editcolor);
                        timeritem.setColorhex(editcolorhex);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });
            }
        });
        holder.tv_timer_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pid = timeritem.getPid();
                timer_hide_check_dialog = new Timer_hide_check_dialog(v.getContext(), timer_hide_check_dialog_Ok, timer_hdie_check_dialog_Cancel);
                timer_hide_check_dialog.show();
                itemposition = holder.getAdapterPosition();
            }
        });
    }
    private View.OnClickListener timer_hide_check_dialog_Ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // OkHttpClient 인스턴스 생성
            boolean hide = true;
            OkHttpClient client = new OkHttpClient();
            // PHP 스크립트의 URL을 정의합니다.
            String url = "http://49.247.32.169/YoWnNer/timer_hide.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
            RequestBody requestBody = new FormBody.Builder()
                    .add("pid", pid) //타이머 고유번호
                    .add("hide", String.valueOf(hide)) //타이머 고유번호
                    .build();
                // URL 및 요청 바디를 사용하여 POST 요청을 생성합니다.
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            // 요청을 비동기적으로 실행합니다.
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // 실패를 처리합니다 (예: 네트워크 오류)
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // 서버에서의 응답을 처리합니다 (필요한 경우)
                    String responseBody = response.body().string();
                    Log.d("respons", responseBody);
                    // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
                }
            });
            // 다이얼로그를 닫습니다.
            timer_hide_check_dialog.dismiss();
            dataList.remove(itemposition);
            notifyItemRemoved(itemposition);
        }
    };
    private View.OnClickListener timer_hdie_check_dialog_Cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_hide_check_dialog.dismiss();
        }
    };
    private View.OnClickListener Cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_edit_dialog.dismiss();
        }
    };
    private View.OnClickListener Write = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_edit_dialog.dismiss();
            timereditname = timer_edit_dialog.getEditTextContent();
            editcolor = timer_edit_dialog.getEditColor();
            editcolorhex = timer_edit_dialog.getEditColorHex();
            System.out.println(timereditname);
            System.out.println(editcolor);
            System.out.println(editcolorhex);
            if (!timereditname.isEmpty() && timereditname != null && !editcolorhex.isEmpty() && editcolorhex != null) {
                // OkHttpClient 인스턴스 생성
                OkHttpClient client = new OkHttpClient();
                // PHP 스크립트의 URL을 정의합니다.
                String url = "http://49.247.32.169/YoWnNer/timer_edit.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                RequestBody requestBody = new FormBody.Builder()
                        .add("name", timereditname) // 작성한 내용
                        .add("color", String.valueOf(editcolor)) //컬러 숫자번호
                        .add("colorhex", editcolorhex) //컬러 문자번호
                        .add("pid", pid) //타이머 고유번호
                        .build();
                // URL 및 요청 바디를 사용하여 POST 요청을 생성합니다.
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                // 요청을 비동기적으로 실행합니다.
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        // 실패를 처리합니다 (예: 네트워크 오류)
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 서버에서의 응답을 처리합니다 (필요한 경우)
                        String responseBody = response.body().string();
                        Log.d("respons", responseBody);
                        // 서버에서 데이터를 받으면 이곳에서 처리할 수 있습니다.
                    }
                });
                // 다이얼로그를 닫습니다.
                timer_edit_dialog.dismiss();
            } else {
                timer_edit_dialog.dismiss();
            }
        }
    };
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_timer_edit_item;
        private TextView tv_timer_edit_name, tv_timer_edit, tv_timer_hide;
        private ImageView iv_timerlist_edit_color, iv_timer_move;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_timer_edit_item = itemView.findViewById(R.id.ll_timer_edit_item);
            tv_timer_edit_name = itemView.findViewById(R.id.tv_timer_edit_name);
            iv_timerlist_edit_color = itemView.findViewById(R.id.iv_timerlist_edit_color);
            tv_timer_edit = itemView.findViewById(R.id.tv_timer_edit);
            tv_timer_hide = itemView.findViewById(R.id.tv_timer_hide);
            iv_timer_move = itemView.findViewById(R.id.iv_timer_move);
        }
    }
}
