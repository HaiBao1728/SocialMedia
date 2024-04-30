package com.example.socialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.models.ModelUser;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {
    Context context;
    List<ModelUser> userList;
    private HashMap<String, String> lastMessageMap;

    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        this.lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_user_list, parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userid = userList.get(position).getUid();
        String userAvatar = userList.get(position).getAvatar();
        String userCover = userList.get(position).getCover();
        String lastMessage = lastMessageMap.get(userid);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar, userOnlineStatus;
        TextView userName, latestMessage;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            userAvatar = itemView.findViewById(R.id.userAvatar);
            userOnlineStatus = itemView.findViewById(R.id.userOnlineStatus);
            userName = itemView.findViewById(R.id.userName);
            latestMessage = itemView.findViewById(R.id.latestMessage);
        }
    }
}
