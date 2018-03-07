package com.dchip.door.smartdoorsdk.http;

import com.dchip.door.smartdoorsdk.Bean.ApiGetAdvertisement;
import com.dchip.door.smartdoorsdk.Bean.ApiGetCardListModel;
import com.dchip.door.smartdoorsdk.Bean.ApiGetDeviceConfigModel;
import com.dchip.door.smartdoorsdk.Bean.ApiGetPropManagement;
import com.dchip.door.smartdoorsdk.Bean.AppUpdateModel;
import com.dchip.door.smartdoorsdk.Bean.JsonResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * The interface Device api.
 *
 * @author zhangdeming
 * date 创建时间 2017/5/11
 * description 描述类的功能
 */
public interface DeviceApi {

    /**
     * 查询服务器上最新版本号
     *
     * @param type 默认为1
     * @return call
     */
    @FormUrlEncoded
    @POST("mine/version")
    Call<JsonResult<AppUpdateModel>> checkVersion(@Field("type") int type);

    /**
     * 上传主控板的MAC码
     *
     * @param mac         the mac
     * @param versionName the version name
     * @param type        the type
     * @return call
     */
    @FormUrlEncoded
    @POST("maincontrol/uploadVersionInfo")
    Call<JsonResult<Object>> uploadAppVersion(@Field("mac") String mac, @Field("versionName") String versionName, @Field("type") int type);

    /**
     * 上传主控板的MAC码 和 网络类型
     *
     * @param mac         the mac
     * @param networkType the network type
     * @param smartType the smartType 1-门锁 2-水表 3-电表 4-车位地磁 5-垃圾桶 6 路灯 7智能家庭
     * @return call
     */
    @FormUrlEncoded
    @POST("maincontrol/upload")
    Call<JsonResult<Object>> uploadMac(@Field("mac") String mac,@Field("networkType") int networkType,@Field("smartType") int smartType);

    /**
     * 上传锁的数据
     *
     * @param mac the mac
     * @param uid the uid
     * @return call
     */
    @FormUrlEncoded
    @POST("lockcontrol/upload")
    Call<JsonResult<Object>> uploadLock(@Field("mac") String mac, @Field("uid") String uid, @Field("category") int category);

    /**
     * 锁板故障上报
     *
     * @param uid  the uid
     * @param type the type
     * @return call
     */
    @FormUrlEncoded
    @POST("lockcontrol/reportFault")
    Call<JsonResult<Object>> reportFault(@Field("uid") String uid, @Field("type") int type);

    /**
     * appCrash 上传
     *
     * @param mac          the mac
     * @param errorContent the error content
     * @return call
     */
    @FormUrlEncoded
    @POST("errorlog/uploadError")
    Call<JsonResult<Object>> reportCrash(@Field("mac") String mac, @Field("errorContent") String errorContent);

    /**
     * 上传是否写卡成功
     *
     * @param mac    the mac
     * @param status the status
     * @return call
     */
    @FormUrlEncoded
    @POST("maincontrol/updateTerminalRecord")
    Call<JsonResult<Object>> reportWriteCardStatus(@Field("mac") String mac, @Field("status") int status);

    /**
     * appCrash 上传
     *
     * @param uid    the uid
     * @param cardId the card id
     * @param phone  the phone
     * @return the call
     */
    @FormUrlEncoded
    @POST("access/setOpenByCardRecord")
    Call<JsonResult<Object>> uploadCardId(@Field("uid") String uid, @Field("cardId") String cardId, @Field("id") String phone);

    /**
     * appCrash 上传
     *
     * @param mac   the mac
     * @param flows the flows
     * @return the call
     */
    @FormUrlEncoded
    @POST("flow/save")
    Call<JsonResult<Object>> uploadFlow(@Field("mac") String mac, @Field("flows") String flows);

    /**
     * app更新失败上传
     *
     * @param mac         the mac
     * @param failReasion the fail reasion
     * @return the call
     */
    @FormUrlEncoded
    @POST("maincontrol/uploadInstallFailReasion")
    Call<JsonResult<Object>> installFail(@Field("mac") String mac, @Field("failReasion") String failReasion);

    /**
     * app更新失败上传
     *
     * @param mac the mac
     * @return the card list by mac
     */
    @FormUrlEncoded
    @POST("access/getCardListByMac")
    Call<JsonResult<ApiGetCardListModel>> getCardListByMac(@Field("mac") String mac);


    /**
     * 获取锁的设置
     *
     * @param mac the mac
     * @return the device config
     */
    @FormUrlEncoded
    @POST("maincontrol/findMainInfo")
    Call<JsonResult<ApiGetDeviceConfigModel>> getDeviceConfig(@Field("mac") String mac);

    /**
     *业主信息写入终端状态
     *
     * @param mac    the mac
     * @param status the status
     * @return the call
     */
    @FormUrlEncoded
    @POST("maincontrol/updateOnwerInfoTerminalRecord")
    Call<JsonResult<Object>> updateOnwerStatus(@Field("mac") String mac,@Field("status") int status);


    /**
     * 物业管理
     *
     * @param mac the mac
     * @return the call
     */
    @FormUrlEncoded
    @POST("areaconcat/pageDeviceList")
    Call<JsonResult<ApiGetPropManagement>> propertyManagement(@Field("mac") String mac);

    /**
     * 获取广告
     *
     * @param mac  the mac
     * @param type the type
     * @return the ad
     */
    @FormUrlEncoded
    @POST("access/getDchipDeviceBanner")
    Call<JsonResult<ApiGetAdvertisement>> getAd(@Field("mac") String mac,@Field("style") int type);

    /**
     * 下载进度
     *
     * @param mac      the mac
     * @param progress the progress
     * @param type     the type
     * @return the call
     */
    @FormUrlEncoded
    @POST("maincontrol/downloadRecord")
    Call<JsonResult<Object>> uploadDownloadProgress(@Field("mac") String mac,@Field("current_progress") int progress,@Field("type") int type);

    /**
     * 上传是否按键开门成功
     *
     * @param uid    the uid
     * @return call
     */
    @FormUrlEncoded
    @POST("access/setOpenByKeyRecord")
    Call<JsonResult<Object>> setOpenByKeyRecord(@Field("uid") String uid);


//    /**
//     * 开锁成功返回
//     *
//     * @param uid
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("lockcontrol/openResult")
//    Call<JsonResult<Object>> openResult(@Field("uid") String uid);
}
