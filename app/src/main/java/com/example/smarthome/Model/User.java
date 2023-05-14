package com.example.smarthome.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.*;
public class User implements Serializable {
    private String email,userName,sdt,avatar;
    private List<Home> homeList;
    public Map<String, Boolean> stars = new HashMap<>();

    public User() {
    }

    public User(String email, String userName, String sdt,String avatar, List<Home> homeList) {
        this.email = email;
        this.userName = userName;
        this.sdt = sdt;
        this.homeList = homeList;
        this.avatar=avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Home> getHomeList() {
        return homeList;
    }

    public void setHomeList(List<Home> homeList) {
        this.homeList = homeList;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName",userName);
        result.put("email",email);
        result.put("sdt",sdt);
        result.put("avatar",avatar);
        result.put("homeList",homeList);
        result.put("stars", stars);

        return result;
    }
}
