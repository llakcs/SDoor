package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Led;

/**
 * Created by jelly on 2017/12/18.
 */
public class LedHandler {
    private static final String TAG = "LedHandler";
    private static LedHandler instance;
    private static Led mLed;
    private static int defOpen = 0;

    /**
     * 获取led单例
     *
     * @return the led handler
     */
    public static LedHandler getInstance(){
        if (instance == null){
            instance = new LedHandler();
        }
        return instance;
    }

    LedHandler(){
        mLed = new Led();
    }

    /**
     * 点亮led
     *
     * @param  i=1,2,3
     * @return the int
     */
//i
    public int openLed(int i){
        if (i==1 || i == 2 || i == 3) {
            mLed.openDevice();
            int ret = mLed.ioDevice(defOpen, i - 1);
            mLed.closeDevice();
            return ret;
        } else return 0;
    }

    /**
     * 关闭led
     *
     * @param i = 1,2,3
     * @return the int
     */
    public int closeLed(int i){
        if (i==1 || i == 2 || i == 3) {
            mLed.openDevice();
            int ret = mLed.ioDevice(inv(defOpen), i - 1);
            mLed.closeDevice();
            return ret;
        }else return 0;

    }

    /**
     * 注销设备
     */
    void finish(){
        mLed.closeDevice();
    }


    protected int inv(int i){
        if (i>0) return 0;
        else return 1;
    }

}
