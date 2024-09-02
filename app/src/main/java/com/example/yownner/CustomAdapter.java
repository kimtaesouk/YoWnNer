package com.example.yownner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements Change_Work_Dialog.OnMemoChangedListener, ItemTouchHelperAdapter {

    private List<Item_memo> dataList;
    private Context context;
    int position;
    private ItemTouchHelper itemTouchHelper;

    public CustomAdapter(Context context, List<Item_memo> dataList, ItemTouchHelper itemTouchHelper) {
        this.context = context;
        this.dataList = dataList;
        this.itemTouchHelper = itemTouchHelper; // ItemTouchHelper 설정
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private boolean isChecked = false;
        private TextView txtTitle;
        private CheckBox checkBox; // 체크박스 추가

        LinearLayout ll_memo_item;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            txtTitle = mView.findViewById(R.id.title);
            checkBox = mView.findViewById(R.id.cb_memo); // 체크박스 초기화
            ll_memo_item = mView.findViewById(R.id.ll_memo_item);

            RecyclerView recyclerView = mView.findViewById(R.id.rv_work); // 이 부분에서 R.id.recyclerView는 XML 레이아웃에서 정의한 RecyclerView의 ID입니다.

            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(CustomAdapter.this);
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView); // recyclerView는 RecyclerView 객체의 참조입니다.

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    position = getAdapterPosition(); // 현재 아이템의 포지션 가져오기
                    Item_memo memoItem = dataList.get(position);
                    String memo = memoItem.getMemo();
                    String tpid = memoItem.getPid();
                    // 다이얼로그를 생성하고 어댑터를 전달
                    Change_Work_Dialog dialog = new Change_Work_Dialog(context, CustomAdapter.this); // 어댑터 전달
                    // 다이어로그 닫힐 때 리사이클러뷰를 갱신하는 리스너 추가
                    dialog.show();
                    dialog.SetEditTextContent(memo, tpid);
                    // 다이어로그 닫힐 때 리사이클러뷰를 갱신하는 리스너 추가
                    return true; // 롱 클릭 이벤트를 소비함
                }
            });
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item_memo memoItem = dataList.get(getAdapterPosition());
                    String pid = memoItem.getPid();
                    boolean isChecked = memoItem.isChecked();
                    // 체크 여부를 토글
                    memoItem.setChecked(!isChecked);
                    if (memoItem.isChecked()) {
                        // 체크됐을 때
                        checkBox.setChecked(true);
                        txtTitle.setPaintFlags(txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // 가로줄 추가
                        ll_memo_item.setBackgroundResource(R.drawable.wradius);
                        memoItem.setStatus(2); // 스테이터스를 2로 설정
                    } else {
                        // 체크 해제됐을 때
                        checkBox.setChecked(false);
                        txtTitle.setPaintFlags(txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // 가로줄 제거
                        ll_memo_item.setBackgroundResource(R.drawable.radius);
                        memoItem.setStatus(1); // 스테이터스를 1로 설정
                    }
                    System.out.println(isChecked);
                    sendPidToServer(pid, isChecked); // 상태 서버로 전송
                }
            });
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && fromPosition < dataList.size() &&
                toPosition >= 0 && toPosition < dataList.size()) {
            Item_memo memo = dataList.get(fromPosition);
            dataList.remove(fromPosition);
            dataList.add(toPosition, memo);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        return false; // 유효하지 않은 위치에서 드래그 시도 시 false 반환
    }
    @Override
    public void onItemSwipe(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onMemoChanged(String memo) {
        Log.d("CustomAdapter", "onMemoChanged: " + memo);

        // 포지션이 유효한 범위 내에 있는지 확인
        if (position >= 0 && position < dataList.size()) {
            // 지정된 위치에 있는 Item_memo 객체 가져오기
            Item_memo item = dataList.get(position);

            if ("삭제".equals(memo)) {
                // memo 값이 "삭제"인 경우, 데이터 목록에서 항목 제거
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
            } else {
                // Item_memo 객체의 memo 필드 업데이트
                item.setMemo(memo);

                // RecyclerView에 지정된 위치의 데이터가 변경되었음을 알림
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(position);
                    }
                });
            }
        } else {
            Log.e("CustomAdapter", "유효하지 않은 위치: " + position);
        }
    }
    private void sendPidToServer(String pid, boolean isCheck) {
        // Retrofit 인스턴스 생성
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        // 서버로 PID를 전송하기 위한 요청 생성
        Call<Void> call = service.sendPidToServer(pid, isCheck);
        System.out.println("ischecked : " + isCheck);

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
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.memo_itemview, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Item_memo memoItem = dataList.get(position);
        // 타이틀 설정
        holder.txtTitle.setText(dataList.get(position).getMemo());

        // status가 2인 경우 체크박스 체크하고 텍스트에 가로줄 추가
        if (memoItem.getStatus() == 2) {
            holder.isChecked = true;
            memoItem.setChecked(true);
            holder.ll_memo_item.setBackgroundResource(R.drawable.wradius);
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.isChecked = false;
            memoItem.setChecked(false);
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // 체크박스 상태 업데이트
        holder.checkBox.setChecked(holder.isChecked);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }


}
