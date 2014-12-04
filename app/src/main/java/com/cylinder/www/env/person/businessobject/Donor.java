package com.cylinder.www.env.person.businessobject;

import android.graphics.Bitmap;

/**
 * Created by hipilee on 2014/11/19.
 */
public class Donor {

    private String userName;

    private Bitmap faceBitmap;

    private static Donor ourInstance = new Donor();

    public static Donor getInstance() {
        return ourInstance;
    }

    private Donor() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Bitmap getFaceBitmap() {
        return faceBitmap;
    }

    public void setFaceBitmap(Bitmap faceBitmap) {
        this.faceBitmap = faceBitmap;
    }



}

