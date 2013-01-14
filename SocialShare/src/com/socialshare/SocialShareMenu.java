package com.socialshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialshare.util.SS_Constants;

public class SocialShareMenu extends Dialog implements android.view.View.OnClickListener {
	
	private transient LinearLayout mMenuView;
	private boolean isFirstTime = true;
	private boolean enableShare = false;

	public SocialShareMenu(Context context, boolean enableShare) {
		super(context, R.style.PopUpTheme);
		this.enableShare = enableShare;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_view);
		
		isFirstTime = true;
		
		findViewById(R.id.viwShareThought).setVisibility(enableShare ? View.VISIBLE : View.GONE);
		
		mMenuView = (LinearLayout) findViewById(R.id.viwSlideMenu);

		findViewById(R.id.viwTopBar).setOnClickListener(this);
		findViewById(R.id.viwSideBar).setOnClickListener(this);
		
		findViewById(R.id.txtShowGuide).setOnClickListener(this);
		findViewById(R.id.txtShowAbout).setOnClickListener(this);
		findViewById(R.id.txtShowSettings).setOnClickListener(this);
		
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
}
