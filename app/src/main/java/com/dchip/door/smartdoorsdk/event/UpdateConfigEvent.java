package com.dchip.door.smartdoorsdk.event;

/**
 * Created by llakcs on 2018/2/22.
 */

public class UpdateConfigEvent {
    private int code;

    public UpdateConfigEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
