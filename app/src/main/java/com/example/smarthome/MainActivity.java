package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.example.smarthome.ViewModel.UserViewModel;
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
    private UserViewModel userViewModel;
    private LiveData<User> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userViewModel= new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getMyData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User data) {
                user=data;
                assert user != null;
                replaceFragment(new HomeFragment());
            }
        });
        addControls();
        addEvents();
    }
    public LiveData<User> getUser(){
        return userData;
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // Ẩn tất cả các Fragment đang hiển thị
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            fragmentTransaction.hide(f);
        }
        // Kiểm tra nếu Fragment đã được thêm vào trước đó
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragmentlayout, fragment);
            fragmentTransaction.show(fragment);
        }
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
    public void sendData(int position) {
        FragmentManager fm = getSupportFragmentManager();
        RoomFragment roomFragment = null;
        Log.d("RoomFragment", "Duyệt danh sách các Fragment đã attach");
        // Duyệt qua danh sách các Fragment đã attach và tìm Fragment có tag RoomFragment.TAG
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof RoomFragment && fragment.getTag().equals(RoomFragment.TAG)) {
                    roomFragment = (RoomFragment) fragment;
                    break;
                }
            }
        }

        if (roomFragment == null) {
            // Nếu không tìm thấy Fragment, tạo mới Fragment
            roomFragment = RoomFragment.newInstance(position);
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            // Ẩn tất cả các Fragment đang hiển thị
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                for (Fragment f : fragments) {
                    fragmentTransaction.hide(f);
                }
            }
            fragmentTransaction.add(R.id.fragmentlayout, roomFragment, RoomFragment.TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            Log.d("RoomFragment", "Tạo mới Fragment RoomFragment");
        } else {
            // Nếu Fragment đã tồn tại, cập nhật dữ liệu
            roomFragment.updateData(position);
            Log.d("RoomFragment", "Cập nhật dữ liệu vào Fragment");
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        userViewModel.getMyData().removeObservers(this);
    }
}