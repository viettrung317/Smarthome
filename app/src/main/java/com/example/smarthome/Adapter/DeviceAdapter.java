package com.example.smarthome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.Model.Device;
import com.example.smarthome.R;
import com.squareup.picasso.Picasso;

import java.util.*;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> deviceList;
    private Context context;
    private IsetEvent isetEvent;
    public interface IsetEvent{
        void setEvent(Device device,int item);
    }
    public DeviceAdapter( Context context){
        this.context=context;
    }
    public void setData(List<Device> deviceList){
        this.deviceList = deviceList;
        notifyDataSetChanged();

    }
    public void AddOnClick(IsetEvent even) {
        this.isetEvent = even;
    }
    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.device_layout,parent,false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device dv=deviceList.get(position);
        if(dv.getStatus()){
            holder.swDevice.setChecked(true);
        }
        int ps=position;
        Picasso.with(context).load(dv.getImgDevice()).into(holder.imgDeviceLayout);
        holder.swDevice.setText(dv.getDeviceName());
        holder.swDevice.setChecked(dv.getStatus());
        holder.swDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dv.getStatus()){
                    dv.setStatus(false);
                }else{
                    dv.setStatus(true);
                }
                isetEvent.setEvent(dv,ps);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(deviceList!=null){
            return deviceList.size();
        }else{
            return 0;
        }
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgDeviceLayout;
        private Switch swDevice;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDeviceLayout=(ImageView) itemView.findViewById(R.id.imgDeviceLayout);
            swDevice=(Switch) itemView.findViewById(R.id.swDevice);
        }
    }
}
