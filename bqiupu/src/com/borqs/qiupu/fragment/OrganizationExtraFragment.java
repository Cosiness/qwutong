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
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.borqs.common.view.AllAppsScreen.LoadDataActionListener;
import com.borqs.common.view.StreamRightAlbumViewUi;
import com.borqs.common.view.StreamRightCircleListViewUi;
import com.borqs.common.view.StreamRightEventListViewUi;
import com.borqs.common.view.StreamRightMemberListViewUi;
import com.borqs.common.view.StreamRightPollListViewUi;
import com.borqs.common.view.pullRefreshGridView.PullToRefreshExpandableListView;
import com.borqs.common.view.pullRefreshGridView.PullToRefreshGridView;
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
		if(mCircle == null) {
			TextView hotListView = new TextView(mActivity);
			hotListView.setText("Not a valid circle.");
            workspace.addView(hotListView);
			
		} else {
			if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_top_formal) {
                createContentPage(mActivity, mCircle);
            } else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_sub_formal){
                createContentPage(mActivity, mCircle);
			}else if(mCircle.mGroup != null && mCircle.mGroup.formal == UserCircle.circle_free) {
                createContentPage(mActivity, mCircle);
			}else {
				Log.d(TAG, "initUI: have no circle type. don't know how to create view");
			}
		}
	}

    private void injectContentView(View newView) {
        workspace.removeAllViews();
        workspace.addView(newView);
    }

	protected PullToRefreshListView ensureContentListView() {
        PullToRefreshListView listView = (PullToRefreshListView) LayoutInflater.from(mActivity).inflate(R.layout.default_refreshable_listview, null);
        listView.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.MATCH_PARENT));
        injectContentView(listView);
		return listView;
	}

    protected PullToRefreshGridView ensureContentGridView() {
        PullToRefreshGridView pullGridView = (PullToRefreshGridView) LayoutInflater.from(mActivity).inflate(R.layout.pull_to_refresh_grid, null);
		pullGridView.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.MATCH_PARENT));
        injectContentView(pullGridView);
        return pullGridView;
    }

    protected PullToRefreshExpandableListView ensureContentExpandListView() {
        PullToRefreshExpandableListView listView = (PullToRefreshExpandableListView) LayoutInflater.from(mActivity).inflate(R.layout.pull_to_refresh_expandable_list, null);
        listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        injectContentView(listView);
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

	public void doSearch(String newText) {
	}

    protected abstract void createContentPage(Activity context, UserCircle circle);

    // sub class begin
    public static class Member extends OrganizationExtraFragment {
        private StreamRightMemberListViewUi mMemberList;
        @Override
        public void onDestroy() {
            super.onDestroy();
            if(mMemberList != null) {
                mMemberList.onDestory();
            }
        }

        @Override
        public void doSearch(String newText) {
            if (mMemberList != null) {
                mMemberList.doSearch(newText);
            }
        }

        @Override
        public void loaddata(int index) {
            // why it was test mCircle.mGroup.formal == UserCircle.circle_top_formal, bug or spec?
            if (null != mMemberList) {
                showSearchbtn(true);
                mMemberList.loadDataOnMove();
            }
        }

        @Override
        protected void createContentPage(Activity activity, UserCircle circle) {
            mMemberList = new StreamRightMemberListViewUi();
            mMemberList.init(activity, ensureContentListView(), circle);
        }
    }

    public static class Album extends OrganizationExtraFragment {
        private StreamRightAlbumViewUi mAlbumView;

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

		if(mAlbumView != null) {
			mAlbumView.onConfigurationChanged(newConfig);
		}
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void loaddata(int index) {
            if (null != mAlbumView) {
                showSearchbtn(false);
                mAlbumView.loadDataOnMove();
            }
        }

        @Override
        protected void createContentPage(Activity activity, UserCircle circle) {
            mAlbumView = new StreamRightAlbumViewUi();
            mAlbumView.init(activity, ensureContentGridView(), circle);
        }
    }

    public static class Circle extends OrganizationExtraFragment {
        private StreamRightCircleListViewUi mCirclelist;

        public void doSearch(String newText) {
            if (mCirclelist != null) {
                mCirclelist.doSearch(newText);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
		if(mCirclelist != null) {
			mCirclelist.onDestory();
		}
        }

        @Override
        public void loaddata(int index) {
        }

        @Override
        protected void createContentPage(Activity activity, UserCircle circle) {
            mCirclelist = new StreamRightCircleListViewUi();
            mCirclelist.init(activity, ensureContentExpandListView(), circle);
        }
    }

    public static class Event extends OrganizationExtraFragment {
        private StreamRightEventListViewUi mEventList;

        @Override
        public void onDestroy() {
            super.onDestroy();
            if(mEventList != null) {
                mEventList.onDestory();
            }
        }

        @Override
        public void loaddata(int index) {
            if (null != mEventList) {
                showSearchbtn(true);
                mEventList.loadDataOnMove();
            }
        }

        @Override
        protected void createContentPage(Activity activity, UserCircle circle) {
            mEventList = new StreamRightEventListViewUi();
            mEventList.init(activity, ensureContentListView(), circle.circleid);
        }
    }

    public static class Poll extends OrganizationExtraFragment {
        private StreamRightPollListViewUi mPollList;

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mPollList != null) {
                mPollList.onDestory();
            }
        }

        @Override
        public void loaddata(int index) {
            if (null != mPollList) {
                showSearchbtn(false);
                mPollList.loadDataOnMove();
            }
        }

        @Override
        protected void createContentPage(Activity activity, UserCircle circle) {
            mPollList = new StreamRightPollListViewUi();
            mPollList.init(activity, ensureContentListView(), circle.circleid);
        }
    }
    // sub class end
}
