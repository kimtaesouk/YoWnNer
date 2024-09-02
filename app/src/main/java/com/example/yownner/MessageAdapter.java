package com.example.yownner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private Context context;
    private List<String> messages;
    private List<String> clients;
    public MessageAdapter(Context context, List<String> messages, List<String> clients) {
        this.context = context;
        this.messages = messages;
        this.clients = clients;
    }
    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        String message = messages.get(position);
        String client = clients.get(position);
        holder.tv_measuringtime.setText(message);
        holder.tv_user_name.setText(client);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tv_measuringtime, tv_user_name;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_measuringtime = itemView.findViewById(R.id.tv_measuringtime);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
        }
    }
}
