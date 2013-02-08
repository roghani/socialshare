package com.socialshare;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialshare.customwidgets.LoadMoreListView;
import com.socialshare.customwidgets.LoadMoreListView.OnLoadMoreListener;
import com.socialshare.datatypes.DT_MyPosts;
import com.socialshare.http.AsyncRequest;
import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_DBHelper;
import com.socialshare.util.SS_Log;

public class MyThoughts extends Activity {
	private LoadMoreListView mNewsList;
	List<DT_MyPosts> mListNewsHeadlines = new ArrayList<DT_MyPosts>();
	private ProgressBar mProgress;
	private Button mOpLeftButton;
	private Button mOpRightButton;
	private transient SocialShareMenu mMenuViewAlert;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.my_thoughts);
	    
	    ((TextView) findViewById(R.id.txtTitleText)).setText("My Thoughts");
	    
		mProgress = (ProgressBar) findViewById(R.id.prgTitleProgress);
		mOpLeftButton = (Button) findViewById(R.id.btnTitleLeftOp);
		mOpRightButton = (Button) findViewById(R.id.btnTitleRightOp);
	    mNewsList = (LoadMoreListView) findViewById(R.id.lstResultList);
	    
	    mNewsList.setAdapter(mListAdapter);
	    
	    mNewsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO: Display Detailed View
			}
		});
	    
		mOpLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
					mMenuViewAlert = new SocialShareMenu(MyThoughts.this, false);
					mMenuViewAlert.show();
				} else if (mMenuViewAlert != null && mMenuViewAlert.isShowing()) {
					mMenuViewAlert.dismiss();
				}
			}
		});
	    
	    mOpRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHTTP_FetchNews.execute();
			}
		});
	    
		mNewsList.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
//				mHTTP_FetchNews.execute();
			}
		});
		mHTTP_FetchNews.execute();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
			mMenuViewAlert = new SocialShareMenu(MyThoughts.this, false);
			mMenuViewAlert.show();
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
		SS_Constants.isShowingMyThoughts = true;
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
			
			DT_MyPosts mItem = null;

			synchronized (mListNewsHeadlines) {
				mItem = mListNewsHeadlines.get(position);
			}

			if (mItem != null) {
				convertView.setVisibility(View.VISIBLE);
				holder.mTitle.setText(mItem.content);
				holder.mPubDate.setText(mItem.link);
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
	
	private transient AsyncRequest<ArrayList<DT_MyPosts>> mHTTP_FetchNews = new AsyncRequest<ArrayList<DT_MyPosts>>() {
		@Override
		public ArrayList<DT_MyPosts> doRequest() throws Exception {
			showProgressBar();
			return new SS_DBHelper(getApplicationContext()).selectMyPosts();
		}
		
		@Override
		public void onResult(final ArrayList<DT_MyPosts> result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mListNewsHeadlines.clear();
					mListNewsHeadlines.addAll(result);
					mListAdapter.notifyDataSetChanged();
				}
			});
			hideProgressBar();
		}
		
		@Override
		public void onException(Exception e) {
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
