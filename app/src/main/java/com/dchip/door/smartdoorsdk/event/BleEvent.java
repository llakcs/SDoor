package com.dchip.door.smartdoorsdk.event;

/**
 * Created by jelly on 2017/8/17.
 */

public class BleEvent {
    String name;
    int rssi;
    String address;

    public BleEvent(String name, int rssi, String address){
        this.address = address;
        this.name = name;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        address = address;
    }
}
