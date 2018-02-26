package com.dchip.door.smartdoorsdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.dchip.door.smartdoorsdk.R;
import com.dchip.door.smartdoorsdk.event.PhotoTakenEvent;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TakePhotoService extends Service implements SurfaceHolder.Callback {
    protected static final String TAG = "TakePhotoService";
    protected WindowManager windowManager;
    WindowManager.LayoutParams params;
    ConstraintLayout toucherLayout;
    int statusBarHeight = -1;

    static final int CAMERA_ID_FRONT = 98;
    static final int CAMERA_ID_BACK = 99;
    protected Camera mCamera;
    protected SurfaceView sv;
    protected SurfaceHolder sh;
    protected int mCameraIndex = -1;
    protected boolean mWaitForTakePhoto =false;
    protected String photoPath = "";

    public TakePhotoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createToucher();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        photoPath = intent.getStringExtra("path");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(toucherLayout);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右下角位置。
     */
    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        //注意，这里的width和height均使用px而非dp.这里我偷了个懒
        //如果你想完全对应布局设置，需要先获取到机器的dpi
        //px与dp的换算为px = dp * (dpi / 160).
        params.width = 1;
        params.height = 1;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.fload, null);
        sv = (SurfaceView) toucherLayout.findViewById(R.id.takePhoto_surface);
        sh = sv.getHolder();
        sh.addCallback(this);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);
        toucherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        Log.i(TAG, "toucherlayout-->left:" + toucherLayout.getLeft());
        Log.i(TAG, "toucherlayout-->right:" + toucherLayout.getRight());
        Log.i(TAG, "toucherlayout-->top:" + toucherLayout.getTop());
        Log.i(TAG, "toucherlayout-->bottom:" + toucherLayout.getBottom());

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);

        //浮动窗口按钮.
        //其他代码...
    }

    protected boolean initializeCamera() {
        Log.d(TAG, "Initialize java camera");
        boolean result = true;
        synchronized (this) {
            mCamera = null;

            if (mCameraIndex == -1) {
                Log.d(TAG, "Trying to open camera with old open()");
                try {
                    mCamera = Camera.open();
                } catch (Exception e) {
                    Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
                }

                if (mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    boolean connected = false;
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
                        try {
                            mCamera = Camera.open(camIdx);
                            connected = true;
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
                            mCamera = null;
                            stopSelf();
                        }
                        if (connected) break;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    int localCameraIndex = mCameraIndex;
                    if (mCameraIndex == 99) {
                        Log.i(TAG, "Trying to open back camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    } else if (mCameraIndex == CAMERA_ID_FRONT) {
                        Log.i(TAG, "Trying to open front camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    }
                    if (localCameraIndex == CAMERA_ID_BACK) {
                        Log.e(TAG, "Back camera not found!");
                    } else if (localCameraIndex == CAMERA_ID_FRONT) {
                        Log.e(TAG, "Front camera not found!");
                    } else {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(localCameraIndex) + ")");
                        try {
                            mCamera = Camera.open(localCameraIndex);
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }

            if (mCamera == null)
                return false;

            /* Now set camera parameters */
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                boolean isSetSize = false;
                for(Camera.Size size:sizeList){
                    if (size.width==1920&&size.height==1080){
                        parameters.setPreviewSize(size.width,size.height);
                        isSetSize = true;
                        break;
                    }else if (size.width==1280&&size.height==720){
                        parameters.setPreviewSize(size.width,size.height);
                        isSetSize = true;
                        break;
                    }else if (size.width==800&&size.height==600){
                        parameters.setPreviewSize(size.width,size.height);
                        isSetSize = true;
                        break;
                    }else if (size.width==640&&size.height==480){
                        parameters.setPreviewSize(size.width,size.height);
                        isSetSize = true;
                        break;
                    }
                }
                if (isSetSize) {
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    parameters.set("jpeg-quality", 85);//设置照片质量
                    parameters.setPictureSize(1280, 720);//设置拍出来的屏幕大小
                    mCamera.setParameters(parameters);
                    mCamera.setPreviewDisplay(sh);
                    mCamera.startPreview();
                    takePhoto();
                }else{
                    Log.e(TAG, "not support 1080p 720p 800*600 640*480 ");
                }
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
        }

        return result;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.e(TAG,"surfaceCreated");
        new Thread(new Runnable() {
            @Override
            public void run() {
                initializeCamera();
            }
        }).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void takePhoto() {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        mWaitForTakePhoto = true;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                EventBus.getDefault().post(new PhotoTakenEvent().setPath(photoPath));
                LogUtil.e(TAG,"onPictureTaken");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(photoPath);
                    fos.write(data);
                    fos.flush();
                    //启动我的裁剪界面
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos!=null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mWaitForTakePhoto = false;
                stopSelf();
            }
        });
    }
}
