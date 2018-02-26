package com.dchip.door.smartdoorsdk.event;

/**
 * Created by jelly on 2017/12/12.
 */

public class OpenLockRecallEvent {
    int openWay = -1;

    public OpenLockRecallEvent(int way){
        openWay = way;
    }

    public int getOpenWay() {
        return openWay;
    }

}
