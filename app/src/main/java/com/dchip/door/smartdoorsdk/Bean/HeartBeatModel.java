package com.dchip.door.smartdoorsdk.Bean;

/**
 * Created by jelly on 2017/9/4.
 * 用于上传纯净的心跳包
 */

public class HeartBeatModel {
    long time;
    int type;

    public HeartBeatModel(long time) {
        this.time = time;
        type = 99;
    }

}
