package com.example.smarthome.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Sensor implements Serializable {
    private String sensorName;
    private Double sensorParameters;
    private Boolean alarm;
    public Map<String, Boolean> stars = new HashMap<>();

    public Sensor() {
    }

    public Sensor(String sensorName, Double sensorParameters,boolean alarm) {
        this.sensorName = sensorName;
        this.sensorParameters = sensorParameters;
        this.alarm=alarm;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public Double getSensorParameters() {
        return sensorParameters;
    }

    public void setSensorParameters(Double sensorParameters) {
        this.sensorParameters = sensorParameters;
    }


    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sensorName",sensorName);
        result.put("sensorParameters",sensorParameters);
        result.put("alarm",alarm);
        result.put("stars", stars);

        return result;
    }
}
