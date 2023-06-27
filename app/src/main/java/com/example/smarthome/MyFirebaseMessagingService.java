//package com.example.smarthome;
//
//import android.app.Notification;
//import androidx.annotation.NonNull;
//import java.util.*;
//
//import com.example.smarthome.Model.Device;
//import com.example.smarthome.Model.Home;
//import com.example.smarthome.Model.Room;
//import com.example.smarthome.Model.Sensor;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private final FirebaseUser fbuser = mAuth.getCurrentUser();
//    private final String userUid;
//    {
//        assert fbuser != null;
//        userUid = fbuser.getUid();
//    }
//    private Home home=new Home();
//    private List<Room> listRoom=new ArrayList<>();
//    private List<Sensor> sensorList=new ArrayList<>();
//    @Override
//    public void onNewToken(String token) {
//        // Lấy registration token và sử dụng nó để gửi thông báo
//        FirebaseMessaging.getInstance().subscribeToTopic("khí gas");
//
//        // Lắng nghe thay đổi dữ liệu trên Realtime Database
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference("User");
//        database.child(userUid).child("homeList").child("0").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                home=dataSnapshot.getValue(Home.class);
//                if (home != null) {
//                    if(home.getRoomList()!=null){
//                        listRoom=home.getRoomList();
//                    }
//                }
//                for(Room room:listRoom){
//                    if(room.getSensorList()!=null){
//                        sensorList=room.getSensorList();
//                        for(Sensor sensor:sensorList){
//                            if(sensor.getSensorName().equals("Khí ga")){
//                                if(sensor.getSensorParameters()>200){
//                                    // Nếu khí gas bị rò rỉ, gửi thông báo đến thiết bị của người dùng
//                                    String title = "Thông báo khí gas";
//                                    String message = "Cảnh báo rò rỉ khí gas tại phòng "+ room.getRoomName();
//                                    FirebaseMessaging fm = FirebaseMessaging.getInstance();
//                                    Notification notification = new Notification.Builder(MyFirebaseMessagingService.this)
//                                            .setContentTitle(title)
//                                            .setContentText(message)
//                                            .setSmallIcon(R.drawable.warning)
//                                            .build();
//                                    fm.send(new RemoteMessage.Builder(token)
//                                            .addData("title", title)
//                                            .addData("message", message)
//                                            .addData("icon", "ic_notification")
//                                            .build());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Xử lý lỗi nếu có
//            }
//        });
//    }
//}
