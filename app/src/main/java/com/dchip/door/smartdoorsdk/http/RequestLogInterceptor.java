package com.dchip.door.smartdoorsdk.http;


import com.dchip.door.smartdoorsdk.utils.LogUtil;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by jelly on 2017/9/22.
 */

public class RequestLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public RequestLogInterceptor() {
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        RequestBody requestBody = request.body();
        ResponseBody responseBody = response.body();
        String responseBodyString = responseBody.string();
        String requestMessage = request.method() + ' ' + request.url();
        if(requestBody != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            requestMessage = requestMessage + "\n" + buffer.readString(UTF8);
        }

        LogUtil.e("RequestLogInterceptor", requestMessage);
        LogUtil.e("RequestLogInterceptor", request.method() + ' ' + request.url() + ' ' + responseBodyString);
        return response.newBuilder().body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes())).build();
    }
}
