package com.cylinder.www.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cylinder.www.env.Mode;
import com.cylinder.www.env.Signal;
import com.cylinder.www.env.TimeInterval;
import com.cylinder.www.env.font.InterfaceTypeface;
import com.cylinder.www.env.font.InterfaceTypefaceCreator;
import com.cylinder.www.env.font.XKTypefaceCreator;
import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.hardware.CameraManager;
import com.cylinder.www.thread.ObservableMainActivityListenerThread;
import com.cylinder.www.thread.SendPictureThread;
import com.cylinder.www.thread.SendVideoThread;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SurfaceHolder.Callback,ToolBarFragment.OnToolBarOnClickListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Donor donor = Donor.getInstance();
    private TextView welcomeTextView;
    private InterfaceTypeface XKface;
    InterfaceTypefaceCreator typefaceCreator;
    private View toolbarFragment;
    private SurfaceView surfaceViewPreview;
    private TakePictureHandler takePictureHandler;
    private ObservableMainActivityListenerThread observableMainActivityListenerThread;
    private ObserverMainHandler observerMainHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the actionBar & delete the status bar, set the screen landscape.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Given the typeface, we should construct a factory pattern for these type face.
        typefaceCreator = new XKTypefaceCreator();
        XKface = typefaceCreator.createTypeface(this);

        welcomeTextView = (TextView) this.findViewById(R.id.tv_welcome);
        welcomeTextView.setTypeface(XKface.getTypeface());

        toolbarFragment = this.findViewById(R.id.tool_bar_fragment);
        toolbarFragment.setVisibility(View.GONE);

        surfaceViewPreview = (SurfaceView) this.findViewById(R.id.surfaceview_preview);
        surfaceViewPreview.getHolder().addCallback(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setVisibility(View.GONE);

        takePictureHandler = new TakePictureHandler(this);

        observerMainHandler = new ObserverMainHandler(new WeakReference<MainActivity>(MainActivity.this));
        observableMainActivityListenerThread = new ObservableMainActivityListenerThread(Donor.getInstance());
        observableMainActivityListenerThread.addObserver(observerMainHandler);
        observableMainActivityListenerThread.start();

        Log.e("error", "MainActivity==onCreate");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("error", "MainActivity==onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("error", "MainActivity==onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("error", donor.getDonorID() + "");
        welcomeTextView.setText(donor.getUserName() + ":  欢迎您来献浆！");

//        CameraManager.getInstance().takePicture();
        Log.e("error", "MainActivity==onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("error", "MainActivity==onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("error", "MainActivity==onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInstance().stopPreview();
        CameraManager.getInstance().release();
        Log.e("error", "MainActivity==onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if(Mode.isDebug){
            Log.e("error","MainActivity==surfaceCreated");
        }
        try {
            CameraManager.getInstance().getCamera().setPreviewDisplay(holder);
            CameraManager.getInstance().setCameraDisplayOrientation(this);
        } catch (IOException e) {
            if(Mode.isDebug){
                Log.e("error","MainActivity==surfaceCreated",e);
            }
        } finally {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraManager.getInstance().getCamera().startPreview();

        // First capture;
        Message msg = Message.obtain();
        msg.obj = Signal.CAPTURE;
        takePictureHandler.sendMessageDelayed(msg,2000);
        TimeInterval.getInstance().setStartTime(System.currentTimeMillis());
        Log.e("error","MainActivity==surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("error","MainActivity==surfaceDestroyed");
    }


    @Override
    public void onButtonSelected(Signal s) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("vp","position="+position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show  total pages.
            return 4;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.e("vp","newInstance "+sectionNumber);
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            Log.e("vp","onCreateView "+getArguments().getInt(ARG_SECTION_NUMBER));
            View rootView = null;
            int sectionNO = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNO) {
                case 1:
                    Log.e("vp","fragment_video");
                    rootView = inflater.inflate(R.layout.fragment_video, container, false);
                    break;
                case 2:
                    Log.e("vp","fragment_request");
                    rootView = inflater.inflate(R.layout.fragment_request, container, false);
                    break;
                case 3:
                    Log.e("vp","fragment_appointment");
                    rootView = inflater.inflate(R.layout.fragment_appointment, container, false);
                    break;
                case 4:
                    Log.e("vp","fragment_assessment");
                    rootView = inflater.inflate(R.layout.fragment_assessment, container, false);
                    break;
                default:
                    break;
            }
            return rootView;
        }
    }


    private  static class TakePictureHandler extends Handler {

        private WeakReference<MainActivity> mActivity;

        public TakePictureHandler(MainActivity Activity) {
            super();
            this.mActivity = new WeakReference<MainActivity>(Activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch((Signal)msg.obj){

                case CAPTURE:
                    Toast.makeText(mActivity.get(), "开始拍照", Toast.LENGTH_SHORT).show();

                    CameraManager.getInstance().takePicture(new SendPictureThread(this));
                    break;

                case ENDCAPTURE:
                    Toast.makeText(mActivity.get(), "结束拍照", Toast.LENGTH_SHORT).show();
                    recordVideo(mActivity.get().surfaceViewPreview);

                    break;

                case LAUNCHVIDEO:
                    Toast.makeText(mActivity.get(), "启动视频", Toast.LENGTH_SHORT).show();
                    recordVideo(mActivity.get().surfaceViewPreview);

                    break;
                default:

            }
        }

    }

    private static class ObserverMainHandler extends Handler implements java.util.Observer {

        private WeakReference<MainActivity> mActivity;
        private ObserverMainHandler(WeakReference<MainActivity> mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e("error","mmmmmmmmmmsssssssssssssssssggggggggggg");
            super.handleMessage(msg);
            switch((Signal)msg.obj){
                case PUNCTURE:
                    mActivity.get().welcomeTextView.setVisibility(View.GONE);
                    break;
                default:
            }
        }

        @Override
        public void update(Observable observable, Object data) {
            Message msg = Message.obtain();
            switch ((Signal)data){
                case PUNCTURE:

                    msg.obj = Signal.PUNCTURE;
                    sendMessage(msg);
                    break;
                case START:
                    msg.obj = Signal.START;
                    sendMessage(msg);
                    break;
                case FIST:
                    msg.obj = Signal.FIST;
                    sendMessage(msg);
                    break;
                case END:
                    msg.obj = Signal.END;
                    sendMessage(msg);
                    break;
                default:
            }
        }
    }



    private static void recordVideo(SurfaceView s){
        final MediaRecorder mediaRecorder =new MediaRecorder();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                mediaRecorder.stop();
                mediaRecorder.release();

                CameraManager.getInstance().getCamera().lock();
                CameraManager.getInstance().stopPreview();
                CameraManager.getInstance().release();

                new SendVideoThread().start();
            }
        };
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        CameraManager.getInstance().getCamera().startPreview();

        CameraManager.getInstance().getCamera().unlock();

        mediaRecorder.setCamera(CameraManager.getInstance().getCamera());

//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4                                                                    );
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoSize(640, 480);

        mediaRecorder.setOutputFile("/sdcard/video.mp4");

        mediaRecorder.setPreviewDisplay(s.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        timer.schedule(task,1000*10);
    }
}
