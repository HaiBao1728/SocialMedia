package com.example.socialmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.activities.AddPostActivity;
import com.example.socialmedia.activities.PostDetailActivity;
import com.example.socialmedia.activities.ThereProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.example.socialmedia.models.ModelComment;
import org.jetbrains.annotations.NotNull;
import com.example.socialmedia.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder> {
    Context context;
    List<ModelComment> commentList;
    String myUid, postId;

    public AdapterComments(Context context, List<ModelComment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @NotNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //bind the row_comments.xml layout
        View view= LayoutInflater.from(context).inflate(R.layout.comment, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyHolder holder, int position) {
        //get the data
        String uid=commentList.get(position).getUid();
        String name=commentList.get(position).getuName();
        String email= commentList.get(position).getuEmail();
        String image= commentList.get(position).getuDp();
        String cid= commentList.get(position).getcId();
        String comment= commentList.get(position).getComment();
        String timestamp= commentList.get(position).getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);

        //set user dp
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_oringe).into(holder.avatarIv);
        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(v, uid, cid);
            }
        });
    }

    private void showMoreOptions(View view, String uid, String cid) {
        String user_comment_id = uid;
        PopupMenu popupMenu = new PopupMenu(context, view, Gravity.END);
        if (uid.equals(myUid)){
            SpannableString s = new SpannableString("Trang cá nhân");
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            SpannableString s1 = new SpannableString("Xóa bình luận");
            s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, s);
            popupMenu.getMenu().add(Menu.NONE, 1, 0, s1);
        }
        else {
            SpannableString s = new SpannableString("Trang cá nhân");
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            //SpannableString s1 = new SpannableString("Xóa bình luận");
            //s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);0);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, s);
            //popupMenu.getMenu().add(Menu.NONE, 1, 0, s1);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    /*Intent intent = new Intent(context, ThereProfileActivity.class);
                    intent.putExtra("uid", user_comment_id);
                    context.startActivity(intent);*/
                    goProfile(uid);
                }

                if (id == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Xóa");
                    builder.setMessage("Bạn có chắc muốn xóa bình luận này?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();

                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void goProfile(String uid) {
        Intent intent = new Intent(context, ThereProfileActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    private void deleteComment(String cid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.child("Comments").child(cid).removeValue();


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = "" + snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) - 1;
                ref.child("pComments").setValue("" + newCommentVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView avatarIv;
        TextView nameTv,commentTv,timeTv;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.userAvatar);
            nameTv = itemView.findViewById(R.id.userName);
            commentTv = itemView.findViewById(R.id.commentText);
            timeTv = itemView.findViewById(R.id.commentTime);

        }
    }
}
