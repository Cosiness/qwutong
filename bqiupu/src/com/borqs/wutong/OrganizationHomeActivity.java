package com.borqs.wutong;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.borqs.account.service.AccountServiceUtils;
import com.borqs.common.SelectionItem;
import com.borqs.common.listener.ActivityFinishListner;
import com.borqs.common.util.DialogUtils;
import com.borqs.common.util.IntentUtil;
import com.borqs.common.view.CorpusSelectionItemView;
import com.borqs.information.util.InformationUtils;
import com.borqs.qiupu.QiupuApplication;
import com.borqs.qiupu.QiupuConfig;
import com.borqs.qiupu.R;
import com.borqs.qiupu.cache.QiupuHelper;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.db.QiupuORM.CircleCirclesColumns;
import com.borqs.qiupu.fragment.OrganizationExtraCallBack;
import com.borqs.qiupu.fragment.OrganizationExtraFragment;
import com.borqs.qiupu.fragment.StreamListFragment;
import com.borqs.qiupu.fragment.StreamRightFlipperFragment;
import com.borqs.qiupu.ui.BasicActivity;
import com.borqs.qiupu.ui.circle.quickAction.BottomMoreQuickAction;
import com.borqs.qiupu.util.CircleUtils;
import com.borqs.qiupu.util.ToastUtil;
import com.borqs.wutong.utils.CacheHelper;
import com.borqs.wutong.utils.ServiceHelper;

import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.PublicCircleRequestUser;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.UserCircle;

/**
 * Created with IntelliJ IDEA.
 * User: yangfeng
 * Date: 13-1-16
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationHomeActivity extends BaseResideMenuActivity implements
        StreamListFragment.StreamListFragmentCallBack,
        StreamListFragment.StreamActionInterface,
        OrganizationExtraCallBack,
        HomePickerActivity.PickerInterface, ActivityFinishListner {

    private static final String TAG = "OrganizationHomeActivity";

    private static final boolean FORCE_SHOW_DROPDOWN = false;

    private UserCircle mCircle;

    StreamListFragment.MetaData mFragmentData = new StreamListFragment.MetaData();
    public static final int in_member_selectcode = 5555;

    private BottomMoreQuickAction mMoreDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
//        enableLeftNav(true);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.wutong_organization_home_activity);

        QiupuHelper.registerFinishListner(getClass().getName(), this);
        parseActivityIntent(getIntent());

        CacheHelper.checkExpandCirCle();

        setUpMenu(StreamListFragment.class);
//        changeFragment();
        overrideRightActionBtn(R.drawable.home_screen_menu_people_icon_default, editProfileClick);

        mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mHandler.obtainMessage(GET_PUBLIC_CIRCLE_INFO).sendToTarget();
				
			}
		}, 1000);
    }

    @Override
    protected void createHandler() {
        mHandler = new MainHandler();
    }

    @Override
    protected void loadSearch()
    {
        gotoSearchActivity();
    }

    private void parseActivityIntent(Intent intent) {
        String url = getIntentURL(intent);
        if (TextUtils.isEmpty(url)) {
            Bundle bundle = intent.getExtras();
            String requestName = bundle.getString(CircleUtils.CIRCLE_NAME);
            mFragmentData.mCircleId = bundle.getLong(CircleUtils.CIRCLE_ID, -1);
            mFragmentData.mFragmentTitle = TextUtils.isEmpty(requestName) ?
                    "" : requestName;
            mFragmentData.mFromHome = QiupuConfig.FROM_HOME;
        }

        mCircle = CacheHelper.queryOneCircleWithGroup(mFragmentData.mCircleId);
        if (mCircle == null){
            mCircle = new UserCircle();
            mCircle.circleid = mFragmentData.mCircleId;
            mCircle.name = mFragmentData.mFragmentTitle;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    };

    @Override
    protected void loadRefresh() {
//        if(mHomeFragment != null) {
//        	mHomeFragment.loadRefresh();
//        }
    }

    @Override
    public StreamListFragment.MetaData getFragmentMetaData(int index) {
        return mFragmentData;
    }

    @Override
    public String getSerializeFilePath() {
        return QiupuHelper.circle + mFragmentData.mCircleId + mFragmentData.mFromHome;
    }

    @Override
    protected void onDestroy() {
    	QiupuHelper.unregisterFinishListner(getClass().getName());
    	InformationUtils.unregisterNotificationListener(getClass().getName());
        super.onDestroy();
    }

    View.OnClickListener editProfileClick = new View.OnClickListener() {
        public void onClick(View v) {
        	ArrayList<SelectionItem> items = new ArrayList<SelectionItem>();
        	items.add(new SelectionItem(String.valueOf(mCircle.circleid), mCircle.name));
            Cursor pOrgazitaionCircles = CacheHelper.queryInCircleCircles(mCircle.circleid);
            if(pOrgazitaionCircles != null) {
            	if(pOrgazitaionCircles.getCount() > 0) {
            		if(pOrgazitaionCircles.moveToFirst()) {
            			do {
            				items.add(new SelectionItem(String.valueOf(pOrgazitaionCircles.getLong(pOrgazitaionCircles.getColumnIndex(CircleCirclesColumns.CIRCLEID))), 
            						pOrgazitaionCircles.getString(pOrgazitaionCircles.getColumnIndex(CircleCirclesColumns.CIRCLE_NAME))));
						} while (pOrgazitaionCircles.moveToNext());
            		}
            	}
            	pOrgazitaionCircles.close();
            	pOrgazitaionCircles = null;
            }else {
            	Log.d(TAG, "have no child Circles");
            }
            showCorpusSelectionDialog(items);
        }
    };

    protected void showCorpusSelectionDialog(ArrayList<SelectionItem> items) {
        if(mRightActionBtn != null) {
            int location[] = new int[2];
            mRightActionBtn.getLocationInWindow(location);
            int x = location[0];
            int y = getResources().getDimensionPixelSize(R.dimen.title_bar_height);

            DialogUtils.showCorpusSelectionDialog(this, x, y, items, actionListItemClickListener);
        }
    }

    AdapterView.OnItemClickListener actionListItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	if(CorpusSelectionItemView.class.isInstance(view)) {
                CorpusSelectionItemView item = (CorpusSelectionItemView) view;
                String selectedstring = item.getItemId();
                if(TextUtils.isEmpty(selectedstring)) {
                	selectedstring = item.getText();
                }
                onCorpusSelected(item.getItemId());             
            }
        }
    };

    @Override
    public UserCircle getCircleInfo() {
        return mCircle;
    }

    private void startComposeActivity() {
        boolean isAdmin = false;
        if (mCircle != null) {
        	HashMap<String, String> receiverMap = new HashMap<String, String>();
        	String receiverid = "";
        	if(mCircle.mGroup != null && mCircle.mGroup.creator != null) {
        		isAdmin = PublicCircleRequestUser.isCreator(mCircle.mGroup.role_in_group)
        				|| PublicCircleRequestUser.isManager(mCircle.mGroup.role_in_group);
        	}
        	long parent_id = -1;
        	if(mCircle.mGroup != null) {
        		if(mCircle.mGroup.formal == UserCircle.circle_top_formal
        				|| (mCircle.mGroup.formal == UserCircle.circle_free && mCircle.mGroup.parent_id <=0)) {
        			parent_id = mCircle.circleid;	
        		}else {
        			parent_id = mCircle.mGroup.parent_id;
        		}
        	}
        	// this is home page, so intent param: scene is the top circle id, fromid is -1.
        	IntentUtil.startComposeActivity(this, receiverid, true, isAdmin, receiverMap, parent_id, -1);
        }else {
        	Log.d(TAG, "startComposeActivity, circle is null ");
        }
    }

    private final static int INVIT_EPEOPLE_END = 101;
    private final static int EXIT_CIRCLE_END = 102;
    private final static int CIRCLE_DELETE_END = 103;
    private final static int CIRCLE_AS_PAGE_END = 104;
    
    private final static int GET_PUBLIC_CIRCLE_INFO = 105;
    private final static int GET_PUBLIC_CIRCLE_INFO_END = 106;

    @Override
    public void onStreamAction(View v, int action) {
        switch (action) {
            case ACTION_SEARCH:
                showSearhView();
                break;
            case ACTION_PHOTO:
                DialogUtils.ShowPhotoPickDialog(this, R.string.share_photo_title,
                        new DialogUtils.PhotoPickInterface() {
                            @Override
                            public void doTakePhotoCallback() {
                                if (mCircle != null) {
                                    long parent_id = -1;
                                    if (mCircle.mGroup != null) {
                                        if (mCircle.mGroup.formal == UserCircle.circle_top_formal) {
                                            parent_id = mCircle.circleid;
                                        } else {
                                            parent_id = mCircle.mGroup.parent_id;
                                        }
                                    }
                                    IntentUtil.startTakingPhotoIntent(OrganizationHomeActivity.this, getDefaultRecipient(), parent_id, -1);
                                }
                            }

                            @Override
                            public void doPickPhotoFromGalleryCallback() {
                                if (mCircle != null) {
                                    long parent_id = -1;
                                    if (mCircle.mGroup != null) {
                                        if (mCircle.mGroup.formal == UserCircle.circle_top_formal) {
                                            parent_id = mCircle.circleid;
                                        } else {
                                            parent_id = mCircle.mGroup.parent_id;
                                        }
                                    }
                                    IntentUtil.startPickingPhotoIntent(OrganizationHomeActivity.this, getDefaultRecipient(), parent_id, -1);
                                }
                            }
                        }
                );
                break;
            case ACTION_COMPOSE:
                startComposeActivity();
                break;
            case ACTION_MORE:
                if(mMoreDialog == null) {
                    mMoreDialog = new BottomMoreQuickAction(OrganizationHomeActivity.this, v.getWidth(), mCircle);
                    mMoreDialog.show(v);
                }else {
                    mMoreDialog.show(v);
                }
                break;
            default:
                Log.w(TAG, "onStreamAction, unknown action = " + action + ", is it a new one?");
        }
    }

    private class MainHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INVIT_EPEOPLE_END: {
                    try {
                        dismissDialog(DIALOG_SET_CIRCLE_PROCESS);
                    } catch (Exception ne) {
                    }
                    boolean ret = msg.getData().getBoolean(RESULT, false);
                    if (ret == true) {
                        Log.d(TAG, "invite people end ");
                        showOperationSucToast(true);
                    } else {
                        showOperationFailToast("", true);
                    }
                    break;
                }
                case EXIT_CIRCLE_END: {
                    try {
                        dismissDialog(DIALOG_SET_CIRCLE_PROCESS);
                    } catch (Exception ne) {
                    }
                    boolean ret = msg.getData().getBoolean(RESULT, false);
                    if (ret == true) {
                        Log.d(TAG, "exit circle end ");
                        showOperationSucToast(true);
                        QiupuHelper.updateCirclesUI();
                        finish();
                    } else {
                        showOperationFailToast("", true);
                    }
                    break;
                }case CIRCLE_DELETE_END: {
                    try {
                        dismissDialog(DIALOG_DELETE_CIRCLE_PROCESS);
                    } catch (Exception ne) {}
                    boolean ret = msg.getData().getBoolean(RESULT, false);
                    if (ret)
                    {
                        showOperationSucToast(true);
                        QiupuHelper.updateCirclesUI();
                        finish();
                    } else {
                        ToastUtil.showOperationFailed(OrganizationHomeActivity.this, mHandler, true);
                    }
                    break;
                } case CIRCLE_AS_PAGE_END: {
                    try {
                        dismissDialog(DIALOG_SET_CIRCLE_PROCESS);
                    } catch (Exception ne) {}
                    if(msg.getData().getBoolean(RESULT, false)) {
                        QiupuHelper.updatePageActivityUI(null);
                        showOperationSucToast(true);
                    }else {
                        ToastUtil.showOperationFailed(OrganizationHomeActivity.this, mHandler, true);
                    }
                    break;
                } case GET_PUBLIC_CIRCLE_INFO: {
                	syncPublicCircleInfo(String.valueOf(mCircle.circleid), false, QiupuConfig.isEventIds(mCircle.circleid));
                	break;
                } case GET_PUBLIC_CIRCLE_INFO_END: {
//                	end();
                	boolean ret = msg.getData().getBoolean(RESULT, false);
                	if (ret) {
//                	    if(mRightFragment != null) {
//                	    	mRightFragment.refreshUI(mCircle);
//            			}
                        // todo: refresh current fragment?
                    } else {
                        ToastUtil.showShortToast(OrganizationHomeActivity.this, mHandler, R.string.get_info_failed);
                    }
                	break;
                }
            }
        }
    }

    boolean inGetPublicInfo;
    Object mLockGetPublicInfo = new Object();
    protected void syncPublicCircleInfo(final String circleId, final boolean with_member, final boolean isEvent) {
        if (!ToastUtil.testValidConnectivity(this)) {
            Log.i(TAG, "syncPublicCircleInfo, ignore while no connection.");
            return;
        }
        
    	if (inGetPublicInfo == true) {
    		ToastUtil.showShortToast(this, mHandler, R.string.string_in_processing);
    		return;
    	}
    	
    	synchronized (mLockGetPublicInfo) {
    		inGetPublicInfo = true;
    	}

        ServiceHelper.syncPublicCirclInfo(AccountServiceUtils.getSessionID(), circleId, with_member, isEvent, new TwitterAdapter() {
            public void syncPublicCirclInfo(UserCircle circle) {
                Log.d(TAG, "finish syncPublicCirclInfo=" + circle.toString());

                mCircle = circle;
                if (mCircle.mGroup != null && PublicCircleRequestUser.isInGroup(mCircle.mGroup.role_in_group)) {
                    CacheHelper.insertOneCircle(mCircle);
                }

                // insert to circle_circles
                if (mCircle.mGroup != null && mCircle.mGroup.parent_id > 0) {
                    CacheHelper.insertOneCircleCircles(mCircle.mGroup.parent_id, mCircle);
                }

                onLoadingCircleReady("", true);
            }

            public void onException(TwitterException ex, TwitterMethod method) {
                onLoadingCircleReady(ex.getMessage(), false);
            }
        });
    }
    
    private void onLoadingCircleReady(String promptText, boolean result) {
    	synchronized (mLockGetPublicInfo) {
    		inGetPublicInfo = false;
    	}
    	if(mHandler != null) {
    		Message msg = mHandler.obtainMessage(GET_PUBLIC_CIRCLE_INFO_END);
    		msg.getData().putBoolean(RESULT, result);
    		msg.sendToTarget();
    	}
    }
    
    boolean inDeletePeople;
    Object mLockDeletePeople = new Object();
    private void deletePublicCirclePeople(final long circleid, final String userids, final String admins) {
        if (inDeletePeople == true) {
            Toast.makeText(this, R.string.string_in_processing, Toast.LENGTH_SHORT).show();
            return;
        }

        synchronized (mLockDeletePeople) {
            inDeletePeople = true;
        }
        showDialog(DIALOG_SET_CIRCLE_PROCESS);

        ServiceHelper.deletePublicCirclePeople(AccountServiceUtils.getSessionID(), circleid, userids, admins, new TwitterAdapter() {
            public void deletePublicCirclePeople(boolean result) {
                Log.d(TAG, "finish deletePublicCirclePeople=" + result);
                if(result) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //delete circle in DB
                            CacheHelper.deleteCircleByCricleId(AccountServiceUtils.getBorqsAccountID(), String.valueOf(circleid));
                        }
                    });
                }

                Message msg = mHandler.obtainMessage(EXIT_CIRCLE_END);
                msg.getData().putBoolean(RESULT, true);
                msg.sendToTarget();
                synchronized (mLockDeletePeople) {
                    inDeletePeople = false;
                }
            }

            public void onException(TwitterException ex, TwitterMethod method) {
                Message msg = mHandler.obtainMessage(EXIT_CIRCLE_END);
                msg.getData().putString(BasicActivity.ERROR_MSG, ex.getMessage());
                msg.getData().putBoolean(RESULT, false);
                msg.sendToTarget();
                synchronized (mLockDeletePeople) {
                    inDeletePeople = false;
                }
            }
        });
    }

    public void onCorpusSelected(String value) {
    	if(getString(R.string.home_label).equals(value)) {
    		Log.d(TAG, "Click home item , do nothing.");
    	}else {
    		try {
    			if(value != null && TextUtils.isDigitsOnly(value)) {
    				long circleid = Long.parseLong(value);
    				UserCircle uc = CacheHelper.queryOneCircleWithGroup(circleid);
    				IntentUtil.startPublicCircleDetailIntent(this, uc);
    				if(uc.mGroup != null && uc.mGroup.formal == UserCircle.circle_top_formal) {
    					IntentUtil.loadCircleDirectoryFromServer(this, circleid);
    				}else {
    				}
    			}
    		} catch (Exception e) {
    		}
    	}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode:"+requestCode+" resultCode:"+resultCode);
        if(requestCode == in_member_selectcode) {
            if(resultCode == Activity.RESULT_OK) {
                String selectUserIds = data.getStringExtra("toUsers");
                Log.d(TAG, "onActivityResult: " + selectUserIds);
                if(TextUtils.isEmpty(selectUserIds)) {
                    Log.d(TAG, "select null , do nothing ");
                }else {
                    deletePublicCirclePeople(mCircle.circleid, String.valueOf(AccountServiceUtils.getBorqsAccountID()), selectUserIds);
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public long getTopStreamTargetId() {

        if (mCircle != null && mCircle.mGroup != null && mCircle.mGroup.viewer_can_update) {
            return mCircle.circleid;
        }

        return super.getTopStreamTargetId();
    }

    public long getCircleId() {
        if(mCircle != null) {
            return mCircle.circleid;
        }else {
            return 0;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            if (mCurrentFragment == profile_detail) {
//                return true;
//            }
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            if (mCurrentFragment == profile_detail) {
//                handlerBackKey(false);
//                return true;
//            }
//        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			showSearhView();
			return true;
		}
        return super.onKeyUp(keyCode, event);
    }

//    @Override
//    protected boolean isPersistActivity() {
//        return true;
//    }

    @Override
    public boolean onCancelled() {
        HomePickerActivity.unregisterPickerListener(getClass().getName());
        return false;
    }

    @Override
    public boolean onPicked(UserCircle circle) {
        HomePickerActivity.unregisterPickerListener(getClass().getName());
        finish();
        return true;
    }

    private String getDefaultRecipient() {
        return "#" + mCircle.circleid;
    }

    @Override
    protected void showCorpusSelectionDialog(View view) {
        int location[] = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = getResources().getDimensionPixelSize(R.dimen.title_bar_height);

        ArrayList<SelectionItem> items =  getChildCircleNameArray();
        DialogUtils.showCorpusSelectionDialog(this, x, y, items, circleListItemClickListener);
    }


    AdapterView.OnItemClickListener circleListItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (CorpusSelectionItemView.class.isInstance(view)) {
                CorpusSelectionItemView item = (CorpusSelectionItemView) view;
                switchCircleStream(item.getText(), item.getItemId());
            }
        }
    };

    private ArrayList<SelectionItem> getChildCircleNameArray() {
        ArrayList<SelectionItem> circleNames = new ArrayList<SelectionItem>();

        if(QiupuApplication.mTopOrganizationId == null) {
        	Log.e(TAG, "getChildCircleNameArray, organization circle is null");
        	return circleNames;
        }
        if (QiupuApplication.mTopOrganizationId.circleid != mCircle.circleid) {
        	SelectionItem item = new SelectionItem(String.valueOf(QiupuApplication.mTopOrganizationId.circleid), QiupuApplication.mTopOrganizationId.name);
        	circleNames.add(item);
        }
        Cursor cursor = FORCE_SHOW_DROPDOWN ? CacheHelper.queryAllCircleList() :
                CacheHelper.queryChildCircleList(QiupuApplication.mTopOrganizationId.circleid);
        if (null != cursor && cursor.moveToFirst()) {
            String name;
            String cid;
            do {
                name = cursor.getString(cursor.getColumnIndex(QiupuORM.CircleColumns.CIRCLE_NAME));
                cid = cursor.getString(cursor.getColumnIndex(QiupuORM.CircleColumns.CIRCLE_ID));
                circleNames.add(new SelectionItem(cid, name));
            } while (cursor.moveToNext());
        }

        return circleNames;
    }

    private void switchCircleStream(String label, String id) {
        Log.d(TAG, "switchCircleStream, circle label = " + label + ", id = " + id);

        setHeadTitle(label);

        long circleId = Long.parseLong(id);
        mFragmentData.mCircleId = circleId;

        // todo: switch to target circle
//        if (null != mHomeFragment) {
//        	mHomeFragment.switchCircle(circleId);
//        }
    }

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//    	Log.d(TAG, "IntentUtil onQueryTextSubmit: " + query);
//    	if(mCurrentPage == PAGE_STRAM ) {
//    		if(query != null && query.length() > 0) {
//    			IntentUtil.startSearchActivity(this, query, BpcSearchActivity.SEARCH_TYPE_STREAM, mCircle.circleid);
//    		}else {
//    			Log.d(TAG, "onQueryTextSubmit, query is null " );
//    			ToastUtil.showShortToast(this, mHandler, R.string.search_recommend);
//    		}
//    	}else if(mCurrentPage == PAGE_RIGHT_INFO){
//    		if(mRightFragment != null) {
//    			mRightFragment.onQueryTextSubmit(query);
//    		}
//    	}
//    	return super.onQueryTextSubmit(query);
//    }

    @Override
    public boolean onQueryTextChange(String newText) {
//		if(mCurrentPage == PAGE_RIGHT_INFO) {
//			if(mRightFragment != null) {
//				mRightFragment.doSearch(newText);
//			}
//		}
    	return super.onQueryTextChange(newText);
    }
    
	@Override
	public void getStreamRightFlipperFragment(Fragment fragment) {
//		mRightFragment = fragment;
	}

	@Override
	public void startSearch() {
		showSearhView();
	}

	@Override
	public void hidSearch() {
		hideSearhView();
	}

	@Override
	public void finishActivity() {
		finish();
	}

//	@Override
//	public int getCurentIndex() {
//		if(mRightFragment != null) {
//			return mRightFragment.getCurrentIndex();
//		}
//		return -1;
//	}


    protected void createRightMenuItems() {
        createRightItem(R.drawable.menu_setting, R.string.home_settings, StreamRightFlipperFragment.class);
        createRightItem(R.drawable.icon_album, R.string.home_others, StreamRightFlipperFragment.class);
    }

    protected void createLeftMenuItems() {
        createLeftItem(R.drawable.home_screen_menu_loop_icon_default, R.string.tab_feed, StreamListFragment.class);
        createLeftItem(R.drawable.home_screen_photo_icon_default, R.string.home_album, OrganizationExtraFragment.Album.class);
        createLeftItem(R.drawable.friend_group_icon, R.string.tab_friends, OrganizationExtraFragment.Member.class);
        createLeftItem(R.drawable.home_screen_menu_people_icon_default, R.string.user_circles, OrganizationExtraFragment.Circle.class);
        createLeftItem(R.drawable.home_screen_event_icon, R.string.event, OrganizationExtraFragment.Event.class);
        createLeftItem(R.drawable.home_screen_voting_icon_default, R.string.poll, OrganizationExtraFragment.Poll.class);
    }

}
