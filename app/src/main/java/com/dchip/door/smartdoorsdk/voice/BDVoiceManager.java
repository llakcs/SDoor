package com.dchip.door.smartdoorsdk.voice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;


/**
 * Created by llakcs on 2017/11/30.
 */
public interface BDVoiceManager {

    /**
     * Start.
     */
    void start();

    /**
     * Stop.
     */
    void stop();

    /**
     * Init recog.
     */
    void initRecog();

    /**
     * On destroy.
     */
    void onDestroy();

    /**
     * Enable offline.
     *
     * @param enable the enable
     */
    void enableOffline(boolean enable);

    /**
     * Speak.
     *
     * @param text the text
     */
    void speak(String text);

    /**
     * Init.
     *
     * @param app      the app
     * @param activity the activity
     * @param handler  the handler
     */
    void init(Application app, Activity activity, Handler handler);

    /**
     * Recogn.
     */
    void recogn();

    /**
     * Media volume up.
     */
    void MediaVolumeUp();

    /**
     * Media volume down.
     */
    void MediaVolumeDown();

    /**
     * Gets system max volume.
     *
     * @return the system max volume
     */
    int getSystemMaxVolume();

    /**
     * Gets stream volume.
     *
     * @return the stream volume
     */
    int getStreamVolume();

    /**
     * Sets stream volume.
     *
     * @param progess the progess
     */
    void setStreamVolume(int progess);
}
