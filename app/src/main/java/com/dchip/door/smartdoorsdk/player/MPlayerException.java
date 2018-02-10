/*
 *
 * MPlayerException.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.dchip.door.smartdoorsdk.player;

/**
 * Description:
 */
public class MPlayerException extends Exception {

    public MPlayerException(String detailMessage) {
        super(detailMessage);
    }

    public MPlayerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MPlayerException(Throwable throwable) {
        super(throwable);
    }
}
