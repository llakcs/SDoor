package com.dchip.door.smartdoorsdk.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.dchip.door.smartdoorsdk.service.ACWebSocketService;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.util.List;

//import com.dchip.smartac.service.ACLockService;


/**
 * @author zhangdeming
 * @date 创建时间 2017/4/26
 * @description 描述类的功能
 */

public class ACBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ACBroadcastReceiver";
    public static final String InfoAction = "com.dchip.device.info";
    public static final String UpdataFailAction = "com.dchip.device.updateFail";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            LogUtil.e(TAG, "开机事件");
//            Intent a = new Intent(context, MainActivity.class);
//            a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(a);
        }else if(intent.getAction().equals(UpdataFailAction)){
            // TODO: 2017/8/30 更新失败
//            EventBus.getDefault().post(new BroadcastEvent(UpdataFailAction,intent.getStringExtra("msg")));
//            EventBus.getDefault().post(new InfoEvent("更新失败 .."+intent.getStringExtra("msg")));
        }else if(intent.getAction().equals(InfoAction)){
//            EventBus.getDefault().post(new InfoEvent(intent.getStringExtra("info")));
        } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            LogUtil.e(TAG, "每分钟提示事件");
            if (!isServiceRunning(context, ACWebSocketService.class.getName())) {
                //启动长链接服务
               LogUtil.e(TAG,"长链接服务崩溃重启");
                context.startService(new Intent(context,ACWebSocketService.class));
            }
//            if (!isServiceRunning(context, BluetoothBandService.class.getName())) {
//                //启动蓝牙的服务
//                // TODO: 2017/8/17 调试蓝牙时开启
//                Intent it = new Intent(Constant.ACTION_BLUETOOTH);
//                intent.setPackage(context.getPackageName());
//                context.startService(it);
//            } else {
//                EventBus.getDefault().post(new InfoEvent("蓝牙服务运行中"));
//            }
        }
    }

    /**
     * 判断服务是否处于运行状态.
     *
     * @param servicename
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Context context, String servicename) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (servicename.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
