package com.socialshare.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This is a utility class for Social Share Application, to deal with its shared preference values. 
 * @author Midhu
 */
public class SS_Preference {
	
	/* Member Variables */
	private static SharedPreferences 	prefSetting 			= null;
	private static Context 				prefContext 			= null;
	public static final String 			PREFERENCE_NAME 		= "SocialSharePref";

	/** Keys Used In This Preference **/
	public static final String 			KEY_AUTH 			= "isAuthenticated";
	public static final String 			KEY_AUTH_FB			= "isAuthenticatedFB";
	public static final String 			KEY_AUTH_TT			= "isAuthenticatedTT";
	public static final String 			KEY_AUTH_GP			= "isAuthenticatedGP";
	
	/** 
	 * This method must be called before using any other functions of {@link SS_Preference} 
	 * @param context : Pass the calling context or Base Context or Application Context
	 **/
	public static void setContext(Context context) {				
		prefContext = context;
		prefSetting = prefContext.getSharedPreferences(PREFERENCE_NAME, 0);
	}
	
	/**
	 * Use this method to GET a value for the supplied key
	 */
	public static String getPreference(String key) throws Exception {
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		return prefSetting.getString(key, "");
	}

	/**
	 * Use this method to SET a value for the supplied key
	 */
	public static void setPreference(String key, String value) throws Exception {	
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		Editor editor = prefSetting.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * Use this method to clear all preference data
	 */
	public static void clearPreference() throws Exception {
		if (prefContext == null) {
			throw new Exception("context not initialised yet");
		}
		Editor editor = prefSetting.edit();
		editor.clear();
		editor.commit();
	}
}