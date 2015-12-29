package com.cylinder.www.env.person.businessobject;

import android.graphics.Bitmap;

/**
 * Created by hipilee on 2014/11/19.
 */
public class Donor {

    private static String userName;

    private static Bitmap faceBitmap;

    private static String donorID="";

    private static final Donor ourInstance = new Donor();

    public static Donor getInstance() {
        return ourInstance;
    }

    private Donor() {
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Donor.userName = userName;
    }


    public static Bitmap getFaceBitmap() {
        return faceBitmap;
    }

    public static void setFaceBitmap(Bitmap faceBitmap) {
        Donor.faceBitmap = faceBitmap;
    }


    public static String getDonorID() {
        return Donor.donorID;
    }
    public static void setDonorID(String donorID) {
        Donor.donorID = donorID;
    }
}

