package com.dchip.door.smartdoorsdk.event;

/**
 * Created by llakcs on 2018/2/22.
 */

public class LogEvent {

    private String logstr;
    private String logtag;

    public LogEvent(String logstr, String logtag) {
        this.logstr = logstr;
        this.logtag = logtag;
    }

    public String getLogstr() {
        return logstr;
    }

    public void setLogstr(String logstr) {
        this.logstr = logstr;
    }

    public String getLogtag() {
        return logtag;
    }

    public void setLogtag(String logtag) {
        this.logtag = logtag;
    }
}
