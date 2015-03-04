package com.cylinder.www.ui;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.cylinder.www.utils.ShowGif;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements SurfaceHolder.Callback,ToolBarFragment.OnToolBarOnClickListener{

    private Donor donor = Donor.getInstance();
    private TextView welcomeTextView;
    private InterfaceTypeface XKface;
    InterfaceTypefaceCreator typefaceCreator;
    private View toolbarFragment;
    private SurfaceView surfaceViewPreview;
    private TakePictureHandler takePictureHandler;
    private ObservableMainActivityListenerThread observableMainActivityListenerThread;
    private ObserverMainHandler observerMainHandler;
    private SloganFragment sloganFragment;
    private VideoFragment videoFragment;
    public Handler handler;
    private AppointmentFragment appointmentFragment;
    private AssessmentFragment assessmentFragment;
    private ImageView iv;
    private  PromotionFragment promotionFragment;
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

        sloganFragment = new SloganFragment();
        getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,sloganFragment).commit();

        welcomeTextView = (TextView)this.findViewById(R.id.tv_welcome);
        welcomeTextView.setTypeface(XKface.getTypeface());
        Log.e("error", donor.getDonorID() + "");
        welcomeTextView.setText(donor.getUserName() + ":  欢迎您来献浆！");

        iv = (ImageView) this.findViewById(R.id.ivHint);


        toolbarFragment = this.findViewById(R.id.tool_bar_fragment);
        toolbarFragment.setVisibility(View.GONE);



        surfaceViewPreview = (SurfaceView) this.findViewById(R.id.surfaceview_preview);
        surfaceViewPreview.getHolder().addCallback(this);



        takePictureHandler = new TakePictureHandler(this);

        observerMainHandler = new ObserverMainHandler(new WeakReference<MainActivity>(MainActivity.this));
        observableMainActivityListenerThread = new ObservableMainActivityListenerThread(Donor.getInstance());
        observableMainActivityListenerThread.addObserver(observerMainHandler);
        observableMainActivityListenerThread.start();

        Log.e("error", "MainActivity==onCreate");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        toolbarFragment.setVisibility(View.VISIBLE);
                        assessmentFragment = new AssessmentFragment();
                        //Create new fragment and transaction
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        //Replace whatever is in the fragment_container view with this fragment,
                        //and add the transaction to the back stack
                        transaction.replace(R.id.main_ui_fragment, assessmentFragment);
                        transaction.addToBackStack(null);
                        //Commit the transaction
                        transaction.commit();

//                        getFragmentManager().beginTransaction().add(R.id.main_ui_fragment, assessmentFragment).commit();
                        break;
                    case 1:
                        toolbarFragment.setVisibility(View.GONE);
                        PlayVideoFragment playVideoFragment =new PlayVideoFragment();
                        getFragmentManager().beginTransaction().add(R.id.main_ui_fragment, playVideoFragment).commit();
                        break;
                    case 3:
                        toolbarFragment.setVisibility(View.VISIBLE);
                        VideoFragment v =new VideoFragment();
                        getFragmentManager().beginTransaction().add(R.id.main_ui_fragment, v).commit();
                        break;

                }

            }
        };
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

        Log.e("error","MainActivity==surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("error","MainActivity==surfaceDestroyed");
    }


    @Override
    public void onButtonSelected(Signal s) {


        switch(s){
            case TOVIDEO:
                videoFragment = new VideoFragment();
                getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,videoFragment).commit();
                break;
            case TOAPPOINTMENT:
                appointmentFragment = new AppointmentFragment();
                getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,appointmentFragment).commit();
                break;
            case TOEVALUATION:
                assessmentFragment = new AssessmentFragment();
                getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,assessmentFragment).commit();
                break;

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
//                    Toast.makeText(mActivity.get(), "开始拍照", Toast.LENGTH_SHORT).show();

                    CameraManager.getInstance().takePicture(new SendPictureThread(this));
                    break;

                case ENDCAPTURE:
//                    Toast.makeText(mActivity.get(), "结束拍照", Toast.LENGTH_SHORT).show();
                    recordVideo(mActivity.get().surfaceViewPreview,1);

                    break;

                case LAUNCHVIDEO:
//                    Toast.makeText(mActivity.get(), "启动视频", Toast.LENGTH_SHORT).show();
                    recordVideo(mActivity.get().surfaceViewPreview,0);

                    break;
                default:

            }
        }

    }

    private static class ObserverMainHandler extends Handler implements java.util.Observer {

        private WeakReference<MainActivity> mActivity;
        private ShowGif showGif=null;

        private ObserverMainHandler(WeakReference<MainActivity> mActivity) {
            this.mActivity = mActivity;

        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch((Signal)msg.obj){
                case PUNCTURE:
                    mActivity.get().welcomeTextView.setVisibility(View.GONE);
                    mActivity.get().promotionFragment = new PromotionFragment();
                    mActivity.get().getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,mActivity.get().promotionFragment).commit();

//                    PlayVideoFragment p = new PlayVideoFragment();
//                    mActivity.get().getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,p).commit();

//                    VideoFragment v = new VideoFragment();
//                    mActivity.get().getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,v).commit();


                    //播放视频
                    Log.e("error","播放视频");
                    break;
                case START:
                    msg = Message.obtain();
                    msg.obj = Signal.CAPTURE;
                    mActivity.get().takePictureHandler.sendMessageDelayed(msg,2000);
                    TimeInterval.getInstance().setStartTime(System.currentTimeMillis());
                    Log.e("error","开始");
                    break;
                case FIST:
                    if(showGif == null)
                    {

                        showGif = new ShowGif(mActivity.get().iv,"makeafist.gif",10,mActivity.get());
                        showGif.start();
                    }
                    else if(!showGif.isShowing()){
                        showGif = new ShowGif(mActivity.get().iv,"makeafist.gif",10,mActivity.get());
                        showGif.start();
                    }

                    Log.e("error","握拳");
                    break;
                case END:
                    Log.e("error","结束");
                    mActivity.get().sloganFragment = new SloganFragment();
                    mActivity.get().getFragmentManager().beginTransaction().add(R.id.main_ui_fragment,mActivity.get().sloganFragment).commit();

                    mActivity.get().welcomeTextView = (TextView) mActivity.get().findViewById(R.id.tv_welcome);
                    mActivity.get().welcomeTextView.setTypeface(mActivity.get().XKface.getTypeface());
                    mActivity.get().welcomeTextView.setVisibility(View.VISIBLE);
                    mActivity.get().welcomeTextView.setText(mActivity.get().donor.getUserName() + ":  谢谢您的献浆！");
                    mActivity.get().toolbarFragment.setVisibility(View.GONE);
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



    private static void recordVideo(SurfaceView s, final int r){
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

                new SendVideoThread(r).start();
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
