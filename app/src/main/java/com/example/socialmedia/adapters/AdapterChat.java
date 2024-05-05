package com.example.socialmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
        String timeStamp = chatList.get(position).getTimeStamp();
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

        if (myHolder.messageLayout != null) {
            myHolder.messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Xoá tin nhắn");
                    TextView messageTextView = new TextView(context);
                    messageTextView.setText("Bạn có chắc muốn xoá tin nhắn này?");
                    messageTextView.setTextSize(16);
                    messageTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    messageTextView.setGravity(Gravity.CENTER);
                    builder.setView(messageTextView);

                    builder.setPositiveButton("Xoá", (dialog, which) -> {
                        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        int position1 = myHolder.getAdapterPosition();
                        if (position1 != RecyclerView.NO_POSITION) {
                            String msgTimeStamp = chatList.get(position1).getTimeStamp();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
                            Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        if (ds.child("sender").getValue().equals(myUID)) {
                                            ds.getRef().removeValue();
                                            Toast.makeText(context, "Đã xoá tin nhắn.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Chỉ có thể xoá tin nhắn của bạn.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());

                    builder.create().show();
                }
            });
        } else {
            Toast.makeText(context, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }

        if (position == getItemCount() - 1) {
            if (chatList.get(position).isSeen()) {
                myHolder.isSeen.setText("Đã xem");
            } else {
                myHolder.isSeen.setText("Đã gửi");
            }
        } else {
            myHolder.isSeen.setVisibility(View.GONE);
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
