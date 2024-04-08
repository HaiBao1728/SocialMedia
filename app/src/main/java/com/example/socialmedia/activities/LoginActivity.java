package com.example.socialmedia.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.identity.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.socialmedia.R;
import org.jetbrains.annotations.NotNull;

import com.example.socialmedia.R;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn;
    ProgressDialog progressDialog;
    TextView notHaveAccTv,recoverPassTv;
    SignInButton gLoginBtn;
    BeginSignInRequest signInRequest;
    SignInClient oneTapClient;
    //Declare an instance of firebaseAuth
    private FirebaseAuth mAuth;
    private static final int REQ_ONE_TAP = 100;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create new account with SoMe");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        notHaveAccTv = findViewById(R.id.not_have_account_tv);
        recoverPassTv=findViewById(R.id.recover_tv);
        gLoginBtn=findViewById(R.id.gLogin_btn);
        //initialize the firebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}