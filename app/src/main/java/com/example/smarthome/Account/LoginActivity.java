package com.example.smarthome.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.MainActivity;
import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText txtEmailLogin,txtPassLogin;
    private ImageButton ibtnShowHidePass;
    private Button btnLogin;
    private TextView txtForgotPassword,txtSignUp;
    private CheckBox checkBox_Remember;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;
    private int temp=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addControls();
        addEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String emaillogin = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        if (!emaillogin.isEmpty() && !password.isEmpty()) {
            txtEmailLogin.setText(emaillogin);
            txtPassLogin.setText(password);
            checkBox_Remember.setChecked(true);
        }
    }

    private void addEvents() {
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        txtEmailLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtPassLogin.setText("");
                txtEmailLogin.requestFocus();
                checkBox_Remember.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ibtnShowHidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtPassLogin.getTransformationMethod() instanceof PasswordTransformationMethod){
                    txtPassLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ibtnShowHidePass.setImageResource(R.drawable.hiddenpassword);
                }
                else{
                    txtPassLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ibtnShowHidePass.setImageResource(R.drawable.showpass);
                }
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotActivity.class));
            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }

    private void login() {
        temp++;
        if(temp==1){
            String email, pass;
            email = txtEmailLogin.getText().toString();
            pass = txtPassLogin.getText().toString();
            if (TextUtils.isEmpty(email)) {
                txtEmailLogin.setError("Email can't empty !");
                txtEmailLogin.requestFocus();
            } else if (TextUtils.isEmpty(pass)) {
                txtPassLogin.setError("Password can't empty!");
                txtPassLogin.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                String emaillogin = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("password", "");
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(checkBox_Remember.isChecked()){
                                saveAcc(email,pass);
                            }
                            else{
                                if(!emaillogin.isEmpty() && !password.isEmpty()){
                                    removeAcc(emaillogin,password);
                                }
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            try{
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException e) {
                                // Người dùng không tồn tại hoặc đã xóa tài khoản
                                Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                                txtPassLogin.setText("");
                                txtEmailLogin.setText("");
                                temp=0;
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Sai tên đăng nhập hoặc mật khẩu
                                Toast.makeText(LoginActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                                txtPassLogin.setText("");
                                temp=0;
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập vui lòng thử lại sau !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        }
    }

    private void saveAcc(String email, String pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", pass);
        editor.apply();
    }
    private void removeAcc(String email, String pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }

    private void addControls() {
        txtEmailLogin=(EditText) findViewById(R.id.txtEmailLogin);
        txtPassLogin=(EditText) findViewById(R.id.txtPassLogin);
        ibtnShowHidePass=(ImageButton) findViewById(R.id.ibtnShowHidePass);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        txtForgotPassword=(TextView)findViewById(R.id.txtForgotPassword);
        txtSignUp=(TextView) findViewById(R.id.txtSignUp);
        checkBox_Remember=(CheckBox) findViewById(R.id.checkBox_Remember);
        progressBar=(ProgressBar) findViewById(R.id.progressBar);
    }

}