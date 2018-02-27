package com.dchip.door.smartdoorsdk.Bean;

/**
 * Created by jelly on 2017/11/23.
 */

public class ApiGetDeviceConfigModel {
    //1-单锁 2-双锁
    int lock_num= 1;
    //1-常闭接法  2-常开接法
    int door_access =  1;
    //1-常闭接法  2-常开接法
    int orignal_lock_access = 1;
    //1-常闭接法  2-常开接法
    int lock_access = 1 ;
    //主控板开放的功能 1-业主开门  2-开门拍照  3-人脸识别  4-智能语音 5-开门语音 6-IC卡开门
    String function = "";
    //锁类型  1-电插锁 2-电磁锁  3-电机锁
    int lock_type = 1;
    //门口机本身环信账号
    String easeAccount;
    int id = 25;

    public int getLock_num() {
        return lock_num;
    }

    public void setLock_num(int lock_num) {
        this.lock_num = lock_num;
    }

    public int getDoor_access() {
        return door_access;
    }

    public void setDoor_access(int door_access) {
        this.door_access = door_access;
    }

    public int getOrignal_lock_access() {
        return orignal_lock_access;
    }

    public void setOrignal_lock_access(int orignal_lock_access) {
        this.orignal_lock_access = orignal_lock_access;
    }

    public int getLock_access() {
        return lock_access;
    }

    public void setLock_access(int lock_access) {
        this.lock_access = lock_access;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getLock_type() {
        return lock_type;
    }

    public void setLock_type(int lock_type) {
        this.lock_type = lock_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEaseAccount() {
        return easeAccount;
    }

    public void setEaseAccount(String easeAccount) {
        this.easeAccount = easeAccount;
    }
}
