package com.cylinder.www.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.cylinder.www.env.Mode;
import com.cylinder.www.env.Signal;
import com.cylinder.www.env.font.AbstractTypeface;
import com.cylinder.www.env.font.AbstractTypefaceCreator;
import com.cylinder.www.env.font.XKTypefaceCreator;
import com.cylinder.www.env.person.businessobject.Donor;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.logging.Handler;


public class LaunchActivity extends Activity {

    AbstractTypeface XKface;
    AbstractTypefaceCreator typefaceCreator;

    TextView textViewWelcome;

    Donor donor=Donor.getInstance();
    ObserverLaunchHandler observerLaunchHandler = new ObserverLaunchHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onCreate");
        }

        // Initialize the UI.
        setContentView(R.layout.activity_launch);

        // Given the typeface, we should construct a factory pattern for these typeface.
        typefaceCreator = new XKTypefaceCreator();
        XKface = typefaceCreator.createTypeface(this);

        textViewWelcome = (TextView) this.findViewById(R.id.tv_slogan);
        textViewWelcome.setTypeface(XKface.getTypeface());

        // Start launch listener thread.


        HandlerThread handlerThread =new HandlerThread("11");


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onStart");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onRestart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onStop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Mode.isDebug) {
            Log.e("ERROR", "LaunchActivity=onDestroy");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }


    private class ObserverLaunchHandler implements java.util.Observer{

        // Avoiding memory leaks, we use reference Activity weakly.
        WeakReference<Activity> wa = new WeakReference<Activity>(LaunchActivity.this);

        @Override
        public void update(Observable observable, Object data) {

            switch ((Signal)data){
            case CONFIRM:
                wa.get().finish();
                Intent intent = new Intent(wa.get(), MainActivity.class);
                wa.get().startActivity(intent);
                break;
            default:
            }
        }
    }

}
