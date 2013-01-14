package com.socialshare;

import com.socialshare.util.SS_Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class SplashScreen extends Activity {
	
	private transient final int WHT_SPLASH_TIMEOUT = 1;
	
	private transient Handler mSplashHandler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case WHT_SPLASH_TIMEOUT:
				startActivity(new Intent(SplashScreen.this, LoginActivity.class));
				break;
			default:
				break;
			}
			return true;
		}
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		Message splashMessage = new Message();
		splashMessage.what = WHT_SPLASH_TIMEOUT;
		mSplashHandler.sendMessageDelayed(splashMessage, 5000);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
	}
	
}
