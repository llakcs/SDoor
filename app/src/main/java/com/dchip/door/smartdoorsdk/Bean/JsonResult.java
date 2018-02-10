package com.dchip.door.smartdoorsdk.Bean;

import com.google.gson.Gson;

/**
 * Created by jelly on 2017/9/22.
 */
public class JsonResult<T> {
    public int code;
    public String msg;
    public T data;
    public boolean success;

    public JsonResult() {
    }

    public JsonResult(int code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public static JsonResult fromString(String value) {
        if(value == null) {
            return null;
        } else {
            JsonResult result = new Gson().fromJson(value, JsonResult.class);
            return result;
        }
    }

    public boolean isOk() {
        return this.code == 200;
    }

    public String getData() {
        return String.valueOf(this.data);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
