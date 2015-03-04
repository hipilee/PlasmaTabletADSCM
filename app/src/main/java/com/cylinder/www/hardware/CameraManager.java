package com.cylinder.www.hardware;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import com.cylinder.www.env.CollectionFaceInfo;
import com.cylinder.www.env.Mode;
import com.cylinder.www.thread.SendPictureThread;
import com.cylinder.www.utils.Save;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by xiutao on 2014/9/19.
 */
public class CameraManager {
    private static Camera camera;
    private static int cameraID;
    private static CameraManager ourInstance = new CameraManager();


    private CameraManager(){
    }

    public static CameraManager getInstance(){
        if(camera == null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            if (Mode.isDebug) {
                Log.e("error", "camera number" + cameraCount);
            }

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {

                Camera.getCameraInfo(camIdx, cameraInfo);

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        CameraManager.camera = Camera.open(camIdx);
                        CameraManager.cameraID = camIdx;
                    } catch (RuntimeException e) {

                        if (Mode.isDebug) {
                            Log.e("error", "RuntimeException", e);
                        }
                    }
                    break;
                }
            }

            setPictureSize();
        }
        return ourInstance;
    }

    private static void setPictureSize(){

        Camera.Parameters campara = camera.getParameters();

        List<Camera.Size> listPictureSize;
        listPictureSize = campara.getSupportedPictureSizes();
        int listSize = listPictureSize.size();
        for(int i=0;i<listSize; i++){
            Log.e("error",listPictureSize.get(i).width+ "   " +listPictureSize.get(i).height);
        }
//        int biggestindex = listPictureSize.get(0).width+listPictureSize.get(0).height > listPictureSize.get(listSize-1).width+listPictureSize.get(listSize-1).height?
//                0:listSize-1;

        campara.setPictureSize(800,600);
        camera.setParameters(campara);
    }

    // Make sure the preview image displaying on the tablet properly in orientation.
    public void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0; break;

            case Surface.ROTATION_90:
                degrees = 90; break;

            case Surface.ROTATION_180:
                degrees = 180; break;

            case Surface.ROTATION_270:
                degrees = 270; break;
        }
        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
            // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;      }
        camera.setDisplayOrientation(result);
    }

    public Camera getCamera() {
        return camera;
    }

    public int getCameraID() {
        return cameraID;
    }

    public Boolean takePicture(SendPictureThread sendPictureThread){
        TakePictureCallback takePictureCallback = new TakePictureCallback(sendPictureThread);
        AutofocusCallback autofocusCallback = new AutofocusCallback(takePictureCallback);
        camera.autoFocus(autofocusCallback);
        return true;
    }

    private final class AutofocusCallback implements Camera.AutoFocusCallback{

        private TakePictureCallback takePictureCallback;

        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            camera.takePicture(null, null, takePictureCallback);

        }

        public AutofocusCallback(TakePictureCallback takePictureCallback) {
            super();
            this.takePictureCallback = takePictureCallback;
        }


    }

    public class TakePictureCallback implements Camera.PictureCallback {
        private SendPictureThread sendPictureThread;
        public TakePictureCallback(SendPictureThread sendPictureThread) {
            this.sendPictureThread = sendPictureThread;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //Remember executing the function startPreview() because camera will stop after every taking picture.
            camera.startPreview();

            //Get the image and rotate it.
//            Matrix matrix=new Matrix();
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.e("wh",cameraBitmap.getHeight()+" "+cameraBitmap.getWidth());

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            cameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            try {
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Save.saveBitmapToFile(cameraBitmap, "/sdcard/camera.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            CollectionFaceInfo.getInstance().setPicture(os.toByteArray());
            sendPictureThread.start();
        }
    }

    public void stopPreview() {
        if(camera!=null)
            camera.stopPreview();
    }

    public void release()
    {
        if(camera!=null){
            camera.release();
            camera = null;
        }
    }
}
