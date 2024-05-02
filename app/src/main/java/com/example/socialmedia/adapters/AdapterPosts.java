package com.example.socialmedia.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.AddPostActivity;
import com.example.socialmedia.activities.PostLikedByActivity;
import com.example.socialmedia.activities.PostDetailActivity;

import android.text.format.DateFormat;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost> postList;
    String myUid;
    boolean mProcessLike = false;
    private DatabaseReference likesRef;
    private DatabaseReference postsRef;
    public AdapterPosts(Context context, List<ModelPost> postList){
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @androidx.annotation.NonNull
    @Override
    public MyHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.uNameTv.setText((uName));
        holder.pTimeTv.setText(pTime);
        holder.pDescriptionTv.setText(pDescription);
        holder.pLikesTv.setText(pLikes + " Thích");
        holder.pCommentsTv.setText(pComments + " Bình luận");
        setLikes(holder, pId);
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_face_light).into(holder.uPictureIv);
        }
        catch (Exception e){

        }

        if (pImage.equals("noImage")) {
            holder.pImageIv.setVisibility(View.GONE);
        }
        else {
            holder.pImageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(holder.pImageIv);
            }
            catch (Exception e) {

            }
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                final String postIde = postList.get(position).getpId();

                Log.d("mProcessLike", String.valueOf(mProcessLike));
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            if (snapshot.child(postIde).hasChild(myUid)) {
                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes - 1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else {
                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes + 1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;
                                addToHisNotifications("" + uid, "" + pId, "Thích bài viết của bạn");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.shareBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.pImageIv.getDrawable();
                if (bitmapDrawable == null) {
                    shareTextOnly(/*pTitle, */pDescription);
                }
                else {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageText(/*pTitle, */pDescription, bitmap);
                }
            }
        }));

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pId);
                context.startActivity(intent);
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostLikedByActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
    }

    private void addToHisNotifications(String hisUid, String pId, String notification){
        String timesptamp = "" + System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timesptamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", myUid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("Notifications").child(timesptamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void shareTextOnly(/*String pTitle, */String pDescription){
        String shareBody = /*pTitle + "\n" + */pDescription;

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

    public void shareImageText(/*String pTitle, */String pDescription, Bitmap bitmap){
        String shareBody = /*pTitle + "\n"*/ pDescription;

        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        context.startActivity(Intent.createChooser(sIntent, "Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uir = null;
        try {
            imageFolder.mkdir();//create if not exists
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uir = FileProvider.getUriForFile(context,"com.example.unity.fileprovider",file);

        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return uir;

    }

    private void setLikes(MyHolder holder, String postKey){
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUid)) {
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    holder.likeBtn.setText("Đã thích");
                }
                else {
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black, 0, 0, 0);
                    holder.likeBtn.setText("Thích");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);
        if (uid.equals(myUid)){
            SpannableString s = new SpannableString("Delete");
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            SpannableString s1 = new SpannableString("Edit");
            s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);
            SpannableString s2 = new SpannableString("View Detail");
            s2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s2.length(), 0);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, s);
            popupMenu.getMenu().add(Menu.NONE, 1, 0, s1);
            popupMenu.getMenu().add(Menu.NONE, 2, 0, s2);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    beginDelete(pId, pImage);
                }

                if (id == 1) {
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);

                }
                else if (id == 2) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);//will get detail of post using this id, its id of posts
                    context.startActivity(intent);
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage")) {
            deleteWithoutImage(pId);
        } else {
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage(String pId){
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pDescriptionTv, pLikesTv, pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;
        LinearLayout profileLayout;

        public MyHolder(@org.checkerframework.checker.nullness.qual.NonNull View itemView) {
            super(itemView);
            uPictureIv = itemView.findViewById(R.id.userAvatar);
            pImageIv = itemView.findViewById(R.id.postImage);
            uNameTv = itemView.findViewById(R.id.userName);
            pTimeTv = itemView.findViewById(R.id.postTime);
            pDescriptionTv = itemView.findViewById(R.id.postDescription);
            pLikesTv = itemView.findViewById(R.id.postLikes);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeButton);
            commentBtn = itemView.findViewById(R.id.commentButton);
            shareBtn = itemView.findViewById(R.id.shareButton);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            pCommentsTv = itemView.findViewById(R.id.postComment);
        }
    }
}
