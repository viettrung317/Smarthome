package com.example.smarthome.Model;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.*;
public class Home implements Serializable {
    private String nameHome,imageHome,passGateHome;
    private Boolean statusGate;
    private List<Room> roomList;
    public Map<String, Boolean> stars = new HashMap<>();


    public Home() {
    }

    public Home(String nameHome, String imageHome, String passGateHome, Boolean statusGate, List<Room> roomList) {
        this.nameHome = nameHome;
        this.imageHome = imageHome;
        this.passGateHome = passGateHome;
        this.statusGate = statusGate;
        this.roomList = roomList;
    }

    public String getNameHome() {
        return nameHome;
    }

    public void setNameHome(String nameHome) {
        this.nameHome = nameHome;
    }

    public String getImageHome() {
        return imageHome;
    }

    public void setImageHome(String imageHome) {
        this.imageHome = imageHome;
    }

    public String getPassGateHome() {
        return passGateHome;
    }

    public void setPassGateHome(String passGateHome) {
        this.passGateHome = passGateHome;
    }

    public Boolean getStatusGate() {
        return statusGate;
    }

    public void setStatusGate(Boolean statusGate) {
        this.statusGate = statusGate;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nameHome",nameHome);
        result.put("imageHome",imageHome);
        result.put("passGateHome",passGateHome);
        result.put("statusGate",statusGate);
        result.put("roomList",roomList);
        result.put("stars", stars);
        return result;
    }
}
