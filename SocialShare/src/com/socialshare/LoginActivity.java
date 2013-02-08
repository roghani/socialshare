package com.socialshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Preference;

public class LoginActivity extends Activity {

	private Button mLoginFB;
	private Button mLoginGP;
	private Button mLoginTT;
	private final static int TW_AUTHORIZE_ACTIVITY_RESULT_CODE = 11423;

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
		}
	}

}
