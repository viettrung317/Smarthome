package com.example.smarthome.ViewModel;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smarthome.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class UserViewModel extends ViewModel {
    private MutableLiveData<User> myLiveData;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser fbuser = mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private User user=new User();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

    public UserViewModel() {
        // Lắng nghe sự kiện khi có dữ liệu mới từ Firebase
        databaseReference.child(userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                // Cập nhật giá trị LiveData khi có dữ liệu mới từ Firebase
                myLiveData.setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public LiveData<User> getMyData() {
        if(myLiveData==null){// Kiểm tra nếu LiveData chưa được khởi tạo
            myLiveData = new MutableLiveData<>(); // Tạo một LiveData mới
        }
        return myLiveData;
    }
}
