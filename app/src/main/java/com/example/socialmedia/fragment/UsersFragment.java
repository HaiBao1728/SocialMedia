package com.example.socialmedia.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia.activities.AddPostActivity;
import com.example.socialmedia.activities.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.socialmedia.activities.MainActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.activities.GroupCreateActivity;
import com.example.socialmedia.activities.SettingsActivity;
import com.example.socialmedia.adapters.AdapterUsers;
import com.example.socialmedia.models.ModelUser;

import java.util.ArrayList;
import java.util.List;
public class UsersFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    FirebaseAuth firebaseAuth;

    public UsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userList = new ArrayList<>();

        getAllUsers();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu);

                menu.findItem(R.id.add_post).setVisible(false);

                MenuItem item = menu.findItem(R.id.search);
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (!TextUtils.isEmpty(query.trim())) {
                            searchUsers(query);
                        } else {
                            getAllUsers();
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        if (!TextUtils.isEmpty(query.trim())) {
                            searchUsers(query);
                        } else {
                            getAllUsers();
                        }
                        return false;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if (id == R.id.logout) {
                    firebaseAuth.signOut();
                    checkUserStatus();
                    return true;
                }else if(id==R.id.settings){
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    private void searchUsers(String query) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (modelUser != null && !modelUser.getUid().equals(fUser.getUid())); {
                            if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                                userList.add(modelUser);
                            }
                        }
                    }
                    catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error converting value for " + ds.getKey(), e);

                    }
                }
                adapterUsers = new AdapterUsers(getActivity(), userList);
                adapterUsers.notifyDataSetChanged();
                recyclerView.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllUsers() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        if (modelUser != null && !modelUser.getUid().equals(fUser.getUid())) {
                            userList.add(modelUser);
                        }
                    }
                    catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error converting value for " + ds.getKey(), e);
                    }
                }
                adapterUsers = new AdapterUsers(getActivity(), userList);
                recyclerView.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        }
        else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
