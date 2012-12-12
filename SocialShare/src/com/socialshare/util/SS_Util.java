package com.socialshare.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * This class holds some commonly used utility functions
 * @author Midhu
 */
public class SS_Util {
	
	/** Pass {@link InputStream} and this method will read that stream and return a {@link String} representation **/
	public static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		String content = sb.toString();
		SS_Log.i("Response", " " + content);
		return content;
	}
	
	/** Pass the JSON {@link String} response and this method will return back the corresponding {@link JSONObject} **/
	public static JSONObject parseJson(String response) throws JSONException {
		JSONObject json = new JSONObject(response);
		return json;
	}
	
	/** Pass a {@link HashMap} of parameters and this method will return them in a URL Encoded {@link String} format **/
	@SuppressWarnings("deprecation")
	public static String encodeUrl(HashMap<String , String> parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "="
					+ URLEncoder.encode(parameters.get(key)));
		}
		return sb.toString();
	}
	
	/**
	 * This function is used to display a message in the main tab activity context.
	 */
	public static void showMessage(final String title, final String message, final String buttonCaption) {
		((Activity)SS_Constants.CurrentActiveContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(SS_Constants.CurrentActiveContext);
				builder.setCancelable(true);
				builder.setTitle(title);
				builder.setMessage(message);
				builder.setPositiveButton(buttonCaption, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}
}
