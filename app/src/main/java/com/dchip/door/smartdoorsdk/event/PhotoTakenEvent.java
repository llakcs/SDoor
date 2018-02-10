package com.dchip.door.smartdoorsdk.event;

/**
 * Created by jelly on 2018/2/2.
 */

public class PhotoTakenEvent {
    String path = "";

    public String getPath() {
        return path;
    }

    public PhotoTakenEvent setPath(String path) {
        this.path = path;
        return this;
    }
}
