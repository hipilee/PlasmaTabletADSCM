package com.cylinder.www.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import mobile.android.ch15.play.gif.GifFrames;

/**
 * Created by Alvin on 2015/3/4.
 */
public class ShowGif {
    private GifFrames gifFrames;
    private InputStream is;
    private ImageView iv;
    private int seconds;
    private Context context;
    private int count;
    private static boolean isS;
    public static synchronized boolean isShowing(){
        return isS;
    }
    public static synchronized void setIsS(boolean b){
        isS = b;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            count--;
            if(count==0){
                timer.cancel();
                iv.setVisibility(View.INVISIBLE);
                setIsS(false);
            }
            iv.setImageBitmap((Bitmap)msg.obj);
            super.handleMessage(msg);
        }
    };

    private final Timer timer = new Timer();

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Bitmap bitmap = gifFrames.getImage();
            gifFrames.nextFrame();
            if(bitmap != null){
                Message msg = Message.obtain();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        }
    };

    public ShowGif(ImageView iv, String gifPath, int seconds, Context context) {

        this.iv = iv;
String s;
        this.seconds = seconds;
        this.context = context;
        try {
            is = context.getResources().getAssets().open(gifPath);
        } catch (IOException e) {
            e.printStackTrace();
            s = e.toString();
            Log.e("error",e.toString());
        }
        gifFrames = GifFrames.createGifFrames(is);
        count = seconds*10;
        setIsS(true);

    }

    public void resetTime(){

        count = seconds*10;
    }

    public void start(){

        iv.setVisibility(View.VISIBLE);
        timer.schedule(task,0,100);
    }

    public void stop(){
        timer.cancel();
        iv.setVisibility(View.INVISIBLE);
    }
}
