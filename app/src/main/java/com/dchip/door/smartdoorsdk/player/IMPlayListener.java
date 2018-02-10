/*
 *
 * IMPlayListener.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.dchip.door.smartdoorsdk.player;

/**
 * Description:
 */
public interface IMPlayListener {

    /**
     * On start.
     *
     * @param player the player
     */
    void onStart(IMPlayer player);

    /**
     * On pause.
     *
     * @param player the player
     */
    void onPause(IMPlayer player);

    /**
     * On resume.
     *
     * @param player the player
     */
    void onResume(IMPlayer player);

    /**
     * On complete.
     *
     * @param player the player
     */
    void onComplete(IMPlayer player);

    /**
     * On prepared.
     */
    void onPrepared();

    /**
     * On error.
     */
    void onError();

    /**
     * On click.
     */
    void onClick();
}
