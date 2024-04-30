package com.example.socialmedia.fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.models.ModelChatList;
import com.example.socialmedia.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatListFragment extends Fragment {
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<ModelChatList> userChatList;
    List<ModelUser> userList;


}
