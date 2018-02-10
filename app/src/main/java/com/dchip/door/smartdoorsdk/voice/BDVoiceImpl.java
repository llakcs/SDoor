package com.dchip.door.smartdoorsdk.voice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.speech.asr.SpeechConstant;
import com.dchip.baiduvoice.lib.control.MyRecognizer;
import com.dchip.baiduvoice.lib.control.MyWakeup;
import com.dchip.baiduvoice.lib.offline.OfflineRecogParams;
import com.dchip.baiduvoice.lib.recognization.CommonRecogParams;
import com.dchip.baiduvoice.lib.recognization.MessageStatusRecogListener;
import com.dchip.baiduvoice.lib.recognization.PidBuilder;
import com.dchip.baiduvoice.lib.recognization.StatusRecogListener;
import com.dchip.baiduvoice.lib.wakeup.IWakeupListener;
import com.dchip.baiduvoice.lib.wakeup.RecogWakeupListener;
import com.dchip.baiduvoice.lib.wakeup.WakeupParams;
import com.dchip.door.smartdoorsdk.s;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by llakcs on 2017/11/30.
 */

public class BDVoiceImpl implements BDVoiceManager {


    private BDVoiceImpl() {
    }

    private static final Object lock = new Object();
    private static volatile BDVoiceImpl instance;
    //定义AudioManager
    private AudioManager mgr;

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new BDVoiceImpl();
                }
            }
        }
        s.Ext.setBDVoiceManager(instance);
    }

    private String TAG = "VideoImpl";
    private Application app;
    protected MyWakeup myWakeup;
    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;
    private Handler mHandler;
    private boolean enableOffline;
    private Activity activity;
    /*
 * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
 */
    protected CommonRecogParams apiParams;

    /**
     * 0: 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
     * >0 : 方案2： 唤醒词说完后，中间有停顿，然后接句子。推荐4个字 1500ms
     * <p>
     * backTrackInMs 最大 15000，即15s
     */
    private int backTrackInMs = 0;

    @Override
    public void start() {
        WakeupParams wakeupParams = new WakeupParams(app.getApplicationContext());
        Map<String, Object> params = wakeupParams.fetch();
        myWakeup.start(params);
    }

    @Override
    public void init(Application app, Activity activity, Handler handler) {
        this.app = app;
        this.mHandler = handler;
        this.activity = activity;
        mgr = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void stop() {
        myWakeup.stop();
        myRecognizer.stop();
    }

    @Override
    public void initRecog() {
        //  初始化语音识别引擎
        StatusRecogListener recogListener = new MessageStatusRecogListener(mHandler);
        myRecognizer = new MyRecognizer(app, recogListener);
        apiParams = new OfflineRecogParams(activity);
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
        IWakeupListener listener = new RecogWakeupListener(mHandler);
        myWakeup = new MyWakeup(app, listener);
    }

    @Override
    public void onDestroy() {
        if (myRecognizer != null && myWakeup != null) {
            myRecognizer.release();
            myWakeup.release();
        }
    }


    @Override
    public void recogn() {
        // 此处 开始正常识别流程
        if (!enableOffline) {
            Log.e(TAG, "####enableOnline");
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
            params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
            int pid = PidBuilder.create().model(PidBuilder.INPUT).toPId(); //如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
            params.put(SpeechConstant.PID, pid);
            if (backTrackInMs > 0) { // 方案1， 唤醒词说完后，直接接句子，中间没有停顿。
                params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);

            }
            myRecognizer.cancel();
            myRecognizer.start(params);
        } else {
            Log.e(TAG, "####enableOffline");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            Map<String, Object> params = apiParams.fetch(sp);
            myRecognizer.start(params);
        }
    }

    @Override
    public void enableOffline(boolean enable) {
        this.enableOffline = enable;
    }

    @Override
    public void speak(String text) {
        TTSHandler.getInstance().speak(text);
    }

    @Override
    public void MediaVolumeUp() {
        // 调高音量
        if (mgr != null)
            mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public void MediaVolumeDown() {
        // 调低音量
        if (mgr != null)
            mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public int getSystemMaxVolume() {
        return mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //获取系统最大音量
    }

    @Override
    public int  getStreamVolume() {
        return  mgr.getStreamVolume(AudioManager.STREAM_MUSIC);  //获取当前值
    }

    @Override
    public void setStreamVolume(int progess) {
        mgr.setStreamVolume(AudioManager.STREAM_MUSIC, progess, 0);
    }
}
