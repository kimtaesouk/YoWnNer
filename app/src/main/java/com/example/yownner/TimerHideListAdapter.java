package com.example.yownner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimerHideListAdapter extends RecyclerView.Adapter<TimerHideListAdapter.CustomViewHolder>{
    private List<Item_timer> hidedataList;
    private Context context;
    private String pid;
    private int itemposition;
    private Timer_delet_check_dialog timer_delet_check_dialog;
    public TimerHideListAdapter(Context context, List<Item_timer> dataList){
        this.context = context;
        this.hidedataList = dataList;
    }
    @NonNull
    @Override
    public TimerHideListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.timer_itemview_hide, parent, false);
        return new TimerHideListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerHideListAdapter.CustomViewHolder holder, int position) {
        Item_timer timeritem = hidedataList.get(position);
        holder.tv_timer_hide_name.setText(timeritem.getName());
        String colorCode = timeritem.getColorhex();
        pid = timeritem.getPid();
        int color = Color.parseColor(colorCode);
        holder.iv_timerlist_hide_color.setColorFilter(color);
        holder.tv_timer_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OkHttpClient 인스턴스 생성
                boolean hide = false;
                OkHttpClient client = new OkHttpClient();
                // PHP 스크립트의 URL을 정의합니다.
                String url = "http://49.247.32.169/YoWnNer/timer_hide.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
                RequestBody requestBody = new FormBody.Builder()
                        .add("pid", pid) //사용자 고유번호
                        .add("hide", String.valueOf(hide)) //사용자 고유번호
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
                hidedataList.remove(itemposition);
                notifyItemRemoved(itemposition);
            }
        });

        holder.tv_timer_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pid = timeritem.getPid();
                itemposition = holder.getAdapterPosition();
                timer_delet_check_dialog = new Timer_delet_check_dialog(v.getContext(), timer_delet_check_dialog_Ok, timer_delet_check_dialog_Cancel);
                timer_delet_check_dialog.show();
            }
        });
    }
    private View.OnClickListener timer_delet_check_dialog_Ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_delet_check_dialog.dismiss();
            // OkHttpClient 인스턴스 생성
            OkHttpClient client = new OkHttpClient();
            // PHP 스크립트의 URL을 정의합니다.
            String url = "http://49.247.32.169/YoWnNer/timer_delete.php"; // 여기에 PHP 스크립트의 URL을 입력하세요.
            RequestBody requestBody = new FormBody.Builder()
                    .add("pid", pid) //사용자 고유번호
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
            hidedataList.remove(itemposition);
            notifyItemRemoved(itemposition);
        }
    };
    private View.OnClickListener timer_delet_check_dialog_Cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timer_delet_check_dialog.dismiss();
        }
    };

    @Override
    public int getItemCount() {
        return hidedataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_timer_edit_item;
        private TextView tv_timer_hide_name, tv_timer_show, tv_timer_delete;
        private ImageView iv_timerlist_hide_color, iv_timer_hide_move;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_timer_hide_name = itemView.findViewById(R.id.tv_timer_hide_name);
            tv_timer_show = itemView.findViewById(R.id.tv_timer_show);
            tv_timer_delete = itemView.findViewById(R.id.tv_timer_delete);
            iv_timerlist_hide_color = itemView.findViewById(R.id.iv_timerlist_hide_color);
            iv_timer_hide_move = itemView.findViewById(R.id.iv_timer_hide_move);
        }
    }
}
