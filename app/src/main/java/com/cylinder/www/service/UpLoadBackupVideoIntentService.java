package com.cylinder.www.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.cylinder.www.env.net.softfanftp.FtpSenderFile;
import com.cylinder.www.env.net.softfanftp.SoftFanFTPException;
import com.cylinder.www.utils.file.SelfFile;

import java.io.File;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpLoadBackupVideoIntentService extends IntentService {

    private static String UPLOADACTION = "upload";

    public UpLoadBackupVideoIntentService() {
        super("UpLoadBackupVideoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (UPLOADACTION.equals(action)) {
                handleUpLoad();
            }
        }

    }

    private void handleUpLoad() {
        String backupVideoPath = null;
        String res = null;
        File dir = new File("/sdcard/backup");
        File[] tmp = dir.listFiles();
        for (File file : tmp) {
            Log.e("aa","1");
            if (file.isFile()) {
                backupVideoPath = file.getPath();
                FtpSenderFile sender = new FtpSenderFile("192.168.0.94", 13021);
                try {
                    res = sender.send(backupVideoPath, switchToRemotePath(backupVideoPath));
                } catch (SoftFanFTPException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }
            if (res.equals("传送成功")) {
                Log.e("aa","2");
                SelfFile.delFile("/sdcard/backup"+file.getPath().substring("/sdcard/backup".length()));
            }
        }
        stopSelf();
    }

    private String switchToRemotePath(String backupVideoPath) {
        String s = "[ShareFtp]jzVideo" + backupVideoPath.replace('_', '/').substring("/sdcard/backup".length());
        return s;
    }
}
