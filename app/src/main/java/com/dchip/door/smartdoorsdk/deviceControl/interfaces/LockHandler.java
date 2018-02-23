package com.dchip.door.smartdoorsdk.deviceControl.interfaces;

import android.util.Log;


import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock;
import com.dchip.door.smartdoorsdk.s;

import java.util.Timer;
import java.util.TimerTask;

import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.CMD_READ;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_DOOR1_ST;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_DOOR2_ST;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_LOCK_CTRL;
import static com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Lock.IO_LOCK_JUDGE;


/**
 * Created by jelly on 2017/11/8.
 */
public abstract class LockHandler {
    public static final String TAG = "LockHandler";
    protected static boolean isSingleLock = true;
    protected static boolean isDebugable = false;
    protected static boolean isLongOpen = false;
    protected static int OPEN_LOCK = 1;
    protected static int OPEN_DOOR = 1;
    protected static int OPEN_ORIGNAL_LOCK = 1;
    protected static Pn512Lock mLock;
    /**
     * 超时关门时间（秒）
     */
    public static int LOCK_CLOSE_TIMEOUT = 3;

    /**
     * 超时上报长时间开门（秒）
     */
    public static int DOOR_LOGN_OPEN_TIMEOUT = 10 * 60;


    /**
     * 初始化一个锁设备
     */
    public LockHandler() {
        mLock = new Pn512Lock();
        mLock.openDevice();
        new Timer().schedule(checkDoorStatusTask, 0, 200);

    }

    /**
     * 开锁命令
     *
     * @return the 是否成功
     */
    public abstract int openLock();

    /**
     * 长时间开锁命令
     *
     * @return the int
     */
    public abstract int longOpenLock();

    /**
     * 关锁命令
     *
     * @return the int
     */
    public abstract int closeLock();

    /**
     * 检测到开门时回调
     *
     * @param doorNum the door num  0=1号门，1=2号门，2=两个门
     */
    public abstract void onDoorOpen(final int doorNum);

    /**
     * 检测到关门时回调
     *
     * @param doorNum the door num  0=1号门，1=2号门，2=两个门
     */
    public abstract void onDoorClose(final int doorNum);


    /**
     * 设置锁的默认状态
     *
     * @param lockStatus    开锁时为 0/1
     * @param doorStatus    开门时为 0/1
     * @param oriLockStatus 原锁开锁时为 0/1
     * @param isSingleLock  原锁开门时为 0/1
     * @return 返回锁对象
     */
    public LockHandler setDefaultStatus(int lockStatus, int doorStatus, int oriLockStatus, boolean isSingleLock) {
        OPEN_LOCK = lockStatus;
        OPEN_DOOR = doorStatus;
        OPEN_ORIGNAL_LOCK = oriLockStatus;
        this.isSingleLock = isSingleLock;
        return this;
    }

    /**
     * 检查锁的状态
     *
     * @return the int
     */
    public int checkLock() {
        int ret = mLock.control(CMD_READ, IO_LOCK_CTRL);
//        if(isDebugable) {Log.w(TAG, "checkLock ret=" + ret);}
        return ret;
    }

    /**
     * 检查本地开锁还是原锁开锁
     *
     * @return the int
     */
    public int checkJudge() {
        int ret = mLock.control(CMD_READ, IO_LOCK_JUDGE);
//        if(isDebugable) {Log.w(TAG, "checkJudge ret=" + ret);}
        return ret;
    }

    /**
     * 检查门状态
     *
     * @param num the num 0=1号门，1=2号门
     * @return the int
     */
    public int checkDoor(int num) {
        int ret = -1;
        if (num == 0) {
            ret = mLock.control(CMD_READ, IO_DOOR1_ST);
        } else {
            ret = mLock.control(CMD_READ, IO_DOOR2_ST);
        }
//        if(isDebugable) {Log.w(TAG, "checkDoor:"+ num +" ret=" + ret);}
        return ret;
    }

    /**
     * 注销设备
     */
    public void finish() {
//        checkDoorStatusThread.stop();
        checkDoorStatusTask.cancel();
    }


    protected int[] doorStatus = {-1, -1};

    protected TimerTask checkDoorStatusTask = new TimerTask() {
        @Override
        public void run() {
            int newDoorStatus = mLock.control(CMD_READ, IO_DOOR1_ST);
            //1号门
            if (doorStatus[0] != newDoorStatus) {
                if (newDoorStatus == inv(OPEN_DOOR)) {
                    onDoorClose(0);
                } else {
                    onDoorOpen(0);
                }
                doorStatus[0] = newDoorStatus;
            }
            //2号门
            if (!isSingleLock) {
                newDoorStatus = mLock.control(CMD_READ, IO_DOOR2_ST);
                if (doorStatus[1] != newDoorStatus) {
                    if (newDoorStatus == inv(OPEN_DOOR)) {
                        onDoorClose(1);
                    } else {
                        onDoorOpen(1);
                    }
                    doorStatus[1] = newDoorStatus;
                }
            }
        }
    };


    protected int inv(int cmd) {
        if (cmd > 0) return 0;
        else return 1;
    }


    protected void readTest() {
        Log.w(TAG, "control 3,0 =" + mLock.control(CMD_READ, IO_LOCK_CTRL));
//        Log.w(TAG,"control 3,1 =" + mLock.control(CMD_READ,IO_DOOR1_ST));
//        Log.w(TAG,"control 3,2 =" + mLock.control(CMD_READ,IO_DOOR2_ST));
//        Log.w(TAG,"control 3,3 =" + mLock.control(CMD_READ,IO_LOCK_JUDGE));
//        Log.w(TAG,"control 3,4 =" + mLock.control(CMD_READ,IO_FB1_ELSE));
    }
}
