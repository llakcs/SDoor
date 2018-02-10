package com.dchip.door.smartdoorsdk.location;

import android.app.Activity;
import android.content.Context;

/**
 * Created by llakcs on 2017/12/1.
 */
public interface LocationManager {
    /**
     * Start location.
     *
     * @param activity the activity
     */
    void startLocation(Activity activity);

    /**
     * On stop.
     */
    void onStop();

    /**
     * Sets location recv listner.
     *
     * @param recvListner the recv listner
     */
    void setLocationRecvListner(BDlocationRecvListner recvListner);
}
