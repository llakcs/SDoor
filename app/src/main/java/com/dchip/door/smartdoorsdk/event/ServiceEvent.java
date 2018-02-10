package com.dchip.door.smartdoorsdk.event;

import java.util.ArrayList;

/**
 * @author zhangdeming
 * @date 创建时间 2017/5/10
 * @description 描述类的功能
 */

public class ServiceEvent {

    public static final int CONNECTED = 0;
    public static final int DISCONNECTED = -1;
    public static final int HEART_BEAT = 1;
    public static final int TIMEOUT = 2;
    public static final int UPDATE_APK = 3;
    public static final int UPDATE_CARD_LIST = 4;
    private boolean updateOwener = false;//是否需要更新本地业主列表

    private boolean connected;
    private boolean writeCardSuccess;
    private int type;
    private int updateType = 2;//立即更新 延时更新
    private boolean updateCards = false;//是否需要更新本地卡
    private ArrayList<String> list;

    public ServiceEvent(boolean connected, int type) {
        this.connected = connected;
        this.type = type;
    }

    public ServiceEvent(int updateType) {
        this.connected = true;
        this.type = UPDATE_APK;
        this.updateType = updateType;
    }

    public ServiceEvent(int type,boolean updateCards,boolean updateOwener) {
        this.connected = true;
        this.type = type;
        this.updateCards = updateCards;
        this.updateOwener = updateOwener;
    }

    public boolean isUpdateOwener() {
        return updateOwener;
    }

    public void setUpdateOwener(boolean updateOwener) {
        this.updateOwener = updateOwener;
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = (ArrayList<String>)list.clone();
    }

    public boolean isConnected() {
        return connected;
    }
    public int getType() { return type; }

    public boolean isWriteCardSuccess() {
        return writeCardSuccess;
    }

    public void setWriteCardSuccess(boolean writeCardSuccess) {
        this.writeCardSuccess = writeCardSuccess;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public boolean isUpdateCards() {
        return updateCards;
    }

    public void setUpdateCards(boolean updateCards) {
        this.updateCards = updateCards;
    }
}
