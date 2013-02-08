package com.socialshare.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.socialshare.datatypes.DT_MyPosts;

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
	
	public static Twitter getTwitterInstance() {
		if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_TT).equalsIgnoreCase("1")) {
			String tocken = SS_Preference.getPreference(SS_Preference.KEY_TWITTER_TOCKEN);
			String secret = SS_Preference.getPreference(SS_Preference.KEY_TWITTER_SECRET);
			SS_Log.i("Twiter Tocken", " : " + tocken);
			SS_Log.i("Twiter Secret", " : " + secret);
			AccessToken t = new AccessToken(tocken, secret);
			
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(SS_Constants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(SS_Constants.TWITTER_SECRET_KEY);
			Configuration configuration = builder.build();
			TwitterFactory factory = new TwitterFactory(configuration);
	
			Twitter twitter = factory.getInstance(t);
			return twitter;
		} else {
			return null;
		}
	}
	
	public static void updateTwitterStatus(final String thought, final String link) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Status status = getTwitterInstance().updateStatus(thought + " " + link);
					DT_MyPosts myPosts = new DT_MyPosts();
					myPosts.content = thought;
					myPosts.link = link;
					myPosts.twiter = String.valueOf(status.getId());
					new SS_DBHelper(SS_Constants.CurrentActiveContext).addPost(myPosts);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public static void updateFacebookStatus(String thought, String link) {
			// TODO FB Update
	}
	
	public static void updateGoogleStatus(String thought, String link) {
			// TODO G+ Update
	}
}
