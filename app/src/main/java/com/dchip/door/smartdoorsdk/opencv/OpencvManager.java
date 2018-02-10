package com.dchip.door.smartdoorsdk.opencv;

import android.content.Context;

import org.opencv.android.JavaCameraView;

/**
 * Created by llakcs on 2017/11/30.
 */
public interface OpencvManager{

     /**
      * On resume.
      */
     void onResume();

     /**
      * On destroy.
      */
     void onDestroy();

     /**
      * On pause.
      */
     void onPause();

     /**
      * Init opencv.
      *
      * @param context           the context
      * @param mOpenCvCameraView the m open cv camera view
      */
     void InitOpencv(Context context,JavaCameraView mOpenCvCameraView);

     /**
      * Sets face count.
      *
      * @param count the count
      */
     void setFaceCount(int count);

     /**
      * Sets detection listner.
      *
      * @param detectionListner the detection listner
      */
     void setDetectionListner(DetectionListner detectionListner);

     /**
      * Un reg detection listner.
      */
     void unRegDetectionListner();

     /**
      * Take photo.
      *
      * @param filePath the file path
      */
     public void takePhoto(String filePath);

     /**
      * Sets take photo listener.
      *
      * @param takePhotoListener the take photo listener
      */
     public void setTakePhotoListener(TakePhotoListener takePhotoListener);

     /**
      * Un reg take photo listener.
      */
     public void unRegTakePhotoListener();
}
