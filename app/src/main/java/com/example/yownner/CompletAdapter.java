package com.example.yownner;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletAdapter extends RecyclerView.Adapter<CompletAdapter.CustomViewHolder> {
    private List<Item_memo> dataList;
    private Context context;
    private OnCheckBoxClickListener checkBoxClickListener;




    public CompletAdapter(Context context, List<Item_memo> dataList) {
        this.context = context;
        this.dataList = dataList;
        sortDataListOnUiThread();
    }
    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(Item_memo memoItem);
    }
    public void setOnCheckBoxClickListener(OnCheckBoxClickListener listener) {
        this.checkBoxClickListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, tv_text_date;
        private LinearLayout ll_memo_item;
        private CheckBox checkBox; // 체크박스 추가
        private boolean isChecked = false;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title);
            tv_text_date = itemView.findViewById(R.id.tv_text_date);
            ll_memo_item = itemView.findViewById(R.id.ll_memo_item);
            checkBox = itemView.findViewById(R.id.cb_memo);
//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }

    private void sendPidToServer(String pid, boolean isChecked) {
        // Retrofit 인스턴스 생성
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        // 서버로 PID를 전송하기 위한 요청 생성
        Call<Void> call = service.sendPidToServer(pid, isChecked);

        // 비동기적으로 요청 전송
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // 서버 응답 처리
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "서버 전송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @NonNull
    @Override
    public CompletAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.memo_itemview2, parent, false);
        return new CompletAdapter.CustomViewHolder(view);
    }
    public void sortDataList() {
        Collections.sort(dataList, new Comparator<Item_memo>() {
            @Override
            public int compare(Item_memo item1, Item_memo item2) {
                // 두 아이템의 월과 일을 비교하여 오름차순으로 정렬
                int month1 = Integer.parseInt(item1.getMonth());
                int day1 = Integer.parseInt(item1.getDay());
                int month2 = Integer.parseInt(item2.getMonth());
                int day2 = Integer.parseInt(item2.getDay());

                if (month1 < month2) {
                    return -1;
                } else if (month1 > month2) {
                    return 1;
                } else {
                    // 월이 같은 경우 일을 비교
                    return Integer.compare(day1, day2);
                }
            }
        });
        notifyDataSetChanged();
    }

    public void sortDataListOnUiThread() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                sortDataList();
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull CompletAdapter.CustomViewHolder holder, int position) {
        Item_memo memoItem = dataList.get(position);
        // 타이틀 설정
        holder.txtTitle.setText(dataList.get(position).getMemo());
        String month = dataList.get(position).getMonth();
        String day = dataList.get(position).getDay();
        String date = month + "월" + day + "일";
        holder.tv_text_date.setText(date);
        if (memoItem.getStatus() == 2) {
            holder.isChecked = true;
            memoItem.setChecked(true);
            holder.checkBox.setChecked(true);
            holder.ll_memo_item.setBackgroundResource(R.drawable.wradius);
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.isChecked = false;
            memoItem.setChecked(false);
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        // 체크박스 상태 업데이트
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Item_memo memoItem = dataList.get(position);
                    String pid = memoItem.getPid();
                    boolean isChecked = memoItem.isChecked();

                    // 체크 여부를 토글
                    memoItem.setChecked(!isChecked);

                    // 가로줄과 배경 설정
                    if (memoItem.isChecked()) {
                        holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.ll_memo_item.setBackgroundResource(R.drawable.wradius);
                        memoItem.setStatus(2);
                    } else {
                        holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        holder.ll_memo_item.setBackgroundResource(0);
                        memoItem.setStatus(1);
                    }
                    // 체크 여부를 토글
                    holder.checkBox.setChecked(true);
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    // 상태 서버로 전송
                    sendPidToServer(pid, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
