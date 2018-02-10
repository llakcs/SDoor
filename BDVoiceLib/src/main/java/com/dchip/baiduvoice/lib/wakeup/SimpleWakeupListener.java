package com.dchip.baiduvoice.lib.wakeup;


import android.util.Log;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {

    private static final String TAG = "SimpleWakeupListener";
    @Override
    public void onSuccess(String word, WakeUpResult result) {
        Log.e(TAG, "唤醒成功，唤醒词：" + word);
    }

    @Override
    public void onStop() {
        Log.e(TAG, "唤醒词识别结束：");
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
        Log.e(TAG,"唤醒错误："+ errorCode +";错误消息："+errorMessge +"; 原始返回"+ result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        Log.e(TAG,"audio data： "+data.length);
    }

}
