package com.cylinder.www.thread;

import android.util.Log;

import com.cylinder.www.env.net.softfanftp.FtpSenderFile;
import com.cylinder.www.env.net.softfanftp.SoftFanFTPException;
import com.cylinder.www.hardware.CameraManager;
import com.cylinder.www.ui.MainActivity;
import com.cylinder.www.utils.file.SelfFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alvin on 2015/3/3.
 */
public class SendVideoThread extends Thread {

    String lPath, rPath;
    MainActivity mainActivity;
    Boolean isEndSignal;
    String resultStr;

    public SendVideoThread(String localPath, String remotePath, MainActivity mainActivity, Boolean isEndSignal) {
        this.lPath = localPath;
        this.rPath = remotePath;
        this.mainActivity = mainActivity;
        this.isEndSignal = isEndSignal;
    }

    @Override
    public void run() {
        super.run();
        long start = System.currentTimeMillis();

        File file = new File(this.lPath);
        boolean b = file.exists();
        if (b) {
            Log.e("camera", "file exists!");
        } else {
            Log.e("camera", "file does not exists!");
        }

        Log.e("camera", "SendVideoThread==run start 1");

        FtpSenderFile sender = new FtpSenderFile("192.168.0.94", 13021);

        try {
            Log.e("camera", "SendVideoThread==run start 2");
            Log.e("camera EXHAUST TIME ", lPath);
            Log.e("camera EXHAUST TIME ", rPath);
            resultStr = sender.send(lPath, rPath);

            Log.e("camera", "SendVideoThread==run start 2");

        } catch (SoftFanFTPException e) {
            Log.e("camera", "SendVideoThread==run start 3");

            e.printStackTrace();
        } catch (IOException e) {
            Log.e("camera", "SendVideoThread==run start 4");

            e.printStackTrace();
        } catch (Exception e) {
            Log.e("camera", "SendVideoThread==run start 5");
        }


        long end = System.currentTimeMillis();
        Log.e("camera EXHAUST TIME ", (end - start)  + "");


        Log.e("camera EXHAUST TIME ", resultStr);

        if (resultStr.equals("传送成功")) {
            // success and delete the video file.
            SelfFile.delFile(lPath);
        } else {
            // save the video if failure.
            File srcFile = SelfFile.createNewFile(SelfFile.generateLocalVideoName());
            File destFile = SelfFile.createNewFile(SelfFile.generateLocalBackupVideoName());
            try {
                SelfFile.copyFile(srcFile, destFile);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        if (isEndSignal) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
            this.mainActivity.finish();
        }

    }
}
