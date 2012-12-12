package com.socialshare.util;

import android.util.Log;

/**
 * This class is created to provide the simple {@link Log} methods.
 * The logging will work only if the <i>isDebugMode</i> flag variable of this class is set to true.
 * <br><br>
 * <b>Usage Examples:</b>
 * <br>
 * <code>
 * <li>MA_Log.i(tag, msg)</li>
 * <li>MA_Log.e(tag, msg)</li>
 * <li>MA_Log.d(tag, msg)</li>
 * <li>MA_Log.v(tag, msg)</li>
 * <li>MA_Log.w(tag, msg)</li>
 * </code>
 * <br><br>
 * @author Midhu
 */
public class SS_Log {
	
	private static boolean isDebugMode = true;
	
	public static void i(String tag, String msg) {
		if (isDebugMode) Log.i(tag, msg);
	}
	
	public static void e(String tag, String msg) {
		if (isDebugMode) Log.e(tag, msg);
	}
	
	public static void d(String tag, String msg) {
		if (isDebugMode) Log.d(tag, msg);
	}
	
	public static void v(String tag, String msg) {
		if (isDebugMode) Log.v(tag, msg);
	}
	
	public static void w(String tag, String msg) {
		if (isDebugMode) Log.w(tag, msg);
	}
	
	public static void printStackTrace(Exception e) {
		if (isDebugMode) e.printStackTrace();
	}
}
