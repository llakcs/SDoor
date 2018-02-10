package com.dchip.door.smartdoorsdk.Bean;

/**
 * Created by zhangdeming on 2017/6/30.
 */

public class DoorOperationModel {
    private String uid;
    private boolean status;
    private String msg;
    private int type;

    public DoorOperationModel(String uid, boolean status, int type) {
        this.uid = uid;
        this.status = status;
        this.type = type;
    }
}
