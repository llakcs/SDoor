package com.dchip.door.smartdoorsdk.opencv;

/**
 * Created by jelly on 2018/2/1.
 */
public interface TakePhotoListener {
    /**
     * 拍照返回
     */
    void onTaken(String path);
}
