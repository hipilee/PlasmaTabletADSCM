package com.cylinder.www.hardware;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.cylinder.www.env.CollectionFaceInfo;
import com.cylinder.www.thread.SendPictureThread;
import com.cylinder.www.thread.SendVideoThread;
import com.cylinder.www.ui.MainActivity;
import com.cylinder.www.utils.file.SelfFile;
import com.cylinder.www.utils.time.TimeRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by xiutao on 2014/9/19.
 */
public class CameraManager {
    private static Camera camera = null;

    public static boolean isRecord() {
        return isRecord;
    }

    public static void setIsRecord(boolean isRecord) {
        CameraManager.isRecord = isRecord;
    }

    private static boolean isRecord = false;
    private static CameraManager ourInstance = null;
    private static MediaRecorder mediaRecorder = null;
    private static Camera.CameraInfo cameraInfo = null;

    private CameraManager() {

    }

    // there is something wrong!
    public synchronized static CameraManager getFrontInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new CameraManager();
            cameraInfo = new Camera.CameraInfo();

            // Get the front camera.
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {

                Camera.getCameraInfo(camIdx, cameraInfo);

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        CameraManager.camera = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                    }
                    break;
                }
            }
            mediaRecorder = new MediaRecorder();


            setCameraDisplayOrientation((Activity) context);

            setPictureSize();

        } else if (cameraInfo.facing != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            release();
        }

        return ourInstance;
    }


    public synchronized static CameraManager getBackInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new CameraManager();

            cameraInfo = new Camera.CameraInfo();

            // Get the front camera.
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {

                Camera.getCameraInfo(camIdx, cameraInfo);

                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    try {
                        CameraManager.camera = Camera.open(camIdx);
                    } catch (RuntimeException e) {

                    }
                    break;
                }
            }
            mediaRecorder = new MediaRecorder();

            setCameraDisplayOrientation((Activity) context);

            setPictureSize();


        } else if (cameraInfo.facing != Camera.CameraInfo.CAMERA_FACING_BACK) {
            release();
        }

        return ourInstance;
    }

    private static void setPictureSize() {

        Camera.Parameters campara = camera.getParameters();

        List<Camera.Size> listPictureSize;
        listPictureSize = campara.getSupportedVideoSizes();
        int listSize = listPictureSize.size();
//        for (int i = 0; i < listSize; i++) {
//            Log.e("error", listPictureSize.get(i).width + "   " + listPictureSize.get(i).height);
//        }
//        int biggestindex = listPictureSize.get(0).width+listPictureSize.get(0).height > listPictureSize.get(listSize-1).width+listPictureSize.get(listSize-1).height?
//                0:listSize-1;

        campara.setPictureSize(800, 600);
        camera.setParameters(campara);
    }

    // Make sure the preview image displaying on the tablet properly in orientation.
    private static void setCameraDisplayOrientation(Activity activity) {

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
            // compensate the mirror
        } else {
            // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void startRecord(SurfaceView surfaceView) {
        TimeRecord.getInstance().setStartDate(new Date());
        if (camera != null) {
            camera.startPreview();
            camera.unlock();
        }

        if (mediaRecorder != null) {
            // Prepare the directories and files for storing and backing videos.
            SelfFile.createDir(SelfFile.generateLocalVideoDIR());

            SelfFile.createDir(SelfFile.generateLocalBackupVideoDIR());
            // Configure the MediaRecorder
            mediaRecorder.setCamera(camera);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错.
            mediaRecorder.setVideoEncodingBitRate(500000);
            mediaRecorder.setVideoSize(1280, 720);
            Log.e("camera", "startRecord 1");
            mediaRecorder.setOutputFile(SelfFile.generateLocalVideoName());
            Log.e("camera", SelfFile.generateLocalVideoName());
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("camera", e.toString());
            }
            Log.e("camera", "startRecord 2");

            mediaRecorder.start();

            Log.e("camera", "startRecord 3");

        }
        Log.e("camera", "startRecord 4");

        isRecord = true;

    }

    public static void stopRecord(Context context, Boolean isEndSignal) {
        Log.e("camera", " stopRecord 1");
        TimeRecord.getInstance().setEndDate(new Date());

        Log.e("camera", " stopRecord 2");

        Log.e("camera", " stopRecord 3");

        // The stop_recording
        if (mediaRecorder != null) {
            // clean the mediaRecorder.
            // if the recording time is less than one seconds,then stop will fail.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
            Log.e("camera", " stopRecord 4");
            mediaRecorder.stop();

            Log.e("camera", " stopRecord 5");
            mediaRecorder.release();
            Log.e("camera", " stopRecord 6");
            mediaRecorder = null;

            new SendVideoThread(SelfFile.generateLocalVideoName(), SelfFile.generateRemoteVideoName(), (MainActivity) context, isEndSignal).start();
        }

        //        The stop_recording must be called before the stop_preview
        if (camera != null) {
            // Clean the camera.
            Log.e("camera", " stopRecord 7" + SelfFile.generateLocalVideoName());
            camera.lock();
            camera.stopPreview();
            Log.e("camera", " stopRecord 8" + SelfFile.generateRemoteVideoName());
            camera.release();
            camera = null;
        }

        isRecord = false;

    }

    public Boolean takePicture(SendPictureThread sendPictureThread) {
        TakePictureCallback takePictureCallback = new TakePictureCallback(sendPictureThread);
        AutofocusCallback autofocusCallback = new AutofocusCallback(takePictureCallback);
        camera.autoFocus(autofocusCallback);
        return true;
    }

    private final class AutofocusCallback implements Camera.AutoFocusCallback {

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
            Log.e("wh", cameraBitmap.getHeight() + " " + cameraBitmap.getWidth());

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            cameraBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            try {
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                SelfFile.saveBitmapToFile(cameraBitmap, "/sdcard/camera.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            CollectionFaceInfo.getInstance().setPicture(os.toByteArray());
            sendPictureThread.start();
        }
    }

    public static void release() {

        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        ourInstance = null;
        cameraInfo = null;
    }




}
