package com.dchip.door.smartdoorsdk.deviceControl.Listener;

/**
 * Created by llakcs on 2017/12/14.
 */
public interface ServerstatusListner {
    /**
     * 检测到服务器心跳时回调
     */
    void getHeartBeats();

    /**
     * 检测到服务器断开连接时回调
     */
    void disconn();

    /**
     * 检测到服务器连接时回调
     */
    void connected();

    /**
     * 检测到服务器要求更新apk时回调
     */
    void updateAPK();

    /**
     * 检测到服务器要求更新门卡列表时回调
     */
    void updatecardlist();
}
