package com.cylinder.www.env;

/**
 * Created by xiutao on 2014/9/19.
 */
public class CollectionFaceInfo {

    public int addTakenPictureNO() {
        return takenPictureNO;
    }

    public void resetTakenPictureNO() {
        this.takenPictureNO = 0;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public int addSendNumber() {
        return sendNumber;
    }

    public void resetSendNumber() {
        this.sendNumber = 0;
    }


    private int takenPictureNO=0;	// How many pictures taken
    private byte picture[];	// The latest picture data.
    private int sendNumber = 0;
    private static CollectionFaceInfo ourInstance = new CollectionFaceInfo();

    public static CollectionFaceInfo getInstance() {
        return ourInstance;
    }

    private CollectionFaceInfo() {
    }
}
