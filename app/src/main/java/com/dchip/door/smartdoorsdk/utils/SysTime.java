package com.dchip.door.smartdoorsdk.utils;

import android.text.format.Time;

/**
 * Created by llakcs on 2017/12/1.
 */

public class SysTime {
    private SysTime(){

    }
    public static Time getTime(){
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
//        String time=t.year+"年 "+(t.month+1)+"月 "+t.monthDay+"日 "+t.hour+"h "+t.minute+"m "+t.second;
//        Log.e("msg", time);
        return t;
    }
}
