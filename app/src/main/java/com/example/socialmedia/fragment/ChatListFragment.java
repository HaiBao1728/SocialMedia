package com.example.socialmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.AdapterChatList;
import com.example.socialmedia.models.ModelChat;
import com.example.socialmedia.models.ModelChatList;
import com.example.socialmedia.models.ModelUser;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<ModelChatList> userChatList;
    List<ModelUser> userList;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    AdapterChatList adapterChatList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public  ChatListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_user_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userChatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChatList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelChatList chatlist = dataSnapshot.getValue(ModelChatList.class);
                    userChatList.add(chatlist);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadChats() {
        userChatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    for(ModelChatList chatlist: userChatList){
                        if (user.getUid() !=null && user.getUid().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }

                    adapterChatList = new AdapterChatList(getContext(), userList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lastMessage(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid())){
                        if ("image".equals(chat.getType())){
                            theLastMessage = "Sent a photo";
                        }
                        else {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }
                adapterChatList.updateLastMessageMap(userId, theLastMessage);
                adapterChatList.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
