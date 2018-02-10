package com.dchip.door.smartdoorsdk.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by llakcs on 2017/6/20.
 */

public class GsonUtil {

    public static GsonUtil GsonUtil;

    public static GsonUtil getIns() {
        return GsonUtil = new GsonUtil();
    }


    // 将Json数据解析成相应的映射对象
    public <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    // 将Json数组解析成相应的映射对象列表
    public <T> List<T> parseJsonArrayWithGson(String jsonData,
                                              Class<T> type) {
        Gson gson = new Gson();
        List<T> result = gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
        return result;
    }
    public  String beanToJSONString(Object bean) {
        return new Gson().toJson(bean);
    }

}
