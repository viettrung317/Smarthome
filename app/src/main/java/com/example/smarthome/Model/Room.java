package com.example.smarthome.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.*;

public class Room implements Serializable {
    private String roomName,imgRoom;
    private Boolean door;
    private List<Device> listDevice;
    private List<Sensor> sensorList;
    public Map<String, Boolean> stars = new HashMap<>();

    public Room() {
    }

    public Room(String roomName, String imgRoom,Boolean door, List<Device> listDevice, List<Sensor> sensorList) {
        this.roomName = roomName;
        this.imgRoom = imgRoom;
        this.door=door;
        this.listDevice = listDevice;
        this.sensorList = sensorList;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getImgRoom() {
        return imgRoom;
    }

    public void setImgRoom(String imgRoom) {
        this.imgRoom = imgRoom;
    }

    public Boolean getDoor() {
        return door;
    }

    public void setDoor(Boolean door) {
        this.door = door;
    }

    public List<Device> getListDevice() {
        return listDevice;
    }

    public void setListDevice(List<Device> listDevice) {
        this.listDevice = listDevice;
    }

    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("roomName",roomName);
        result.put("imgRoom",imgRoom);
        result.put("door",door);
        result.put("listDevice",listDevice);
        result.put("sensorList",sensorList);
        result.put("stars", stars);

        return result;
    }
}
