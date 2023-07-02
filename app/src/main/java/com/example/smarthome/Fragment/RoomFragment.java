package com.example.smarthome.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Adapter.DataAddAdapter;
import com.example.smarthome.Adapter.DeviceAdapter;
import com.example.smarthome.Model.DataAdd;
import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.Room;
import com.example.smarthome.Model.Sensor;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.example.smarthome.ViewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RoomFragment extends Fragment{
    public static final String TAG = RoomFragment.class.getName();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = db.getReference("User");
    private final FirebaseUser fbUser = mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbUser != null;
        userUid = fbUser.getUid();
    }

    private ImageView imgBackRoom, imgAddDevice, imgRoomLayout1,btnMic;
    private TextView txtRoomName1, txtTemperature, txtHumidity,nodeGas;
    private Switch swDoor;
    private int mPosition = 0;
    private Home mHome=new Home();
    private List<Room> roomList=new ArrayList<>();
    private Room roomla = new Room();
    private List<Device> deviceList;
    private RecyclerView rcvListDevice;
    private DeviceAdapter deviceAdapter;
    private User user=new User();
    private Device device = new Device();
    private Sensor sensor;
    private List<Sensor> listSensor=new ArrayList<>();
    private int positiondv = -1;
    private List<Home> listHome=new ArrayList<>();
    private ListView lvAddinRoom;
    private ListView lvAddSensor;
    private List<DataAdd> listDataRoom;
    private List<DataAdd> listAddSensor;
    private DataAddAdapter dataAddAdapterRoom;
    private UserViewModel mViewModel; // Khai báo UserViewModel
    private TextToSpeech textToSpeech;//Text-to-Speech (TTS)
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean isPermissionGranted = false;


    public RoomFragment() {
    }

    public static RoomFragment newInstance( int position) {
        RoomFragment fragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        mViewModel = viewModelProvider.get(UserViewModel.class);

        // Kiểm tra quyền microphone
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Nếu quyền chưa được cấp, yêu cầu người dùng cấp quyền
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        } else {
            // Quyền đã được cấp
            isPermissionGranted = true;
        }
    }

    // Kiểm tra đã cấp quyền microphone chưa
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp
                isPermissionGranted = true;
            } else {
                // Quyền bị từ chối
                isPermissionGranted = false;
            }
        }
    }

    // Phương thức để phát âm thanh từ văn bản
    private void speakText(String text) {
        // Khởi tạo Text-to-Speech (TTS)
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    {
                        int result = textToSpeech.setLanguage(new Locale("vi_VN"));
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(getContext(), "Language not supported", Toast.LENGTH_LONG).show();
                        } else {
                            textToSpeech.setSpeechRate(1.5F);
                            String utteranceId = UUID.randomUUID().toString();
                            textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                                @Override
                                public void onUtteranceCompleted(String utteranceId) {
                                    // Khi hoàn thành phát âm thanh, hiển thị Toast
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                            // Phát âm thanh văn bản
                            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "TextToSpeech initialization failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Bắt đầu quá trình nhận dạng giọng nói
    private void startSpeechToText() {
        if (isPermissionGranted) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói mệnh lệnh...");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Thiết bị của bạn không hỗ trợ Speech-to-Text", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Vui lòng cấp quyền microphone để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            String orders=spokenText.substring(0,3).trim();
            String deviceName=spokenText.substring(4).trim();
            // Xử lý yêu cầu dựa trên văn bản nhận dạng được
            if(orders.equals("bật")) {
                turnOnDevice(deviceName);
            }
            else if(orders.equals("tắt")){
                turnOffDevice(deviceName);
            }else if(orders.equals("đọc")){
                readSensor(deviceName);
            }
            else{
                speakText("Yêu cầu không hợp lệ !");
            }
        }
    }
    private void turnOnDevice(String deviceName) {
        String homeIndex = "0";
        String roomIndex =Integer.toString(mPosition);

        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference()
                .child("User").child(userUid).child("homeList").child(homeIndex)
                .child("roomList").child(roomIndex).child("listDevice");

        devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean deviceFound = false;
                boolean deviceAlreadyOn = false;

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    String currentDeviceName = deviceSnapshot.child("deviceName").getValue(String.class);
                    Boolean currentDeviceStatus = deviceSnapshot.child("status").getValue(Boolean.class);

                    if (currentDeviceName != null && currentDeviceName.equalsIgnoreCase(deviceName)) {
                        deviceFound = true;

                        if (currentDeviceStatus != null && currentDeviceStatus) {
                            deviceAlreadyOn = true;
                            break;
                        }

                        deviceSnapshot.getRef().child("status").setValue(true);
                        break;
                    }
                }

                if (deviceFound) {
                    if (deviceAlreadyOn) {
                        String response = "Thiết bị " + deviceName + " đang bật";
                        speakText(response);
                    } else {
                        String response = "Đã bật " + deviceName;
                        speakText(response);
                    }
                } else {
                    String response = "Không có thiết bị " + deviceName + " trong phòng này";
                    speakText(response);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
    private void turnOffDevice(String deviceName) {
        String homeIndex = "0";
        String roomIndex =Integer.toString(mPosition);

        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference()
                .child("User").child(userUid).child("homeList").child(homeIndex)
                .child("roomList").child(roomIndex).child("listDevice");

        devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean deviceFound = false;
                boolean deviceAlreadyOff = false;

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    String currentDeviceName = deviceSnapshot.child("deviceName").getValue(String.class);
                    Boolean currentDeviceStatus = deviceSnapshot.child("status").getValue(Boolean.class);

                    if (currentDeviceName != null && currentDeviceName.equalsIgnoreCase(deviceName)) {
                        deviceFound = true;

                        if (currentDeviceStatus != null && !currentDeviceStatus) {
                            deviceAlreadyOff = true;
                            break;
                        }

                        deviceSnapshot.getRef().child("status").setValue(false);
                        break;
                    }
                }

                if (deviceFound) {
                    if (deviceAlreadyOff) {
                        String response = "Thiết bị " + deviceName + " đang tắt";
                        speakText(response);
                    } else {
                        String response = "Đã tắt " + deviceName;
                        speakText(response);
                    }
                } else {
                    String response = "Không có thiết bị " + deviceName + " trong phòng này";
                    speakText(response);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
    private void readSensor(String deviceName) {
        String homeIndex = "0";
        String roomIndex =Integer.toString(mPosition);
        final Double[] currentDeviceStatus = {0.0};

        DatabaseReference devicesRef = FirebaseDatabase.getInstance().getReference()
                .child("User").child(userUid).child("homeList").child(homeIndex)
                .child("roomList").child(roomIndex).child("sensorList");

        devicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean deviceFound = false;

                for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                    String currentDeviceName = deviceSnapshot.child("sensorName").getValue(String.class);

                    if (currentDeviceName != null && currentDeviceName.toLowerCase(Locale.ROOT).equalsIgnoreCase(deviceName)) {
                        deviceFound = true;
                        currentDeviceStatus[0] = deviceSnapshot.child("sensorParameters").getValue(Double.class);
                        break;
                    }
                }

                if (deviceFound) {
                    if(deviceName.equals("nhiệt độ")){
                        String response = deviceName + " trong phòng hiện tại là "+ Double.toString(currentDeviceStatus[0]) +"độ C";
                        speakText(response);
                    } else if(deviceName.equals("độ ẩm")){
                        String response = deviceName + " trong phòng hiện tại là "+ Double.toString(currentDeviceStatus[0]) +"%";
                        speakText(response);
                    }else if(deviceName.equals("khí gas")){
                        String response = "Nồng độ "+deviceName + " trong phòng hiện tại là "+ Double.toString(currentDeviceStatus[0]) +" miligam trên mét khối";
                        speakText(response);
                    }
                } else {
                    String response = "Không có cảm biến " + deviceName + " trong phòng này";
                    speakText(response);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        setControls(view);
        if(getArguments()!=null){
            getDatas();
        }
        return view;
    }


    private void setData() {
        deviceList = new ArrayList<>();
        txtRoomName1.setText(roomla.getRoomName());
        Picasso.with(getContext()).load(roomla.getImgRoom()).into(imgRoomLayout1);
        if(roomla.getDoor()!=null){
            swDoor.setChecked(roomla.getDoor());
        }else{
            swDoor.setVisibility(View.GONE);
        }
        listSensor=roomla.getSensorList();
        if(listSensor!=null) {
            for (Sensor sensor : listSensor) {
                if (sensor.getSensorName().equals("Nhiệt độ")) {
                    String format = getString(R.string.temperature);
                    String temperatureText = String.format(format, sensor.getSensorParameters().toString());
                    txtTemperature.setText(temperatureText);
                }else
                if (sensor.getSensorName().equals("Độ ẩm")) {
                    txtHumidity.setText("Độ ẩm : " + sensor.getSensorParameters().toString() + " %");
                }else
                if (sensor.getSensorName().equals("Khí gas")) {
                    nodeGas.setVisibility(View.VISIBLE);
                    if(sensor.getSensorParameters()>400){
                        nodeGas.setTextColor(Color.RED);
                    }else{
                        nodeGas.setTextColor(Color.rgb(76,175,80));
                    }
                }
            }
        }else{
            txtTemperature.setText("");
            txtHumidity.setText("");
        }
        deviceList = roomla.getListDevice();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvListDevice.setLayoutManager(linearLayoutManager);
        deviceAdapter = new DeviceAdapter(getContext());
        deviceAdapter.setData(deviceList);
        deviceAdapter.AddOnClick(new DeviceAdapter.IsetEvent() {
            @Override
            public void setEvent(Device dv, int position) {
                deviceList.set(position, dv);
                device = dv;
                positiondv = position;
                if (positiondv != -1) {
                    //Toast.makeText(getContext(),Integer.toString(positiondv)+device.getStatus(),Toast.LENGTH_LONG).show();
                    ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).child("listDevice").child(Integer.toString(positiondv)).updateChildren(device.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        positiondv = -1;

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        rcvListDevice.setAdapter(deviceAdapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int psItem=viewHolder.getAdapterPosition();
                deviceList.remove(psItem);
                roomla.setListDevice(deviceList);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deviceAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(rcvListDevice);

    }

    private void setEvents() {
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted){
                    startSpeechToText();
                }
                else{
                    // Quyền chưa được cấp, hiển thị thông báo hoặc yêu cầu người dùng cấp quyền
                    Toast.makeText(requireContext(), "Vui lòng cấp quyền sử dụng microphone", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
                }
            }
        });
        swDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomla.getDoor()){
                    roomla.setDoor(false);
                }else{
                    roomla.setDoor(true);
                }
                addDoor(roomla.getDoor());
            }
        });
        imgBackRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });
        imgAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvAddinRoom.setVisibility(View.VISIBLE);
            }
        });
        lvAddSensor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvAddSensor.setVisibility(View.GONE);
                if (roomla.getSensorList() != null) {
                    listSensor = roomla.getSensorList();
                } else {
                    listSensor = new ArrayList<>();
                }
                boolean hasTemperature = false;
                boolean hasHumidity = false;
                boolean hasGas = false;
                Sensor sTemperature=new Sensor();
                Sensor sHumidity=new Sensor();
                Sensor sGas=new Sensor();
                for (Sensor sensor : listSensor) {
                    if (sensor.getSensorName().equals("Nhiệt độ")) {
                        hasTemperature = true;
                        sTemperature=sensor;

                    }
                    if (sensor.getSensorName().equals("Độ ẩm")) {
                        hasHumidity = true;
                        sHumidity=sensor;
                    }
                    if (sensor.getSensorName().equals("Khí gas")) {
                        hasGas = true;
                        sGas=sensor;
                    }
                }
                switch (position) {
                    case 0:
                        if (hasTemperature) {
                            deleteTemperature(sTemperature);
                        } else {
                            addTemperature();
                        }
                        break;
                    case 1:
                        if (hasHumidity) {
                            deleteHumidity(sHumidity);
                        } else {
                            addHumidity();
                        }
                        break;
                    case 2:
                        if (hasGas) {
                            deleteGas(sGas);
                        } else {
                            addGas();
                        }
                        break;
                }
            }




        private void addGas() {
                if(roomla.getSensorList()!=null) {
                    listSensor = roomla.getSensorList();
                }else{
                    listSensor=new ArrayList<>();
                }
                sensor=new Sensor();
                sensor.setSensorName("Khí gas");
                sensor.setSensorParameters(0.0);
                listSensor.add(sensor);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            getAddSensor();
                        }
                    }
                });
            }

            private void addHumidity() {
                if(roomla.getSensorList()!=null) {
                    listSensor = roomla.getSensorList();
                }else{
                    listSensor=new ArrayList<>();
                }
                sensor=new Sensor();
                sensor.setSensorName("Độ ẩm");
                sensor.setSensorParameters(0.0);
                listSensor.add(sensor);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            txtHumidity.setText("Độ ẩm : "+sensor.getSensorParameters().toString()+" %");
                            getAddSensor();
                        }
                    }
                });
            }
            private void addTemperature() {
                if(roomla.getSensorList()!=null) {
                    listSensor = roomla.getSensorList();
                }else{
                    listSensor=new ArrayList<>();
                }
                sensor=new Sensor();
                sensor.setSensorName("Nhiệt độ");
                sensor.setSensorParameters(0.0);
                listSensor.add(sensor);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            String format = getString(R.string.temperature);
                            String temperatureText = String.format(format, sensor.getSensorParameters().toString());
                            txtTemperature.setText(temperatureText);
                            getAddSensor();
                        }
                    }
                });
            }

            private void deleteTemperature(Sensor sensor1) {
                listSensor=roomla.getSensorList();
                listSensor.remove(sensor1);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            txtTemperature.setText("");
                            getAddSensor();
                        }
                    }
                });

            }
            private void deleteHumidity(Sensor sensor1) {
                listSensor=roomla.getSensorList();
                listSensor.remove(sensor1);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            txtHumidity.setText("");
                            getAddSensor();
                        }
                    }
                });
            }
            private void deleteGas(Sensor sensor1) {
                listSensor=roomla.getSensorList();
                listSensor.remove(sensor1);
                roomla.setSensorList(listSensor);
                ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            nodeGas.setVisibility(View.GONE);
                            getAddSensor();
                        }
                    }
                });
            }

        });
        lvAddinRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvAddinRoom.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        if(roomla.getDoor()==null) {
                            addDoor(false);
                        }else{
                            deleteDoor();
                        }
                        break;
                    case 1:
                        openDialogaddDevice();
                        break;
                    case 2:
                        lvAddinRoom.setVisibility(View.GONE);
                        lvAddSensor.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        deleteRoom();
                        break;
                }
            }

            private void deleteDoor() {
                ref.child(userUid).child("homeList").child("0").child("roomList").child(String.valueOf(mPosition)).child("door").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            swDoor.setVisibility(View.GONE);
                            lvDataAddRoom();
                        }
                    }
                });
            }


            private void deleteRoom() {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
                roomList.remove(mPosition);
                mHome.setRoomList(roomList);
                ref.child(userUid).child("homeList").child("0").updateChildren(mHome.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

            }

            private void openDialogaddDevice() {
                if(roomla!=null){
                    AddDeviceFragment addDeviceFragment=AddDeviceFragment.newInstace(roomla,mPosition);
                    addDeviceFragment.show(requireActivity().getSupportFragmentManager(),addDeviceFragment.getTag());
                }
                else {
                    Toast.makeText(getContext(),"error",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void getAddSensor() {
        dataAddAdapterRoom = new DataAddAdapter((Activity) getContext(), R.layout.addlayout, getListAddSensor());
        lvAddSensor.setAdapter(dataAddAdapterRoom);
        dataAddAdapterRoom.notifyDataSetChanged();
    }

    private void addDoor(Boolean statusDoor) {
        roomla.setDoor(statusDoor);
        ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(mPosition)).updateChildren(roomla.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    swDoor.setVisibility(View.VISIBLE);
                    lvDataAddRoom();
                }
            }
        });
    }


    private void setControls(View view) {
        imgBackRoom = (ImageView) view.findViewById(R.id.imgBackRoom);
        imgAddDevice = (ImageView) view.findViewById(R.id.imgAddDevice);
        imgRoomLayout1 = (ImageView) view.findViewById(R.id.imgRoomLayout1);
        txtRoomName1 = (TextView) view.findViewById(R.id.txtRoomName1);
        txtTemperature = (TextView) view.findViewById(R.id.txtTemperature);
        txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
        nodeGas=(TextView) view.findViewById(R.id.nodeGas);
        rcvListDevice = (RecyclerView) view.findViewById(R.id.rcvListDevice);
        lvAddinRoom = (ListView) view.findViewById(R.id.lvAddinRoom);
        lvAddSensor=(ListView) view.findViewById(R.id.lvAddSensor);
        swDoor=(Switch) view.findViewById(R.id.swDoor);
        btnMic=(ImageView) view.findViewById(R.id.btnMic);


    }

    private void getDatas() {
        mViewModel.getMyData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User userData) {
                // Xử lý cập nhật dữ liệu mới nhất từ server vào Fragment
                user=userData;
                assert user != null;
                listHome=user.getHomeList();
                mHome=listHome.get(0);
                mPosition = getArguments().getInt("position");
                if(mHome.getRoomList()!=null) {
                    roomList = mHome.getRoomList();
                }
                roomla = roomList.get(mPosition);
                lvDataAddRoom();
                getAddSensor();
                setData();
                setEvents();
            }
        });
        mViewModel.LoadUser();

    }

    private void lvDataAddRoom() {
        dataAddAdapterRoom = new DataAddAdapter((Activity) getContext(), R.layout.addlayout, getListAddRoom());
        lvAddinRoom.setAdapter(dataAddAdapterRoom);
        dataAddAdapterRoom.notifyDataSetChanged();
    }

    private List<DataAdd> getListAddRoom() {
        listDataRoom = new ArrayList<>();
        if(roomla.getDoor()!=null){
            listDataRoom.add(new DataAdd("Xóa cửa",R.drawable.room_24));
        }else{
            listDataRoom.add(new DataAdd("Thêm cửa",R.drawable.room_24));
        }
        listDataRoom.add(new DataAdd("Thêm thiết bị",R.drawable.ic_baseline_devices_other_24));
        listDataRoom.add(new DataAdd("Thêm cảm biến",R.drawable.ic_baseline_device_thermostat_24));
        listDataRoom.add(new DataAdd("Xóa phòng",R.drawable.ic_baseline_delete_24));
        return listDataRoom;
    }
    private List<DataAdd> getListAddSensor() {
        listAddSensor = new ArrayList<>();
        if (roomla.getSensorList() != null) {
            listSensor = roomla.getSensorList();
        } else {
            listSensor = new ArrayList<>();
        }
        boolean hasNhietDo = false;
        boolean hasDoAm = false;
        boolean hasKhiGas = false;
        for (Sensor sensor : listSensor) {
            if (sensor.getSensorName().equals("Nhiệt độ")) {
                hasNhietDo = true;
            }
            if (sensor.getSensorName().equals("Độ ẩm")) {
                hasDoAm = true;
            }
            if (sensor.getSensorName().equals("Khí gas")) {
                hasKhiGas = true;
            }
        }
        if (!hasNhietDo) {
            listAddSensor.add(new DataAdd("Nhiệt độ", R.drawable.ic_baseline_device_thermostat_24));
        } else {
            listAddSensor.add(new DataAdd("Xóa Nhiệt độ", R.drawable.ic_baseline_delete_24));
        }
        if (!hasDoAm) {
            listAddSensor.add(new DataAdd("Độ ẩm", R.drawable.ic_baseline_device_thermostat_24));
        } else {
            listAddSensor.add(new DataAdd("Xóa Độ ẩm", R.drawable.ic_baseline_delete_24));
        }
        if (!hasKhiGas) {
            listAddSensor.add(new DataAdd("Khí gas", R.drawable.ic_baseline_device_thermostat_24));
        } else {
            listAddSensor.add(new DataAdd("Xóa Khí gas", R.drawable.ic_baseline_delete_24));
        }
        return listAddSensor;
    }


    public void updateData(int positionRoom) {
        mPosition=positionRoom;

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên của TextToSpeech khi Fragment bị hủy
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}