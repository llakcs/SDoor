package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

/**
 * Created by jelly on 2017/11/23.
 */

public class HumanCheck {
    private static final String TAG = "HumanCheck";
    static {
        System.loadLibrary("devicecontrol");
    }//加载so文件，这里加载的为Pn512的JNI文件。

    public boolean openDvice(){
        return open();
    }
    public void closeDvice(){
        close();
    }
    public int checkHuman(){
        return check();
    }


    private native boolean open();//打开设备
    private native int check();//检查是否有人
    private native void close();//关闭设备

}
