package com.dchip.door.smartdoorsdk.location;

import com.baidu.location.BDLocation;

/**
 * Created by llakcs on 2017/12/1.
 */
public interface BDlocationRecvListner {
    /**
     * 接收到地址定位时回调
     *
     * @param location the location
     */
    void recv(BDLocation location);
}
