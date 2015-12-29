package com.cylinder.www.thread;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Message;
import android.util.Log;

import com.cylinder.www.env.Signal;
import com.cylinder.www.env.TimeInterval;
import com.cylinder.www.env.person.businessobject.Donor;
import com.cylinder.www.hardware.CameraManager;
import com.cylinder.www.ui.FarewellFragment;
import com.cylinder.www.ui.MainActivity;
import com.cylinder.www.ui.PlayVideoFragment;
import com.cylinder.www.ui.R;
import com.cylinder.www.ui.SloganFragment;
import com.cylinder.www.ui.WelcomeFragment;
import com.cylinder.www.utils.ShowGif;

import java.lang.ref.SoftReference;
import java.util.Observable;

/**
 * Created by Administrator on 2015/9/13 0013.
 */
public class ObserverZXDCSignalUIHandler extends android.os.Handler implements java.util.Observer {

    private SoftReference<MainActivity> srMActivity;
    private ShowGif showGif = null;
    private Boolean isDeal = true;

    public Boolean getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(Boolean isDeal) {
        this.isDeal = isDeal;
    }

    public ObserverZXDCSignalUIHandler(SoftReference<MainActivity> mActivity) {
        Log.e("camera", "ObserverZXDCSignalUIHandler constructor" + mActivity.get().toString());

        this.srMActivity = mActivity;
    }

    @Override
    public void handleMessage(Message msg) {

        super.handleMessage(msg);
        if (isDeal) {
            switch ((Signal) msg.obj) {

                // The nurse make sure the info of the donor is right.
                case CONFIRM:
                    Log.e("camera", "ObserverZXDCSignalUIHandler-CONFIRM");

                    dealSignalConfirm(this);
                    break;

                // The nurse punctuate the donor.
                case PUNCTURE:
                    dealSignalPuncture(this);
                    break;

                // Start the collection of plasma.
                case START:
                    dealSignalStart(this);
                    break;

                // The pressure is not enough, recommend the donor to make a fist.
                case FIST:
                    dealSignalFist(this);
                    break;

                // The collection is over.
                case END:
                    dealSignalEnd(this);
                    break;
                case TOHOME:
                    // Initialize the first fragment which is the slogan which says "献血献浆同样光荣！"
                    SloganFragment sloganFragment = SloganFragment.newInstance(srMActivity.get().getString(R.string.slogan), "");
                    // Construct the fragment manager to manager the fragment.
                    FragmentManager fragmentManager = srMActivity.get().getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    // Add whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.main_ui_fragment_container, sloganFragment);
                    // Commit the transaction which won't occur immediately.
                    transaction.commit();
                    break;

                default:
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Message msg = Message.obtain();
        switch ((Signal) data) {
            case CONFIRM:
                msg.obj = Signal.CONFIRM;
                sendMessage(msg);
                break;

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
                break;
        }
    }

    private void dealSignalConfirm(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalConfirm");
        if (observerMainHandler.srMActivity.get().getVisibility()) {
            Log.e("error", "dealSignalConfirm true");
            // The main ui switches to welcome fragment which says "某某，欢迎你来献浆！"
            Donor donor = Donor.getInstance();
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance(srMActivity.get().getString(R.string.sloganoneabove), donor.getUserName() + ", " + srMActivity.get().getString(R.string.sloganonebelow));
            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, welcomeFragment).commit();

        } else {
            Log.e("error", "dealSignalConfirm false");

        }
    }

    private void dealSignalPuncture(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalPuncture");

//        if (observerMainHandler.srMActivity.get().getVisibility()) {
//            Log.e("error", "dealSignalPuncture true");
////            observerMainHandler.srMActivity.get().getActionBar().hide();
//            // Begin playing the promotion video.
//            observerMainHandler.srMActivity.get().playVideoFragment = PlayVideoFragment.newInstance("", "");
//            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, observerMainHandler.srMActivity.get().playVideoFragment).commit();
//        } else {
//            Log.e("error", "dealSignalPuncture false");
//
//        }

    }

    private void dealSignalStart(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalStart");
        if (observerMainHandler.srMActivity.get().getVisibility()) {
            Log.e("error", "dealSignalStart true");

            // Start recording the video.
            CameraManager.getFrontInstance(observerMainHandler.srMActivity.get()).startRecord(observerMainHandler.srMActivity.get().surfaceViewPreview);
//            observerMainHandler.srMActivity.get().getActionBar().hide();
            // Begin playing the promotion video.
            observerMainHandler.srMActivity.get().playVideoFragment = PlayVideoFragment.newInstance("", "");
            observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, observerMainHandler.srMActivity.get().playVideoFragment).commit();
        } else {
            Log.e("error", "dealSignalStart false");

        }


        {
            TimeInterval.getInstance().setStartTime(System.currentTimeMillis());
            Log.e("error", "开始");
        }
    }

    private void dealSignalFist(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "dealSignalFist");

        if (observerMainHandler.srMActivity.get().getVisibility()) {
            Log.e("error", "dealSignalFist true");
            if (observerMainHandler.showGif == null) {

                observerMainHandler.showGif = new ShowGif(observerMainHandler.srMActivity.get().iv, "makeafist.gif", 10, observerMainHandler.srMActivity.get());
                observerMainHandler.showGif.start();
            } else if (!observerMainHandler.showGif.isShowing()) {
                observerMainHandler.showGif = new ShowGif(observerMainHandler.srMActivity.get().iv, "makeafist.gif", 10, observerMainHandler.srMActivity.get());
                observerMainHandler.showGif.start();
            }
        } else {
            Log.e("error", "dealSignalFist false");

        }
    }

    private void dealSignalEnd(ObserverZXDCSignalUIHandler observerMainHandler) {
        Log.e("error", "结束");
        Donor donor = Donor.getInstance();
        String slogan = srMActivity.get().getString(R.string.slogantwoabove);
        String thanks = donor.getUserName() + ", " + srMActivity.get().getString(R.string.slogantwoabelow);
        FarewellFragment farewellFragment = FarewellFragment.newInstance(slogan, thanks);

        observerMainHandler.srMActivity.get().getFragmentManager().beginTransaction().replace(R.id.main_ui_fragment_container, farewellFragment).commit();

        if (CameraManager.isRecord()) {
            CameraManager.getFrontInstance(observerMainHandler.srMActivity.get()).stopRecord(srMActivity.get(), true);
        }
    }

}


