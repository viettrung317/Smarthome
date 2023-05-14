package com.example.smarthome.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.smarthome.R;
import com.example.smarthome.ViewModel.DeviceViewModel;
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

public class RoomFragment extends Fragment {
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

    private ImageView imgBackRoom, imgAddDevice, imgRoomLayout1;
    private TextView txtRoomName1, txtTemperature, txtHumidity;
    private Switch swDoor;
    private int position1 = 0;
    private Home mHome=new Home();
    private List<Room> roomList=new ArrayList<>();
    private Room roomla = new Room();
    private List<Device> deviceList = new ArrayList<>();
    private RecyclerView rcvListDevice;
    private DeviceAdapter deviceAdapter;
    private DeviceViewModel deviceViewModel;
    private Device device = new Device();
    private int positiondv = -1;
    private ListView lvAddinRoom;
    private List<DataAdd> listDataRoom = new ArrayList<>();
    private DataAddAdapter dataAddAdapterRoom;


    public RoomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        setControls(view);
        getDatas();
        setData();
        setEvents();
        return view;
    }


    private void setData() {
        txtRoomName1.setText(roomla.getRoomName());
        Picasso.with(getContext()).load(roomla.getImgRoom()).into(imgRoomLayout1);
        deviceList = roomla.getListDevice();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvListDevice.setLayoutManager(linearLayoutManager);
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.initData(deviceList);
        deviceViewModel.getListDeviceLiveData().observe(getViewLifecycleOwner(), new Observer<List<Device>>() {
            @Override
            public void onChanged(List<Device> deviceList) {
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
                            ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(position1)).child("listDevice").child(Integer.toString(positiondv)).updateChildren(device.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ref.child(userUid).child("homeList").child("0").child("roomList").child(Integer.toString(position1)).addValueEventListener(new ValueEventListener() {
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
            }
        });
    }

    private void setEvents() {
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
        lvAddinRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvAddinRoom.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        openDialogaddDevice();
                        break;
                    case 2:
                        deleteRoom();
                }
            }

            private void deleteRoom() {
                roomList.remove(position1);
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
                    AddDeviceFragment addDeviceFragment=AddDeviceFragment.newInstace(roomla,position1);
                    addDeviceFragment.show(requireActivity().getSupportFragmentManager(),addDeviceFragment.getTag());
                }
                else {
                    Toast.makeText(getContext(),"error",Toast.LENGTH_LONG).show();
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
        rcvListDevice = (RecyclerView) view.findViewById(R.id.rcvListDevice);
        lvAddinRoom = (ListView) view.findViewById(R.id.lvAddinRoom);


    }

    private void getDatas() {
        Bundle bundle = getArguments();
        assert bundle != null;
        position1 = (int) bundle.get("position");
        mHome=(Home) bundle.get("home");
        roomList=mHome.getRoomList();
        roomla = roomList.get(position1);

        lvDataAddRoom();
    }

    private void lvDataAddRoom() {
        dataAddAdapterRoom = new DataAddAdapter((Activity) getContext(), R.layout.addlayout, getListAddRoom());
        lvAddinRoom.setAdapter(dataAddAdapterRoom);
        dataAddAdapterRoom.notifyDataSetChanged();
    }

    private List<DataAdd> getListAddRoom() {
        listDataRoom.add(new DataAdd("Thêm thiết bị",R.drawable.ic_baseline_devices_other_24));
        listDataRoom.add(new DataAdd("Thêm cảm biến",R.drawable.ic_baseline_device_thermostat_24));
        listDataRoom.add(new DataAdd("Xóa phòng",R.drawable.ic_baseline_delete_24));
        return listDataRoom;
    }

}