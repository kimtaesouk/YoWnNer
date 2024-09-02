package com.example.yownner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordTimeAdapter extends RecyclerView.Adapter<RecordTimeAdapter.CustomViewHolder> {
    private List<Item_time> dataList;
    private Context context;
    private String selectedDate;

    public RecordTimeAdapter(Context context, List<Item_time> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecordTimeAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recordtime_itemview, parent, false);
        return new RecordTimeAdapter.CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecordTimeAdapter.CustomViewHolder holder, int position) {
        int nextItem = position + 1; // 다른 위치로 설정
        int beforItem = position - 1;
        Item_time item = dataList.get(position);
        String name = item.getTimername();
        holder.tv_recordtime_name.setText(name);
        String stime = item.getTotalSeconds();
        if (stime != null) { // null 체크
            int time = Integer.parseInt(stime);
            int hours = time / 3600;
            int minutes = (time % 3600) / 60;
            int seconds = time % 60;
            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            holder.tv_recordtime_measurementtime.setText(formattedTime);
        }
        String starttime = item.getStartTime();
        String stoptime = item.getStopTime();
        String[] startTokens = new String[]{};
        String[] stopTokens = new String[]{};
        if (starttime != null) {
            startTokens = starttime.split("\\s+");
        }
        if (stoptime != null) {
            stopTokens = stoptime.split("\\s+");
        }

        String startTimePart = "";
        String stopTimePart = "";

        if (startTokens.length > 1) {
            startTimePart = startTokens[1];
        }

        if (stopTokens.length > 1) {
            stopTimePart = stopTokens[1];
        }

        holder.tv_start_time.setText(startTimePart);
        holder.tv_stop_time.setText(stopTimePart);

        int color = 0; // 기본값 설정

        String colorString = item.getColor();
        if (colorString != null) {
            try {
                color = Integer.parseInt(colorString);
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우에 대한 처리를 수행
            }
        }

        holder.iv_recordtime_color.setColorFilter(color);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tpid = String.valueOf(item.getpid());
                // 여기에서 다이얼로그를 표시하는 코드를 추가
                Change_Record_Dialog dialog = new Change_Record_Dialog(context, item );
                String nextstarttime = "24:00:00";
                String beforestoptime = "00:00:00";
                dialog.show();
                if (nextItem < dataList.size()) {
                    Item_time otherItem = dataList.get(nextItem);
                    String next = String.valueOf(otherItem.getStartTime());
                    String[] Start = next.split("\\s+");
                    nextstarttime = Start[1];
                }

                if (beforItem >= 0) {  // Check for a valid index
                    Item_time otherItem2 = dataList.get(beforItem);
                    String before = String.valueOf(otherItem2.getStopTime());
                    String[] Before = before.split("\\s+");
                    beforestoptime = Before[1];
                }
                dialog.SetEditTextContent(starttime,stoptime,tpid,nextstarttime,beforestoptime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    private void showMyDialog(Item_time item) {
        // 다이얼로그를 생성

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_start_time, tv_stop_time, tv_recordtime_name, tv_recordtime_measurementtime;
        private ImageView iv_recordtime_color;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_stop_time = itemView.findViewById(R.id.tv_stop_time);
            tv_recordtime_name = itemView.findViewById(R.id.tv_recordtime_name);
            tv_recordtime_measurementtime = itemView.findViewById(R.id.tv_recordtime_measurementtime);
            iv_recordtime_color = itemView.findViewById(R.id.iv_recordtime_color);

        }
    }
}
