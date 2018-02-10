package com.dchip.baiduvoice.lib.online;

import android.app.Activity;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class InFileStream {

    private static Activity context;

    private static final String TAG = "InFileStream";
    public static void setContext(Activity context){
        InFileStream.context = context;
    }

    public static InputStream create16kStream(){
        InputStream is = null;
        Log.d(TAG,"cmethod call");
        try {
            is = context.getAssets().open("outfile.pcm");
            Log.d(TAG,"create input stream ok" + is.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is;
    }
}