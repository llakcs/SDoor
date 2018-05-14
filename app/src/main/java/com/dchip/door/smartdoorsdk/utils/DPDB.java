package com.dchip.door.smartdoorsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DPDB {
	private static SharedPreferences reader = null;
	private static SharedPreferences.Editor writer = null;
	private static Context mContext = null;
	private static DPDB instance = null;
	public static final String tag = "DPDb";
	public DPDB() {
		super();
		// TODO Auto-generated constructor stub
	}
	/** 初始化读写 */
	public static DPDB InitDPDbRW(Context context) {
		if (instance == null) {
			instance = new DPDB();
			mContext = context;
			reader = mContext
					.getSharedPreferences("DPDB", Context.MODE_PRIVATE);
			writer = mContext
					.getSharedPreferences("DPDB", Context.MODE_PRIVATE).edit();
		}
		return instance;
	}

	public static boolean isRead() {
		if (reader == null) {
			Log.e(tag, "reader = null,may be not call InitRW() methods");
			if(mContext == null){
				Log.e("DPDB", "mContext == null");
				return false;
			}
			reader = mContext.getSharedPreferences("DPDB", Context.MODE_PRIVATE);
			return false;
		}
		return true;
	}

	public static boolean isWrite() {
		if (writer == null) {
			if(mContext == null){
				Log.e("DPDB", "mContext == null");
				return false;
			}
			writer = mContext
					.getSharedPreferences("DPDB", Context.MODE_PRIVATE).edit();
			Log.e(tag, "writer = null,may be not call InitRW() methods");
			return false;
		}
		return true;
	}
	
	
	/** 获取屏保时间 */
	public static int getScreenTime() {
		if (!isRead()) {
			return 0;
		}
		return reader.getInt("ScreenTime", 30);
	}

	/** 保存屏保时间 */
	public static boolean setScreenTime(int ScreenTime) {
		if (!isWrite()) {
			return false;
		}
		writer.putInt("ScreenTime", ScreenTime);
		return writer.commit();
	}

	/** 保存地址 */
	public static boolean setaddr(String addr) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("addr", addr);
		return writer.commit();
	}

	/** 获取地址 */
	public static String getaddr() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("addr", null);
	}


	/** 保存mac */
	public static boolean setmac(String mac) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("mac", mac);
		return writer.commit();
	}
	
	/** 获取mac */
	public static String getmac() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("mac", null);
	}

	/** 保存uid */
	public static boolean setUid(String name) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("uid", name);
		return writer.commit();
	}

	/** 获取uid */
	public static String getUid() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("uid", null);
	}


	/** 保存工程密码 */
	public static boolean setSyspass(String name) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("syspass", name);
		return writer.commit();
	}

	/** 获取工程密码 */
	public static String getSyspass() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("syspass", null);
	}



	/** 获取服务器连接状态 */
	public static boolean getServiceconn() {
		if (!isRead()) {
			return false;
		}
		return reader.getBoolean("Serviceconn", false);
	}

	/** 保存服务器连接状态 */
	public static boolean setServiceconn(boolean state) {
		if (!isWrite()) {
			return false;
		}
		writer.putBoolean("Serviceconn", state);
		return writer.commit();
	}


	/** 获取是否播放广告 */
	public static boolean getAdPlay() {
		if (!isRead()) {
			return false;
		}
		return reader.getBoolean("adplay", false);
	}

	/** 保存广告播放 */
	public static boolean setAdPlay(boolean state) {
		if (!isWrite()) {
			return false;
		}
		writer.putBoolean("adplay", state);
		return writer.commit();
	}

	/** 保存工程密码 */
	public static boolean setLockID(int lockId) {
		if (!isWrite()) {
			return false;
		}
		writer.putInt("lockId", lockId);
		return writer.commit();
	}

	/** 获取工程密码 */
	public static int getLockID() {
		if (!isRead()) {
			return 0;
		}
		return reader.getInt("lockId", -1);
	}



	/** 保存蓝牙范围 */
	public static boolean setBluethrange(int range) {
		if (!isWrite()) {
			return false;
		}
		writer.putInt("Bluethrange", range);
		return writer.commit();
	}

	/** 获取蓝牙范围 */
	public static int getBluethrange() {
		if (!isRead()) {
			return 0;
		}
		return reader.getInt("Bluethrange", -85);
	}



	/** 保存wsurl */
	public static boolean setwsUrl(String wsUrl) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("wsUrl", wsUrl);
		return writer.commit();
	}

	/** 获取wsUrl */
	public static String getwsUrl() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("wsUrl", null);
	}

	/** 保存serverUrl */
	public static boolean setserverUrl(String serverUrl) {
		if (!isWrite()) {
			return false;
		}
		writer.putString("serverUrl", serverUrl);
		return writer.commit();
	}

	/** 获取serverUrl */
	public static String getserverUrl() {
		if (!isRead()) {
			return null;
		}
		return reader.getString("serverUrl", null);
	}


}
