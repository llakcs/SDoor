package com.dchip.door.smartdoorsdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.dchip.door.smartdoorsdk.Bean.CardsModel;
import com.dchip.door.smartdoorsdk.Bean.DoorOperationModel;
import com.dchip.door.smartdoorsdk.Bean.HeartBeatModel;
import com.dchip.door.smartdoorsdk.Bean.OperationModel;
import com.dchip.door.smartdoorsdk.event.OpenLockRecallEvent;
import com.dchip.door.smartdoorsdk.event.OpenLockStatusEvent;
import com.dchip.door.smartdoorsdk.event.ServiceEvent;
import com.dchip.door.smartdoorsdk.event.UpdateConfigEvent;
import com.dchip.door.smartdoorsdk.s;
import com.dchip.door.smartdoorsdk.utils.Constant;
import com.dchip.door.smartdoorsdk.utils.DPDB;
import com.dchip.door.smartdoorsdk.utils.FileHelper;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.dchip.door.smartdoorsdk.utils.ShellUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketHandler;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.dchip.door.smartdoorsdk.event.ServiceEvent.HEART_BEAT;


/**
 * @author zhangdeming
 * @date 创建时间 2017/4/26
 * @description 控制长链接的后台服务
 */
public class ACWebSocketService extends Service {

    private final String TAG = "ACWebSocketService";

    private final byte WS_STATUS_CONNECTING = 0x12;
    private final byte WS_STATUS_STOP = 0x13;
    private final byte WS_STATUS_RECONNECT = 0x14;
    private final byte WS_STATUS_CONNECTED = 0X15;
    private final byte WS_STATUS_INIT = 0x16;
    private final byte WS_STATUS_CLOSED = 0x17;
    private final byte WS_STATUS_CONNECT_ERROR = 0x18;

    /**
     * 心跳的间隔时间
     */
    private int HEART_INTERVAL = 10 * 1000;

    /**
     * 服务器回应间隔时间
     */
    private final int INTERVAL_DISCONNECT = 10*1000;

    /**
     * 长链接操作类
     */
    private WebSocketConnection mConnection = null;
    /**
     * 链接状态检测
     */
    private Handler heartHandler = null;

    /**
     * 检测链接状态（是否在连接中）
     */
    private byte wsStatus = WS_STATUS_INIT;

    /**
     * 心跳检测的保存的时间
     */
    private long networkChekTime = 0L;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        heartHandler = new Handler();
        heartHandler.postDelayed(heartRunnable, HEART_INTERVAL);
        LogUtil.e(TAG, " ###ACWebSocketService onCreate");
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 连接服务器
     */
    private void connectAC() {
        String mac = "";
        mac = DPDB.getmac();
//        if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//            ShellUtil.CommandResult cr = ShellUtil.execCommand("cat /proc/cpuinfo", false);
//            int i = cr.successMsg.indexOf("Serial");
//            if (i != -1) {
//                String cpuId = cr.successMsg.substring(i);
//                cpuId = cpuId.substring(cpuId.indexOf(":") + 1).trim();
//                mac = cpuId.substring(0, 16);
//            }
//        }else {
//            mac = android.os.Build.SERIAL;
//        }
        wsStatus = WS_STATUS_CONNECTING;
        String wsUri = String.format(Constant.WS_URI, DPDB.getwsUrl() + mac );//+ "000000"
        // LogUtil.e(TAG, "Status: Connecting to " + wsUri);
        mConnection = new WebSocketConnection();
        try {
            mConnection.connect(wsUri, webSocketHandler);
        } catch (Exception e) {
            disconnectAC();
            //LogUtil.e(TAG, e.toString());
            EventBus.getDefault().post(new ServiceEvent(false,ServiceEvent.TIMEOUT));
            DPDB.setServiceconn(false);
            LogUtil.e(TAG,"长链接连接出错" + e.toString());
            wsStatus = WS_STATUS_CONNECT_ERROR;
        }

    }

    /**
     * 断开当前的连接
     */
    private void disconnectAC() {
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenLockEvent(OpenLockStatusEvent event) {
        if (mConnection != null && mConnection.isConnected()) {
            LogUtil.d(TAG,"发送开锁成功数据");
            String domsg = new Gson().toJson(new DoorOperationModel(event.getUid(),event.isStatus(), event.getType()));
            LogUtil.e(TAG, "###"+domsg);
            mConnection.sendTextMessage(domsg);
        }
    }
    /**
     * 处理交互的数据
     */
    private WebSocketHandler webSocketHandler = new WebSocketHandler() {
        @Override
        public void onOpen() {
            // LogUtil.e(TAG, "Status: Connected");
            wsStatus = WS_STATUS_CONNECTED;
            EventBus.getDefault().post(new ServiceEvent(true, ServiceEvent.CONNECTED));
            DPDB.setServiceconn(true);
            networkChekTime = System.currentTimeMillis();
        }

        @Override
        public void onTextMessage(String payload) {
            //  LogUtil.e(TAG, "接受：" + payload);
            try {
                OperationModel operationModel = new Gson().fromJson(payload, OperationModel.class);
                switch(operationModel.getType()) {
                    case 1: {
                        LogUtil.d(TAG, "接收到开锁socket：" + payload);
                        //1为开锁信息
                        switch (operationModel.getOpenWay()){
                            case 1://手机
                            case 2://刷卡
                            case 3://手环
                            case 4://扫码
                                startService(new Intent(getApplicationContext(),TakePhotoService.class));
                                break;
                            case 6://视频对讲开锁
                            case 5://人脸识别
                            default:
                                break;
                        }

                        if (s.device().getLock()!=null) {
                            int ret = s.device().getLock().openLock();
                            LogUtil.e(TAG, "###ACWEBSOKCET.ret =" + ret + " // MainActivity.uid =" + DPDB.getUid());
                            if (ret == 1) {
                                EventBus.getDefault().post(new OpenLockStatusEvent(DPDB.getUid(), true));
                                EventBus.getDefault().post(new OpenLockRecallEvent());
                            } else {
                                LogUtil.d(TAG, "开锁出错 err:" + ret);
                            }
                        }else
                            LogUtil.e(TAG, "getLock() == null");
                        break;
                    }
                    case 99: {
                        //99为心跳返回信息
                        networkChekTime = System.currentTimeMillis();
                        LogUtil.v(TAG,"收到服务器的心跳回复：" + operationModel.getTime());
//                        EventBus.getDefault().post(new InfoEvent("收到服务器的心跳回复：" + operationModel.getTime()));
                        EventBus.getDefault().post(new ServiceEvent(HEART_BEAT,operationModel.isUnTerminal(),operationModel.isOwnerInfoUnTerminal()));
                        if (operationModel.isOffline()) {
                            disconnectAC();
                        }

                        break;
                    }
                    case 100:{
                        //100更新配置信息
                        LogUtil.e(TAG, "###case 100");
                        EventBus.getDefault().post(new UpdateConfigEvent(100));
                    }
                    case 98: {
                        //后台推送新版本
                        EventBus.getDefault().post(new ServiceEvent(operationModel.getUpdateType()));
                        break;
                    }
                    case 95: {
                        //后台推送卡列表
                        List<CardsModel> cards = operationModel.getUserCardList();
                        LogUtil.d(TAG,"后台推送卡列表 " + cards.size()+"条信息");
                        ArrayList writeCards = new ArrayList<String>();
                        for (int i = 0 ;i<cards.size();i++) {
                            LogUtil.d(TAG,(i+1) + " cardId:" + cards.get(i).getCardId()+" id:"+cards.get(i).getId());
                            writeCards.add(cards.get(i).getCardId()+"/"+cards.get(i).getId());
                        }
                        boolean writeOK = FileHelper.writeByFileOutputStream(Constant.CARDS_FILE_PATH,writeCards);
                        ServiceEvent se = new ServiceEvent(true,ServiceEvent.UPDATE_CARD_LIST);
                        se.setList(writeCards);
                        se.setWriteCardSuccess(writeOK);
                        EventBus.getDefault().post(se);


                        break;
                    }
                }
            } catch (Exception e) {
                // LogUtil.e(TAG, "无效的消息");
            }
        }

        @Override
        public void onClose(int code, String reason) {
            //LogUtil.e(TAG, "服务器连接中断:原因" + reason);
            EventBus.getDefault().post(new ServiceEvent(false,ServiceEvent.DISCONNECTED));
            disconnectAC();
            LogUtil.e(TAG,"服务器连接中断：原因" + reason);
            wsStatus = WS_STATUS_CLOSED;

        }
    };

    @Override
    public void onDestroy() {
        disconnectAC();
        //停止心跳检测
        heartHandler.removeCallbacks(heartRunnable);
        wsStatus = WS_STATUS_STOP;
        EventBus.getDefault().unregister(this);
        // LogUtil.e(TAG, " ACWebSocketService onDestroy");
        super.onDestroy();
    }


    /**
     * 心跳检测的任务
     */
    private Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            if (wsStatus == WS_STATUS_STOP) {
                //服务已停止，不需要再进行心跳检测
                return;
            }
            boolean status = false;
            switch (wsStatus) {
                case WS_STATUS_CONNECTING:
                    //长链接连接中
//                    Log.e(TAG, "心跳检测：连接中");
                    break;
                case WS_STATUS_INIT:
                    //服务刚初始化，需要长链接进行连接
//                    Log.e(TAG, "心跳检测：初始化状态下");
                    connectAC();
                    break;
                case WS_STATUS_RECONNECT:
                    //长链接断开，需要重新连接
//                    Log.e(TAG, "心跳检测：未链接 重新连接");
                    connectAC();
                    break;
                case WS_STATUS_CLOSED:
                    //长链接关闭，需要重新连接
//                    Log.e(TAG, "心跳检测：长链接关闭，需要重新连接");
                    connectAC();
                    break;
                case WS_STATUS_CONNECT_ERROR:
                    //长链接连接错误，再次链接
//                    Log.e(TAG, "心跳检测：长链接连接错误，再次链接");
                    connectAC();
                    break;
                case WS_STATUS_CONNECTED:
                    //长链接处于连接状态下
                    long time = System.currentTimeMillis();

                    if (time - networkChekTime > INTERVAL_DISCONNECT) {
                        //等待回复时间内没有收到服务器的信息，认定为服务器断开连接
                        EventBus.getDefault().post(new ServiceEvent(false,ServiceEvent.TIMEOUT));
                        DPDB.setServiceconn(false);
                        LogUtil.d(TAG,"没有收到服务器的回复，认定为服务器断线，重新连接服务器");
                        disconnectAC();
                        wsStatus = WS_STATUS_RECONNECT;
                    } else {
                        if (mConnection != null && mConnection.isConnected()) {
                            status = true;
                            String hmsg = new Gson().toJson(new HeartBeatModel(time));
                            mConnection.sendTextMessage(hmsg);
//                        Log.e(TAG, "心跳检测：正常链接===发送：" + hmsg);
                        }
                    }
                    break;
                default:

            }
            EventBus.getDefault().post(new ServiceEvent(status, HEART_BEAT));
            heartHandler.postDelayed(heartRunnable, HEART_INTERVAL);
        }
    };

//    public String getVersionCode(){
//        PackageManager packageManager=getPackageManager();
//        PackageInfo packageInfo;
//        String versionCode="";
//        try {
//            packageInfo=packageManager.getPackageInfo(getPackageName(),0);
//            versionCode=packageInfo.versionCode+"";
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return versionCode;
//    }
//
//    /**
//     * get App versionName
//     * @return
//     */
//    public String getVersionName(){
//        PackageManager packageManager=getPackageManager();
//        PackageInfo packageInfo;
//        String versionName="";
//        try {
//            packageInfo=packageManager.getPackageInfo(getPackageName(),0);
//            versionName=packageInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return versionName;
//    }

}
