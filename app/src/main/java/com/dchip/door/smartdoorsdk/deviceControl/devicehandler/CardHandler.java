package com.dchip.door.smartdoorsdk.deviceControl.devicehandler;

import android.util.Log;

import com.dchip.door.smartdoorsdk.deviceControl.nativeLev.Pn512Card;
import com.dchip.door.smartdoorsdk.event.ReadCardEven;
import com.dchip.door.smartdoorsdk.s;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by jelly on 2017/11/11.
 */

public class CardHandler {
    private static final String TAG = "CardHandler";
    private static CardHandler instance;
    private static Pn512Card mcard;
    private boolean stop = false;

    public static CardHandler getInstance() {
        if (instance == null) {
            instance = new CardHandler();
        }
        return instance;
    }

    public CardHandler() {
        mcard = new Pn512Card();
        new Thread(runnable).start();
    }

    public void finish() {
        stop = true;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "mcard.open()=" + mcard.open());
            while (!stop) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mcard.cardDetect()) {
                    // 验证卡
                    if (mcard.cardChecked()) {
                        String id = mcard.operation("FFCA000000");
                        LogUtil.d(TAG,"读卡成功：" + id);
                        EventBus.getDefault().post(new ReadCardEven(id.replace("9000","").replace(" ","")));
                    }
//                    Log.w(TAG,"验证B密码:"+mcard.operation("FF82000006CDCDCDCDCDCD"));
//                    Log.w(TAG,"验证:"+mcard.operation("FF8800076000"));

                }
            }
            LogUtil.d(TAG,"读卡设备关闭 stop:"+stop);
            mcard.close();
        }
    };
}
