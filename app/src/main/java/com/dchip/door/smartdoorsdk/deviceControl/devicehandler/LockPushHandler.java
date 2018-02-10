package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.LockSwitch;
import com.dchip.door.smartdoorsdk.event.DeviceCheckEvent;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jelly on 2017/12/18.
 */

public class LockPushHandler {
    private static final String TAG = "LockPushHandler";
    private static LockPushHandler instance;
    private static LockSwitch mPush;
    private boolean stop = false;
    private static int defOpen = 1;

    public static LockPushHandler getInstance(){
        if (instance == null){
            instance = new LockPushHandler();
        }
        return instance;
    }

    LockPushHandler(){
        mPush = new LockSwitch();
        new Thread(runnable).start();
    }

    public void finish() {
        stop = true;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int statuslod = inv(defOpen);
            while (!stop) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int statusnew = mPush.checkDevice();
                if (statuslod != statusnew){
                    if (statusnew == defOpen)
                        LogUtil.e(TAG,"###DeviceCheckEvent.LockPushHandler");
                        EventBus.getDefault().post(new DeviceCheckEvent("lockPush"));
                }
                LogUtil.v(TAG,"###DeviceCheckEvent 按钮检测:" + statusnew);
                statuslod = statusnew;
            }
        }
    };

    protected int inv(int i){
        if (i>0) return 0;
        else return 1;
    }
}
