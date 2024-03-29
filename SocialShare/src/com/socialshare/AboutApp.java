package com.socialshare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.socialshare.util.SS_Constants;

public class AboutApp extends Activity {
	
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
		setContentView(R.layout.about_app);
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SS_Constants.BROADCAST_SIGNOUT_ALL);
		registerReceiver(mBroadcastReceiver, intentFilter);
		
		((Button) findViewById(R.id.btnTitleRightOp)).setVisibility(View.GONE);
		((Button) findViewById(R.id.btnTitleLeftOp)).setBackgroundResource(R.drawable.selector_title_back);
		((TextView) findViewById(R.id.txtTitleText)).setText("About");
		
		((Button) findViewById(R.id.btnTitleLeftOp)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		PackageInfo mPInfo = null;
		try {
			mPInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		((TextView) findViewById(R.id.txtVersionLabel)).setText(Html.fromHtml("Social Share <b><i>v-" + mPInfo.versionName + "</i></b>, Eljos"));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
	}
	
}
