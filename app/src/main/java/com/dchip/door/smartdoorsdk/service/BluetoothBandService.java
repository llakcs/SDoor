package com.dchip.door.smartdoorsdk.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dchip.door.smartdoorsdk.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;

public class BluetoothBandService extends Service {

    private static final int MIN_RSSI = -90;
    private String mac1 = "F0:E9:C2:72:C9:2F";
    private String mac2 = "C8:0F:10:9F:67:99";
    private String mac3 = "C8:0F:10:9F:71:94";
    private String mac4 = "C8:0F:10:9F:47:BC";
    private String mac5 = "88:0F:10:31:0A:D8";
    private List<String> maclist = new ArrayList<>();

    private String tag = "BluetoothBandService";

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private boolean isExit = false;
    private long restartB4  = 0;
    private long scanTime  = 0;


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(tag, "#######onCreate");
        addmac();
        bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.startLeScan(mLeScanCallback);
            restartB4 = System.currentTimeMillis();
        }else{
            bluetoothAdapter.enable();
//            EventBus.getDefault().post(new InfoEvent("蓝牙设备未打开，稍后重试。"));
            finish();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isExit){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (System.currentTimeMillis()-scanTime>2000){
                        if(!bluetoothAdapter.isEnabled()){
//                            EventBus.getDefault().post(new InfoEvent("蓝牙设备未打开，稍后重试。"));
                            finish();
                        }
//                        EventBus.getDefault().post(new InfoEvent("蓝牙扫描超时，重启扫描功能。"));
                        restartScan();
                    }
                    if (System.currentTimeMillis()-restartB4>(1000*60)){
                        if(!bluetoothAdapter.isEnabled()){
//                            EventBus.getDefault().post(new InfoEvent("蓝牙设备未打开，稍后重试。"));
                            finish();
                        }
//                        EventBus.getDefault().post(new InfoEvent("常规重启扫描功能。"));
                        restartScan();
                    }
                }
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void finish(){
        stopSelf();
    }

    @Override
    public void onDestroy() {
        LogUtil.e(tag, "#######onDestroy");
        isExit = true;
        super.onDestroy();
    }


//    private boolean isSupportBle() {
//        return bleManager.isSupportBle();
//    }

    private void addmac() {
        maclist.add(mac1);
        maclist.add(mac2);
        maclist.add(mac3);
        maclist.add(mac4);
        maclist.add(mac5);
    }

    private void restartScan(){
        bluetoothAdapter.stopLeScan(mLeScanCallback);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bluetoothAdapter.startLeScan(mLeScanCallback);
        restartB4 = System.currentTimeMillis();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            scanTime = System.currentTimeMillis();
            for (String mac : maclist) {
                if (device.getAddress().equals(mac)) {
//                    EventBus.getDefault().post(new InfoEvent("蓝牙搜索结果：" + device.getName() + "  " + rssi + "  " + device.getAddress()));
                    if (rssi > MIN_RSSI){
//                        EventBus.getDefault().post(new BleEvent(device.getName(), rssi, device.getAddress()));
                }}
            }
        }
    };

    /**
     * 扫描出周围所有设备
     */
//    private void scanDevice() {
//        bleManager.scanDevice(new ListScanCallback(TIME_OUT) {
//            @Override
//            public void onScanning(ScanResult result) {
//                Log.e(tag,"#######onScanning");
//                if(result != null && result.getDevice() != null && result.getDevice().getName() != null &&!result.getDevice().getName().equals("")){
//                    if(result.getDevice().getName().contains(devName)){
//                        for(String mac:maclist){
//                            if(result.getDevice().getAddress().equals(mac)){
////                            if(result.getRssi() > -90){
//                                //停止扫描
//                                EventBus.getDefault().post(new InfoEvent(result.getDevice().getName() +"  "+ result.getRssi() +"  "+ result.getDevice().getAddress()));
//                                EventBus.getDefault().post(new BleEvent(result.getDevice().getName(),result.getRssi() , result.getDevice().getAddress()));
//                                bleManager.cancelScan();
//
////                            }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScanComplete(ScanResult[] results) {
//                isScan = true;
//            }
//        });
//    }

}
