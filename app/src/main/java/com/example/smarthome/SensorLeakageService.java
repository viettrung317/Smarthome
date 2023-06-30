package com.example.smarthome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.Room;
import com.example.smarthome.Model.Sensor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SensorLeakageService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "GasLeakageChannel";

    private boolean isGasLeakageDetected = false;
    private boolean isTemperatureLeakageDetected = false;
    private boolean isNotificationDisplayed = false;
    private boolean isAppRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // Đăng ký kênh thông báo (nếu chưa đăng ký)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Gas Leakage Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private Notification buildNotification() {
        // Nếu ứng dụng đã khởi chạy, trả về null để không hiển thị thông báo
        if (isAppRunning) {
            return null;
        }
        String title = "Ứng dụng Smart Home";
        String message = "Đang kiểm tra cảnh báo...";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kiểm tra nếu ứng dụng chưa khởi chạy, hiển thị thông báo và bật cờ isAppRunning
        if (!isAppRunning) {
            startForeground(NOTIFICATION_ID, buildNotification());
            // Kiểm tra giá trị khí gas và gửi thông báo nếu cần thiết
            checkGasLeakage();
            // Kiểm tra giá trị nhiệt độ và gửi thông báo nếu cần thiết
            checkTemperatureLeakage();
            isAppRunning = true;
        }else {
            // Trả về START_NOT_STICKY để dịch vụ không được khởi động lại
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissNotification();
        isAppRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkGasLeakage() {
        // Lấy danh sách cảm biến từ Firebase
        getSensorListFromFirebase(new FirebaseCallback() {
            @Override
            public void onCallback(List<Sensor> sensorList) {
                boolean isLeakageDetected = false; // Cập nhật trạng thái

                // Duyệt qua danh sách cảm biến và kiểm tra rò rỉ khí gas
                for (Sensor sensor : sensorList) {
                    if (sensor.getSensorName().equals("Khí gas") && sensor.getSensorParameters() > 200) {
                        isLeakageDetected = true;
                        break;
                    }
                }

                isGasLeakageDetected = isLeakageDetected; // Cập nhật trạng thái

                // Kiểm tra giá trị khí gas và gửi thông báo nếu cần thiết
                if (isGasLeakageDetected && !isNotificationDisplayed) {
                    createNotification("Rò rỉ khí gas", "Phát hiện rò rỉ khí gas tại nhà, Vui lòng kiểm tra ngay.");
                    isNotificationDisplayed = true;
                } else if (!isGasLeakageDetected && isNotificationDisplayed) {
                    dismissNotification();
                    isNotificationDisplayed = false;
                }
            }
        });
    }

    private void checkTemperatureLeakage() {
        // Lấy danh sách cảm biến từ Firebase
        getSensorListFromFirebase(new FirebaseCallback() {
            @Override
            public void onCallback(List<Sensor> sensorList) {
                boolean isLeakageDetected = false; // Cập nhật trạng thái

                // Duyệt qua danh sách cảm biến và kiểm tra rò rỉ nhiệt độ
                for (Sensor sensor : sensorList) {
                    if (sensor.getSensorName().equals("Nhiệt độ") && sensor.getSensorParameters() > 33) {
                        isLeakageDetected = true;
                        break;
                    }
                }

                isTemperatureLeakageDetected = isLeakageDetected; // Cập nhật trạng thái

                // Kiểm tra giá trị nhiệt độ và gửi thông báo nếu cần thiết
                if (isTemperatureLeakageDetected && !isNotificationDisplayed) {
                    createNotification("Nhiệt độ quá mức", "Phát hiện nhiệt độ quá mức, Vui lòng kiểm tra ngay!");
                    isNotificationDisplayed = true;
                } else if (!isTemperatureLeakageDetected && isNotificationDisplayed) {
                    dismissNotification();
                    isNotificationDisplayed = false;
                }
            }
        });
    }

    private void getSensorListFromFirebase(final FirebaseCallback callback) {
        // Lắng nghe thay đổi dữ liệu trên Realtime Database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("User");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference homeListRef = database.child(userUid).child("homeList");
            homeListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Sensor> sensorList = new ArrayList<>();

                    for (DataSnapshot homeSnapshot : dataSnapshot.getChildren()) {
                        Home home = homeSnapshot.getValue(Home.class);
                        if (home != null && home.getRoomList() != null) {
                            List<Room> listRoom = home.getRoomList();
                            for (Room room : listRoom) {
                                if (room.getSensorList() != null) {
                                    List<Sensor> roomSensorList = room.getSensorList();
                                    sensorList.addAll(roomSensorList);
                                }
                            }
                        }
                    }

                    // Gọi callback với danh sách cảm biến từ Firebase
                    callback.onCallback(sensorList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                    Log.e("GasLeakageService", "Firebase Database onCancelled: " + error.getMessage());
                }
            });
        }
    }

    private interface FirebaseCallback {
        void onCallback(List<Sensor> sensorList);
    }
    private void createNotification(String title, String message) {
        // Tạo và hiển thị thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void dismissNotification() {
        // Tắt thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);
        isNotificationDisplayed = false;
    }
}
