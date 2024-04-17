package com.example.socialmedia.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText emailEt, passwordEt;
    Button registerBtn;
    ProgressDialog progressDialog;
    TextView haveAccTv;
    EditText accountName;
    EditText accountPhone;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate((savedInstanceState));
        setContentView(R.layout.signin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tạo tài khoản Turmeric");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        emailEt = findViewById((R.id.emailEt));
        passwordEt = findViewById(R.id.passwordEt);
        registerBtn = findViewById(R.id.SigninButton);
        haveAccTv = findViewById(R.id.subLoginText);
        accountName = findViewById(R.id.usernameEt);
        accountPhone = findViewById(R.id.phoneEt);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đăng kí");
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "ăda", Toast.LENGTH_SHORT).show();
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String phone = accountPhone.getText().toString().trim();
                String name = accountName.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Email không hợp lệ");
                    emailEt.setFocusable(true);
                }
                else if (password.length() < 6){
                    passwordEt.setError("Mật khẩu không hợp lệ");
                    passwordEt.setFocusable(true);
                }
                else if (!Patterns.PHONE.matcher(phone).matches()){
                    accountPhone.setError("Số điện thoại không hợp lệ");
                    accountPhone.setFocusable(true);
                }
                else if (name.length() < 2){
                    accountName.setError("Tên tài khoản phải hơn 2 ký tự");
                    accountName.setFocusable(true);
                }
                else {
                    regisiterUser(email, password);
                }
            }
        });

        haveAccTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void regisiterUser(String email, String password){
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    String email = user.getEmail();
                    String uid = user.getUid();
                    String phone = user.getPhoneNumber();
                    String name = user.getDisplayName();

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", name);
                    hashMap.put("onlineStatus", "online");
                    hashMap.put("typingTo", "noOne");
                    hashMap.put("phone", phone);
                    hashMap.put("image", "");
                    hashMap.put("cover", "");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("User");
                    reference.child(uid).setValue(hashMap);

                    Toast.makeText(RegisterActivity.this, "Đăng kí...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, Dashboard.class));
                    finish();

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Không thể xác nhận", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        NavUtils.navigateUpFromSameTask(this);
        return super.onSupportNavigateUp();
    }
}
