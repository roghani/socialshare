package com.socialshare.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class WhatsNewPager extends ViewPager {
	
	public WhatsNewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return super.onInterceptTouchEvent(arg0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return super.onTouchEvent(arg0);
	}
	
	public void setPagingEnabled(boolean enabled) {
	}
	
	public ImageView getCurrentImageView() {
		return getImageView(getCurrentItem());
	}
	
	public ImageView getImageView(int pos) {
		PagerAdapter adapter = getAdapter();
		if (adapter instanceof ViewPagerAdapter) {
			ViewPagerAdapter imgAdapter = (ViewPagerAdapter) adapter;
			View view = imgAdapter.getItem(pos);
			if (view instanceof ImageView) {
				return (ImageView)view;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
