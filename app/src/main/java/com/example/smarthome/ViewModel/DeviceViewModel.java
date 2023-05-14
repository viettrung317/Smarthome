package com.example.smarthome.ViewModel;

import androidx.lifecycle.MutableLiveData;
import java.util.*;
import androidx.lifecycle.ViewModel;

import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Room;

public class DeviceViewModel extends ViewModel {
    private MutableLiveData<List<Device>> listDeviceLiveData;
    private List<Device> deviceList;

    public DeviceViewModel() {
        listDeviceLiveData = new MutableLiveData<>();
        initData(deviceList);
    }

    public MutableLiveData<List<Device>> getListDeviceLiveData() {
        return listDeviceLiveData;
    }

    public void initData(List<Device> deviceList) {
        deviceList = deviceList;
        listDeviceLiveData.setValue(deviceList);
    }
}
