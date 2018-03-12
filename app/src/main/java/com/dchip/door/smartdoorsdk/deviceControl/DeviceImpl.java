package com.dchip.door.smartdoorsdk.deviceControl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dchip.door.smartdoorsdk.Bean.AdvertisementModel;
import com.dchip.door.smartdoorsdk.Bean.ApiGetAdvertisement;
import com.dchip.door.smartdoorsdk.Bean.ApiGetCardListModel;
import com.dchip.door.smartdoorsdk.Bean.ApiGetDeviceConfigModel;
import com.dchip.door.smartdoorsdk.Bean.ApiGetPropManagement;
import com.dchip.door.smartdoorsdk.Bean.AppUpdateModel;
import com.dchip.door.smartdoorsdk.Bean.CardsModel;
import com.dchip.door.smartdoorsdk.Bean.ManagementMemberModel;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.EaseAccountListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.HumanCheckListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LockBreakListener;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LockPushListener;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.LogStrListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.ServerstatusListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.ServiceOpenLockListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.onPhotoTakenListener;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.UpdateOwenerListner;
import com.dchip.door.smartdoorsdk.deviceControl.Listener.onTickListener;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.BoltLockHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.CardHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.HumanCheckHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.LedHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.LockBreakHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.LockPushHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.MagneticLockHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.MotorLockHandler;
import com.dchip.door.smartdoorsdk.deviceControl.devicehandler.SteerHandler;
import com.dchip.door.smartdoorsdk.deviceControl.interfaces.LockHandler;
import com.dchip.door.smartdoorsdk.event.BroadcastEvent;
import com.dchip.door.smartdoorsdk.event.DoorTimeOutCloseEvent;
import com.dchip.door.smartdoorsdk.event.FaultEvent;
import com.dchip.door.smartdoorsdk.event.DeviceCheckEvent;
import com.dchip.door.smartdoorsdk.event.OpenLockRecallEvent;
import com.dchip.door.smartdoorsdk.event.OpenLockStatusEvent;
import com.dchip.door.smartdoorsdk.event.PhotoTakenEvent;
import com.dchip.door.smartdoorsdk.event.ReadCardEven;
import com.dchip.door.smartdoorsdk.event.ServiceEvent;
import com.dchip.door.smartdoorsdk.event.UpdateConfigEvent;
import com.dchip.door.smartdoorsdk.http.ApiCallBack;
import com.dchip.door.smartdoorsdk.receiver.ACBroadcastReceiver;
import com.dchip.door.smartdoorsdk.s;
import com.dchip.door.smartdoorsdk.service.ACWebSocketService;
import com.dchip.door.smartdoorsdk.service.TakePhotoService;
import com.dchip.door.smartdoorsdk.utils.Constant;
import com.dchip.door.smartdoorsdk.utils.DPDB;
import com.dchip.door.smartdoorsdk.utils.DeviceTimer;
import com.dchip.door.smartdoorsdk.utils.FileHelper;
import com.dchip.door.smartdoorsdk.utils.GlobalMonitor;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.dchip.door.smartdoorsdk.utils.NetworkStats;
import com.dchip.door.smartdoorsdk.utils.ShellUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.bugly.crashreport.CrashReport;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.dchip.door.smartdoorsdk.SdkInit.deviceApi;

/**
 * Created by llakcs on 2017/12/5.
 */

public class DeviceImpl implements DeviceManager {
    //锁类型：1=电插锁 2=电磁力锁 3=电机锁
    private LockHandler mLockHandler;
    private static String TAG = "DeviceImpl";
    private static final Object lock = new Object();
    private Handler controlhandler;
    private static volatile DeviceImpl instance;
    private String mac;
    private String uid;
    private DeviceTimer dTimer;
    private Activity mAcitvity;
    //是否已经上传mac地址
    private boolean isUploadMaced = false;
    //保存在本地的卡列表
    private ArrayList<String> cardList;
    //表示设备是否在线
    private boolean deviceOnline = false;
    //app更新类型 1.立即更新 2.延时更新
    private int updateType = 2;
    //app类型 0-手机 1-android终端&普通版本 2-qt 5-android终端&十寸屏(人脸，视频对讲) 6-android终端&十寸屏(视频对讲) 7-android终端&十五寸屏(16:9) 8-android终端&十五寸屏(4:3)
    private int appType = 1;
    //表示是否在长开锁状态
    private boolean longOpen = false;
    private boolean cardsProgressing = false;
    //接受离线事件若干次后设置设备不在线。
    private int offlineCount = 0;
    private HumanCheckListner mHumanChcekListner;
    private LockBreakListener mLockBreakListener;
    private LockPushListener mLockPushListener;
    private UpdateOwenerListner mUpdateOwner;
    private ServiceOpenLockListner serviceOpenLockListner;
    private ServerstatusListner mServerstatusListner;
    private EaseAccountListner easeAccountListner;
    private onPhotoTakenListener photoTakenListener;
    private LogStrListner mlogStrListner;
    private boolean enableLed = false;
    private boolean enableSteer = false;
    private boolean enableLock = false;
    private boolean enableTakePhoto = false;
    private boolean enableFaceDetect = false;
    private boolean enableOpenVoice = false;
    private int GET_AD_TIME = 1;
    private int AdvType = 1;

    private DeviceImpl() {
    }

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DeviceImpl();
                }
            }
        }
        s.Ext.setDeviceManager(instance);
    }

    @Override
    public void setLock(LockHandler lock) {
        this.mLockHandler = lock;
        mLockHandler.closeLock();
    }
    @Override
    public DeviceManager init(Activity activity, int appTypeNum) {
        controlhandler = new Handler();
        this.mAcitvity = activity;
        appType = appTypeNum;
        EventBus.getDefault().register(this);
        //获取mac
        if (appType == 9) {
            mac = getLocalMacAddressFromNetcfg().replace(":", "");
        } else if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            ShellUtil.CommandResult cr = ShellUtil.execCommand("cat /proc/cpuinfo", false);
            int i = cr.successMsg.indexOf("Serial");
            if (i != -1) {
                String cpuId = cr.successMsg.substring(i);
                cpuId = cpuId.substring(cpuId.indexOf(":") + 1).trim();
                mac = cpuId.substring(0, 16);
            }
        } else {
            mac = android.os.Build.SERIAL;
        }
        CrashReport.setUserId(mac);
        uid = mac + "lockId";
        DPDB.setmac(mac);
        DPDB.setUid(uid);
        LogUtil.e(TAG, "###mac =" + mac);
        cardList = FileHelper.readByBufferedReader(Constant.CARDS_FILE_PATH);
        //启动长链接服务
        activity.startService(new Intent(activity, ACWebSocketService.class));
        FileDownloadMonitor.setGlobalMonitor(GlobalMonitor.getImpl());
        return instance;
    }

    @Override
    public void showMsg(String tag, String msg) {
        if(mlogStrListner != null) {
            LogUtil.e(TAG, "###showmsg");
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            mlogStrListner.resultStr(tag, msg);
        }
    }

    @Override
    public DeviceManager EnableCardReader() {
        //初始化读卡模块
        CardHandler.getInstance();

        return instance;
    }

    @Override
    public DeviceManager EnableLock() {
        //初始化锁配置
        enableLock = true;
        setLock(FileHelper.readFileToString(Constant.LOCK_CONFIG_FILE_PATH));
        return instance;
    }


    @Override
    public DeviceManager EnableLed() {
        enableLed = true;
        //取消更新led
        s.device().getLed().closeLed(3);
        return instance;
    }


    @Override
    public DeviceManager EnableSteer() {
        enableSteer = true;
        return instance;
    }

    @Override
    public DeviceManager EnableTakePhoto() {
        enableTakePhoto = true;
        return instance;
    }

    public boolean isEnableFaceDetect() {
        return enableFaceDetect;
    }

    public boolean isEnableOpenVoice() {
        return enableOpenVoice;
    }


    @Override
    public int getAppType() {
        return appType;
    }

    int adcount = 0;

    @Override
    public DeviceManager EnableDtimer() {
        dTimer = new DeviceTimer(new onTickListener() {
            @Override
            public void onOneWeek() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        showMsg("test ----- 每星期打印");
                        //凌晨2：30更新
                        String local = "GMT+8";
                        Calendar c = new GregorianCalendar(TimeZone.getTimeZone(local));
                        c.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
                        c.set(Calendar.HOUR_OF_DAY, 2);
                        c.set(Calendar.MINUTE, 30);
                        c.set(Calendar.SECOND, 0);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }, c.getTime(), 0);

                    }
                }).start();
            }

            @Override
            public void onOneDay() {
                controlhandler.postDelayed(upload4GFlow, 500);
            }

            @Override
            public void onOneHouer() {
            }

            @Override
            public void onOneMinute() {
                adcount++;
                if (GET_AD_TIME!=0 && adcount > GET_AD_TIME) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getAd();
                        }
                    }).start();
                    adcount = 0;
                }

            }
        });
        return instance;
    }

    @Override
    public void release() {
        if (controlhandler != null) {
            controlhandler.removeCallbacksAndMessages(null);
            controlhandler = null;
        }
        EventBus.getDefault().unregister(this);
        if (mServerstatusListner != null) {
            mServerstatusListner = null;
        }
        if (serviceOpenLockListner != null) {
            serviceOpenLockListner = null;
        }
        if (mUpdateOwner != null) {
            mUpdateOwner = null;
        }
        if (mHumanChcekListner != null) {
            mHumanChcekListner = null;
        }
    }

    @Override
    public void setLock(String config) {
        if (config != null) {
            LogUtil.e(TAG, "读取锁配置：" + config);
            String arg[] = config.split("/");
            if (arg.length != 5) return;
            int lockArg = Integer.parseInt(arg[0]);
            int doorArg = Integer.parseInt(arg[1]);
            int oriLockArg = Integer.parseInt(arg[2]);
            boolean isSign = Boolean.parseBoolean(arg[3]);
            switch (arg[4]) {
                case "1":
                    setLock(new BoltLockHandler().setDefaultStatus(lockArg, doorArg, oriLockArg, isSign));
                    break;
                case "2":
                    setLock(new MagneticLockHandler().setDefaultStatus(lockArg, doorArg, oriLockArg, isSign));
                    break;
                case "3":
                    setLock(new MotorLockHandler().setDefaultStatus(lockArg, doorArg, oriLockArg, isSign));
                    break;
            }
        }
    }

    @Override
    public DeviceManager setHumanCheckListner(HumanCheckListner humanCheckListner) {
        this.mHumanChcekListner = humanCheckListner;
        //初始化人体检测设备
        HumanCheckHandler.getInstance();
        return instance;
    }


    @Override
    public void unRegHumanCheckListner() {
        if (mHumanChcekListner != null) {
            this.mHumanChcekListner = null;
        }
    }

    @Override
    public DeviceManager setLockPushListener(LockPushListener lockPushListener) {
        this.mLockPushListener = lockPushListener;
        LockPushHandler.getInstance();
        return instance;
    }


    @Override
    public void unRegLockPushListenerListner() {
        if (mLockPushListener != null) {
            this.mLockPushListener = null;
            LockPushHandler.getInstance().finish();
        }
    }

    @Override
    public DeviceManager setLockBreakListener(LockBreakListener lockBreakListener) {
        this.mLockBreakListener = lockBreakListener;
        LockBreakHandler.getInstance();
        return instance;
    }


    @Override
    public void unRegLockBreakListener() {
        if (mLockBreakListener != null) {
            this.mLockBreakListener = null;
            LockBreakHandler.getInstance().finish();
        }
    }


    @Override
    public void setUpdateOwenerListner(UpdateOwenerListner updateOwenerListner) {
        this.mUpdateOwner = updateOwenerListner;
    }

    @Override
    public void unRegUpdateOwnerListner() {
        if (mUpdateOwner != null) {
            this.mUpdateOwner = null;
        }
    }


    @Override
    public void setServiceOpenLockListner(ServiceOpenLockListner serviceOpenLockListner) {
        this.serviceOpenLockListner = serviceOpenLockListner;
    }

    @Override
    public void unRegServiceOpenLockListner() {
        if (serviceOpenLockListner != null) {
            this.serviceOpenLockListner = null;
        }
    }

    @Override
    public void setEaseAcountListner(EaseAccountListner acountListner) {
        this.easeAccountListner = acountListner;
    }

    @Override
    public void unRegEaseAcountListner() {
        if (this.easeAccountListner != null) {
            this.easeAccountListner = null;
        }
    }

    @Override
    public void takePhoto(onPhotoTakenListener tp) {
        if (enableTakePhoto) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
            String fileneme = com.dchip.door.smartdoorsdk.utils.Constant.VIST_PHOTO_PATH + sdf.format(System.currentTimeMillis()) + ".jpg";
            Intent intent = new Intent().setClass(mAcitvity, TakePhotoService.class);
            intent.putExtra("path", fileneme);
            mAcitvity.startService(intent);
            photoTakenListener = tp;
        }
    }

    @Override
    public void setServerstatusListner(ServerstatusListner serverstatusListner) {
        this.mServerstatusListner = serverstatusListner;
    }

    @Override
    public void unRegServerstatusListner() {
        if (mServerstatusListner != null) {
            this.mServerstatusListner = null;
        }
    }

    @Override
    public LockHandler getLock() {
        if (enableLock) {
            return mLockHandler;
        }
        return null;
    }

    @Override
    public LedHandler getLed() {
        if (enableLed) {
            return LedHandler.getInstance();
        }
        return null;
    }

    @Override
    public SteerHandler getSteer() {
        if (enableSteer) {
            return SteerHandler.getInstance();
        }
        return null;
    }

    @Override
    public int getAdvType() {
        return AdvType;
    }

    /**
     * 上传mac
     */
    @Override
    public void upLoadMac() {
        controlhandler.post(uploadMacRunnable);
    }

    @Override
    public void uploadAppVer(String ver) {
        controlhandler.post(uploadAppVersionRunnable);
    }

    @Override
    public void checkVer() {
        controlhandler.postDelayed(checkVersionRunnable, 3000);
    }

    @Override
    public void uploadLock() {
        controlhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUploadMaced) {
                    deviceApi.uploadLock(mac, uid,appType).enqueue(new ApiCallBack<Object>() {
                        @Override
                        public void success(Object o) {
//                            showMsg("上传锁板MAC信息成功");
                        }

                        @Override
                        public void fail(int i, String s) {
//                            showMsg("上传锁板信息失败" + s);
                            uploadLock();
                        }
                    });
                } else {
                    uploadLock();
                }
            }
        }, 2000);
    }

    @Override
    public void checkCrashLogAndUpload() {
        LogUtil.e(TAG, "checkCrashLogAndUpload");
        String[] logs = new File(Constant.CRASH_LOG_UPLOAD_FAIL_PATH).list();
        if (logs != null)
            for (int i = 0; i < logs.length; i++) {
                String content = FileHelper.readFileToString(Constant.CRASH_LOG_UPLOAD_FAIL_PATH + logs[i]);
                final String logPath = Constant.CRASH_LOG_UPLOAD_FAIL_PATH + logs[i];
                deviceApi.reportCrash(mac, content).enqueue(new ApiCallBack<Object>() {
                    @Override
                    public void success(Object o) {
                        LogUtil.e(TAG, "onCrashEvent upload ok");
//                    showMsg("测试打印 " + new Date() + " app崩溃上报成功----！");
                        File f = new File(logPath);
                        f.renameTo(new File(Constant.CRASH_LOG_PATH + f.getName()));
                    }

                    @Override
                    public void fail(int i, String s) {
                        if (s != null) {
//                        showMsg("reportCrash:"+s);
                        }
                    }
                });
            }
    }

    /**
     * 上传app版本号
     */
    private Runnable uploadAppVersionRunnable = new Runnable() {
        @Override
        public void run() {
            deviceApi.uploadAppVersion(mac, getVersionName(), appType).enqueue(new ApiCallBack<Object>() {
                @Override
                public void success(Object o) {

                    LogUtil.d(TAG, "上传app版本号成功");
                }

                @Override
                public void fail(int i, String s) {

                    LogUtil.d(TAG, "上传MAC失败" + s);
                }
            });
        }
    };

    /**
     * 上传app版本号
     */
    private Runnable upload4GFlow = new Runnable() {
        @Override
        public void run() {
            deviceApi.uploadFlow(mac, (NetworkStats.getIns().getMobileRxBytes() + NetworkStats.getIns().getMobileTxBytes()) / 1000 + "").enqueue(new ApiCallBack<Object>() {
                @Override
                public void success(Object o) {
//                                showMsg("上传流量成功");
                }

                @Override
                public void fail(int i, String s) {
//                                showMsg("上传流量失败:" + s);

                }
            });
        }
    };

    /**
     * get App versionName
     *
     * @return
     */
    public String getVersionName() {
        PackageManager packageManager = mAcitvity.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(mAcitvity.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * 上传主控板数据
     */
    private Runnable uploadMacRunnable = new Runnable() {
        @Override
        public void run() {
            deviceApi.uploadMac(mac, GetNetworkType(),1).enqueue(new ApiCallBack<Object>() {
                @Override
                public void success(Object o) {
                    isUploadMaced = true;
                    LogUtil.d(TAG, "上传MAC成功");
                }

                @Override
                public void fail(int i, String s) {
                    isUploadMaced = false;
                    LogUtil.e(TAG, "上传MAC失败" + s);
                    controlhandler.postDelayed(uploadMacRunnable, 3000);
                }
            });
        }
    };
    /**
     * 上传按键开门
     */
    private Runnable openByKeyRecordRunnable = new Runnable() {
        @Override
        public void run() {
            deviceApi.setOpenByKeyRecord(uid).enqueue(new ApiCallBack<Object>() {
                @Override
                public void success(Object o) {
                    LogUtil.d(TAG, "上传按键开门 状态 成功");
                }

                @Override
                public void fail(int i, String s) {
                    LogUtil.e(TAG, "上传按键开门 状态 失败" + s);
                }
            });
        }
    };

    /**
     * 检查服务器版本
     */
    private Runnable checkVersionRunnable = new Runnable() {
        @Override
        public void run() {
            deviceApi.checkVersion(appType).enqueue(new ApiCallBack<AppUpdateModel>() {
                @Override
                public void success(AppUpdateModel o) {
                    if (o == null) {
                        LogUtil.e(TAG, "服务器上不存在该版本：" + appType);
                        return;
                    }
//                    String serverUrl = DPDB.getserverUrl();
//                    final String url = serverUrl.substring(0, serverUrl.length() - 5) + o.getAddress();
                    final String url =o.getDetailAddress();
                    LogUtil.e(TAG,"DownloadUrl ="+url);
//                    showMsg("检查版本号成功 " + o.getVersion() + " url:" + url);
                    if(url != null && !url.equals("")){
                        if (!o.getVersion().equals(getVersionName())) {//检查版本号不一致时更新
                            LogUtil.w(TAG, "checkVersionRunnable  " + o.getVersion());
                            LogUtil.w(TAG, "url:" + url);
                            //删除旧apk
                            File[] fs = new File(Constant.DOWNLOAD_APK_PATH).listFiles();
                            for (File f : fs) {
                                if (url.indexOf(f.getName()) < 0) {
                                    f.delete();
                                }
                            }
                            //延迟下载
                            Random r = new Random();
                            long startTime = (long) (r.nextFloat() * 1000 * 60 * 1); //y延迟时间。
                            LogUtil.w(TAG, "延迟下载(1分钟内):" + startTime + "毫秒");
//                        showMsg("与当前版本不一致，" + (startTime / 1000) + "秒后开始下载..");
//                        createTask(url).start();
                            //// TODO: 2017/8/31 10分钟随机时间开始下载
                            final String md5 = o.getMd5();
                            controlhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    createTask(url, Constant.DOWNLOAD_APK_PATH, getNameFromUrl(url), md5).start();
                                }
                            }, startTime);

                        } else {
//                        showMsg("与当前版本一致,无须更新");
                        }
                    }
                }

                @Override
                public void fail(int i, String s) {
//                    showMsg("检查版本号失败:" + s);

                }
            });
        }
    };

    /**
     * 上传锁信息
     */
    @Override
    public void updateOnwerStatus() {
        deviceApi.updateOnwerStatus(mac, 1).enqueue(new ApiCallBack<Object>() {
            @Override
            public void success(Object o) {
                Log.w(TAG, "updateOnwerStatus success");
            }

            @Override
            public void fail(int i, String s) {
                Log.e(TAG, "updateOnwerStatus fail :" + s);
            }
        });

    }

    /**
     * 上传锁信息
     */
    public void getAd() {
        controlhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceApi.getAd(mac, appType).enqueue(new ApiCallBack<ApiGetAdvertisement>() {
                    @Override
                    public void success(ApiGetAdvertisement o) {
                        AdvType = o.getAdvType();
                        Log.w(TAG, "getAd success AdvType=" + o.getAdvType());
                        if (o.getAdvType() == 1 || o.getAdvType() == 3) {
                            Log.w(TAG, "video:" + o.getBannerVideoList().size());
                            //查寻是否有多余视频广告
                            List<File> vFiles = scanSDcardVideoList(Constant.VIDEOPATH);
                            for (File f : vFiles) {
                                boolean isFind = false;
                                for (AdvertisementModel ad : o.getBannerVideoList()) {
                                    if (ad.getContent().indexOf(f.getName()) >= 0) {
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (isFind) {
                                    LogUtil.d(TAG, "本地已存在视频广告:" + f.getName());
                                } else {
                                    LogUtil.d(TAG, "本地多余视频广告:" + f.getName());
                                    f.delete();
                                }
                            }
                            //轮询是否有新加视频广告
                            for (AdvertisementModel ad : o.getBannerVideoList()) {
                                boolean isFind = false;
                                for (File f : vFiles) {
                                    if (ad.getContent().indexOf(f.getName()) >= 0) {
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    LogUtil.d(TAG, "新加视频广告需要下载:" + ad.getContent());
                                    createTask(ad.getContent(), Constant.VIDEOPATH, getNameFromUrl(ad.getContent()), ad.getMd5()).start();
                                }
                            }
                        }
                        if (o.getAdvType() == 1 || o.getAdvType() == 2) {
                            Log.w(TAG, "photo:" + o.getBannerPicList().size());
                            //查寻是否有多余图片广告  132
                            List<File> PFiles = scanSDcardImageFileList(Constant.ADIMGPATH);
                            for (File f : PFiles) {
                                boolean isFind = false;
                                for (AdvertisementModel ad : o.getBannerPicList()) {
                                    if (ad.getPhoto().indexOf(f.getName()) >= 0) {
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (isFind) {
                                    LogUtil.d(TAG, "本地已存在图片广告:" + f.getName());
                                } else {
                                    LogUtil.d(TAG, "本地多余图片广告:" + f.getName());
                                    f.delete();
                                }
                            }
                            //轮询是否有新加图片广告
                            for (AdvertisementModel ad : o.getBannerPicList()) {
                                boolean isFind = false;
                                for (File f : PFiles) {
                                    if (ad.getPhoto().indexOf(f.getName()) >= 0) {
                                        isFind = true;
                                        break;
                                    }
                                }
                                if (!isFind) {
                                    LogUtil.d(TAG, "新加图片广告需要下载:" + ad.getPhoto());
                                    createTask(ad.getPhoto(), Constant.ADIMGPATH, getNameFromUrl(ad.getPhoto()), ad.getMd5()).start();
                                }
                            }
                        }
                    }

                    @Override
                    public void fail(int i, String s) {
                        Log.e(TAG, "updateOnwerStatus fail :" + s);
                    }
                });
            }
        }, 2000);

    }

    /**
     * 获取物管联系
     */
    public void getCallCenterInfo() {
        controlhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceApi.propertyManagement(mac).enqueue(new ApiCallBack<ApiGetPropManagement>() {
                    @Override
                    public void success(ApiGetPropManagement o) {
                        Log.w(TAG, "propertyManagement success");
                        StringBuffer sb = new StringBuffer();
                        for (ManagementMemberModel mem : o.getList()) {
                            sb.append(mem.getRemark() + "/" + mem.getPhone() + "\r\n");
                            Log.w(TAG, mem.getRemark() + ":" + mem.getPhone());
                        }
                        FileHelper.writeByFileOutputStream(Constant.MANAGEMENT_FILE_PATH, sb.toString());
                    }

                    @Override
                    public void fail(int i, String s) {
                        Log.e(TAG, "propertyManagement fail :" + s);
                    }
                });
            }
        }, 2000);

    }

    /**
     * 上传下载进度
     */
    public void uploadDownloadProgress(int progress) {
        deviceApi.uploadDownloadProgress(mac, progress, appType).enqueue(new ApiCallBack<Object>() {
            @Override
            public void success(Object o) {

            }

            @Override
            public void fail(int i, String s) {
                Log.e(TAG, "uploadDownloadProgress fail :" + s);
            }
        });

    }


    /**
     * 获取锁设置。
     */
    protected Runnable getDeviceConfigRunnable = new Runnable() {
        @Override
        public void run() {
            deviceApi.getDeviceConfig(mac).enqueue(new ApiCallBack<ApiGetDeviceConfigModel>() {
                @Override
                public void success(ApiGetDeviceConfigModel model) {

                    LogUtil.e(TAG, "成功获取锁配置：锁:" + model.getLock_access() + " 门:" + model.getDoor_access() + " 原锁:" + model.getOrignal_lock_access() +
                            " 单锁:" + (model.getLock_num() == 1) + " 锁类型:" + model.getLock_type() + " 环信账号:" + model.getEaseAccount()+ " 功能控制:" + model.getFunction());

                    if (model.getEaseAccount() != null) {
                        if (easeAccountListner != null) {
                            easeAccountListner.ResultAcount(model.getEaseAccount().toString());
                        }
                    }
                    if (enableLock) {
                        switch (model.getLock_type()) {
                            case 1:
                                if (s.device().getLock() == null) {
                                    s.device().setLock(new BoltLockHandler());
                                } else if (!s.device().getLock().TAG.equals("BoltLockHandler")) {
                                    s.device().getLock().finish();
                                    s.device().setLock(new BoltLockHandler());
                                }
                                break;

                            case 2:
                                if (s.device().getLock() == null) {
                                    s.device().setLock(new MagneticLockHandler());
                                } else if (!s.device().getLock().TAG.equals("MagneticLockHandler")) {
                                    s.device().getLock().finish();
                                    s.device().setLock(new MagneticLockHandler());
                                }
                                break;

                            case 3:
                                if (s.device().getLock() == null) {
                                    s.device().setLock(new MotorLockHandler());
                                } else if (!s.device().getLock().TAG.equals("MotorLockHandler")) {
                                    s.device().getLock().finish();
                                    s.device().setLock(new MotorLockHandler());
                                }
                                break;

                            default:
                                if (s.device().getLock() == null) {
                                    s.device().setLock(new BoltLockHandler());
                                } else if (!s.device().getLock().TAG.equals("BoltLockHandler")) {
                                    s.device().getLock().finish();
                                    s.device().setLock(new BoltLockHandler());
                                }
                                break;

                        }
                        s.device().getLock().setDefaultStatus(model.getLock_access(), model.getDoor_access()
                                , model.getOrignal_lock_access(), model.getLock_num() == 1);

                        FileHelper.writeByFileOutputStream(Constant.LOCK_CONFIG_FILE_PATH, model.getLock_access()
                                + "/" + model.getDoor_access() + "/" + model.getOrignal_lock_access() + "/" + (model.getLock_num() == 1) + "/" + model.getLock_type());

                    }

                    String[] Functions = model.getFunction().split("-");
                    for (String fun:Functions){
                        switch(fun){
                            case "1"://业主开门
                                LogUtil.w(TAG,"业主开门功能 无效");
                                break;
                            case "2"://开门拍照
                                LogUtil.w(TAG,"开门拍照功能 无效");
                                break;
                            case "3"://人脸识别
                                enableFaceDetect = true;
                                break;
                            case "4"://智能语音
                                LogUtil.w(TAG,"智能语音功能 无效");
                                break;
                            case "5"://开门语音
                                enableOpenVoice = true;
                                break;
                            case "6"://IC卡开门
                                EnableCardReader();
                                break;
                        }
                    }
                }

                @Override
                public void fail(int i, String s) {
                    LogUtil.e(TAG, "getDeviceConfigRunnable 失败 " + s);
                }
            });
        }

    };


    @Override
    public void setLogStrListner(LogStrListner logStrListner) {
          this.mlogStrListner = logStrListner;
    }

    @Override
    public void unRegLogStrListner() {
        if(mlogStrListner != null){
            this.mlogStrListner = null;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateConfigEvent(UpdateConfigEvent updateConfigEvent){
        LogUtil.e(TAG, "###UpdateConfigEvent ");
        controlhandler.post(getDeviceConfigRunnable);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenLockRecallEvent(OpenLockRecallEvent openLockRecallEvent) {
        if (serviceOpenLockListner != null) {
            serviceOpenLockListner.lockopen(openLockRecallEvent.getOpenWay());
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPhotoTakenEvent(PhotoTakenEvent photoTakenEvent) {
        if (photoTakenListener != null) {
            LogUtil.w(TAG,"get onPhotoTakenEvent");
            photoTakenListener.onTaken(photoTakenEvent.getPath());
            photoTakenListener = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceCheckEvent(DeviceCheckEvent event) {
        switch (event.eventName) {
            case "human": {
                if (mHumanChcekListner != null)
                    mHumanChcekListner.humanCheck();
                break;
            }
            case "lockBreak": {
                if (mLockBreakListener != null)
                    mLockBreakListener.onLockBreak();
                break;
            }
            case "lockPush": {
                if (mLockPushListener != null)
                    mLockPushListener.onPush();
                int i=0;
                if (getLock()!=null) {
                    i = getLock().openLock();
                    LogUtil.e(TAG, "###result lockcode =" + i);
                    EventBus.getDefault().post(new OpenLockStatusEvent(DPDB.getUid(), true));
                    new Handler().postDelayed(openByKeyRecordRunnable,100);
                }else{
                    LogUtil.e(TAG, "###result getLock=null");
                }
                break;
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadCardEvent(ReadCardEven event) {
        LogUtil.w(TAG, "onReadCardEvent:" + event.getCardId());
        String checkedId = null;
        for (String info : cardList) {
            String[] infos = info.split("/");
            if (infos[0].equals(event.getCardId())) {
                if (enableLock)
                    s.device().getLock().openLock();
                LogUtil.d(TAG, event.getCardId() + " 与本地卡库匹配成功");
                checkedId = event.getCardId();
                deviceApi.uploadCardId(uid, event.getCardId(), infos[1]).enqueue(new ApiCallBack<Object>() {
                    @Override
                    public void success(Object o) {
                        LogUtil.d(TAG, "上传卡信息成功");
                    }

                    @Override
                    public void fail(int i, String s) {
                        LogUtil.d(TAG, "上传卡信息失败:" + s);

                    }
                });
            }
        }
        if (checkedId == null) {

        }
//            showMsg(event.getCardId() + " 与本地卡库匹配失败");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoorTimeOutCloseEvent(DoorTimeOutCloseEvent event) {
        deviceApi.doorTimeOutClose(uid).enqueue(new ApiCallBack<Object>() {
            @Override
            public void success(Object o) {
                LogUtil.d(TAG, "onDoorTimeOutCloseEvent " + new Date() + " 关门上报成功----！");
            }

            @Override
            public void fail(int i, String s) {
                if (s != null) {
                    LogUtil.d(TAG, "onDoorTimeOutCloseEvent:" + s);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFaultEvent(FaultEvent event) {
        deviceApi.reportFault(uid, event.getType()).enqueue(new ApiCallBack<Object>() {
            @Override
            public void success(Object o) {
                LogUtil.d(TAG, "测试打印 " + new Date() + " 锁控板故障上报成功----！");
            }

            @Override
            public void fail(int i, String s) {
                if (s != null) {
                    LogUtil.d(TAG, "reportFault:" + s);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBroadcastEvent(BroadcastEvent event) {
        if (event.getAction().equals(ACBroadcastReceiver.UpdataFailAction)) {
            deviceApi.installFail(mac, event.getExtraString()).enqueue(new ApiCallBack<Object>() {
                @Override
                public void success(Object o) {

                    LogUtil.d(TAG, "上传更新失败信息成功");
                }

                @Override
                public void fail(int i, String s) {
                    LogUtil.d(TAG, "上传更新失败信息失败 " + s);
                }
            });
        }
    }


    //成功链接service后触发even。
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEvent(ServiceEvent event) {
        if (event.isConnected()) {
            switch (event.getType()) {
                case ServiceEvent.HEART_BEAT: {
                    if (mServerstatusListner != null) {
                        mServerstatusListner.getHeartBeats();
                    }
                    if (event.isUpdateOwener()) {
                        if (mUpdateOwner != null) {
                            this.mUpdateOwner.update();
                        }
                    }
                    if (event.isUpdateCards() && !cardsProgressing) {
                        cardsProgressing = true;
                        deviceApi.getCardListByMac(mac).enqueue(new ApiCallBack<ApiGetCardListModel>() {
                            @Override
                            public void success(ApiGetCardListModel cardLists) {
                                LogUtil.e(TAG, "成功获取卡列表");
                                List<CardsModel> cards = cardLists.getData();
//                                showMsg("api后台推送卡列表 " + cards.size() + "条信息");
                                ArrayList writeCards = new ArrayList<String>();
                                for (int i = 0; i < cards.size(); i++) {
//                                    showMsg((i + 1) + " cardId:" + cards.get(i).getCardId() + " id:" + cards.get(i).getId());
                                    writeCards.add(cards.get(i).getCardId() + "/" + cards.get(i).getId());
                                }
                                boolean writeOK = FileHelper.writeByFileOutputStream(Constant.CARDS_FILE_PATH, writeCards);
                                ServiceEvent se = new ServiceEvent(true, ServiceEvent.UPDATE_CARD_LIST);
                                se.setList(writeCards);
                                se.setWriteCardSuccess(writeOK);
                                EventBus.getDefault().post(se);

                            }

                            @Override
                            public void fail(int i, String s) {
                                if (s != null) {
                                    //                                    showMsg("getCardListByMac:"+s);
                                }
                            }
                        });
                    }
                    break;
                }
                case ServiceEvent.CONNECTED: {
                    if (mServerstatusListner != null) {
                        mServerstatusListner.connected();
                    }
                    controlhandler.post(uploadMacRunnable);
                    controlhandler.post(uploadAppVersionRunnable);
                    controlhandler.post(getDeviceConfigRunnable);
                    checkCrashLogAndUpload();
                    getCallCenterInfo();
                    uploadLock();
                    getAd();
                    if (enableLed) {
                        s.device().getLed().openLed(2);
                    }
                    if (enableLock) {
                        getLock().openLock();
                    }
                    break;
                }
                case ServiceEvent.UPDATE_APK: {
                    if (mServerstatusListner != null) {
                        mServerstatusListner.updateAPK();
                    }
                    controlhandler.post(checkVersionRunnable);
                    updateType = event.getUpdateType();
                    break;
                }
                case ServiceEvent.UPDATE_CARD_LIST: {
                    if (mServerstatusListner != null) {
                        mServerstatusListner.updatecardlist();
                    }
                    cardList = (ArrayList<String>) event.getList().clone();
                    int status = 0;
                    if (event.isWriteCardSuccess()) status = 1;
                    if (event.isWriteCardSuccess()) {
                        deviceApi.reportWriteCardStatus(mac, status).enqueue(new ApiCallBack<Object>() {
                            @Override
                            public void success(Object o) {
//                                showMsg("测试打印:" + new Date() + " 上传写卡状态成功");
                                cardsProgressing = false;
                            }

                            @Override
                            public void fail(int i, String s) {
                                if (s != null)
//                                    showMsg("reportWriteCardStatus:"+s);
                                    cardsProgressing = false;
                            }

                        });
                    }
                    break;
                }
            }
//            if (longOpen) mServiceInfo.setText("在线  长开锁状态");
//            else mServiceInfo.setText("在线  正常开锁状态");
            if (enableLock && !deviceOnline && longOpen)
                getLock().openLock();
                deviceOnline = true;
            offlineCount = 0;
        } else {
            if (event.getType() == ServiceEvent.DISCONNECTED)
                if (mServerstatusListner != null) {
                    mServerstatusListner.disconn();
                }
            if (offlineCount > 3) {
                deviceOnline = false;
            } else {
                offlineCount++;
            }
            if (enableLed) {
                s.device().getLed().closeLed(2);
            }
            if (enableLock) {
                getLock().longOpenLock();
            }
//            if (longOpen) mServiceInfo.setText("离线  长开锁状态");
//            else mServiceInfo.setText("离线  正常开锁状态");

        }
    }


    public BaseDownloadTask createTask(final String url, final String path, final String name, final String md5) {
        final File file = new File(path + name);
        return FileDownloader.getImpl().create(url)
                .setPath(file.getAbsolutePath(), false)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        String a = String.format("%.0f", (double) soFarBytes / (double) totalBytes * 100);
                        LogUtil.w(TAG, name + " downloading " + a + "%");
                        if (path.equals(Constant.DOWNLOAD_APK_PATH)) {
                            uploadDownloadProgress(Integer.parseInt(a));
                        }
//                        showMsg("apk downloading " + a + "%");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        new File(path + name).delete();
                        if (!deviceOnline) {
                            //showMsg("apk 下载失败,设备已掉线，停止下载。");
                        } else {
//                            showMsg("apk 下载失败,15秒后重试。");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    createTask(url, path, name, md5);
                                }
                            }, 1000 * 15);
                        }
                        e.printStackTrace();
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        LogUtil.w(TAG, name + "downloading 100%");
                        file.renameTo(new File(path + name));
                        String downMd5 = FileHelper.getMd5ByFile(new File(path + name));
                        LogUtil.w(TAG, "saved in " + path + name);
                        LogUtil.w(TAG, "md5 Conpare net:" + md5 + " download:" + downMd5);
                        if (md5 != null && downMd5.equals(md5)) {
                            if (path.equals(Constant.DOWNLOAD_APK_PATH)) {

                                installApp(path + name);
                            } else if (path.equals("")) {

                            }
                        } else {
                            LogUtil.w(TAG, "check md5 fail");
                            new File(path + name).delete();
                            controlhandler.post(checkVersionRunnable);
                        }

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                });
    }

    private static final String INSTALL_REBOOT_ACTION = "com.dchip.install.reboot";
    private void installApp(final String fullPath) {
        if (updateType == 1) {
            LogUtil.w(TAG, "即时更新");
            //安装app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fullPath)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (enableLed) {
                s.device().getLed().openLed(3);
            }
            mAcitvity.getApplicationContext().startActivity(intent);
            intent = new Intent(INSTALL_REBOOT_ACTION);
            intent.putExtra("install", true);
            mAcitvity.getApplicationContext().sendBroadcast(intent);
        } else {
            //凌晨安装
            LogUtil.w(TAG, "凌晨2时20分更新");
//                                showMsg("凌晨2时20分更新");
            String local = "GMT+8";
            Calendar c = new GregorianCalendar(TimeZone.getTimeZone(local));
            c.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
            c.set(Calendar.HOUR_OF_DAY, 2);
            c.set(Calendar.MINUTE, 20);
            c.set(Calendar.SECOND, 0);
            long delay = c.getTimeInMillis() - System.currentTimeMillis();
            if (c.getTimeInMillis() - System.currentTimeMillis() < 0) {
                delay += 24 * 60 * 60 * 1000;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                                        new File(Constant.DOWNLOAD_APK_PATH + "temp.apk").renameTo(new File(Constant.DOWNLOAD_APK_PATH + "aa.apk"));
                    //安装app
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(fullPath)), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (enableLed) {
                        s.device().getLed().openLed(3);
                    }
                    mAcitvity.getApplicationContext().startActivity(intent);
                    intent = new Intent(INSTALL_REBOOT_ACTION);
                    intent.putExtra("install", true);
                    mAcitvity.getApplicationContext().sendBroadcast(intent);
                }
            }, delay);
//                                showMsg("update after " + delay + "ms");
        }
    }


    //根据busybox获取本地Mac
    public static String getLocalMacAddressFromNetcfg() {
        String result = "";
        String Mac = "";
        result = callCmd("netcfg", "eth0");
        if (result == null) {
            return "网络出错，请检查网络";
        }
        if (result.length() > 0 && result.contains("eth0") == true) {
            Mac = result.substring(result.length() - 17, result.length());
            Log.e(TAG, "Mac:" + Mac + " Mac.length: " + Mac.length());
            result = Mac;
        }
        return result;
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                //result += line;
                Log.i("test", "line: " + line);
            }

            result = line;
            Log.i("test", "result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    protected String getNameFromUrl(String url) {
        String ss[] = url.split("/");
        if (ss.length > 0) {
            return ss[ss.length - 1];
        } else return null;
    }

    //获取广告时间间隔 单位 分钟
    public DeviceManager setGET_AD_TIME(int GET_AD_TIME) {
        this.GET_AD_TIME = GET_AD_TIME;
        return instance;
    }

    private List<File> scanSDcardVideoList(String path) {
        // File file = new File(sdcardRingPath);
        File file = new File(path);
        List<File> files = new ArrayList<File>();
        if (!file.exists()) {
            return files;
        }
        File[] subFile = file.listFiles();
        files.clear();
        if (subFile == null) {
            return files;
        }

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为apk结尾
                if (filename.trim().toLowerCase().endsWith(".mp4")//
                        || filename.trim().toLowerCase().endsWith(".MP4")//
                        ) {
                    if (subFile[iFileLength].length() < 500 * 1000 * 1000) {
                        // 文件大小
                        files.add(subFile[iFileLength]);
                    }

                }
            }
        }
        return files;
    }

    private List<File> scanSDcardImageFileList(String path) {
        File file = new File(path);
        List<File> files = new ArrayList<File>();
        if (!file.exists()) {
            return files;
        }
        File[] subFile = file.listFiles();
        files.clear();
        if (subFile == null) {
            return files;
        }

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为MP4结尾
                if (filename.trim().toLowerCase().endsWith(".jpg")//
                        || filename.trim().toLowerCase().endsWith(".jpeg")//
                        || filename.trim().toLowerCase().endsWith(".png")//
                        ) {
                    if (subFile[iFileLength].length() < 5 * 1000 * 1000) {
                        // 文件大小
                        files.add(subFile[iFileLength]);
                    }

                }
            }
        }
        return files;
    }

    public int GetNetworkType() {
        int strNetworkType = -1;

        NetworkInfo networkInfo = ((ConnectivityManager) mAcitvity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.getTypeName().equals("WIFI")) {
                    strNetworkType = 1;
                } else if (networkInfo.getTypeName().equals("ETHERNET")) {
                    strNetworkType = 5;
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = 2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = 3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = 4;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = 3;
                        } else {
                            strNetworkType = 4;
                        }

                        break;
                }

                Log.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        Log.e("cocos2d-x", "Network Type : " + strNetworkType);

        return strNetworkType;
    }
}
