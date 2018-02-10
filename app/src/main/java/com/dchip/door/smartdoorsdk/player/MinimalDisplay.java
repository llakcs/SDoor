/*
 *
 * MinimalDisplay.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.dchip.door.smartdoorsdk.player;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Description:
 */
public class MinimalDisplay implements IMDisplay {

    private SurfaceView mSurfaceView;

    public MinimalDisplay(SurfaceView surfaceView){
        this.mSurfaceView=surfaceView;
    }

    @Override
    public View getDisplayView() {
        return mSurfaceView;
    }

    @Override
    public SurfaceHolder getHolder() {
        return mSurfaceView.getHolder();
    }



    @Override
    public void onStart(IMPlayer player) {

    }

    @Override
    public void onPause(IMPlayer player) {

    }

    @Override
    public void onResume(IMPlayer player) {

    }

    @Override
    public void onComplete(IMPlayer player) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onClick() {

    }
}
