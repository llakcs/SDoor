package com.dchip.door.smartdoorsdk.Bean;

import java.util.List;

/**
 * @author zhangdeming
 * @date 创建时间 2017/5/12
 * @description 后台发送的操作类
 */
public class OperationModel {
    private int type;
    private String uid;
    private long time;
    private boolean offline;
    private int updateType;
    private boolean unTerminal;
    private boolean ownerInfoUnTerminal;
    private int openWay = -1;

    public boolean isOwnerInfoUnTerminal() {
        return ownerInfoUnTerminal;
    }

    public void setOwnerInfoUnTerminal(boolean ownerInfoUnTerminal) {
        this.ownerInfoUnTerminal = ownerInfoUnTerminal;
    }

    private List<CardsModel> userCardList;

    public List<CardsModel> getUserCardList() {
        return userCardList;
    }

    public void setUserCardList(List<CardsModel> userCardList) {
        this.userCardList = userCardList;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public OperationModel(long time) {
        this.time = time;
        type = 99;
        setOffline(false);
    }

    public boolean isUnTerminal() {
        return unTerminal;
    }

    public void setUnTerminal(boolean unTerminal) {
        this.unTerminal = unTerminal;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public int getOpenWay() {
        return openWay;
    }

    public void setOpenWay(int openWay) {
        this.openWay = openWay;
    }
}
