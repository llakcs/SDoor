package com.dchip.door.smartdoorsdk.utils;


import com.dchip.door.smartdoorsdk.deviceControl.Listener.onTickListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jelly on 2017/8/15.
 * 计时器 初始化后定点报时
 */

public class DeviceTimer {
    onTickListener listener;
    public DeviceTimer(final onTickListener listener){
        this.listener = listener;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int hourCounter = 0;
            int dateCounter = 0;
            int weekCounter = 0;
            public void run() {
                listener.onOneMinute();
                hourCounter ++ ;

                if (hourCounter>60){
                    hourCounter = 0;
                    listener.onOneHouer();
                    dateCounter++;
                }

                if (dateCounter>24){
                    dateCounter = 0;
                    listener.onOneDay();
                    weekCounter++;
                }

                if (weekCounter>7){
                    weekCounter = 0;
                    listener.onOneWeek();
                }
            }
        },0, 60 * 1000);
    }
}
