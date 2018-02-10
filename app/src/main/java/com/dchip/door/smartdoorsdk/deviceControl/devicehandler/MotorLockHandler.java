package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;



import com.dchip.door.smartdoorsdk.deviceControl.interfaces.LockHandler;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_LOCK_CTRL;


/**
 * Created by jelly on 2017/11/21.
 */

public class MotorLockHandler extends LockHandler {
    public static final String TAG = "MotorLockHandler";
//    public static MotorLockHandler instance ;

    public static int OPEN_LOCK = 1;
    public static int OPEN_DOOR = 1;
    public static int OPEN_ORIGNAL_LOCK = 1;

//    public MotorLockHandler getInstance() {
//        if (instance == null) {
//            instance = new MotorLockHandler();
//        }
//        return instance;
//    }

    public MotorLockHandler(){
        setDefaultStatus(1,1,1,true);
    }

    @Override
    public int openLock() {
        LogUtil.d(TAG,"###开锁");
        int ret = mLock.control(OPEN_LOCK,IO_LOCK_CTRL);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                closeLock();
            }
        }, 30);
        return ret;
    }

    @Override
    public int longOpenLock() {
        return 0;
    }

    @Override
    public int closeLock() {
        LogUtil.d(TAG,"###关锁");
        return mLock.control(inv(OPEN_LOCK),IO_LOCK_CTRL);
    }


    @Override
    public void onDoorOpen(int doorNum) {
        LogUtil.d(TAG,"###开门");
    }

    @Override
    public void onDoorClose(int doorNum) {
        LogUtil.d(TAG,"###关门");
    }


}
