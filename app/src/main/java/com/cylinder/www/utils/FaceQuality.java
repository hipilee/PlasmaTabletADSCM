package com.cylinder.www.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.util.Log;

/**
 * Created by xiutao on 2014/9/19.
 */
public class FaceQuality {

    int numberOfAllocationFace=2;
    private static FaceQuality ourInstance = new FaceQuality();

    public static FaceQuality getInstance() {
        return ourInstance;
    }

    private FaceQuality() {
    }

    public boolean evaluate(byte[] face, float confidence) {

        // Create the rawBitmap and then copy it, at present i have no idea why we should copy it, if not it won't work in order.
        Bitmap rawBitmap = BitmapFactory.decodeByteArray(face,0,face.length);
        Bitmap faceBitmap = rawBitmap.copy(Bitmap.Config.RGB_565, true);


        // Allocate the memory for face array and set the minimum confidence for face and face number to detect.
        FaceDetector.Face[] detectedFace = new FaceDetector.Face[numberOfAllocationFace];
        FaceDetector myFaceDetect = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), numberOfAllocationFace);
        int numberOfFaceDetected = myFaceDetect.findFaces(faceBitmap, detectedFace);


        // The specification for face picture:
        // 1.The distance between tow eyes is more than 60 pixels.
        // 2.YAW [-15:+15]   PITCH [-10:10]  ROLL[-15:-15]
        if (detectedFace[0] != null) {
            Log.e("error", "numberOfFaceDetected " + numberOfFaceDetected);

            Log.e("error", " confidence " + detectedFace[0].confidence());
//
//            PointF f = new PointF();
//            detectedFace[0].getMidPoint(f);
//            Log.e("error", " midPoint "+f.x+" "+f.y);
//            Log.e("error","distance "+detectedFace[0].eyesDistance());
//            Log.e("error", " x "+detectedFace[0].pose(Face.EULER_X));
//            Log.e("error", " y "+detectedFace[0].pose(Face.EULER_Y));
//            Log.e("error", " z "+detectedFace[0].pose(Face.EULER_Z));
            if (detectedFace[0].confidence() > confidence && detectedFace[0].eyesDistance() > 100) {

                return true;
            }


            return false;
        }
        return false;
    }
}

