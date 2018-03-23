package com.dchip.door.smartdoorsdk;

import android.app.Application;
import android.content.Context;
import com.dchip.door.smartdoorsdk.deviceControl.DeviceImpl;
import com.dchip.door.smartdoorsdk.deviceControl.DeviceManager;
import com.dchip.door.smartdoorsdk.location.LocationManager;
import com.dchip.door.smartdoorsdk.opencv.OpencvImpl;
import com.dchip.door.smartdoorsdk.opencv.OpencvManager;
import com.dchip.door.smartdoorsdk.player.IMPlayer;
import com.dchip.door.smartdoorsdk.player.MPlayer;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.dchip.door.smartdoorsdk.voice.BDVoiceImpl;
import com.dchip.door.smartdoorsdk.voice.BDVoiceManager;
import java.lang.reflect.Method;


/**
 * Created by llakcs on 2017/11/29.
 */
public class s {


    private s(){
    }


    /**
     * 返回当前app的Application
     *
     * @return the application
     */
    public static Application app() {
        if (s.Ext.app == null) {
            try {
                // 在IDE进行布局预览时使用
                Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                Context context = (Context) method.invoke(null);
                s.Ext.app = new s.MockApplication(context);
            } catch (Throwable ignored) {
                throw new RuntimeException("please invoke x.Ext.init(app) on Application#onCreate()"
                        + " and register your Application in manifest.");
            }
        }
        return s.Ext.app;
    }
    private static class MockApplication extends Application {
        public MockApplication(Context baseContext) {
            this.attachBaseContext(baseContext);
        }
    }

    /**
     * 返回 人面识别管理器
     *
     * @return the opencv manager
     */
    public static OpencvManager opencv(){
        if(Ext.opencvManager == null){
            OpencvImpl.registerInstance();
        }
        return Ext.opencvManager;
    }

    /**
     * 返回 声音管理器
     *
     * @return the bd voice manager
     */
    public static BDVoiceManager voice(){
        if(Ext.bdVoiceManager == null){
            BDVoiceImpl.registerInstance();
        }
        return Ext.bdVoiceManager;
    }

    /**
     *返回 定位管理器
     *
     * @return the location manager
     */
//    public static LocationManager location(){
//        if(Ext.locationManager == null){
//            locationImpl.registerInstance();
//        }
//        return Ext.locationManager;
//    }


    /**
     * 返回 设备管理器
     *
     * @return the device manager
     */
    public static DeviceManager device(){
        if(Ext.deviceManager == null){
            DeviceImpl.registerInstance();
        }
        return Ext.deviceManager;
    }

    /**
     * 返回播放器
     *
     * @return the im player
     */
    public static IMPlayer player(){
        if(Ext.imPlayer == null){
            MPlayer.registerInstance();
        }
        return Ext.imPlayer;
    }


    /**
     * 扩展类
     */
        public static class Ext{

        private static Application app;
        private static OpencvManager opencvManager;
        private static BDVoiceManager bdVoiceManager;
//        private static LocationManager locationManager;
        private static DeviceManager deviceManager;
        private static IMPlayer imPlayer;
        /**
         *标识是否为调试模式
         */
        public static boolean debug;
        private Ext(){
        }

        /**
         * DEBUG模式下打印日志
         *
         * @param debug the debug
         */
        public static void setDebug(boolean debug) {
            Ext.debug = debug;
            if(debug){
                LogUtil.setLevel(1);
            }else{
                LogUtil.setLevel(5);
            }

        }


        /**
         * 初始化入口
         *
         * @param app       APPLICATION
         * @param wsUrl     websocket地址
         * @param serverUrl http服务器地址
         */
        public static void init(Application app,String wsUrl,String serverUrl){
            if (Ext.app == null) {
                Ext.app = app;
            }
            SdkInit.onCreate(Ext.app,wsUrl,serverUrl);
        }

        /**
         * 设置人面识别管理器
         *
         * @param opencvManager the opencv manager
         */
        public static void setOpencvManager(OpencvManager opencvManager){
            Ext.opencvManager = opencvManager;
        }


        /**
         *  设置声音管理器
         *
         * @param bdVoiceManager the bd voice manager
         */
        public static void setBDVoiceManager(BDVoiceManager bdVoiceManager){
            Ext.bdVoiceManager = bdVoiceManager;
        }

        /**
         * 设置地址管理器
         *
         * @param locationManager the location manager
         */
//        public static void setLocationManager(LocationManager locationManager){
//            Ext.locationManager = locationManager;
//        }

        /**
         * 设置设备管理器
         *
         * @param deviceManager the device manager
         */
        public static void setDeviceManager(DeviceManager deviceManager){
            Ext.deviceManager = deviceManager;
        }

        /**
         * 设置播放器
         *
         * @param player the player
         */
        public static void setImPlayerManager(IMPlayer player){
            Ext.imPlayer = player;
        }


    }



}
