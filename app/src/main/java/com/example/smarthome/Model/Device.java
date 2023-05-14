package com.example.smarthome.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Device implements Serializable {
    private Boolean status;
    private  String deviceName,imgDevice;
    public Map<String, Boolean> stars = new HashMap<>();

    public Device() {
    }

    public Device(Boolean status, String deviceName, String imgDevice) {
        status = status;
        this.deviceName = deviceName;
        this.imgDevice = imgDevice;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean Status) {
        this.status = Status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getImgDevice() {
        return imgDevice;
    }

    public void setImgDevice(String imgDevice) {
        this.imgDevice = imgDevice;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status",status);
        result.put("deviceName",deviceName);
        result.put("imgDevice",imgDevice);
        result.put("stars", stars);

        return result;
    }
}
