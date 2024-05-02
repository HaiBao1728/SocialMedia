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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.socialmedia.activities.Dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.socialmedia.activities.MainActivity;
import com.example.socialmedia.R;
import com.example.socialmedia.activities.AddPostActivity;
import com.example.socialmedia.activities.GroupCreateActivity;
import com.example.socialmedia.activities.SettingsActivity;
import com.example.socialmedia.adapters.AdapterPosts;
import com.example.socialmedia.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.postsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        loadPosts();
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu);

                menu.findItem(R.id.add_group_chat).setVisible(false);
                menu.findItem(R.id.add_user).setVisible(false);
                menu.findItem(R.id.action_groupInfo).setVisible(false);

                MenuItem item = menu.findItem(R.id.search);
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (!TextUtils.isEmpty(query)) {
                            searchPosts(query);
                        } else {
                            loadPosts();
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (!TextUtils.isEmpty(newText)) {
                            searchPosts(newText);
                        } else {
                            loadPosts();
                        }
                        return false;
                    }
                });
                onCreateMenu(menu, menuInflater);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if (id == R.id.logout) {
                    firebaseAuth.signOut();
                    checkUserStatus();
                } else if (id == R.id.add_post) {
                    startActivity(new Intent(getActivity(), AddPostActivity.class));
                } else if (id == R.id.settings) {
                    //go to settings activity
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                }else if(id==R.id.add_group_chat){
                    //got to settings activity
                    startActivity(new Intent(getActivity(), GroupCreateActivity.class));
                }

                return onMenuItemSelected(menuItem);

            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                }
                adapterPosts = new AdapterPosts(getActivity(), postList);
                recyclerView.setAdapter(adapterPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (modelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(modelPost);
                    }
                }
                adapterPosts = new AdapterPosts(getActivity(), postList);
                recyclerView.setAdapter(adapterPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged-in user
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        menu.findItem(R.id.add_group_chat).setVisible(false);
        menu.findItem(R.id.add_user).setVisible(false);
        menu.findItem(R.id.action_groupInfo).setVisible(false);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchPosts(query);
                } else {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchPosts(newText);
                } else {
                    loadPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        } else if (id == R.id.add_post) {
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        } else if (id == R.id.settings) {
            //go to settings activity
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }else if(id==R.id.add_group_chat){
            //got to settings activity
            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }*/
}
