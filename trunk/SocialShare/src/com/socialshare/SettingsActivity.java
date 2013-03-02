package com.socialshare;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Preference;
import com.socialshare.util.SS_Util;

public class SettingsActivity extends Activity {

	private TextView txtSignOutAll;
	private TextView txtTT;
	private TextView txtFB;
	private TextView txtGP;
	private ToggleButton tglTT;
	private ToggleButton tglFB;
	private ToggleButton tglGP;
	
	private boolean isAuth_FB;
	private boolean isAuth_TT;
	private boolean isAuth_GP;
	private Facebook mFacebook = new Facebook(SS_Constants.FB_APP_ID);
	private String 					mFacebookUserId;
	private String 					mFacebookSnsTocken;
	private String 					mFacebookUserName;
	private String 					mFacebookUserImage;
	
	
	private final static int TW_AUTHORIZE_ACTIVITY_RESULT_CODE = 11423;

	
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(SS_Constants.BROADCAST_SIGNOUT_ALL)) {
				finish();
			}
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SS_Constants.BROADCAST_SIGNOUT_ALL);
		registerReceiver(mBroadcastReceiver, intentFilter);
		
		setContentView(R.layout.settings_view);
		txtSignOutAll = (TextView) findViewById(R.id.txtSignoutAll);
		txtTT = (TextView) findViewById(R.id.txtSignTT);
		txtFB = (TextView) findViewById(R.id.txtSignFB);
		txtGP = (TextView) findViewById(R.id.txtSignGP);
		
		tglTT = (ToggleButton) findViewById(R.id.tglTT);
		tglFB = (ToggleButton) findViewById(R.id.tglFB);
		tglGP = (ToggleButton) findViewById(R.id.tglGP);
		
		initializeComponents();

		
		if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_GP).equalsIgnoreCase("1")) {
			txtGP.setText("Signout fom GP");
			isAuth_GP = true;
			tglGP.setEnabled(true);
		} else {
			txtGP.setText("signin to GP");
			isAuth_GP = false;
			tglGP.setEnabled(false);
		}
		
		txtSignOutAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SS_Preference.setPreference(SS_Preference.KEY_AUTH_TT, "0");
				txtTT.setText("Sign Into TT");
				isAuth_TT = false;
				
				SS_Preference.setPreference(SS_Preference.KEY_AUTH_FB, "0");
				txtFB.setText("Sign in to FB");
				isAuth_FB=false;
				SS_Util.checkSocialAuthStatus();
			}
		});
		txtTT.setOnClickListener(onTwiterSign);
		txtFB.setOnClickListener(onFacebookSign);
		
		tglTT.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_TT, isChecked ? "1" : "0");
			}
		});
		
		tglFB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_FB, isChecked ? "1" : "0");
			}
		});
		
		tglGP.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_GP, isChecked ? "1" : "0");
			}
		});
		
		if (isAuth_TT && SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_TT).equalsIgnoreCase("1")) {
			tglTT.setChecked(true);
		} else {
			tglTT.setChecked(false);
			SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_TT, "0");
		}
		
		if (isAuth_FB && SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_FB).equalsIgnoreCase("1")) {
			tglFB.setChecked(true);
		} else {
			tglFB.setChecked(false);
			SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_FB, "0");
		}
		
		if (isAuth_GP && SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_GP).equalsIgnoreCase("1")) {
			tglGP.setChecked(true);
		} else {
			tglGP.setChecked(false);
			SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_GP, "0");
		}
	}
	
	private void initializeComponents() {
		if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_TT).equalsIgnoreCase("1")) {
			txtTT.setText("Signout fom TT");
			isAuth_TT = true;
			tglTT.setEnabled(true);
		} else {
			txtTT.setText("signin to TT");
			isAuth_TT = false;
			tglTT.setEnabled(false);
		}
		
		if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_FB).equalsIgnoreCase("1")) {
			txtFB.setText("Signout fom FB");
			isAuth_FB = true;
			tglFB.setEnabled(true);
		} else {
			txtFB.setText("signin to FB");
			isAuth_FB = false;
			tglFB.setEnabled(false);
		}
		SS_Util.checkSocialAuthStatus();
	}

	private OnClickListener onTwiterSign = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isAuth_TT) {
				SS_Preference.setPreference(SS_Preference.KEY_AUTH_TT, "0");
				SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_TT, "0");
				initializeComponents();

			} else {
				Intent intent = new Intent(SettingsActivity.this, TwitterOauth.class);
				startActivityForResult(intent, TW_AUTHORIZE_ACTIVITY_RESULT_CODE);
			}
		}
	};
	private OnClickListener onFacebookSign = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (isAuth_FB) {
				SS_Preference.setPreference(SS_Preference.KEY_AUTH_FB, "0");
				SS_Preference.setPreference(SS_Preference.KEY_DEF_SHARE_FB, "0");
				initializeComponents();
			} else {
				Util.clearCookies(getBaseContext());
				mFacebook.authorize(SettingsActivity.this, SS_Constants.FB_PERMISSIONS, new MyDialogListener());
			}
		}
	};
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
						
						initializeComponents();
					} catch (Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SettingsActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
							}
						});
					}
				};
			}.start();
		}

		public void onFacebookError(FacebookError error) {
			Toast.makeText(SettingsActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
		}

		public void onError(DialogError error) {
			Toast.makeText(SettingsActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
		}
		public void onCancel() {
			finish();
		}
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
			
			initializeComponents();
		}
	}
}