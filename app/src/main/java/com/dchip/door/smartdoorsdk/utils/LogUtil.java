package com.dchip.door.smartdoorsdk.utils;

import android.util.Log;

import com.dchip.door.smartdoorsdk.s;

/**
 * Created by jelly on 2017/9/20.
 */

public class LogUtil {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    public static final int NONE = 5;

    private static int level = DEBUG;

    public static void setLevel(int level) {
        LogUtil.level = level;
    }

    public static void v(String tag, String msg){
        if(msg==null) {
            Log.e(tag,"v msg == null");
            return;
        }
        if(level<=VERBOSE)
            Log.v(tag,msg);
    }
    public static void d(String tag,String msg){
        if(msg==null) {
            Log.e(tag,"d msg == null");
            return;
        }
        if(level<=DEBUG)
            Log.d(tag,msg);
    }
    public static void i(String tag,String msg){
        if(msg==null)  {
            Log.e(tag,"i msg == null");
            return;
        }
        if(level<=INFO)
            Log.i(tag,msg);
    }
    public static void w(String tag,String msg){
        if(msg==null)  {
            Log.e(tag,"w msg == null");
            return;
        }
        if(level<=WARN)
            Log.w(tag,msg);
    }
    public static void e(String tag,String msg){
        if(msg==null)  {
            Log.e(tag,"e msg == null");
            return;
        }
        if(level<=ERROR)
            Log.e(tag,msg);
    }
    public static void e(String tag,String msg,Exception e){
        if(msg==null) {
            Log.e(tag,"e msg == null");
            return;
        }
        if(level<=ERROR)
            Log.e(tag,msg,e);
    }

}
