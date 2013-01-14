package com.socialshare.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ViewPagerAdapter extends PagerAdapter {

	public ArrayList<Integer> sImageResourceIDs = new ArrayList<Integer>();
	private Context mContext;
	private HashMap<Integer, View> viewList = new HashMap<Integer, View>();

	public ViewPagerAdapter(Context mContext) {
		this.mContext = mContext;
		viewList.clear();
	}

	@Override
	public int getCount() {
		return sImageResourceIDs.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewGroup) container).removeView((View) object);
		viewList.remove(position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView mWhatsNewImageView = new ImageView(mContext);
		LayoutParams params = new LayoutParams();
		((ImageView) mWhatsNewImageView).setLayoutParams(params);
		((ImageView) mWhatsNewImageView).setScaleType(ScaleType.FIT_XY);
		mWhatsNewImageView.setImageResource(sImageResourceIDs.get(position));
		viewList.put(position, mWhatsNewImageView);
		container.addView(mWhatsNewImageView, 0);
		return mWhatsNewImageView;
	}

	public View getItem(int position) {
		return viewList.get(position);
	}
}
