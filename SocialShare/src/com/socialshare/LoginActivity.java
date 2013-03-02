package com.socialshare;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Preference;

public class LoginActivity extends Activity {

	private Button mLoginFB;
	private Button mLoginGP;
	private Button mLoginTT;
	private final static int TW_AUTHORIZE_ACTIVITY_RESULT_CODE = 11423;
	private Facebook mFacebook = new Facebook(SS_Constants.FB_APP_ID);
	private String 					mFacebookUserId;
	private String 					mFacebookSnsTocken;
	private String 					mFacebookUserName;
	private String 					mFacebookUserImage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_login);
	    
	    mLoginFB = (Button) findViewById(R.id.btnLoginFB);
	    mLoginGP = (Button) findViewById(R.id.btnLoginGP);
	    mLoginTT = (Button) findViewById(R.id.btnLoginTT);
	    
	    mLoginTT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_TT).equalsIgnoreCase("1")) {
					//TODO: Already logined with twitter
				} else {
					Intent intent = new Intent(LoginActivity.this, TwitterOauth.class);
					startActivityForResult(intent, TW_AUTHORIZE_ACTIVITY_RESULT_CODE);
				}
			}
		});
	    
	    
	    
	    mLoginFB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.clearCookies(getBaseContext());
				mFacebook.authorize(LoginActivity.this, SS_Constants.FB_PERMISSIONS, new MyDialogListener());
			}
		});
	}

	
	private class MyDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			new Thread() {
				@Override
				public void run() {
					try {
						String response = mFacebook.request("/me", new Bundle(), "GET");
						JSONObject obj = Util.parseJson(response);
						mFacebookUserId 	= obj.optString("id");
						mFacebookUserName 	= obj.optString("name");
						mFacebookUserImage	= "http://graph.facebook.com/" + mFacebookUserId + "/picture";
						mFacebookSnsTocken 	= mFacebook.getAccessToken();
						
						SS_Preference.setPreference(SS_Preference.KEY_FB_ID, mFacebookUserId);
						SS_Preference.setPreference(SS_Preference.KEY_FB_NAME, mFacebookUserName);
						SS_Preference.setPreference(SS_Preference.KEY_FB_IMAGE, mFacebookUserImage);
						SS_Preference.setPreference(SS_Preference.KEY_FB_TOCKEN, mFacebookSnsTocken);
						
						SS_Preference.setPreference(SS_Preference.KEY_AUTH, "1");
						SS_Preference.setPreference(SS_Preference.KEY_AUTH_FB, "1");
						
						startActivity(new Intent(LoginActivity.this, HomeActivity.class));
						finish();
					} catch (Exception e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
							}
						});
					}
				};
			}.start();
		}

		public void onFacebookError(FacebookError error) {
			error.printStackTrace();
			Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
		}

		public void onError(DialogError error) {
			error.printStackTrace();
			Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
		}

		public void onCancel() {
			finish();
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(resultCode != RESULT_OK ){
			return;
		}
		if (requestCode == TW_AUTHORIZE_ACTIVITY_RESULT_CODE) {
			Toast.makeText(getBaseContext(),"Login Success.", Toast.LENGTH_SHORT).show();
			String userId = intent.getStringExtra("twitterId");
			String userName = intent.getStringExtra("userName");
			String imageUrl  = intent.getStringExtra("imageurl");
			String token = intent.getStringExtra("twitterAccessToken");
			String secret = intent.getStringExtra("twitterAccessTokenSecret");
			
			SS_Preference.setPreference(SS_Preference.KEY_TWITTER_ID, userId);
			SS_Preference.setPreference(SS_Preference.KEY_TWITTER_NAME, userName);
			SS_Preference.setPreference(SS_Preference.KEY_TWITTER_IMAGEURL, imageUrl);
			SS_Preference.setPreference(SS_Preference.KEY_TWITTER_TOCKEN, token);
			SS_Preference.setPreference(SS_Preference.KEY_TWITTER_SECRET, secret);
			
			SS_Preference.setPreference(SS_Preference.KEY_AUTH, "1");
			SS_Preference.setPreference(SS_Preference.KEY_AUTH_TT, "1");
			
			startActivity(new Intent(LoginActivity.this, HomeActivity.class));
			finish();
		}
	}
	

}
