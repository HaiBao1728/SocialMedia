package com.example.socialmedia.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmedia.adapters.AdapterNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.socialmedia.R;
import com.example.socialmedia.models.ModelNotification;

import java.util.ArrayList;
public class NotificationsFragment extends Fragment {
    RecyclerView notificationRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notifications = new ArrayList<>();
    private AdapterNotification adapterNotification;

    public NotificationsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationRv = view.findViewById(R.id.notificationRv);

        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotification();

        return view;
    }

    private void getAllNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Notifications").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notifications.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelNotification model = ds.getValue(ModelNotification.class);
                            notifications.add(model);
                        }
                        adapterNotification = new AdapterNotification(getActivity(), notifications);
                        notificationRv.setAdapter(adapterNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
