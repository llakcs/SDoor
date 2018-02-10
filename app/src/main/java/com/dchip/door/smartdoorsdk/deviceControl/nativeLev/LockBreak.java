package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

/**
 * Created by jelly on 2017/12/18.
 */

public class LockBreak {
    private static final String TAG = "LockBreak";
    static {
        System.loadLibrary("devicecontrol");
    }//加载so文件，这里加载的为Pn512的JNI文件。

    public int checkDevice() {
        return check();
    }

    private native int check();
}
