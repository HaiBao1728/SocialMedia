package com.example.socialmedia.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imgUrl;
    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imgUrl){
        this.context = context;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new MyHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyHolder myHolder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();
        String type = chatList.get(position).getType();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), Locale.getDefault());
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if ("text".equals(type)){
            myHolder.messageText.setVisibility(View.VISIBLE);
            myHolder.messageImage.setVisibility(View.GONE);
            myHolder.messageText.setText(message);
        }
        else {
            myHolder.messageText.setVisibility(View.GONE);
            myHolder.messageImage.setVisibility(View.VISIBLE);
            Picasso.get().load(message).placeholder(R.drawable.baseline_tag_faces_24).into(myHolder.messageImage);
        }

        myHolder.messageText.setText(message);
        myHolder.messageTime.setText(dateTime);
        try {
            Picasso.get().load(imgUrl).into(myHolder.messageImage);
        } catch (Exception ignored) {

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView messageTime;
        TextView messageText;
        ImageView messageImage;
        TextView isSeen;
        LinearLayout messageLayout;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            userAvatar = itemView.findViewById(R.id.userAvatar);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageText = itemView.findViewById(R.id.messageText);
            messageImage = itemView.findViewById(R.id.messageImage);
            isSeen = itemView.findViewById(R.id.isSeen);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
