package com.example.smarthome.Adapter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smarthome.Fragment.HomeFragment;
import com.example.smarthome.Fragment.ListRoomFragment;
import com.example.smarthome.Model.Room;
import com.example.smarthome.R;

import java.util.*;

public class RoomAdapter extends FragmentStateAdapter{
    private List<Room> roomList;
    public RoomAdapter(@NonNull HomeFragment fragmentActivity, List<Room> roomList) {
        super(fragmentActivity);
        this.roomList=roomList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Room room=roomList.get(position);
        Bundle bundle=new Bundle();
        bundle.putSerializable("room",room);
        ListRoomFragment listRoomFragment=new ListRoomFragment();
        listRoomFragment.setArguments(bundle);
        return listRoomFragment;
    }

    @Override
    public int getItemCount() {
        if(roomList!=null){
            return roomList.size();
        }
        return 0;
    }
    public static class RoomViewHolder extends  RecyclerView.ViewHolder{
        private ImageView imgRoomLayout;
        private TextView txtRoomName,txtQuantityDevice;
        public RoomViewHolder(@NonNull View itemView, ImageView imgRoom) {
            super(itemView);
            imgRoomLayout=(ImageView) itemView.findViewById(R.id.imgRoomLayout);
            txtRoomName=(TextView) itemView.findViewById(R.id.txtRoomName);
            txtQuantityDevice=(TextView) itemView.findViewById(R.id.txtQuantityDevice);
        }
    }
}
