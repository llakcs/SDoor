package com.dchip.door.smartdoorsdk.deviceControl.Listener;

/**
 * Created by jelly on 2017/12/18.
 */
public interface LockPushListener {
    /**
     * 检测到门内按钮被按下时回调
     */
    void onPush();
}
