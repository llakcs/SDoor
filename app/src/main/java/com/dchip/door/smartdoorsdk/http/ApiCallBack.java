package com.dchip.door.smartdoorsdk.http;



import com.dchip.door.smartdoorsdk.Bean.JsonResult;
import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.net.ConnectException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jelly on 2017/9/22.
 */


public abstract class ApiCallBack<T> implements Callback<JsonResult<T>> {
    private static final String TAG = "ApiCallBack";

    public ApiCallBack() {
    }

    public void onResponse(Call<JsonResult<T>> call, Response<JsonResult<T>> response) {
        JsonResult result = (JsonResult) response.body();
        try {
            if (result == null) {
                this.fail(-1, "服务器执行错误，请重试");
            } else if (result.isOk()) {
                this.success((T)result.data);
            } else {
                this.fail(result.code, result.msg);
            }
        } catch (Exception var5) {
            LogUtil.e(TAG,"onResponse err");
            var5.printStackTrace();
        }

    }

    public void onFailure(Call<JsonResult<T>> call, Throwable t) {
        try {
            if (t instanceof ConnectException) {
                this.fail(-2, "网络错误，请检查本地网络");
            } else {
                this.fail(-3, t.getMessage());
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public abstract void success(T var1);

    public abstract void fail(int var1, String var2);
}
