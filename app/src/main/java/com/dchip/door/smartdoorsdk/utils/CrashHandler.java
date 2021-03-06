package com.dchip.door.smartdoorsdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler {
    public static final String TAG = "CrashHandler";

    //CrashHandler实例
    private static CrashHandler instance;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }
    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }
    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
    }

    /**
     * Crash处理.
     *
     * @param crashInfo 错误信息
     * @return 是否处理成功
     */
    public boolean handleException(String crashInfo) {
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,程序出现异常。", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        //保存日志文件
//        EventBus.getDefault().post(new CrashEvent(crashInfo));
        saveCatchInfo2File(crashInfo);
        return true;
    }
    /**
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            LogUtil.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                LogUtil.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtil.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }
    /**
     * 保存错误信息到文件中
     * @param err
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCatchInfo2File(String err) {
        StringBuffer sb = new StringBuffer();
        //输入设备信息
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        //输入出错信息
        sb.append(err);

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt" ;
            String file_dir = Constant.CRASH_LOG_UPLOAD_FAIL_PATH;
            File dir = new File(file_dir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(file_dir + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            //发送给开发人员
            sendCrashLog2PM(file_dir + fileName);
            fos.close();
            //			}
            return fileName;
        } catch (Exception e) {
            LogUtil.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
    /**
     * 将捕获的导致崩溃的错误信息发送给开发人员
     * 目前只将log日志保存在sdcard 和输出到LogCat中，并未发送给后台。
     */
    private void sendCrashLog2PM(String fileName) {
        //		if (!new File(fileName).exists()) {
        //			Toast.makeText(mContext, "日志文件不存在！", Toast.LENGTH_SHORT).show();
        //			return;
        //		}
        //		FileInputStream fis = null;
        //		BufferedReader reader = null;
        //		String s = null;
        //		try {
        //			fis = new FileInputStream(fileName);
        //			reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
        //			while (true) {
        //				s = reader.readLine();
        //				if (s == null)
        //					break;
        //				//由于目前尚未确定以何种方式发送，所以先打出log日志。
        //				Log.i("info", s.toString());
        //			}
        //		} catch (FileNotFoundException e) {
        //			e.printStackTrace();
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		} finally { // 关闭流
        //			try {
        //				reader.close();
        //				fis.close();
        //			} catch (IOException e) {
        //				e.printStackTrace();
        //			}
        //		}
    }
}

