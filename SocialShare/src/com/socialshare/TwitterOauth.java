package com.socialshare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.android.Util;
import com.socialshare.util.SS_Preference;
public class TwitterOauth extends Activity {

	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessToken = null;
	private WebView webview;
	private ProgressDialog mDialog;
	private static final String TWITTER_CONSUMER_KEY = "MEiVETank3JeXgrm9sKFw";
	private static final String TWITTER_SECRET_KEY = "f7yhBFVcEe0YSoypuOI289003jejTlVDTs4lmc5o";
	private boolean isDialogShowing = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.oauth_webview);
		mDialog = new ProgressDialog(this);

		webview = (WebView)findViewById(R.id.webview);
		
		Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
		webview.setLayoutParams(new LinearLayout.LayoutParams( display.getWidth() - ((int) (30 * scale + 0.5f)),
                display.getHeight() - ((int) (120 * scale + 0.5f))));

		Util.clearCookies(getBaseContext());
		webview.setVerticalScrollBarEnabled(false);
		webview.setHorizontalScrollBarEnabled(false);
		WebSettings settings = webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showProgressDialog();
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if(url.equalsIgnoreCase("https://api.twitter.com/oauth/authorize") ||
						url.equalsIgnoreCase("http://api.twitter.com/oauth/authorize")){
					webview.loadUrl("javascript:window.HTMLOUT.showHTML(document.getElementById('oauth_pin').innerHTML)");
				}
				killProgressDialog();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showProgressDialog();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				killProgressDialog();
				Toast.makeText(getBaseContext(),"Login Failed!", Toast.LENGTH_SHORT).show();
			}
		});

		new Thread() {
			@Override
			public void run() {
				getRequesttoken();
			}
		}.start();
		showProgressDialog();
	}

	public void getRequesttoken() {
		System.setProperty("twitter4j.oauth.consumerKey", TWITTER_CONSUMER_KEY);
		System.setProperty("twitter4j.oauth.consumerSecret", TWITTER_SECRET_KEY);

		String url = "";
		try {
			twitter = new TwitterFactory().getInstance();
			requestToken = twitter.getOAuthRequestToken();

			String token = requestToken.toString();
			String oauth_token = "";
			Pattern patern = Pattern.compile("token=\'(.*?)\'", Pattern.CASE_INSENSITIVE);
			Matcher matcher = patern.matcher(token);
			if (matcher.find()) {
				oauth_token = matcher.group(1);
			}
			url = "http://api.twitter.com/oauth/authorize?oauth_token=" + oauth_token;
		} catch (Exception e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					killProgressDialog();
				}
			});
			finish();
		}
		if(url != null ){
			System.out.print(url);
			webview.loadUrl(url);
		}
	}

	class MyJavaScriptInterface {
		public void showHTML(String html) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showProgressDialog();
					webview.setVisibility(View.INVISIBLE);
				}
			});

			String pin = "";
			Pattern patern = Pattern .compile( "<kbd aria-labelledby=\"code-desc\"><code>(.*?)</code></kbd>",
							Pattern.CASE_INSENSITIVE);
			Matcher matcher = patern.matcher(html);
			if (matcher.find()) {
				pin = matcher.group(1);
			}

			webview.clearCache(false);

			Intent intent = new Intent();
			try {
				if (pin.length() > 0) {
					accessToken = twitter .getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken(requestToken);
				}
				String imageUrl = "http://api.twitter.com/1/users/profile_image/" + accessToken.getUserId() + ".json";

				intent.putExtra("userName", accessToken .getScreenName());
				intent.putExtra("twitterAccessToken", accessToken.getToken());
				intent.putExtra( "twitterAccessTokenSecret", accessToken .getTokenSecret());
				intent.putExtra("isAuthenticated", "1");
				intent.putExtra("provider", "twitter");
				intent.putExtra("twitterId", Long.toString(twitter.verifyCredentials().getId()));
				intent.putExtra("imageurl", imageUrl);
				setResult(RESULT_OK, intent);
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					setResult(RESULT_CANCELED, intent);
				} else {
					te.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Twitterã?¸ã?®ãƒ­ã‚°ã‚¤ãƒ³ã?«å¤±æ•—ã?—ã?¾ã?—ã?Ÿ", Toast.LENGTH_LONG).show();
					}
				});
			}catch (Exception ex) {
				ex.printStackTrace();
				setResult(RESULT_CANCELED, intent);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getBaseContext(), "Twitterã?¸ã?®ãƒ­ã‚°ã‚¤ãƒ³ã?«å¤±æ•—ã?—ã?¾ã?—ã?Ÿ", Toast.LENGTH_LONG).show();
					}
				});
			}
		

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					killProgressDialog();
				}
			});
			
			finish();
		}

	}
	
	
	private void showProgressDialog() {
		if (mDialog != null && !isDialogShowing ) {
			isDialogShowing = true;
			mDialog.show();
		}
	}

	private void killProgressDialog() {
		if (mDialog != null && isDialogShowing ) {
			isDialogShowing = false;
			mDialog.hide();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mDialog.dismiss();
		mDialog = null;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDialog != null ){
			mDialog.dismiss();
		}
		mDialog = null;

	}
	public static void clear(Context context) {
		Editor editor = context.getSharedPreferences(SS_Preference.PREFERENCE_NAME, 0).edit();
		editor.putString("TW_USERNAME", "");
		editor.putString("TW_USERID", "");
		editor.putString("TW_USER_IMG", "");
		editor.putString("TW_PROVIDER", "");
		editor.putString("isTWAuthenticated", "0");
		editor.putString("twitter_token", "");
		editor.putString("twitter_secret", "");
		editor.commit();
		
	}
	    
	

}
