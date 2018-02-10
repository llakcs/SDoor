package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import android.util.Log;

import com.dchip.door.smartdoorsdk.deviceControl.interfaces.LockHandler;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_LOCK_CTRL;


/**
 * Created by jelly on 2017/11/20.
 */

public class MagneticLockHandler extends LockHandler {
    public static final String TAG = "MagneticLockHandler";
//    public static MagneticLockHandler instance;
//
//
//    public MagneticLockHandler getInstance() {
//        if (instance == null){
//            instance = new MagneticLockHandler();
//        }
//        return instance;
//    }

    public MagneticLockHandler(){
        setDefaultStatus(1,1,1,true);
    }

    @Override
    public int openLock() {
        LogUtil.d(TAG,"###openlock");
        int ret = mLock.control(OPEN_LOCK, IO_LOCK_CTRL);
        //
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                closeLock();
            }
        }, 1000 * LOCK_CLOSE_TIMEOUT);
        return ret;
    }

    @Override
    public int longOpenLock() {
        int ret = mLock.control(OPEN_LOCK, IO_LOCK_CTRL);
        return ret;
    }

    @Override
    public int closeLock() {
        LogUtil.d(TAG,"####closeLock");
        int ret = mLock.control(inv(OPEN_LOCK), IO_LOCK_CTRL);
        return ret;
    }


    @Override
    public void onDoorOpen(int doorNum) {
        if (checkDoor(0)==inv(OPEN_LOCK)) {
            LogUtil.e(TAG,"未开锁，开门异常报警。 门号:" + doorNum + 1);
        }
        LogUtil.e(TAG,"检测到门打开。 门号:" + doorNum + 1);
    }

    @Override
    public void onDoorClose(int doorNum) {
        LogUtil.e(TAG, "检测到门关闭,门号:"+doorNum+1);
    }
}
