package com.dchip.door.smartdoorsdk.event;

/**
 * @author zhangdeming
 * @date 创建时间 2017/6/2
 * @description 描述类的功能
 */
public class OpenLockStatusEvent {
    private String uid;
    private boolean status;
    private int type;

    public OpenLockStatusEvent(String uid, boolean status) {
        this.uid = uid;
        this.status = status;
        type = 98;
    }

    public OpenLockStatusEvent(String uid, boolean status, int type) {
        this.uid = uid;
        this.status = status;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
