package com.example.smarthome.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtEmailSignUp,txtUserSignUp,txtPassWordSignUp,txtConfirmPassWordSignUp;
    private Button btnSignUp;
    private TextView txtHaveaAcc;
    private ProgressBar progressBar2;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private FirebaseDatabase data;
    private DatabaseReference ref;
    private User user;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        addControls();
        addEvents();
    }
    private void addEvents() {
        txtEmailSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Validator validator=new Validator();
                String email;
                email=txtEmailSignUp.getText().toString();
                if(!validator.validateEmail(email)) {
                    txtEmailSignUp.setError("Invalid email!");
                    txtEmailSignUp.requestFocus();
                }else{
                    txtUserSignUp.setText("");
                    txtPassWordSignUp.setText("");
                    txtConfirmPassWordSignUp.setText("");
                    txtEmailSignUp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtUserSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String username;
                username=txtUserSignUp.getText().toString();
                if(username.length()>20){
                    txtUserSignUp.setError("Number of characters must not exceed 20!");
                    txtUserSignUp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtPassWordSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password,confirmpass;
                password=txtPassWordSignUp.getText().toString();
                confirmpass=txtConfirmPassWordSignUp.getText().toString();
                Validator validator=new Validator();
                if(!validator.validatePass(password)){
                    txtPassWordSignUp.setError("Password must be at least 8 characters, with uppercase and special characters ! ");
                    txtPassWordSignUp.requestFocus();
                }
                else if(!password.equals(confirmpass)){
                    txtConfirmPassWordSignUp.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtConfirmPassWordSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password,confirmpass;
                password=txtPassWordSignUp.getText().toString();
                confirmpass=txtConfirmPassWordSignUp.getText().toString();
                if(!confirmpass.equals(password)){
                    txtConfirmPassWordSignUp.setError("Password does not match!");
                    txtConfirmPassWordSignUp.requestFocus();
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,username,password,confirmpass;
                email=txtEmailSignUp.getText().toString();
                username=txtUserSignUp.getText().toString();
                password=txtPassWordSignUp.getText().toString();
                confirmpass=txtConfirmPassWordSignUp.getText().toString();
                if(TextUtils.isEmpty(email)){
                    txtEmailSignUp.setError("Email can't empty!");
                    txtEmailSignUp.requestFocus();
                }
                else if(TextUtils.isEmpty(username)){
                    txtUserSignUp.setError("User name can't empty!");
                    txtUserSignUp.requestFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    txtPassWordSignUp.setError("Password can't empty!");
                    txtPassWordSignUp.requestFocus();
                }else if(TextUtils.isEmpty(confirmpass)){
                    txtConfirmPassWordSignUp.setError("Confirmpass can't empty!");
                    txtConfirmPassWordSignUp.requestFocus();
                }
                else{
                    progressBar2.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseUser=mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                uid=firebaseUser.getUid();
                                user=new User();
                                user.setEmail(email);
                                user.setUserName(username);
                                ref.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                            finish();
                                        }
                                        else{
                                            progressBar2.setVisibility(View.GONE);
                                            Toast.makeText(SignUpActivity.this, "Đăng ký không thành công!" , Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                progressBar2.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Đăng ký không thành công!" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }

        });
        txtHaveaAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void addControls() {
        txtEmailSignUp=(EditText) findViewById(R.id.txtEmailSignUp);
        txtUserSignUp=(EditText) findViewById(R.id.txtUserSignUp);
        txtPassWordSignUp=(EditText) findViewById(R.id.txtPassWordSignUp);
        txtConfirmPassWordSignUp=(EditText) findViewById(R.id.txtConfirmPassWordSignUp);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);
        txtHaveaAcc=(TextView) findViewById((R.id.txtHaveaAcc));
        progressBar2=(ProgressBar) findViewById(R.id.progressBar2);
        data=FirebaseDatabase.getInstance();
        ref=data.getReference("User");

    }
}