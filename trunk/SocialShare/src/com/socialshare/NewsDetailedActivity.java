package com.socialshare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialshare.util.SS_Constants;

public class NewsDetailedActivity extends Activity {
	private TextView mTitle;
	private TextView mPubDate;
	private WebView mWebNewsDetailed;
	private String mNewsTitle;
	private String mNewsPubDate;
	private String mNewsLink;
	private ProgressBar mProgress;
	private Button mOpLeftButton;
	private Button mOpRightButton;
	private transient SocialShareMenu mMenuViewAlert;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    mNewsTitle = getIntent().getStringExtra("E_TITLE");
	    mNewsPubDate = getIntent().getStringExtra("E_PUBDATE");
	    mNewsLink = getIntent().getStringExtra("E_LINK");
	    
	    setContentView(R.layout.news_detailedview);
	    
	    ((TextView) findViewById(R.id.txtTitleText)).setText("News Detail");
	    
		mProgress = (ProgressBar) findViewById(R.id.prgTitleProgress);
		mOpLeftButton = (Button) findViewById(R.id.btnTitleLeftOp);
		mOpRightButton = (Button) findViewById(R.id.btnTitleRightOp);
		
	    mTitle = (TextView) findViewById(R.id.txtTitle);
	    mPubDate = (TextView) findViewById(R.id.txtPubDate);
	    mWebNewsDetailed = (WebView) findViewById(R.id.webNewsDetail);
	    
	    mTitle.setText(mNewsTitle);
	    mPubDate.setText(mNewsPubDate);
	    
	    mOpLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
					mMenuViewAlert = new SocialShareMenu(NewsDetailedActivity.this, true, mNewsLink);
					mMenuViewAlert.show();
				} else if (mMenuViewAlert != null && mMenuViewAlert.isShowing()) {
					mMenuViewAlert.dismiss();
				}
			}
		});
	    
	    mOpRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebNewsDetailed.loadUrl(mNewsLink);
			}
		});
	    
	    mWebNewsDetailed.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				hideProgressBar();
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showProgressBar();
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
	    });
	    
	    mWebNewsDetailed.loadUrl(mNewsLink);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mMenuViewAlert == null || !mMenuViewAlert.isShowing()) {
			mMenuViewAlert = new SocialShareMenu(NewsDetailedActivity.this, true, mNewsLink);
			mMenuViewAlert.show();
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SS_Constants.CurrentActiveContext = this;
	}
	
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
					mProgress.setVisibility(View.INVISIBLE);
				}
			});
		}
	}

}
