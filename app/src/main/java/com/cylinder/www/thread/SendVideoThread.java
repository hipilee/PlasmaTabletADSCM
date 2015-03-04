package com.cylinder.www.thread;

import com.cylinder.www.env.net.InterfaceNetwork;
import com.cylinder.www.env.net.InterfaceNetworkCreator;
import com.cylinder.www.env.net.ServerInformationTransaction;
import com.cylinder.www.env.net.ServerNetworkCreator;
import com.cylinder.www.env.person.businessobject.Donor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Alvin on 2015/3/3.
 */
public class SendVideoThread extends Thread {

    private InterfaceNetwork network =null;
    private InterfaceNetworkCreator NetworkCreator = null;

    int r;
    public SendVideoThread(int r) {
        this.r = r;
        NetworkCreator = new ServerNetworkCreator();
        network = NetworkCreator.creator();
    }

    @Override
    public void run() {
        super.run();
        JSONObject jsonObject = new JSONObject();
        Date date = new Date();
        long elapsedtime = date.getTime();

        byte[] videoByte = getBytesFromFile("/sdcard/video.mp4");



        // Put the byte array into the JSONArray.
        JSONArray jsonArray = new JSONArray();
        for(byte rgb: videoByte)
            jsonArray.put(rgb);

        try {
            jsonObject.put("createTime",elapsedtime);
            jsonObject.put("recordTime",elapsedtime);
            jsonObject.put("length",videoByte.length);
            jsonObject.put("donorId", Donor.getInstance().getDonorID());
            jsonObject.put("machineId","1898");
            jsonObject.put("data", jsonArray);
            jsonObject.put("recognitionResult",r);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerInformationTransaction serverInformationTransaction = new ServerInformationTransaction();
        serverInformationTransaction.sendToServer(network.getJsonURL()+"/rest/videos",jsonObject);
    }

    /**
     * 文件转化为字节数组
     *
     * @param
     * @return
     */
    public static byte[] getBytesFromFile(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024*1024);
            byte[] b = new byte[1024*1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
