package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

import android.util.Log;

/**
 * Created by jelly on 2017/11/8.
 */

public class Pn512Lock {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("devicecontrol");
    }//加载so文件，这里加载的为Pn512的JNI文件。

    public static final String TAG = "Pn512Lock";

    public static final int CMD_READ = 3;

    public static final int IO_LOCK_CTRL = 0;
    public static final int IO_DOOR1_ST = 1;
    public static final int IO_DOOR2_ST = 2;
    public static final int IO_LOCK_JUDGE = 3;
    public static final int IO_FB1_ELSE = 4;
    public static final int IO_FB2_ELSE = 5;

    private boolean isOpen =false;

    public boolean openDevice(){
        isOpen = open();
        return isOpen;
    }
    public int control(int cmd, int arg){
        if (isOpen)
            return ioctl(cmd,arg);
        else {
            Log.e(TAG,"control fail,device not open!");
            return -1;
        }
    }

    public void closeDevice(){
        isOpen = false;
        close();
    }


    /*
 * Class:     com_dchip_hd_led_gpio_LedCtrl
 * Method:    ioctl
 *cmd为0x00,0x01和0x02:当为0x00时，设置IO电平为高1，当为0x01时设置IO电平为低0,0x02对应的为获取IO电平
 *arg对应的为管脚IO：0为LOCK_CTRL,1为DOOR1_ST,2为DOOR2_ST，3为LOCK_JUDGE,4为FB1_ELSE,
 *管脚LOCK_CTRL和FB1_ELSE可以设置高低电平，其他不可以。但是所有管脚都可以获取高低电平。
 * Signature: (II)I
 */
    private native int ioctl(int cmd,int arg);
    private native boolean open();
    private native void close();

}
