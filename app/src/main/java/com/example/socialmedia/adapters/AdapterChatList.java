package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.ChatActivity;
import com.example.socialmedia.models.ModelUser;
import com.squareup.picasso.Picasso;

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
        View v = LayoutInflater.from(context).inflate(R.layout.chat_user, parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String userid = userList.get(position).getUid();
        String userAvatar = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(userid);
        holder.userName.setText(userName);

        if (lastMessage == null || ("default").equals(lastMessage)) {
            holder.latestMessage.setVisibility(View.GONE);
        }
        else {
            holder.latestMessage.setVisibility(View.VISIBLE);
            holder.latestMessage.setText(lastMessage);
        }

        try {
            Picasso.get().load(userAvatar).placeholder(R.drawable.ic_face_light).into(holder.userAvatar);
        }
        catch (Exception e) {

        }

        if (userList.get(position).getOnlineStatus().equals("online")) {
            holder.userOnlineStatus.setImageResource(R.drawable.circle_online);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", userid);
                context.startActivity(intent);
            }
        });
    }

    public void updateLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
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
