package com.dchip.door.smartdoorsdk.http;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jelly on 2017/9/22.
 */

public class RequestHeaderInterceptor implements Interceptor {
    public RequestHeaderInterceptor() {
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        Request.Builder builder = request.newBuilder();
        String l = "";
        String key = "platform";
        String value = "5";
        if (!TextUtils.isEmpty(value)) {
            l = l + key + "===>" + value + "\n";
            builder.addHeader(key, value);
        }

//        LogUtil.e("RequestHeaderInterceptor", l);
        return chain.proceed(builder.build());
    }
}
