package com.dchip.door.smartdoorsdk.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by llakcs on 2017/11/29.
 */

public class Constant {
    //sdcardpath
//    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String SDCARD_PATH = "/sdcard";
    public static final String BASE_PATh = SDCARD_PATH + File.separator + "smdsdk" + File.separator;
    //opencv
    public static final String VISTPATH = BASE_PATh + "vistdata"+File.separator;
    //apk download
    public static final String DOWNLOAD_APK_PATH = BASE_PATh + "downloadAPK" + File.separator;
    //video download
    public static final String VIDEOPATH =BASE_PATh+"video"+File.separator;
    //image download
    public static final String ADIMGPATH=BASE_PATh+"adimage"+File.separator;
    //vist photo
    public static final String VIST_PHOTO_PATH=BASE_PATh+"vistPhoto"+File.separator;
    //crash log
    public static final String CRASH_LOG_PATH = BASE_PATh + "crashLog" + File.separator;
    //crash not upload
    public static final String CRASH_LOG_UPLOAD_FAIL_PATH = BASE_PATh + "crashLog" + File.separator + "uploadFail" + File.separator;
    //save card list
    public static final String CARDS_FILE_PATH = BASE_PATh + "cards.txt";
    //保存工作人员信息
    public static final String MANAGEMENT_FILE_PATH = BASE_PATh + "propManagement.txt";
    //lock config
    public static final String LOCK_CONFIG_FILE_PATH = BASE_PATh + "lockConfig.txt";
    public static final String WS_URI = "ws://%s";
}
