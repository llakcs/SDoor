package com.dchip.door.smartdoorsdk.event;

/**
 * @author zhangdeming
 * @date 创建时间 2017/5/19
 * @description 描述类的功能
 */

public class FaultEvent {
    private int type;
    private String uid;

    public FaultEvent(int type, String uid) {
        this.type = type;
        this.uid = uid;
    }
    public FaultEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }
}
