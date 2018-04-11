package com.dchip.door.smartdoorsdk;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import com.dchip.door.smartdoorsdk.http.DeviceApi;
import com.dchip.door.smartdoorsdk.http.RequestHeaderInterceptor;
import com.dchip.door.smartdoorsdk.receiver.ACBroadcastReceiver;
import com.dchip.door.smartdoorsdk.utils.Constant;
import com.dchip.door.smartdoorsdk.utils.CrashHandler;
import com.dchip.door.smartdoorsdk.utils.DPDB;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.tencent.bugly.crashreport.CrashReport;
import java.io.File;
import java.net.Proxy;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by llakcs on 2017/11/29.
 */

public class SdkInit {

    private static String TAG = "SdkInit";
    public static DeviceApi deviceApi;

    public static void onCreate(final Application app,final String wsUrl,final String serverUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //创建opencv拍照文件夹
                new File(Constant.VISTPATH).mkdirs();
                new File(Constant.VIDEOPATH).mkdirs();
                new File(Constant.ADIMGPATH).mkdirs();
                new File(Constant.VIST_PHOTO_PATH).mkdirs();
                new File(Constant.DOWNLOAD_APK_PATH).mkdirs();
                new File(Constant.CRASH_LOG_PATH).mkdirs();
                //初始化DPDB
                DPDB.InitDPDbRW(app);
                //服务器地址和websocket地址不为空
                if(wsUrl != null && serverUrl != null){
                    DPDB.setwsUrl(wsUrl);
                    DPDB.setserverUrl(serverUrl);
                    //初始化http模块
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(String message) {
                            //打印retrofit日志
                            LogUtil.i(TAG, "api_msg:" + message);
                        }
                    });
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)//添加Log拦截器
                            .addInterceptor(new RequestHeaderInterceptor())//注入header
                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(DPDB.getserverUrl())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    deviceApi = retrofit.create(DeviceApi.class);
                }


                //初始化bugly
                CrashHandler.getInstance().init(app.getApplicationContext());
                CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(app.getApplicationContext());
                strategy.setAppChannel("test");  //设置渠道
                strategy.setAppVersion("1.0.0");      //App的版本
                strategy.setAppPackageName("name");  //App的包名
                strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
                    public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                                  String errorMessage, String errorStack) {
                        StringBuffer sb = new StringBuffer();
                        switch (crashType) {
                            case 0:
                                sb.append("CRASHTYPE_JAVA_CRASH" + "\n");
                                break;
                            case 1:
                                sb.append("CRASHTYPE_JAVA_CATCH" + "\n");
                                break;
                            case 2:
                                sb.append("CRASHTYPE_NATIVE" + "\n");
                                break;
                            case 3:
                                sb.append("CRASHTYPE_U3D" + "\n");
                                break;
                            case 4:
                                sb.append("CRASHTYPE_ANR" + "\n");
                                break;
                            case 5:
                                sb.append("CRASHTYPE_COCOS2DX_JS" + "\n");
                                break;
                            case 6:
                                sb.append("CRASHTYPE_COCOS2DX_LUA" + "\n");
                                break;
                        }
                        sb.append(errorType + "\n");
                        sb.append(errorMessage + "\n");
                        sb.append(errorStack + "\n");
                        CrashHandler.getInstance().handleException(sb.toString());
                        return null;
                    }

                    @Override
                    public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                                   String errorMessage, String errorStack) {
                        //e.g.
             /*   try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
                */
                        return null;
                    }

                });
                CrashReport.initCrashReport(app.getApplicationContext(), "972b5e3622", false, strategy);

                //添加每分钟监听
                IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
                ACBroadcastReceiver receiver = new ACBroadcastReceiver();
                app.getApplicationContext().registerReceiver(receiver, filter);
                //初始化下载类
                FileDownloadLog.NEED_LOG = false;
                /**
                 * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
                 * by below code, so please do not worry about performance.
                 * @see FileDownloader#init(Context)
                 */
                FileDownloader.setupOnApplicationOnCreate(app)
                        .connectionCreator(new FileDownloadUrlConnection
                                .Creator(new FileDownloadUrlConnection.Configuration()
                                .connectTimeout(15_000) // set connection timeout.
                                .readTimeout(15_000) // set read timeout.
                                .proxy(Proxy.NO_PROXY) // set proxy
                        ))
                        .commit();
            }
        }).start();

    }
}
