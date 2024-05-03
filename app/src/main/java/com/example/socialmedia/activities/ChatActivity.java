package com.example.socialmedia.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView chatUserAvatar;
    TextView chatUserName;
    TextView chatUserStatus;
    ImageButton attachButton;
    ImageButton sendButton;
    EditText messageEditText;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.chat_recycleView);
        chatUserAvatar = findViewById(R.id.chatUserAvatar);
        chatUserName = findViewById(R.id.chatUserName);
        chatUserStatus = findViewById(R.id.chatUserStatus);
        attachButton = findViewById(R.id.attachButton);
        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
    }
}
