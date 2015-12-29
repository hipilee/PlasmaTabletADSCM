package com.cylinder.www.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cylinder.www.env.net.FilterSignal;
import com.cylinder.www.env.net.RecordState;
import com.cylinder.www.env.net.listener.WatcherKeyReceiver;
import com.cylinder.www.hardware.CameraManager;
import com.cylinder.www.thread.ObservableZXDCSignalListenerThread;
import com.cylinder.www.thread.ObserverZSDCSignalRecordAndFilter;
import com.cylinder.www.thread.ObserverZXDCSignalUIHandler;
import com.cylinder.www.utils.time.SyncTime;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Random;

public class MainActivity extends FragmentActivity implements SurfaceHolder.Callback, ConfigurationFragment.OnFragmentInteractionListener {


    public PlayVideoFragment playVideoFragment;
    public ImageView iv;
    public SurfaceView surfaceViewPreview;
    public View toolbarFragmentContainer;
    private ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread;
    private ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler;
    private ObserverZSDCSignalRecordAndFilter observerZSDCSignalRecordAndFilter;
    private SloganFragment sloganFragment;
    // Whether the worker close the APP.
    private Boolean isFinish = false;
    private Boolean visibility = false;
    private WatcherKeyReceiver mWatcherKeyReceiver = null;
    private SyncWork syncWork;
    private RecordState recordState;
    private FilterSignal filterSignal;
    private Intent uploadIntent;
    private FrameLayout configurationFragmentContainer;

    PowerManager.WakeLock mWakelock;
    KeyguardManager km;
    PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent forceClosingIntent = getIntent();

        Log.e("camera", "onCreate begin  " + "  " + Thread.currentThread().getId() + this.toString() + forceClosingIntent.getStringExtra("reason"));
        if (savedInstanceState != null) {
            Log.e("camera", savedInstanceState.getString("msg"));
        }

        // Don't show the status bar, set the screen landscape, and keep the screen light all the time.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");

        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();  //解锁
        mWakelock.acquire();//点亮

        // Initialization
        setContentView(R.layout.activity_main);

        // Initialize the UI component.
        initializeUI();

        // Initialize the object which used in the interaction of signal.
        initializeCommunication();
        Log.e("camera", "onCreate end  ");
    }

    private void initializeUI() {

        // Initialize the first fragment which is the slogan which says "献血献浆同样光荣！"
        sloganFragment = SloganFragment.newInstance(getString(R.string.slogan), "");
        // Construct the fragment manager to manager the fragment.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Add whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.add(R.id.main_ui_fragment_container, sloganFragment);
        // Commit the transaction which won't occur immediately.
        transaction.commit();

        // The container to hold the configuration view.
        configurationFragmentContainer = (FrameLayout) this.findViewById(R.id.configuration_fragment_container);

        // The toolbarFragmentContainer to show some long-time visible info such as time and calling for help button.
        toolbarFragmentContainer = this.findViewById(R.id.tool_bar_fragment);
        toolbarFragmentContainer.setVisibility(View.GONE);
        // The image view to show hint animation such as the fist hint.
        iv = (ImageView) toolbarFragmentContainer.findViewById(R.id.ivAnimaiton);

        // The surface view is to show the video stream.The camera surfaceView and holder is conform to the MVC pattern.
        surfaceViewPreview = (SurfaceView) this.findViewById(R.id.surface_view_preview);
        // Make sure the surfaceView holder and camera ready and then do other things.
        // The prepare work finishes after surfaceCreate() function.
        // This method addCallback() is asynchronous, So! you should do something in surfaceCreated() method
        // which need that camera is ready.
        surfaceViewPreview.getHolder().addCallback(this);

    }

    private void initializeCommunication() {

        // Synchronize the time with the server.
        // TODO
        SyncTime.syncTime(null);

        // Used to record the signals received.
        recordState = new RecordState(this);
        recordState.retrieve();

        // Used to filter the unnecessary
        filterSignal = new FilterSignal();

        // Deal the difference whether the onPause() or observableZXDCSignalListenerThread is firstly executed.
        syncWork = new SyncWork();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            Log.e("camera", "surfaceCreated " + this.toString());

            // Initialize the front camera.
            CameraManager.getFrontInstance(this).getCamera().setPreviewDisplay(holder);

            // Start the listener thread to listen the signal from the server,
            // the reason why we start the thread here is to make sure the camera holder surface
            // surfaceView all ready.
            // The observerZXDCSignalUIHandler, observerZSDCSignalRecordAndFilter and observableZXDCSignalListenerThread use the observer pattern.
            observerZSDCSignalRecordAndFilter = new ObserverZSDCSignalRecordAndFilter(recordState, filterSignal);
            observerZXDCSignalUIHandler = new ObserverZXDCSignalUIHandler(new SoftReference<MainActivity>(this));
            observableZXDCSignalListenerThread = new ObservableZXDCSignalListenerThread(recordState, filterSignal);

            // Add the observers into the observable object.
            observableZXDCSignalListenerThread.addObserver(observerZXDCSignalUIHandler);
            observableZXDCSignalListenerThread.addObserver(observerZSDCSignalRecordAndFilter);
            //********************************
            syncWork.workAfterSurfaceCreated(observableZXDCSignalListenerThread, observerZXDCSignalUIHandler);

        } catch (IOException e) {

        } finally {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("camera", "surfaceChanged " + this.toString());

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("camera", "surfaceDestroyed " + this.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.network_settings:
                dealNetworkSettings();
                break;

            case R.id.app_finish:
                dealAPPFinish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dealNetworkSettings() {
        configurationFragmentContainer.setVisibility(View.VISIBLE);
    }

    private void dealAPPFinish() {
        this.isFinish = true;
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("camera", "onStart " + this.toString());
        registerWatcherKeyReceiver(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("camera", "onRestart " + this.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        setVisibility(true);

        Log.e("camera", "onResume" + this.toString());

//        uploadIntent = new Intent(this, UpLoadBackupVideoIntentService.class);
//        uploadIntent.setAction("upload");
//        startService(uploadIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("camera", "onPause begin" + this.toString());

        setVisibility(false);

        syncWork.workAfterOnPause(observableZXDCSignalListenerThread, observerZXDCSignalUIHandler);

        Log.e("camera", "onPause end" + this.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("camera", "onStop " + this.toString());
        CameraManager.release();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("camera", "onDestroy " + this.toString());
        CameraManager.release();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            // The donor can't use the BACK button to close the APP.
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerWatcherKeyReceiver(Context context) {
        Log.i("camera", "registerHomeKeyReceiver");
        this.mWatcherKeyReceiver = new WatcherKeyReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(this.mWatcherKeyReceiver, homeFilter);
    }

    private void unregisterWatcherKeyReceiver(Context context) {
        Log.i("camera", "unregisterHomeKeyReceiver");
        if (null != this.mWatcherKeyReceiver) {
            context.unregisterReceiver(this.mWatcherKeyReceiver);
        }
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }


    private class SyncWork {
        private Boolean isFirst = true;

        public synchronized void workAfterSurfaceCreated(ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread, ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler) {
            if (isFirst) {
                Log.e("camera", "workAfterSurfaceCreated true" + MainActivity.this.toString());
                isFirst = false;
                observableZXDCSignalListenerThread.start();
            } else {
                Log.e("camera", "workAfterSurfaceCreated false" + MainActivity.this.toString());
                CameraManager.release();
            }
        }

        public synchronized void workAfterOnPause(ObservableZXDCSignalListenerThread observableZXDCSignalListenerThread, ObserverZXDCSignalUIHandler observerZXDCSignalUIHandler) {
            if (isFirst) {// this logical come out because the sleep button is pressed.
                isFirst = false;
                Log.e("camera", "workAfterOnPause true" + MainActivity.this.toString());

                unregisterWatcherKeyReceiver(MainActivity.this);
                if (CameraManager.isRecord()) {
                    CameraManager.stopRecord(MainActivity.this, false);
                }
                CameraManager.release();
                MainActivity.this.finish();
//                ***************************
                Intent intentToNewMainActivity = new Intent(MainActivity.this, MainActivity.class);

                int a = new Random().nextInt(100);
                Log.e("camera", "workAfterOnPause false isFinish" + a + MainActivity.this.toString());
                intentToNewMainActivity.putExtra("reason", "foreclosing  =  " + a + "  =  ");
                startActivity(intentToNewMainActivity);
//                *****************************************

            } else {
                Log.e("camera", "workAfterOnPause false" + MainActivity.this.toString());

                if (!isFinish) {
                    Intent intentToNewMainActivity = new Intent(MainActivity.this, MainActivity.class);

                    int a = new Random().nextInt(100);
                    Log.e("camera", "workAfterOnPause false isFinish" + a + MainActivity.this.toString());
                    MainActivity.this.finish();
                    intentToNewMainActivity.putExtra("reason", "foreclosing  =  " + a + "  =  ");
                    startActivity(intentToNewMainActivity);
                }
                Log.e("camera", "workAfterOnPause false " + MainActivity.this.toString());
                // Stop the listener thread.
                observableZXDCSignalListenerThread.setIsContinue(false);
                Log.e("camera", "workAfterOnPause false 1" + MainActivity.this.toString());

                // Stop dealing the signal from ZXDC.
                observerZXDCSignalUIHandler.setIsDeal(false);
                Log.e("camera", "workAfterOnPause false 2" + MainActivity.this.toString());

                // Wait the Listener thread to stop receiving the signal.
                observableZXDCSignalListenerThread.commitSignal(isFinish);
                Log.e("camera", "workAfterOnPause false 3" + MainActivity.this.toString());

                if (CameraManager.isRecord()) {
                    CameraManager.stopRecord(MainActivity.this, false);
                }
                Log.e("camera", "workAfterOnPause false 4" + MainActivity.this.toString());

                CameraManager.release();
                Log.e("camera", "workAfterOnPause false 5" + MainActivity.this.toString());

                unregisterWatcherKeyReceiver((MainActivity.this));
                Log.e("camera", "workAfterOnPause false 6" + MainActivity.this.toString());

            }
        }
    }

    @Override
    public void onFragmentInteraction(int id) {

        switch (id) {
            case R.id.savebutton:
                Log.e("onFragmentInteraction", "save");
                break;
            case R.id.quitbutton:
                Log.e("onFragmentInteraction", "quit");
                configurationFragmentContainer.setVisibility(View.INVISIBLE);
                break;
        }
    }


}
