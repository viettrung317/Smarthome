package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.smarthome.Account.LoginActivity;
import com.example.smarthome.Account.SignUpActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}