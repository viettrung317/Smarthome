package com.example.smarthome.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthome.Model.Room;
import com.example.smarthome.R;
import com.squareup.picasso.Picasso;

public class ListRoomFragment extends Fragment {
    private View mView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.room_layout,container,false);

        Bundle bundle=getArguments();
        assert bundle != null;
        Room room= (Room) bundle.get("room");
        ImageView imgRoomLayout=mView.findViewById(R.id.imgRoomLayout);
        TextView txtRoomName=mView.findViewById(R.id.txtRoomName);
        TextView txtQuantityDevice=mView.findViewById(R.id.txtQuantityDevice);
        Picasso.with(mView.getContext()).load(room.getImgRoom()).into(imgRoomLayout);
        txtRoomName.setText(room.getRoomName());
        int size=0;
        if(room.getListDevice()!=null) {
            size = room.getListDevice().size();
        }else{
            size=0;
        }
        txtQuantityDevice.setText(size+" Devices");
        return mView;
    }
}
