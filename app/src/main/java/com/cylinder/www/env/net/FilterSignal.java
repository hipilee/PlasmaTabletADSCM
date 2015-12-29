package com.cylinder.www.env.net;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/30 0030.
 */
public class FilterSignal {
    private HashMap<String, Boolean> signalMap;


    public void setConfirm(Boolean confirm) {
        this.signalMap.put("confirm", confirm);
    }

    public void setPuncture(Boolean puncture) {
        this.signalMap.put("puncture", puncture);
    }

    public void setStart(Boolean start) {
        this.signalMap.put("start", start);
    }

    public void setEnd(Boolean end) {
        this.signalMap.put("end", end);
    }

    public void setFist(Boolean fist) {
        this.signalMap.put("fist", fist);

    }


    public Boolean checkConfirm() {
        return this.signalMap.get("confirm");
    }

    public Boolean checkPuncture() {
        return this.signalMap.get("puncture");
    }

    public Boolean checkStart() {
        return this.signalMap.get("start");
    }

    public Boolean checkEnd() {
        return this.signalMap.get("end");
    }

    public Boolean checkFist() {
        return this.signalMap.get("fist");

    }

    public FilterSignal() {
        this.signalMap = new HashMap<String, Boolean>();
        this.signalMap.put("confirm", true);
        this.signalMap.put("puncture", false);
        this.signalMap.put("start", false);
        this.signalMap.put("fist", false);
        this.signalMap.put("end", false);

    }


}
