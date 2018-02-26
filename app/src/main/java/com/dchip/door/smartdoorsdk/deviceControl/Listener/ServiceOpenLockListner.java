package com.dchip.door.smartdoorsdk.deviceControl.Listener;

/**
 * Created by llakcs on 2017/12/14.
 */
public interface ServiceOpenLockListner {
    /**
     * 服务器端打开锁时回调
     */
    void lockopen(int way);
}
