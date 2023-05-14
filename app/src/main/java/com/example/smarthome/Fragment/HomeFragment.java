package com.example.smarthome.Fragment;

import android.app.Activity;

import java.util.*;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.smarthome.Adapter.DataAddAdapter;
import com.example.smarthome.Adapter.RoomAdapter;
import com.example.smarthome.Adapter.SelectHomeAdapter;
import com.example.smarthome.MainActivity;
import com.example.smarthome.Model.DataAdd;
import com.example.smarthome.Model.Home;
import com.example.smarthome.Model.Room;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseDatabase database=FirebaseDatabase.getInstance();
    private final DatabaseReference ref=database.getReference("User");
    private final FirebaseUser fbuser=mAuth.getCurrentUser();
    private final String userUid;

    {
        assert fbuser != null;
        userUid = fbuser.getUid();
    }
    private IsendData mIsendData;
    public interface IsendData{
        void sendData(Home home,int position);
    }

    private RoomAdapter roomAdapter;
    private List<Room> listRoom;
    private int positionlist;
    private List<DataAdd> listData;
    private DataAddAdapter dataAddAdapter;
    private List<Home> listHome=new ArrayList<>();
    private Home mHome=new Home();
    private User user=new User();
    private SelectHomeAdapter selectHomeAdapter;
    private MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater, container, false);
        mainActivity=(MainActivity) getActivity();
        getData();
        lvDataAdd();
        addEvents();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIsendData= (IsendData) getActivity();
    }

    private void selectHome() {
        selectHomeAdapter=new SelectHomeAdapter(requireContext(),R.layout.select_home_layout,listHome);
        binding.spinnerSlHome.setAdapter(selectHomeAdapter);
        binding.spinnerSlHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHome=selectHomeAdapter.getItem(position);
                resetListHome(listHome,mHome);
                getHome();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void resetListHome(List<Home> listHome, Home mHome) {
        int index=listHome.indexOf(mHome);
        if(index!=-1){
            listHome.remove(index);
            listHome.add(0,mHome);
        }
        user.setHomeList(listHome);
        ref.child(userUid).updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
            }
        });

    }

    private void getData() {
        user=mainActivity.getUser();
        assert user != null;
        binding.txtUserName.setText(user.getUserName());
        listHome=user.getHomeList();
        selectHome();
    }
    private void addEvents() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.lvAdd.setVisibility(View.VISIBLE);
            }
        });
        binding.lvAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.lvAdd.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        openDialogaddRoom();
                        break;
                }
            }
        });
        binding.swGateHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHome.getStatusGate()==false){

                }
            }
        });
    }

    private void openDialogaddRoom() {
        if(mHome!=null){
            AddRoomFragment addRoomFragment=AddRoomFragment.newInstace(mHome,user);
            addRoomFragment.show(requireActivity().getSupportFragmentManager(),addRoomFragment.getTag());
        }
        else {
            Toast.makeText(getContext(),"error",Toast.LENGTH_LONG).show();
        }
    }

    private void lvDataAdd() {
        dataAddAdapter=new DataAddAdapter((Activity) getContext(),R.layout.addlayout,getListAdd());
        binding.lvAdd.setAdapter(dataAddAdapter);
        dataAddAdapter.notifyDataSetChanged();
    }

    private List<DataAdd> getListAdd() {
        listData=new ArrayList<>();
        listData.add(new DataAdd("Thêm phòng",R.drawable.room_24));
        listData.add(new DataAdd("Thêm thành viên",R.drawable.member_24));
        return listData;
    }

    private void getHome() {
        listRoom=mHome.getRoomList();
        //Setting viewpager2
        binding.vpListRoom.setOffscreenPageLimit(3);
        binding.vpListRoom.setClipToPadding(false);
        binding.vpListRoom.setClipChildren(false);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
                page.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Room room=listRoom.get(positionlist);
                        mIsendData.sendData(mHome,positionlist);
                    }
                });
            }
        });
        binding.vpListRoom.setPageTransformer(compositePageTransformer);
        roomAdapter=new RoomAdapter(HomeFragment.this,listRoom);
        binding.vpListRoom.setAdapter(roomAdapter);
        binding.vpListRoom.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                positionlist=position;
            }
        });
        Picasso.with(getContext()).load(mHome.getImageHome()).into(binding.imgHomeLayout);
        binding.swGateHome.setChecked(mHome.getStatusGate());

    }
}