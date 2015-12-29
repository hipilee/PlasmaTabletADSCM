package com.cylinder.www.env.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/20 0020.
 */
public class RecordState {
    private SharedPreferences settings;
    private SharedPreferences.Editor localEditor;
    private HashMap<String, Boolean> stateMap;
    private HashMap<String, String> informationMap;
    private Activity activity;

    public RecordState(Activity activity) {

        // Initialize the variables.
        this.activity = activity;
        settings = this.activity.getPreferences(Context.MODE_PRIVATE);
        localEditor = this.settings.edit();

        stateMap = new HashMap<String, Boolean>();
        informationMap = new HashMap<String, String>();

        stateMap.put("confirm", getConfirm());
        stateMap.put("puncture", getPuncture());
        stateMap.put("start", getStart());
        stateMap.put("end", getEnd());

        informationMap.put("donorname", getDonorName());
        informationMap.put("donorid", getDonorID());
    }

    public void commit() {
        localEditor.putBoolean("confirm", stateMap.get("confirm"));
        localEditor.putString("donorname", informationMap.get("donorname"));
        localEditor.putString("donorid", informationMap.get("donorid"));
        localEditor.putBoolean("puncture", stateMap.get("puncture"));
        localEditor.putBoolean("start", stateMap.get("start"));
        localEditor.putBoolean("end", stateMap.get("end"));
        localEditor.commit();
    }

    public void retrieve() {
        stateMap.put("confirm", settings.getBoolean("confirm", false));
        informationMap.put("donorname", settings.getString("donorname", null));
        informationMap.put("donorid", settings.getString("donorid", null));
        stateMap.put("puncture", settings.getBoolean("puncture", false));
        stateMap.put("start", settings.getBoolean("start", false));
        stateMap.put("end", settings.getBoolean("end", false));
    }

    public void reset() {
        stateMap.put("confirm", false);
        stateMap.put("puncture", false);
        stateMap.put("start", false);
        stateMap.put("end", false);
    }

    public Boolean getConfirm() {
        return stateMap.get("confirm");
    }

    public void setConfirm(Boolean confirm) {
        stateMap.put("confirm", confirm);
    }

    public Boolean getPuncture() {
        return stateMap.get("puncture");
    }

    public void setPuncture(Boolean puncture) {
        stateMap.put("puncture", puncture);
    }

    public Boolean getStart() {
        return stateMap.get("start");
    }

    public void setStart(Boolean start) {
        stateMap.put("start", start);
    }


    public Boolean getEnd() {
        return stateMap.get("end");
    }

    public void setEnd(Boolean end) {
        stateMap.put("end", end);
    }


    public String getDonorName() {
        return informationMap.get("donorname");
    }

    public void setDonorName(String donorName) {
        informationMap.put("donorname", donorName);
    }

    public String getDonorID() {
        return informationMap.get("donorid");
    }

    public void setDonorID(String donorID) {
        informationMap.put("donorid", donorID);
    }
}

