package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smarthome.Account.LoginActivity;
import com.example.smarthome.Fragment.HomeFragment;
import com.example.smarthome.Fragment.ProfileFragment;
import com.example.smarthome.Fragment.RoomFragment;
import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.Room;
import com.example.smarthome.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HomeFragment.IsendData{
    private long backPressedTime;
    private Toast mToast;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private BottomNavigationView bottomNavigationView;
    private final FirebaseDatabase database=FirebaseDatabase.getInstance();
    private final DatabaseReference ref=database.getReference("User");
    private final FirebaseUser fbuser=mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private User user=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addData();
        addControls();
        addEvents();
    }
    private void addData() {
        ref.child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);
                assert user != null;
                replaceFragment(new HomeFragment());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public User getUser(){
        return user;
    }

    private void addEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        replaceFragment(new HomeFragment());
                        break;
                    case R.id.menu_profile:
                        replaceFragment(new ProfileFragment());
                        break;
                    default:
                }
                return true;
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentlayout,fragment);
        fragmentTransaction.commit();
    }
    private void addControls() {
        bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomNavigationView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis()){
            mToast.cancel();//khi thoát ứng dụng sẽ ko hiện thông báo toast
            super.onBackPressed();
            return;
        }else {
            mToast= Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT);
            mToast.show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    @Override
    public void sendData(Home home,int positionRoom) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        RoomFragment roomFragment=new RoomFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("position",positionRoom);
        bundle.putSerializable("home",home);
        roomFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragmentlayout,roomFragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.addToBackStack(RoomFragment.TAG);
        fragmentTransaction.commit();

    }
}