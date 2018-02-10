package com.dchip.door.smartdoorsdk.deviceControl.nativeLev;

/**
 * Created by jelly on 2017/11/11.
 */

public class Pn512Card {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("devicecontrol");
    }//加载so文件，这里加载的为Pn512的JNI文件。

    public int open(){
        return Pn512_OpenAndPowerOn();
    }
    public int close(){
        return Pn512_PowerOffAndClose();
    }
    public boolean cardDetect(){
        return CardDetect();
    }public boolean cardChecked(){
        return CardChecked();
    }
    public String operation(String cmd){
        return Pn512_CardCmdOperation(cmd);
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //定义以下三个JAVA函数用来控制Pn512，具体实现为在JNI的C/C++部分。
    private native int Pn512_OpenAndPowerOn();//使用Pn512前，首先打开设备，启动读卡天线
    private native int Pn512_PowerOffAndClose();//如果需要关闭pn512，则调用此函数
    private native boolean CardDetect();//检测是否有卡
    private native boolean CardChecked();//检测是否加密
    private native String Pn512_CardCmdOperation(String cmd);//打开了Pn512之后，就可以使用这个函数发送卡命令，然后此函数的返回值为IC卡返回的数值

}
