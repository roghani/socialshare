package com.socialshare;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.socialshare.customwidgets.LoadMoreListView;
import com.socialshare.customwidgets.LoadMoreListView.OnLoadMoreListener;
import com.socialshare.datatypes.DT_NewsHeadlines;
import com.socialshare.http.AsyncRequest;
import com.socialshare.http.SS_HttpOperations;
import com.socialshare.parser.json.Parse_NewsHeadlines;
import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Log;
import com.socialshare.util.SS_Preference;

public class HomeActivity extends Activity {
	private LoadMoreListView mNewsList;
	List<DT_NewsHeadlines> mListNewsHeadlines = new ArrayList<DT_NewsHeadlines>();
	private int mPageNumber = -1;
	private transient Spinner mSpinMainFeeds;
	private String[] mMainFeedsList;
	private ProgressBar mProgress;
	private Button mOpLeftButton;
	private Button mOpRightButton;
	private transient SocialShareMenu mMenuViewAlert;

	
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
		
	    //TODO : move this line of code to Splash Screen activity after testing
	    SS_Preference.setContext(getApplicationContext());
	    
	    
	    setContentView(R.layout.latest_newsheadlines);
	    
	    ((TextView) findViewById(R.id.txtTitleText)).setText("Home");
	    
		mProgress = (ProgressBar) findViewById(R.id.prgTitleProgress);
		mOpLeftButton = (Button) findViewById(R.id.btnTitleLeftOp);
		mOpRightButton = (Button) findViewById(R.id.btnTitleRightOp);
	    mSpinMainFeeds = (Spinner) findViewById(R.id.spnMainFeeds);
	    mNewsList = (LoadMoreListView) findViewById(R.id.lstResultList);
	    
	    mNewsList.setAdapter(mListAdapter);
	    
	    mMainFeedsList = getResources().getStringArray(R.array.sListMainFeedsURL);
	    
	    /** Initialize Components **/
	    fnSetupSpinners();
	    
	    mSpinMainFeeds.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mPageNumber = -1;
				String value = mMainFeedsList[arg2];
				SS_Preference.setPreference(SS_Preference.KEY_MAIN_FEED, value);
				mHTTP_FetchNews.execute();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	    
	    mNewsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO: Display Detailed View
				Intent detailedActivity = new Intent(HomeActivity.this, NewsDetailedActivity.class);
				detailedActivity.putExtra("E_TITLE", mListNewsHeadlines.get(position).title);
				detailedActivity.putExtra("E_PUBDATE", mListNewsHeadlines.get(position).pubDate);
				detailedActivity.putExtra("E_LINK", mListNewsHeadlines.get(position).link);
				startActivity(detailedActivity);
			}
		});
	    
		mOpLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
					mMenuViewAlert = new SocialShareMenu(HomeActivity.this, false);
					mMenuViewAlert.show();
				} else if (mMenuViewAlert != null && mMenuViewAlert.isShowing()) {
					mMenuViewAlert.dismiss();
				}
			}
		});
	    
	    mOpRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPageNumber = -1;
				mHTTP_FetchNews.execute();
			}
		});
	    
		mNewsList.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mHTTP_FetchNews.execute();
			}
		});
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
			mMenuViewAlert = new SocialShareMenu(HomeActivity.this, false);
			mMenuViewAlert.show();
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
		SS_Constants.isShowingMyThoughts = false;
	}
	
	private void fnSetupSpinners() {
    	try {
    		/** Main Feeds Spinner **/
    		{
				String value = SS_Preference.getPreference(SS_Preference.KEY_MAIN_FEED);
				int i;
				for (i = 0; i < mMainFeedsList.length; i++) {
					if (value.equalsIgnoreCase(mMainFeedsList[i])) {
						mSpinMainFeeds.setSelection(i);
						break;
					}
				}
				
				if (i >= mMainFeedsList.length) {
					mSpinMainFeeds.setSelection(0);
				}
    		}
		} catch (Exception e) {
			SS_Log.printStackTrace(e);
		}
	}
	
    BaseAdapter mListAdapter = new BaseAdapter() {
    	class ItemHolder {
			TextView mTitle;
			TextView mPubDate;
		}
    	@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.latest_newsheadlines_item, null);
				holder = new ItemHolder();
				holder.mTitle = (TextView) convertView.findViewById(R.id.txtTitle);
				holder.mPubDate = (TextView) convertView.findViewById(R.id.txtPubDate);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}
			
			DT_NewsHeadlines mItem = null;

			synchronized (mListNewsHeadlines) {
				mItem = mListNewsHeadlines.get(position);
			}

			if (mItem != null) {
				convertView.setVisibility(View.VISIBLE);
				holder.mTitle.setText(mItem.title);
				holder.mPubDate.setText(mItem.pubDate);
			}
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			return mListNewsHeadlines.get(position);
		}
		
		@Override
		public int getCount() {
			return mListNewsHeadlines.size();
		}
	};
	
	private transient AsyncRequest<JSONObject> mHTTP_FetchNews = new AsyncRequest<JSONObject>() {
		@Override
		public JSONObject doRequest() throws Exception {
			showProgressBar();
			String feedURL = SS_Preference.getPreference(SS_Preference.KEY_MAIN_FEED);
			return new SS_HttpOperations().getNewsData(feedURL, ++mPageNumber);
		}
		
		@Override
		public void onResult(final JSONObject result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mPageNumber == 0) {
						mListNewsHeadlines.clear();
					}
					mListNewsHeadlines.addAll(Parse_NewsHeadlines.parse(result));
					mListAdapter.notifyDataSetChanged();
				}
			});
			hideProgressBar();
		}
		
		@Override
		public void onException(Exception e) {
			--mPageNumber;
			hideProgressBar();
			SS_Log.printStackTrace(e);
		}
	};

	public void showProgressBar() {
		if (mProgress.getVisibility() != View.VISIBLE) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mProgress.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	public void hideProgressBar() {
		if (mProgress.getVisibility() != View.INVISIBLE) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mNewsList.onLoadMoreComplete();
					mProgress.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
}
