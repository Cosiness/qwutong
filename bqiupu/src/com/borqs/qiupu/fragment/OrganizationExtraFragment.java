package com.borqs.qiupu.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borqs.common.view.AllAppsScreen.LoadDataActionListener;
import com.borqs.common.view.StreamRightPollListViewUi;
import com.borqs.common.view.pullRefreshGridView.PullToRefreshListView;
import com.borqs.qiupu.R;
import com.borqs.qiupu.util.CircleUtils;

import twitter4j.UserCircle;

abstract public class OrganizationExtraFragment extends BasicFragment implements LoadDataActionListener {
    private final static String TAG = OrganizationExtraFragment.class.getSimpleName();
    private Activity mActivity;

    private OrganizationExtraCallBack mCallBackListener;

    private UserCircle mCircle;

    private View mContentView;

    private ViewGroup workspace;
    private View mSearch;

    //	private StreamRightMemberListViewUi mMemberList;
//	private StreamRightEventListViewUi mEventList;
//    private StreamRightPollListViewUi mPollList;
//	private StreamRightAlbumViewUi mAlbumView;
//	private StreamRightCircleListViewUi mCirclelist;

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

        mSearch = mContentView.findViewById(R.id.bottom_search);
        mSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCallBackListener != null) {
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
    public void onSaveInstanceState(Bundle outState) {
        if (mCircle != null) {
            outState.putLong(CircleUtils.CIRCLE_ID, mCircle.circleid);
        }
        super.onSaveInstanceState(outState);
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
		} else {
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
//				createPollPage();
				// album
//				createAlbumPage(maxTab, fiveTab);
                createContentPage(mActivity, addContentListView(), mCircle.circleid);

            }else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_sub_formal){
//				maxTab = 1;
//				workspace.setScreenNumber(maxTab);
				// member list
//				createMemberPage(maxTab, firstTab);
//				mMemberList.loadDataOnMove();

				// event list
//				createEventPage(maxTab, secondTab);
				// poll list
//				createAlbumPage(maxTab, fiveTab);
                createContentPage(mActivity, addContentListView(), mCircle.circleid);
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
//				createAlbumPage(maxTab, fiveTab);
                createContentPage(mActivity, addContentListView(), mCircle.circleid);
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
	
//	private PullToRefreshGridView createContentGridView() {
//		PullToRefreshGridView pullGridView = (PullToRefreshGridView) LayoutInflater.from(mActivity).inflate(R.layout.pull_to_refresh_grid, null);
//		pullGridView.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.MATCH_PARENT));
//		return pullGridView;
//	}

	private PullToRefreshListView addContentListView() {
		PullToRefreshListView listView = createContentList();
        workspace.addView(listView);
		return listView;
	}

	protected void showSearchbtn(boolean isShow) {
		if(mSearch != null) {
			mSearch.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

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

    protected abstract void createContentPage(Activity context, PullToRefreshListView listView, long circleId);

    // sub class begin

    public static class Poll extends OrganizationExtraFragment {
        private StreamRightPollListViewUi mPollList;

        @Override
        public void onDestroy() {
            super.onDestroy();
//		if(mEventList != null) {
//			mEventList.onDestory();
//		}
            if (mPollList != null) {
                mPollList.onDestory();
            }
//		if(mCirclelist != null) {
//			mCirclelist.onDestory();
//		}
//		if(mMemberList != null) {
//			mMemberList.onDestory();
//		}
        }

        @Override
        public void loaddata(int index) {
            if (null != mPollList) {
                showSearchbtn(false);
                mPollList.loadDataOnMove();
            }
//            if(mCircle == null) {
//            }else {
//                if (null != mPollList) {
//                    showSearchbtn(false);
//                    mPollList.loadDataOnMove();
//                }
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
//            }
        }

        @Override
        protected void createContentPage(Activity activity, PullToRefreshListView listView, long circleId) {
            mPollList = new StreamRightPollListViewUi();
            mPollList.init(activity, listView, circleId);
        }
    }
    // sub class end
}
