package com.dchip.door.smartdoorsdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.widget.Toast;

import java.util.List;

/**
 * Created by llakcs on 2017/7/20.
 */

public class NetworkStats {


    private static NetworkStats nwStats = new NetworkStats();
    public static NetworkStats getIns(){
        return nwStats;
    }


    /**
     * 获取从此次开机起总接受流量
     * @return
     */
    public long getTotalRxBytes(){

        return TrafficStats.getTotalRxBytes();
    }

    /**
     * 获取从此次开机起总发送流量
     * @return
     */
    public long getTotalTxBytes(){
        return TrafficStats.getTotalTxBytes();
    }

    /**
     * 获取从此次开机起不包括Wifi的接受流量，即只统计数据网Gprs接受的流量；
     * @return
     */
    public long getMobileRxBytes(){
        return TrafficStats.getMobileRxBytes();
    }

    /**
     * 获取从此次开机起不包括Wifi的发送流量，即只统计数据网Gprs发送的流量；
     * @return
     */
    public long getMobileTxBytes(){

        return TrafficStats.getMobileTxBytes();
    }

    /**
     * 获取从此次开机手机Wifi的接受流量
     * @return
     */
    public long getWifiRxBytes(){

       return getTotalRxBytes() - getMobileRxBytes();
    }


    /**
     * 获取从此次开机手机Wifi的发送流量
     * @return
     */
    public long getWifiTxBytes(){

        return getTotalTxBytes() - getMobileTxBytes();
    }


    /**
     * 统计当前APP消耗流量
     * @param context
     */
    public void getAppTrafficList(Context context){
        //获取所有的安装在手机上的应用软件的信息，并且获取这些软件里面的权限信息
        PackageManager pm=context.getPackageManager();//获取系统应用包管理
        //获取每个包内的androidmanifest.xml信息，它的权限等等
        List<PackageInfo> pinfos=pm.getInstalledPackages
                (PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        //遍历每个应用包信息
        for(PackageInfo info:pinfos){
            //请求每个程序包对应的androidManifest.xml里面的权限
            String[] premissions=info.requestedPermissions;
            if(premissions!=null && premissions.length>0){
                //找出需要网络服务的应用程序
                for(String premission : premissions){
                    if("android.permission.INTERNET".equals(premission)){
                        //获取每个应用程序在操作系统内的进程id
                        int uId=info.applicationInfo.uid;
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long rx= TrafficStats.getUidRxBytes(uId);
                        //如果返回-1，代表不支持使用该方法，注意必须是2.2以上的
                        long tx=TrafficStats.getUidTxBytes(uId);
                        if(rx<0 || tx<0){
                            continue;
                        }else{
                            Toast.makeText(context, info.applicationInfo.loadLabel(pm)+"消耗的流量--"+Formatter.formatFileSize(context, rx+tx)
                                   , Toast.LENGTH_SHORT);
                        }
                    }
                }
            }
        }
    }


}
