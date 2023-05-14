package com.example.smarthome.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ForgotActivity extends AppCompatActivity {
    private EditText txtEmailReset;
    private Button btnSendEmail;
    private ProgressBar progressBar3;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        addControls();
        addEvents();
    }

    private void addEvents() {
        txtEmailReset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = txtEmailReset.getText().toString();
                Validator validator = new Validator();
                if (!validator.validateEmail(email)) {
                    txtEmailReset.setError("Invalid email!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = txtEmailReset.getText().toString();
                if(TextUtils.isEmpty(email)){
                    txtEmailReset.setError("Email can't empty!");
                    txtEmailReset.requestFocus();
                }else{
                    progressBar3.setVisibility(View.VISIBLE);
                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()){
                                List<String> signInMethods = task.getResult().getSignInMethods();
                                assert signInMethods != null;
                                if(!signInMethods.isEmpty()){
                                    // Email đã được đăng ký với một phương thức đăng nhập nào đó
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(ForgotActivity.this,LoginActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                                } else{
                                    // Email chưa được đăng ký với bất kỳ phương thức đăng nhập nào
                                    progressBar3.setVisibility(View.GONE);
                                    txtEmailReset.setError("Email chưa được đăng ký!");
                                    txtEmailReset.requestFocus();
                                }
                            }
                            else {
                                // Xảy ra lỗi khi lấy danh sách phương thức đăng nhập cho email
                                Log.d("Error","Xảy ra lỗi khi lấy danh sách phương thức đăng nhập cho email!");
                            }
                        }
                    });
                }

            }
        });
    }

    private void addControls() {
        txtEmailReset=(EditText) findViewById(R.id.txtEmailReset);
        btnSendEmail=(Button) findViewById(R.id.btnSendEmail);
        progressBar3=(ProgressBar) findViewById(R.id.progressBar3);
        mAuth= FirebaseAuth.getInstance();

    }
}