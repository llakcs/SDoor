package com.dchip.door.smartdoorsdk.deviceControl;

import android.app.Activity;

import com.dchip.door.smartdoorsdk.deviceControl.Listener.EaseAccountListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.HumanCheckListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LockBreakListener;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LockPushListener;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LogStrListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.ServerstatusListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.ServiceOpenLockListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.UpdateOwenerListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.onPhotoTakenListener;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.LedHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.SteerHandler;
import com.dchip.door.smartdoorsdk.deviceControl.interfaces.LockHandler;

/**
 * Created by llakcs on 2017/12/5.
 */
public interface DeviceManager {

    /**
     * 通过传入对象的方式，设置锁。
     *
     * @param lock the lock
     */
    void setLock(LockHandler lock);

    /**
     * 通过读取配置文件的方式，设置锁。
     *
     * @param config the config
     */
    void setLock(String config);

    /**
     * 获取锁对象
     *
     * @return the lock
     */
    LockHandler getLock();

    /**
     * 获取led对象
     *
     * @return the led
     */
    LedHandler getLed();

    /**
     * 获取关节控制(点头，摇头),舵机对象
     *
     * @return the steer
     */
    SteerHandler getSteer();

    /**
     * 获取广告类型，(图片，视频)
     *
     * @return the adv type
     */
    int getAdvType();

    /**
     * 上传本机主板mac至服务器
     */
    void upLoadMac();

    /**
     * 上传本机运行app的版本至服务器
     *
     * @param ver the ver
     */
    void uploadAppVer(String ver);

    /**
     * 检查服务器上的最新的版本信息
     */
    void checkVer();

    /**
     * 注册 人体检测的监听器
     *
     * @param humanCheckListner the human check listner
     * @return the human check listner
     */
    DeviceManager setHumanCheckListner(HumanCheckListner humanCheckListner);

    /**
     * 系统日志打印
     * @param tag
     * @param msg
     */
    void showMsg(String tag,String msg);

    /**
     * 注销 人体检测监听器
     */
    void unRegHumanCheckListner();

    /**'
     * 输出打印内容
     * @param logStrListner
     */
    void setLogStrListner(LogStrListner logStrListner);

    /**
     * 注销打印接口
     */
    void unRegLogStrListner();

    /**
     * 注册 户主信息更变监听器
     *
     * @param updateOwenerListner the update owener listner
     */
    void setUpdateOwenerListner(UpdateOwenerListner updateOwenerListner);

    /**
     * 注消 户主信息更变监听器
     */
    void unRegUpdateOwnerListner();

    /**
     * 注册 服务器开锁监听器
     *
     * @param serviceOpenLockListner the service open lock listner
     */
    void setServiceOpenLockListner(ServiceOpenLockListner serviceOpenLockListner);

    /**
     * 注消 服务器开锁监听器
     */
    void unRegServiceOpenLockListner();

    /**
     * 注册 服务器状态检测监听器
     *
     * @param serverstatusListner the serverstatus listner
     */
    void setServerstatusListner(ServerstatusListner serverstatusListner);

    /**
     * 注消 服务器状态检测监听器
     */
    void unRegServerstatusListner();

    /**
     * 注册 门内开门按钮监听器
     *
     * @param lockPushListener the lock push listener
     * @return the lock push listener
     */
    DeviceManager setLockPushListener(LockPushListener lockPushListener);

    /**
     * 注消 门内开门按钮监听器
     */
    void unRegLockPushListenerListner();

    /**
     * 注册 锁异常监听器
     *
     * @param lockBreakListener the lock break listener
     * @return the lock break listener
     */
    DeviceManager setLockBreakListener(LockBreakListener lockBreakListener);

    /**
     * 注消 锁异常监听器
     */
    void unRegLockBreakListener();

    /**
     * 上传本机主板uid至服务器
     */
    void uploadLock();

    /**
     * 上传崩溃信息至服务器
     */
    void checkCrashLogAndUpload();

    /**
     * 初始化设备管理器
     * 0-手机
     * 1-android终端&普通版本
     * 2-qt
     * 5-android终端&十寸屏(人脸，视频对讲)
     * 6-android终端&十寸屏(视频对讲)
     * 7-android终端&十五寸屏(16:9)
     * 8-android终端&十五寸屏(4:3)
     *
     * @param activity   activity
     * @param appTypeNum app的类型，参照后台配置.
     * @return the device
     */
    DeviceManager init(Activity activity,int appTypeNum);

    /**
     * 注销设备管理器
     */
    void release();

    /**
     * 返回业主信息写入终端状态至服务器
     */
    void updateOnwerStatus();

    /**
     * 注册 获取环信帐号监听器
     *
     * @param acountListner the acount listner
     */
    void setEaseAcountListner(EaseAccountListner acountListner);

    /**
     * 注消 获取环信帐号监听器
     */
    void unRegEaseAcountListner();

    /**
     * 开启悬浮框服务拍照
     */
    public void takePhoto(onPhotoTakenListener tp);

    /**
     * 设置检测广告更新的间隔时间
     *
     * @param GET_AD_TIME 分钟
     * @return the get ad time
     */
    DeviceManager setGET_AD_TIME(int GET_AD_TIME);

    /**
     * 设置使能读卡设备
     *
     * @return the device
     */
    DeviceManager EnableCardReader();

    /**
     * 设置使能锁设备
     *
     * @return the device
     */
    DeviceManager EnableLock();

    /**
     * 设置使能提示灯设备
     *
     * @return the device
     */
    DeviceManager EnableLed();

    /**
     * 设置使能计时器设备
     *
     * @return the device
     */
    DeviceManager EnableDtimer();

    /**
     * 设置使能舵机设备
     *
     * @return the device
     */
    DeviceManager EnableSteer();

    /**
     * 设置使能开门拍照
     *
     * @return the device
     */
    DeviceManager EnableTakePhoto();

    /**
     * 获取app 类型app 0-手机 1-android终端&普通版本 2-qt 5-android终端&十寸屏(人脸，视频对讲) 6-android终端&十寸屏(视频对讲) 7-android终端&十五寸屏(16:9) 8-android终端&十五寸屏(4:3) 9-支持暗盒版本
     *
     * @return the device
     */
    int getAppType();

}
