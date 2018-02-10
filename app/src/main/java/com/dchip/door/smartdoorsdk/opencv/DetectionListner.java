package com.dchip.door.smartdoorsdk.opencv;

/**
 * Created by llakcs on 2017/11/30.
 */
public interface DetectionListner {

    /**
     * 人面识别成功时回调
     *
     * @param path 返回最后识别的图片地址
     */
    void complete(String path);
}
