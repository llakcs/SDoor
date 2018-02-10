package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Led;
import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Steer;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.util.logging.Handler;

/**
 * Created by jelly on 2018/1/22.
 */
public class SteerHandler {
    private static final String TAG = "SteerHandler";
    private static SteerHandler instance;
    private static Steer mSteer;

    private static final int SHAKE = 1;
    private static final int NOD = 1;
    private static final int SHAKE_STOP = 0;
    private static final int NOD_STOP = 0;

    /**
     * 获取单例
     *
     * @return the steer handler
     */
    public static SteerHandler getInstance(){
        if (instance == null){
            instance = new SteerHandler();
        }
        return instance;
    }

    SteerHandler(){
        mSteer = new Steer();
        mSteer.openDevice();
    }

    /**
     * 调用舵机使机器人摇头
     */
    public void shake(){
        LogUtil.e(TAG,"check shake b4 start "+mSteer.control(3,0));
        mSteer.control(SHAKE,0);
        LogUtil.e(TAG,"check shake ft start "+mSteer.control(3,0));
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e(TAG,"check shake b4 end "+mSteer.control(3,0));
                mSteer.control(SHAKE_STOP,0);
                LogUtil.e(TAG,"check shake ft end "+mSteer.control(3,0));
            }
        },500);
    }

    /**
     * 调用舵机使机器人点头
     */
    public void nod(){
        LogUtil.e(TAG,"check nod b4 start "+mSteer.control(3,1));
        mSteer.control(NOD,1);
        LogUtil.e(TAG,"check nod ft start "+mSteer.control(3,1));
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.e(TAG,"check nod b4 end "+mSteer.control(3,1));
                mSteer.control(NOD_STOP,1);
                LogUtil.e(TAG,"check nod ft end "+mSteer.control(3,1));
            }
        },500);
    }

    /**
     * 关闭设备
     */
    public void close(){
        mSteer.closeDevice();
        instance = null;
    }



}
