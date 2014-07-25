package com.borqs.qiupu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borqs.common.view.AllAppsScreen;
import com.borqs.common.view.AllAppsScreen.LoadDataActionListener;
import com.borqs.common.view.AllAppsScreen.TitleActionListener;
import com.borqs.common.view.PageIndicatorLineStyleView;
import com.borqs.common.view.StreamRightPollListViewUi;
import com.borqs.common.view.pullRefreshGridView.PullToRefreshGridView;
import com.borqs.common.view.pullRefreshGridView.PullToRefreshListView;
import com.borqs.qiupu.R;
import com.borqs.qiupu.util.CircleUtils;

import twitter4j.UserCircle;

public class OrganizationExtraFragment extends BasicFragment implements LoadDataActionListener {
	private final static String TAG = "StreamRightFlipperFragment";
	private Activity mActivity;

	private OrganizationExtraCallBack mCallBackListener;

	private UserCircle mCircle;

	private View mContentView;

	private ViewGroup workspace;
//	private LinearLayout mTabTitle;
	private ImageView mSearch;
	private final int firstTab = 0;
	private final int secondTab = firstTab + 1;
	private final int thirdTab = secondTab + 1;
	private final int fourTab = thirdTab + 1;
	private final int fiveTab = fourTab + 1;

//	private StreamRightMemberListViewUi mMemberList;
//	private StreamRightEventListViewUi mEventList;
	private StreamRightPollListViewUi mPollList;
//	private StreamRightAlbumViewUi mAlbumView;
//	private StreamRightCircleListViewUi mCirclelist;
	
	private int mCurrentIndex = firstTab;

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach");
		super.onAttach(activity);
		mActivity = activity;

		if (mActivity instanceof OrganizationExtraCallBack) {
			mCallBackListener = (OrganizationExtraCallBack) activity;
			mCallBackListener.getStreamRightFlipperFragment(this);
			mCircle = mCallBackListener.getCircleInfo();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		parserSavedState(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");

		mContentView = inflater.inflate(R.layout.fragment_organization_extra,
				container, false);

		workspace = (ViewGroup) mContentView.findViewById(R.id.workspace);
//		PageIndicatorLineStyleView pv = (PageIndicatorLineStyleView) mContentView
//				.findViewById(R.id.page_line_indicator);
		
//		mTabTitle = (LinearLayout) mContentView.findViewById(R.id.tab_title);
//		workspace.setPageIndicatorLineStyleView(pv);
//		workspace.setTitleListener(this);
//		workspace.setloadDataListener(this);
		
		mSearch = (ImageView) mContentView.findViewById(R.id.bottom_search);
		mSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mCallBackListener != null) {
					mCallBackListener.startSearch();
				}
			}
		});
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				initUI();
				
			}
		}, 500);

		return mContentView;
	}

	private void parserSavedState(Bundle savedInstanceState) {
		if (null != savedInstanceState) {
			if (mCircle == null) {
				mCircle = new UserCircle();
			}

			mCircle.circleid = savedInstanceState
					.getLong(CircleUtils.CIRCLE_ID);
			mCircle.uid = savedInstanceState.getLong(CircleUtils.CIRCLE_ID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
//        setisCurrentScreen(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mCircle != null) {
			outState.putLong(CircleUtils.CIRCLE_ID, mCircle.circleid);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		if(mEventList != null) {
//			mEventList.onDestory();
//		}
		if(mPollList != null) {
			mPollList.onDestory();
		}
//		if(mCirclelist != null) {
//			mCirclelist.onDestory();
//		}
//		if(mMemberList != null) {
//			mMemberList.onDestory();
//		}
	}

	private void initUI() {
//		int mActivity = 0;
//		mTabTitle.removeAllViews();
		if(mCircle == null) {
//			maxTab = 2;
//			workspace.setScreenNumber(maxTab);
//			mTabTitle.addView(createTabTitleView(R.string.organization_circle_label, maxTab, firstTab));
			TextView hotListView = new TextView(mActivity);
			hotListView.setText("Not a valid circle.");
//			ViewGroup vg0 = (ViewGroup) workspace.getChildAt(firstTab);
//			vg0.addView(hotListView);
            workspace.addView(hotListView);
			
//			mTabTitle.addView(createTabTitleView(R.string.user_circles, maxTab, secondTab));
//			TextView recommendlist = new TextView(mActivity);
//			recommendlist.setText("bbbbb");
//			ViewGroup vg1 = (ViewGroup) workspace.getChildAt(secondTab);
//			vg1.addView(recommendlist);
		}else {
//			maxTab = 1;
			if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_top_formal) {
//				workspace.setScreenNumber(maxTab);
				
				// circle list
//				mCirclelist = new StreamRightCircleListViewUi();
//				mTabTitle.addView(createTabTitleView(R.string.user_circles, maxTab, firstTab));

//				PullToRefreshExpandableListView listview = (PullToRefreshExpandableListView) LayoutInflater.from(mActivity).inflate(R.layout.pull_to_refresh_expandable_list, null);
//				listview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//				mCirclelist.init(mActivity, listview, mCircle);
//				ViewGroup vg0 = (ViewGroup) workspace.getChildAt(firstTab);
//				vg0.addView(listview);
				
				// member list
//				createMemberPage(maxTab, secondTab);
				// event list
//				createEventPage(maxTab, thirdTab);
				// poll list
				createPollpage(1, fourTab);
				// album
//				createAlbumPage(maxTab, fiveTab);
				
			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_sub_formal){
//				maxTab = 1;
//				workspace.setScreenNumber(maxTab);
				// member list
//				createMemberPage(maxTab, firstTab);
//				mMemberList.loadDataOnMove();

				// event list
//				createEventPage(maxTab, secondTab);
				// poll list
				createPollpage(1, thirdTab);
				// album 
//				createAlbumPage(maxTab, fourTab);
				
			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_free) {
//				maxTab = 1;
//				workspace.setScreenNumber(maxTab);
				// member list
//				createMemberPage(maxTab, firstTab);
//				mMemberList.loadDataOnMove();
				
				// event list
//				createEventPage(maxTab, secondTab);
				// poll list
				createPollpage(1, thirdTab);
				// album 
//				createAlbumPage(maxTab, fourTab);
			}else {
				Log.d(TAG, "initUI: have no circle type. don't know how to create view");
			}
		}
	}
	
//	private void createMemberPage(int maxTab, int tab) {
//		mMemberList = new StreamRightMemberListViewUi();
//		mTabTitle.addView(createTabTitleView(R.string.circle_member_label, maxTab, tab));
//		mMemberList.init(mActivity, addContentListView(tab), mCircle);
//	}
	
//	private void createEventPage(int maxTab, int tab) {
//		mEventList = new StreamRightEventListViewUi();
//		mTabTitle.addView(createTabTitleView(R.string.event, maxTab, tab));
//		mEventList.init(mActivity, addContentListView(tab), mCircle.circleid);
//	}
	
	private void createPollpage(int maxTab, int tab) {
		mPollList = new StreamRightPollListViewUi();
//		mTabTitle.addView(createTabTitleView(R.string.poll, maxTab, tab));
		mPollList.init(mActivity, addContentListView(tab), mCircle.circleid);
	}
	
//	private void createAlbumPage(int maxTab, int tab) {
//		mAlbumView = new StreamRightAlbumViewUi();
//		mTabTitle.addView(createTabTitleView(R.string.home_album, maxTab, tab));
//		PullToRefreshGridView albumGridView = createContentGridView();
//		ViewGroup vg4 = (ViewGroup) workspace.getChildAt(tab);
//		vg4.addView(albumGridView);
//		mAlbumView.init(mActivity, albumGridView, mCircle);
//	}

	private PullToRefreshListView createContentList() {
		PullToRefreshListView listview = (PullToRefreshListView) LayoutInflater.from(mActivity).inflate(R.layout.default_refreshable_listview, null);
		listview.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.MATCH_PARENT));
		return listview;
	}
	
	private PullToRefreshGridView createContentGridView() {
		PullToRefreshGridView pullGridView = (PullToRefreshGridView) LayoutInflater.from(mActivity).inflate(R.layout.pull_to_refresh_grid, null);
		pullGridView.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.MATCH_PARENT));
		return pullGridView;
	}
	
//	private TextView createTabTitleView(final int titleres, final int tabcount, final int mappingTab) {
//		Log.d(TAG, "creattabtitleView: " + tabcount);
//		TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.second_tab_textview, null);
//		tv.setTextColor(getResources().getColor(R.color.lightgrey));
//		WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
//
//		int width = wm.getDefaultDisplay().getWidth();
//		Log.d(TAG, "width: " + width/tabcount);
//		tv.setLayoutParams(new LayoutParams(width/tabcount, LayoutParams.MATCH_PARENT));
//		tv.setText(titleres);
//		tv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				workspace.snapToScreenWithVelocityX(mappingTab, 0);
//			}
//		});
//		return tv;
//	}
	
	private PullToRefreshListView addContentListView(int tab) {
		PullToRefreshListView eventListView = createContentList();
//		ViewGroup vg = (ViewGroup) workspace.getChildAt(tab);
//		vg.addView(eventListView);
        workspace.addView(eventListView);
		return eventListView;
	}

	private void showSearchbtn(boolean isShow) {
		if(mSearch != null) {
			mSearch.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}
	@Override
	public void loaddata(int index) {
		if(mCircle == null) {
		}else {
            if (null != mPollList) {
                showSearchbtn(false);
                mPollList.loadDataOnMove();
            }
//			if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_top_formal) {
//				if(index == firstTab && mMemberList != null) {
//					showSearchbtn(true);
//					mMemberList.loadDataOnMove();
//				}else if(index == secondTab && mEventList != null) {
//					showSearchbtn(true);
//					mEventList.loadDataOnMove();
//				}else if(index == thirdTab && mPollList != null) {
//					showSearchbtn(false);
//					mPollList.loadDataOnMove();
//				}else if(index == fourTab && mAlbumView != null) {
//					showSearchbtn(false);
//					mAlbumView.loadDataOnMove();
//				}
//			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_sub_formal){
//				if(index == firstTab && mEventList != null) {
//					showSearchbtn(true);
//					mEventList.loadDataOnMove();
//				}else if(index == secondTab && mPollList != null) {
//					showSearchbtn(false);
//					mPollList.loadDataOnMove();
//				}else if(index == fourTab && mAlbumView != null) {
//					showSearchbtn(false);
//					mAlbumView.loadDataOnMove();
//				}
//
//			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_free) {
//				if(index == firstTab && mEventList != null) {
//					showSearchbtn(true);
//					mEventList.loadDataOnMove();
//				}else if(index == secondTab && mPollList != null) {
//					showSearchbtn(false);
//					mPollList.loadDataOnMove();
//				}else if(index == fourTab && mAlbumView != null) {
//					showSearchbtn(false);
//					mAlbumView.loadDataOnMove();
//				}
//			}else {
//				Log.d(TAG, "initUI: have no circle type. don't know how to create view");
//			}
		}
	}

//	@Override
//	public void setPageTitle(int index) {
//		if(mCallBackListener != null) {
//			mCallBackListener.hidSearch();
//		}
//
//		mCurrentIndex = index;
//	}

	public void refreshUI(UserCircle circle) {
		// if same formal, no need refresh ui 
		if(mCircle.mGroup != null && circle.mGroup != null && mCircle.mGroup.formal == circle.mGroup.formal) {
			mCircle = circle;
		}else {
			mCircle = circle;
			initUI();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
//		if(mAlbumView != null) {
//			mAlbumView.onConfigurationChanged(newConfig);
//		}
	}

//	public void setisCurrentScreen(boolean b) {
//		if(workspace != null) {
//			workspace.setIsCurrentScreen(b);
//		}
//
//	}

	public void doSearch(String newText) {
		if(mCircle == null) {
		}else {
//			if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_top_formal) {
//				if(mCurrentIndex == firstTab && mCirclelist != null) {
//					mCirclelist.doSearch(newText);
//				}else if(mCurrentIndex == secondTab && mMemberList != null) {
//					mMemberList.doSearch(newText);
//				}
//			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_sub_formal){
//				if(mCurrentIndex == firstTab && mMemberList != null) {
//					mMemberList.doSearch(newText);
//				}
//
//			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_free) {
//				if(mCurrentIndex == firstTab && mMemberList != null) {
//					mMemberList.doSearch(newText);
//				}
//			}else {
//				Log.d(TAG, "initUI: have no circle type. don't know how to create view");
//			}
		}
	}

//	public int getCurrentIndex() {
//		if(workspace != null) {
//			return workspace.getCurrentScreen();
//		}
//		return -1;
//	}
}
