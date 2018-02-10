package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.LockBreak;
import com.dchip.door.smartdoorsdk.event.DeviceCheckEvent;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jelly on 2017/12/18.
 */

public class LockBreakHandler {
    private static final String TAG = "LockBreakHandler";
    private static LockBreakHandler instance;
    private static LockBreak mBreak;
    private boolean stop = false;
    private static int defOpen = 1;

    public static LockBreakHandler getInstance(){
        if (instance == null){
            instance = new LockBreakHandler();
        }
        return instance;
    }

    LockBreakHandler(){
        mBreak = new LockBreak();
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
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int statusnew = mBreak.checkDevice();
                if (statuslod != statusnew){
                    if (statusnew == defOpen)
                        LogUtil.e(TAG,"###DeviceCheckEvent.LockBreakHandler");
                        EventBus.getDefault().post(new DeviceCheckEvent("lockBreak"));
                }
                statuslod = statusnew;
            }
        }
    };

    protected int inv(int i){
        if (i>0) return 0;
        else return 1;
    }

}
