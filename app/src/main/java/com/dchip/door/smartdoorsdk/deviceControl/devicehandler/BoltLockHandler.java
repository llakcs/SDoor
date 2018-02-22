package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import android.util.Log;


import com.dchip.door.smartdoorsdk.deviceControl.interfaces.LockHandler;
import com.dchip.door.smartdoorsdk.event.FaultEvent;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_FB1_ELSE;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_FB2_ELSE;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_LOCK_CTRL;


/**
 * Created by jelly on 2017/11/17.
 */

public class BoltLockHandler extends LockHandler {
    private static final String TAG = "BoltLockHandler";



    public  BoltLockHandler(){
        setDefaultStatus(1,1,1,true);
    }

    @Override
    public int openLock() {
        if(!isSingleLock) onDoorClose(1);
        int ret = mLock.control(OPEN_LOCK, IO_LOCK_CTRL);
        LogUtil.d(TAG,"本地锁已经开起");
        isLongOpen = false;
        onDoorClose(0);
        return ret;
    }

    @Override
    public int longOpenLock() {
        int ret = mLock.control(OPEN_LOCK, IO_LOCK_CTRL);
        if(isDebugable){ Log.w(TAG, "openLock sent commend=" + OPEN_LOCK);}
        LogUtil.d(TAG,"本地锁已经长时间开起");
        isLongOpen = true;
        return ret;
    }

    @Override
    public int closeLock() {
        LogUtil.d(TAG,"本地锁已经关闭");
        if(isDebugable) {Log.w(TAG, "closeLock sent commend=" + inv(OPEN_LOCK));}
        return mLock.control(inv(OPEN_LOCK), IO_LOCK_CTRL);
    }


    //doorNum：0=1号门，1=2号门，2=两个门
    @Override
    public void onDoorOpen(final int doorNum) {
        //检测为常开锁时不尽行倒计时
        if (isLongOpen) return;
        //检测是否未原门开的锁。
        if(checkJudge() == OPEN_ORIGNAL_LOCK){
            int door = -1;
            if (doorNum==0) door = IO_FB1_ELSE;
            else door = IO_FB2_ELSE;//// TODO: 2017/11/15 暂时不能用。
            mLock.control(OPEN_DOOR,door);
            LogUtil.d(TAG,"原锁已开启----开门");
            if(isDebugable) {LogUtil.d(TAG, "checkLock()=" + checkLock());}
            if(checkLock() == inv(OPEN_LOCK))
                return;
        }
        //
        if(isDebugable) {LogUtil.d(TAG, "onDoorOpen() doorNum=" + doorNum);}

        if (checkLock() == inv(OPEN_LOCK)) {
//        if (checkLock() == getIntFromCommend(!OPEN_LOCK)) {
            LogUtil.d(TAG,"未开锁，开门异常报警。 门号:" + doorNum + 1);
            EventBus.getDefault().post(new FaultEvent(1));
        } else {
            LogUtil.d(TAG,"检测到开门");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int SecCounter = 0;

                public void run() {
//                    Log.d(TAG, "opened door counting " + SecCounter);
                    if (checkDoor(doorNum) == OPEN_DOOR) {
                        if (SecCounter > DOOR_LOGN_OPEN_TIMEOUT) {
                            LogUtil.e(TAG,"已开锁，开长开 异常报警。 门号:" + doorNum + 1);
                            EventBus.getDefault().post(new FaultEvent(4));
                            SecCounter = 0;
                            this.cancel();
                        }
                        SecCounter++;
                    } else {
                        SecCounter = 0;
                        this.cancel();
                    }
                }
            }, 0, 1000);
        }
    }

    //doorNum：0=1号门，1=2号门，2=两个门
    @Override
    public void onDoorClose(final int doorNum) {
        if(isDebugable) {LogUtil.d(TAG, "onDoorClose() OPEN_DOOR=" + OPEN_DOOR);}

        if (doorNum != 0 && doorNum != 1 ) {
            LogUtil.e(TAG, "onDoorClose() err doorNum:" + doorNum);
            return;
        }
        //检测为常开锁时不尽行倒计时
        if (isLongOpen) return;
        //EventBus.getDefault().post(new InfoEvent("checkLock():"+checkLock()+" inv(OPEN_LOCK)："+inv(OPEN_LOCK)));
        //检测是否为原门开的锁。
        if(checkJudge() == OPEN_ORIGNAL_LOCK){
            int door = -1;
            if (doorNum==0) door = IO_FB1_ELSE;
            else door = IO_FB2_ELSE;//// TODO: 2017/11/15 暂时不能用。
            mLock.control(inv(OPEN_DOOR),door);
            LogUtil.d(TAG,"原锁已开启----关门");
            if(checkLock() == inv(OPEN_LOCK))
                return;
        }

        LogUtil.d(TAG,"检测到关门");
        if(isDebugable) {LogUtil.d(TAG, "onDoorClose() doorNum=" + doorNum+" open:="+(checkDoor(doorNum)==OPEN_DOOR));}
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            int HalfSecCounter = 0;

            public void run() {
                if(isDebugable) {LogUtil.d(TAG, "count:" + HalfSecCounter+" isSingleLock:"+isSingleLock+" 0_open:"+(checkDoor(0)==OPEN_DOOR)+" 1_open:"+(checkDoor(1)==OPEN_DOOR));}
                if (isSingleLock && checkDoor(doorNum) == inv(OPEN_DOOR)
                        ||  !isSingleLock && checkDoor(0) == inv(OPEN_DOOR) && checkDoor(1) == inv(OPEN_DOOR)) {
                    if (HalfSecCounter > LOCK_CLOSE_TIMEOUT * 2) {
                        closeLock();
                        HalfSecCounter = 0;
                        this.cancel();
                    }
                    HalfSecCounter++;
                } else {
                    HalfSecCounter = 0;
                    this.cancel();
                }
            }
        }, 0, 500);
    }


}
