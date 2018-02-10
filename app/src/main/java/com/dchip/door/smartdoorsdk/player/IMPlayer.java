/*
 *
 * IMPlayer.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.dchip.door.smartdoorsdk.player;

import android.view.SurfaceView;

import java.util.List;

/**
 * Description:
 */
public interface IMPlayer {

//    /**
//     * 设置资源
//     * @param url 资源路径
//     * @throws MPlayerException
//     */
//    void setSource(String url) throws MPlayerException;

//    /**
//     * 设置显示视频的载体
//     * @param display 视频播放的载体及相关界面
//     */
//    void setDisplay(IMDisplay display);

    /**
     * 设置
     *
     * @param Urls 播放的视频地址列表
     * @param view 播放空间
     */
    void setUp(List<String> Urls, SurfaceView view);

    /**
     * Sets up.
     *
     * @param Url  the url
     * @param view the view
     */
    void setUp(String Url, SurfaceView view);

    /**
     * 更新播放列表
     *
     * @param Urls the urls
     */
    void updateUrl(List<String> Urls);

    /**
     * 播放视频
     *
     * @throws MPlayerException the m player exception
     */
    void play() throws MPlayerException;

    /**
     * 暂停视频
     */
    void pause();

    /**
     * Sets play listener.
     *
     * @param listener the listener
     */
    void setPlayListener(IMPlayListener listener);

    /**
     * On pause.
     */
    void onPause();

    /**
     * On resume.
     */
    void onResume();

    /**
     * On destroy.
     */
    void onDestroy();

    /**
     * 关闭播放声音
     */
    public void CloseVolume();

    /**
     * 打开播放声音
     */
    public void OpenVolume();

}
