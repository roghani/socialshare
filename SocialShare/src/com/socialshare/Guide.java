package com.socialshare;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Preference;
import com.socialshare.util.ViewPagerAdapter;
import com.socialshare.util.WhatsNewPager;

public class Guide extends Activity {
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
	    SS_Preference.setPreference(SS_Preference.KEY_GUIDE_DISPLAYED, "1");
		
	    setContentView(R.layout.whats_new);
	    
	    WhatsNewPager pager = (WhatsNewPager) findViewById(R.id.whatsNewPager);
	    final ImageView bottomSheets = (ImageView) findViewById(R.id.imgBottomLabel);
	    findViewById(R.id.btnTitleRightOp).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				bottomSheets.setImageResource(arrBottomSheets[arg0]);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		ViewPagerAdapter mImgAdapter = new ViewPagerAdapter(this);
		mImgAdapter.sImageResourceIDs.addAll(createImageResourceList());
		pager.setAdapter(mImgAdapter);
		pager.setPageMargin(0);
	    
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
	}
	
	private ArrayList<Integer> createImageResourceList() {
		ArrayList<Integer> resImageList = new ArrayList<Integer>();
		resImageList.add(R.drawable.bar_bottom_1);
		resImageList.add(R.drawable.bar_bottom_1);
		resImageList.add(R.drawable.bar_bottom_1);
		return resImageList;
	}
	
	private int []arrBottomSheets = {
										R.drawable.bar_bottom_1,
										R.drawable.bar_bottom_1,
										R.drawable.bar_bottom_1
									};

}
