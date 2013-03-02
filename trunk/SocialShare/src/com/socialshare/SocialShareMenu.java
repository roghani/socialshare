package com.socialshare;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Preference;
import com.socialshare.util.SS_Util;

public class SocialShareMenu extends Dialog implements android.view.View.OnClickListener {
	
	private transient LinearLayout mMenuView;
	private boolean isFirstTime = true;
	private boolean enableShare = false;
	private String mLinkToShare;
	private EditText mThought;
	private TextView mSharingLink;
	private ToggleButton tglTT;
	private ToggleButton tglGP;
	private ToggleButton tglFB;
	private Button mShare;

	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(SS_Constants.BROADCAST_SIGNOUT_ALL)) {
				dismiss();
			}
		}
	};
	
	public SocialShareMenu(Context context, boolean enableShare) {
		super(context, R.style.PopUpTheme);
		this.enableShare = enableShare;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SS_Constants.BROADCAST_SIGNOUT_ALL);
		context.registerReceiver(mBroadcastReceiver, intentFilter);
	}
	
	public SocialShareMenu(Context context, boolean enableShare, String link) {
		super(context, R.style.PopUpTheme);
		this.enableShare = enableShare;
		mLinkToShare = link;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SS_Constants.BROADCAST_SIGNOUT_ALL);
		context.registerReceiver(mBroadcastReceiver, intentFilter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_view);
		isFirstTime = true;
		
		if (enableShare) {
			findViewById(R.id.viwShareThought).setVisibility(View.VISIBLE);
			mThought = (EditText) findViewById(R.id.edtThought);
			mSharingLink = (TextView) findViewById(R.id.txtSharingLink);
			tglTT = (ToggleButton) findViewById(R.id.tglTT);
			tglGP = (ToggleButton) findViewById(R.id.tglGP);
			tglFB = (ToggleButton) findViewById(R.id.tglFB);
			mShare = (Button) findViewById(R.id.btnShareThought);
			mSharingLink.setText(mLinkToShare);
			mShare.setOnClickListener(onShare);
			
			if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_TT).equalsIgnoreCase("1")) {
				tglTT.setEnabled(true);
			} else {
				tglTT.setEnabled(false);
			}
			
			if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_FB).equalsIgnoreCase("1")) {
				tglFB.setEnabled(true);
			} else {
				tglFB.setEnabled(false);
			}
			
			if (SS_Preference.getPreference(SS_Preference.KEY_AUTH_GP).equalsIgnoreCase("1")) {
				tglGP.setEnabled(true);
			} else {
				tglGP.setEnabled(false);
			}
			
			if (SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_TT).equalsIgnoreCase("1")) {
				tglTT.setChecked(true);
			} else {
				tglTT.setChecked(false);
			}
			
			if (SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_FB).equalsIgnoreCase("1")) {
				tglFB.setChecked(true);
			} else {
				tglFB.setChecked(false);
			}
			
			if (SS_Preference.getPreference(SS_Preference.KEY_DEF_SHARE_GP).equalsIgnoreCase("1")) {
				tglGP.setChecked(true);
			} else {
				tglGP.setChecked(false);
			}
			
		} else {
			findViewById(R.id.viwShareThought).setVisibility(View.GONE);
		}
		
		
		mMenuView = (LinearLayout) findViewById(R.id.viwSlideMenu);

		findViewById(R.id.viwTopBar).setOnClickListener(this);
		findViewById(R.id.viwSideBar).setOnClickListener(this);
		
		findViewById(R.id.txtShowGuide).setOnClickListener(this);
		findViewById(R.id.txtShowAbout).setOnClickListener(this);
		findViewById(R.id.txtShowSettings).setOnClickListener(this);
		findViewById(R.id.txtSwitchHomeThoughts).setOnClickListener(this);
		
		PackageInfo mPInfo = null;
		try {
			mPInfo = SS_Constants.CurrentActiveContext.getPackageManager().getPackageInfo(SS_Constants.CurrentActiveContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		((TextView) findViewById(R.id.txtShowVersion)).setText("Version " + mPInfo.versionName);
	};
	
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && isFirstTime) {
			Animation anim = AnimationUtils.loadAnimation(SS_Constants.CurrentActiveContext, R.anim.slide_left_to_right);
			mMenuView.startAnimation(anim);
			isFirstTime = !isFirstTime;
		}
	};
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		dismissMenu();
		return false;
	}
	
	@Override
	public void onBackPressed() {
		dismissMenu();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.txtShowGuide) {
			SS_Constants.CurrentActiveContext.startActivity(new Intent(SS_Constants.CurrentActiveContext, Guide.class));
		} else if (v.getId() == R.id.txtShowAbout) {
			SS_Constants.CurrentActiveContext.startActivity(new Intent(SS_Constants.CurrentActiveContext, AboutApp.class));
		} else if (v.getId() == R.id.txtShowSettings) {
			SS_Constants.CurrentActiveContext.startActivity(new Intent(SS_Constants.CurrentActiveContext, SettingsActivity.class));
		} else if (v.getId() == R.id.txtSwitchHomeThoughts) {
			if (SS_Constants.isShowingMyThoughts)
				SS_Constants.CurrentActiveContext.startActivity(new Intent(SS_Constants.CurrentActiveContext, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			else
				SS_Constants.CurrentActiveContext.startActivity(new Intent(SS_Constants.CurrentActiveContext, MyThoughts.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		} else {
			dismissMenu();
		}
	}
	
	public void dismissMenu() {
		Animation anim = AnimationUtils.loadAnimation(SS_Constants.CurrentActiveContext, R.anim.slide_right_to_left);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				dismiss();
			}
		});
		mMenuView.startAnimation(anim);
	}
	
	private View.OnClickListener onShare = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			SS_Util.updateSocialStatus(mThought.getText().toString(), mSharingLink.getText().toString(), tglTT.isChecked(), tglFB.isChecked(), tglGP.isChecked());
		}
	};
}
