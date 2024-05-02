package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.socialmedia.fragment.ChatListFragment;
import com.example.socialmedia.fragment.HomeFragment;
import com.example.socialmedia.fragment.UsersFragment;
import com.example.socialmedia.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.example.socialmedia.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.example.socialmedia.fragment.GroupChatsFragment;
//import com.example.socialmedia.fragment.ProfileFragment;


public class DashboardActivity extends AppCompatActivity {
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    String mUID;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        actionBar = getSupportActionBar();
        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        actionBar.setTitle("Trang chủ");
        navigationView.setOnItemSelectedListener(selectedListener);
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, fragment1, "");
        ft1.commit();

        checkUserStatus();

    }


    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token) {
        if (mUID != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
            Token mToken = new Token(token);
            ref.child(mUID).setValue(mToken);
        }
    }

    private final BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.home) {
//                actionBar.setTitle("Home");
//                HomeFragment fragment1 = new HomeFragment();
//                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                ft1.replace(R.id.container, fragment1, "");
//                ft1.commit();
//                return true;
            }
            if (menuItem.getItemId() == R.id.profile) {
//                actionBar.setTitle("Profile");
//                ProfileFragment fragment2 = new ProfileFragment();
//                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
//                ft2.replace(R.id.container, fragment2, "");
//                ft2.commit();
//                return true;
            }
            if (menuItem.getItemId() == R.id.users) {
                actionBar.setTitle("Bạn bè");
                UsersFragment fragment3 = new UsersFragment();
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.container, fragment3, "UsersFragment");
                ft3.commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.chat) {
                actionBar.setTitle("Nhắn tin");
                ChatListFragment fragment4 = new ChatListFragment();
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.container, fragment4, "");
                ft4.commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.more){
                //showMoreOptions();
                return true;
            }
            return false;
        }
    };

//    private void showMoreOptions() {
//        //popup menu to show more options
//        PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);
//        //items to show in menu
//        SpannableString s = new SpannableString("Notifications");
//        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
//        SpannableString s1 = new SpannableString("Group Chats");
//        s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);
//        popupMenu.getMenu().add(Menu.NONE, 0, 0, s);
//        popupMenu.getMenu().add(Menu.NONE, 1, 0, s1);
//
//        //menu clicks
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id= item.getItemId();
//                if(id==0){
//                    //notificaitons clicked
//                    actionBar.setTitle("Notification");
//                    NotificationsFragment fragment5 = new NotificationsFragment();
//                    FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
//                    ft5.replace(R.id.container, fragment5, "");
//                    ft5.commit();
//
//                }else if(id==1){
//                    //group chats clicked
//                    actionBar.setTitle("Group Chats");
//                    GroupChatsFragment fragment6 = new GroupChatsFragment();
//                    FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
//                    ft6.replace(R.id.container, fragment6, "");
//                    ft6.commit();
//                }
//                return false;
//            }
//        });
//        popupMenu.show();
//    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String token = task.getResult();
                            updateToken(token);
                        }
                    });
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
