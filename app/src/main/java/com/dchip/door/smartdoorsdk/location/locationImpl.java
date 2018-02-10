package com.dchip.door.smartdoorsdk.location;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.dchip.door.smartdoorsdk.s;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.dchip.door.smartdoorsdk.utils.SysTime;

import static com.dchip.door.smartdoorsdk.SdkInit.locationService;

/**
 * Created by llakcs on 2017/12/1.
 */

public class locationImpl implements LocationManager {


    private locationImpl(){

    }

    private static final Object lock = new Object();
    private static volatile locationImpl instance;
    private String TAG="locationImpl";
    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new locationImpl();
                }
            }
        }
        s.Ext.setLocationManager(instance);
    }
    private BDlocationRecvListner mRecvListner;
    @Override
    public void startLocation(Activity activity) {
        if(SysTime.getTime().year >2016 && (SysTime.getTime().month+1) >5){
            // -----------location config ------------

            if(locationService != null){
                //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                locationService.registerListener(mListener);
                //注册监听
                int type =activity.getIntent().getIntExtra("from", 0);
                if (type == 0) {
                    locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                } else if (type == 1) {
                    locationService.setLocationOption(locationService.getOption());
                }
                locationService.start();// 定位SDK
                // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
            }else{
                LogUtil.e(TAG,"###locationImpl == null");
            }

        }
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
               mRecvListner.recv(location);
            }
        }

    };

    @Override
    public void onStop() {
        if(locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }

    }

    @Override
    public void setLocationRecvListner(BDlocationRecvListner recvListner) {
        this.mRecvListner = recvListner;
    }
}
