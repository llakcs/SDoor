package com.dchip.door.smartdoorsdk.event;

/**
 * Created by jelly on 2017/8/31.
 */

public class BroadcastEvent {
    String action ;
    String extraString;
    public BroadcastEvent(String action, String extraString){
        this.action = action;
        this.extraString = extraString;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getExtraString() {
        return extraString;
    }

    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }
}
