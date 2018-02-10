package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

/**
 * Created by jelly on 2018/1/22.
 */

public class Steer {

    public int control(int cmd,int arg){
       return ioctl(cmd,arg);
    }

    public boolean openDevice(){
        return open();
    }

    public void closeDevice(){
        close();
    }

    //0 1 为高低电平
    private native int ioctl(int cmd,int arg);
    private native boolean open();
    private native void close();
}
