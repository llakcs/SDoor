package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

/**
 * Created by jelly on 2017/12/18.
 */

public class Led {
    private static final String TAG = "Led";
    static {
        System.loadLibrary("devicecontrol");
    }//加载so文件

    public boolean openDevice(){
        return open();
    }

    /*
    * LED状态指示灯：
    设备名 /dev/sys_stat
    Arg对应管脚为：
    SYS_STAT1  0     门口机APP是否在运行
    SYS_STAT2  1     门口机是否和服务器连接正常
    SYS_STAT3  2     门口机APP是否正在更新当中
    */
    public int ioDevice(int cmd, int arg){
        return  ioCtl(cmd, arg);
    }

    public void closeDevice(){
        close();
    }

    private native boolean open();//打开设备
    private native int ioCtl(int cmd, int arg);//控制灯
    private native void close();//关闭设备

}
