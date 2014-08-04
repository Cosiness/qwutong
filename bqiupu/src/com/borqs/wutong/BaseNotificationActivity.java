package com.borqs.wutong;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.borqs.account.service.AccountServiceUtils;
import com.borqs.common.adapter.InformationAdapter;
import com.borqs.common.adapter.RequestsAdapter;
import com.borqs.common.api.BpcApiUtils;
import com.borqs.common.listener.NotificationListener;
import com.borqs.common.listener.RequestActionListner;
import com.borqs.common.listener.RequestRefreshListner;
import com.borqs.common.util.TwitterExceptionUtils;
import com.borqs.common.util.UserTask;
import com.borqs.common.view.InformationItemView;
import com.borqs.information.InformationBase;
import com.borqs.information.db.Notification;
import com.borqs.information.db.NotificationOperator;
import com.borqs.information.util.InformationConstant;
import com.borqs.information.util.InformationReadCache;
import com.borqs.information.util.InformationUtils;
import com.borqs.qiupu.QiupuConfig;
import com.borqs.qiupu.R;
import com.borqs.qiupu.cache.QiupuHelper;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.service.QiupuService;
import com.borqs.qiupu.service.RequestsService;
import com.borqs.qiupu.ui.BasicActivity;
import com.borqs.qiupu.ui.bpc.InformationListActivity;
import com.borqs.qiupu.ui.circle.quickAction.NtfQuickAction;
import com.borqs.qiupu.util.CircleUtils;
import com.borqs.qiupu.util.JSONUtil;
import com.borqs.qiupu.util.StringUtil;
import com.borqs.wutong.utils.CacheHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import twitter4j.Requests;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

/**
 * Created with IntelliJ IDEA.
 * User: b608
 * Date: 12-12-20
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */

public abstract class BaseNotificationActivity extends BasicActivity.SimpleBaseActivity implements
        NotificationListener, RequestActionListner, NtfQuickAction.OnDismissListener,
        RequestsService.RequestListener, RequestRefreshListner,
        InformationAdapter.MoreItemCheckListener {
    private final String TAG = BaseNotificationActivity.class.getSimpleName();

    protected final void initHeadViews(View parent) {
//        getPluginItemInfo();

        initTitleNtfView();
    }

    // code from SlidingMenuOverlayActivity begin

    private NtfQuickAction mRequestQuickDialog;
    private NtfQuickAction mToMeQuickDialog;
    private NtfQuickAction mOtherNtfQuickDialog;
    private InformationAdapter mInfomationAdapter;
    private RequestsAdapter mRequestAdapter;
    private NotificationOperator mOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOperator = new NotificationOperator(this);
        orm = CacheHelper.getOrm(this);
//        QiupuHelper.registerNotificationListener(getClass().getName(), this);
        InformationUtils.registerNotificationListener(getClass().getName(), this);
        RequestsService.regiestRequestListener(getClass().getName(), this);
        QiupuHelper.registerRequestRefreshListner(getClass().getName(), this);
    }


    @Override
    protected void onResume() {
        super.onResume();
////            setLeftMenuPosition();
//        setListAdapterForDynamic();

//        new NotificationEntry().execute();
        InformationUtils.getInforByDelay(this, 3 * QiupuConfig.A_SECOND);
        // sync requests
        if(QiupuService.mRequestsService != null) {
//		    	QiupuService.mRequestsService.regiestRequestListener(LeftMenuListView.class.getName(), LeftMenuListView.this);
            QiupuService.mRequestsService.rescheduleRequests(true);
        }
        else {
            mHandler.postDelayed(new Runnable() {
                public void run() {
//		    			QiupuService.mRequestsService.regiestRequestListener(LeftMenuListView.class.getName(), LeftMenuListView.this);
                    QiupuService.mRequestsService.rescheduleRequests(true);
                }
            }, 5 * QiupuConfig.A_SECOND);
        }
    }
//
//    private void setListAdapterForDynamic() {
//        if (leftMenuListView != null && isStreamIndex()) {
//            getPluginItemInfo();
//            leftMenuListView.setAdapter(new LeftMenuAdapter(this, getPosition(), mPluginInfo));
//            setHeaderViewUI();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InformationUtils.unregisterNotificationListener(getClass().getName());
//        QiupuHelper.unregisterNotificationListener(getClass().getName());
        RequestsService.unRegiestRequestListener(getClass().getName());
        if(mRequestAdapter != null) {
            mRequestAdapter.setRequestActionListener(null);
        }
        QiupuHelper.unregisterRequestRefreshListner(getClass().getName());
    }

    protected void initTitleNtfView() {
        Log.d(TAG, "initHeadViews");

        refreshRequestNtf();
        refreshToMeNtf();
        refreshOtherNtf();
    }

    private void doShowRequestNtf(View v, long sceneId) {
        if(mRequestQuickDialog == null ) {
            mRequestQuickDialog = new NtfQuickAction(this, NtfQuickAction.VERTICAL, Notification.ntf_type_request);
            mRequestAdapter = new RequestsAdapter(this);
            mRequestAdapter.setRequestActionListener(this);
            mRequestQuickDialog.setListAdapter(mRequestAdapter);
            mRequestAdapter.alterRequests(orm.buildRequestList("", sceneId));
            mRequestQuickDialog.setOnDismissListener(this);
            mRequestQuickDialog.show(v);
        }else {
            if(mRequestAdapter == null) {
                mRequestAdapter = new RequestsAdapter(this);
            }
            mRequestAdapter.alterRequests(orm.buildRequestList("", sceneId));
            mRequestQuickDialog.setListAdapter(mRequestAdapter);
            mRequestQuickDialog.show(v);
        }
    }

    private void refreshRequestNtf() {
        final long sceneId = getSceneId();
        ImageView requestView = (ImageView) findViewById(R.id.head_request);
        ArrayList<Requests> requeslist = orm.buildRequestList("", sceneId);
        if(requeslist != null && requeslist.size() > 0) {
            Drawable requestIcon = getResources().getDrawable(R.drawable.request_icon_light);
            requestView.setImageBitmap(generatorTargetCountIcon(requestIcon, requeslist.size()));
        }else {
            requestView.setImageResource(R.drawable.request_icon);
        }

        requestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShowRequestNtf(v, sceneId);
            }
        });
    }

    private void doShowToMeNtf(View v) {
        if(mToMeQuickDialog == null ) {
            mToMeQuickDialog = new NtfQuickAction(this,NtfQuickAction.VERTICAL, Notification.ntf_type_tome);
            mInfomationAdapter = new InformationAdapter(this, this);
            mToMeQuickDialog.setListAdapter(mInfomationAdapter);
            mToMeQuickDialog.setListItemClickListener(infomationItemClickListenter);
            mInfomationAdapter.alterDataList(mOperator.loadNtfToMe(""));
            mToMeQuickDialog.setOnDismissListener(this);
            mToMeQuickDialog.show(v);
            //refresh title ToMe ntf icon
//					onNotificationDownloadCallBack(true, 0);

        }else {
            if(mInfomationAdapter == null) {
                mInfomationAdapter = new InformationAdapter(this);
            }
            mInfomationAdapter.alterDataList(mOperator.loadNtfToMe(""));
            mToMeQuickDialog.setListAdapter(mInfomationAdapter);
            mToMeQuickDialog.show(v);
        }
    }
    private void refreshToMeNtf() {
        ImageView tomeView = (ImageView) findViewById(R.id.head_send_me);
        int count = mOperator.loadUnReadToMeNtfCount();
        if(count > 0) {
            Drawable tomeicon = getResources().getDrawable(R.drawable.letter_icon_light);
            tomeView.setImageBitmap(generatorTargetCountIcon(tomeicon, count));
        }else {
            tomeView.setImageResource(R.drawable.letter_icon);
        }
        tomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShowToMeNtf(v);
            }
        });
    }

    private void doShowOtherNtf(View v) {
        // show quick dialog
        if (mOtherNtfQuickDialog == null) {
            mOtherNtfQuickDialog = new NtfQuickAction(this, NtfQuickAction.VERTICAL, Notification.ntf_type_other);
            mInfomationAdapter = new InformationAdapter(this, this);
            mOtherNtfQuickDialog.setListAdapter(mInfomationAdapter);
            mOtherNtfQuickDialog.setListItemClickListener(infomationItemClickListenter);
            mInfomationAdapter.alterDataList(mOperator.loadNtfWithOutToMe(""));
            mOtherNtfQuickDialog.setOnDismissListener(this);
            mOtherNtfQuickDialog.show(v);

            //refresh title other ntf icon
//					onNotificationDownloadCallBack(false, 0);

        } else {
            if (mInfomationAdapter == null) {
                mInfomationAdapter = new InformationAdapter(this);
            }
            mInfomationAdapter.alterDataList(mOperator.loadNtfWithOutToMe(""));
            mOtherNtfQuickDialog.setListAdapter(mInfomationAdapter);
            mOtherNtfQuickDialog.show(v);
        }
    }
    private void refreshOtherNtf() {
        ImageView otherNtfView = (ImageView) findViewById(R.id.head_ntf);
        int count = mOperator.loadUnReadOtherNtfCount();
        if(count > 0) {
            Drawable otherntfIcon = getResources().getDrawable(R.drawable.notice_icon_light);
            otherNtfView.setImageBitmap(generatorTargetCountIcon(otherntfIcon, count));
        }else {
            otherNtfView.setImageResource(R.drawable.notice_icon);
        }
        otherNtfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShowOtherNtf(v);
            }
        });
    }


    private void refreshHeadTomeIcon(int count) {
        ImageView tomeView = (ImageView) findViewById(R.id.head_send_me);
        if(tomeView == null) {
            Log.d(TAG, "find head ntf icon is null");
            return;
        }
        if(count > 0) {
            Drawable tomeicon = getResources().getDrawable(R.drawable.letter_icon_light);
            tomeView.setImageBitmap(generatorTargetCountIcon(tomeicon, count));
        }else {
            tomeView.setImageResource(R.drawable.letter_icon);
        }
    }

    private void refreshHeadotherIcon(int count) {
        ImageView otherNtfView = (ImageView) findViewById(R.id.head_ntf);
        if(otherNtfView == null) {
            Log.d(TAG, "find head ntf icon is null");
            return;
        }
        if(count > 0) {
            Drawable otherntfIcon = getResources().getDrawable(R.drawable.notice_icon_light);
            otherNtfView.setImageBitmap(generatorTargetCountIcon(otherntfIcon, count));
        }else {
            otherNtfView.setImageResource(R.drawable.notice_icon);
        }
    }

    private void refreshHeadNtfIcon() {
        if(mToMeQuickDialog != null && mToMeQuickDialog.isShow()) {
            refreshHeadTomeIcon(mOperator.loadUnReadToMeNtfCount());
        }else if(mOtherNtfQuickDialog != null && mOtherNtfQuickDialog.isShow()) {
            refreshHeadotherIcon(mOperator.loadUnReadOtherNtfCount());
        }
    }

    AdapterView.OnItemClickListener infomationItemClickListenter = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(InformationItemView.class.isInstance(view)) {
                InformationItemView informationview = (InformationItemView) view;
                InformationBase infor = informationview.getItem();
                if(infor != null) {
                    if(!infor.read) {
                        informationview.reverContentText(infor.read);
                        mOperator.updateReadStatus(infor.id, true);
                        InformationReadCache.ReadStreamCache.cacheUnReadNtfIdsWithoutDb(infor.id);
                        refreshHeadNtfIcon();
                    }
                }
//				closeNtfPopupWindow();
                forwardInformation(informationview.getItem());
            }
        }
    };

    private void forwardInformation(InformationBase msg) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (TextUtils.isEmpty(msg.uri)) {
            return;
        } else {
            intent.setData(Uri.parse(msg.uri));
        }

        if (BpcApiUtils.isActivityReadyForIntent(this, intent)) {
            intent.putExtra("MSG_ID", msg.id);
            intent.putExtra("DATA", msg.data);
            intent.putExtra("SENDER_ID", msg.senderId);
            intent.putExtra("WHEN", msg.lastModified);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }


    private void closePopupWindow() {
        if(mToMeQuickDialog != null && mToMeQuickDialog.isShow()) {
            mToMeQuickDialog.dismiss();
        }
        if(mOtherNtfQuickDialog != null && mOtherNtfQuickDialog.isShow()) {
            mOtherNtfQuickDialog.dismiss();
        }
        if(mRequestQuickDialog != null && mRequestQuickDialog.isShow()) {
            mRequestQuickDialog.dismiss();
        }
    }

    // load more interface code begin

    @Override
    public boolean isMoreItemHidden() {
        return false;
    }

    @Override
    public View.OnClickListener getMoreItemClickListener() {
        return seeAllInformationListener;
    }

    @Override
    public int getMoreItemCaptionId() {
        return  R.string.ntf_see_all;
    }

    View.OnClickListener seeAllInformationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mToMeQuickDialog != null && mToMeQuickDialog.isShow()) {
                Intent intent = new Intent(BaseNotificationActivity.this, InformationListActivity.class);
                intent.putExtra(InformationConstant.NOTIFICATION_INTENT_PARAM_ISTOME, true);
                startActivity(intent);
                mToMeQuickDialog.dismiss();
            }else if(mOtherNtfQuickDialog != null && mOtherNtfQuickDialog.isShow()) {
                Intent intent = new Intent(BaseNotificationActivity.this, InformationListActivity.class);
                intent.putExtra(InformationConstant.NOTIFICATION_INTENT_PARAM_ISTOME, false);
                startActivity(intent);
                mOtherNtfQuickDialog.dismiss();
            }
        }
    };

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        closePopupWindow();
    };
    // load more interface code end

    // OnDismissListener interface begin

    private void setRead(String unReadIds) {
        new BatchReadTask(unReadIds).execute();
    }

    private class BatchReadTask extends UserTask<Long, Void, Void> {

        private String mUnReadIds;
        private String mCacheUnReadIds;

        public BatchReadTask(String unReadIds) {
            mUnReadIds = unReadIds;
            mCacheUnReadIds = InformationReadCache.ReadStreamCache.getCacheUnReadNtfIds();
        }

        @Override
        public Void doInBackground(Long... params) {
            if(QiupuConfig.DBLOGD)Log.d(TAG, "unread ids : " + mUnReadIds + " " + mCacheUnReadIds);
            StringBuilder unreads = new StringBuilder();
            if(StringUtil.isValidString(mUnReadIds)) {
                unreads.append(mUnReadIds);
            }
            if(StringUtil.isValidString(mCacheUnReadIds)) {
                if(unreads.length() > 0) {
                    unreads.append(",");
                }
                unreads.append(mCacheUnReadIds);
            }
            if(unreads.length() > 0) {
                boolean res = InformationUtils.setReadStatus(BaseNotificationActivity.this, unreads.toString());
                if(res) {
                    InformationReadCache.ReadStreamCache.removeNtfCacheWithIds(mCacheUnReadIds);
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onDismiss(boolean onTop, int ntftype) {
        if(QiupuConfig.LOGD)Log.d(TAG, "onDismiss: " + onTop + " " + ntftype);
        if(ntftype == Notification.ntf_type_tome) {
            refreshHeadTomeIcon(0);
            if(mToMeQuickDialog != null) {
                mToMeQuickDialog.OnRefreshComplete();
            }
//			onNotificationDownloadCallBack(true, 0);
            //setRead to server
            setRead(mOperator.loadUnReadNtfToMeString());
            //refresh all local other ntf to read status
            mOperator.updateReadStatusWithType(true, true);


        }else if(ntftype == Notification.ntf_type_other) {
            refreshHeadotherIcon(0);
            if(mOtherNtfQuickDialog != null) {
                mOtherNtfQuickDialog.OnRefreshComplete();
            }
//			onNotificationDownloadCallBack(false, 0);
            //setRead to server
            setRead(mOperator.loadunReadNtfOtherString());
            //refresh all local other ntf to read status
            mOperator.updateReadStatusWithType(false, true);
        }else if(ntftype == Notification.ntf_type_request) {
            if(mRequestQuickDialog != null) {
                mRequestQuickDialog.OnRefreshComplete();
            }
        }else {
            Log.d(TAG, "setPopupTitle: have not this ntf type. " + ntftype);
        }
    }

    // OnDismissListener interface end

    // RequestActionListner interface begin

    private Requests mRequest = new Requests();
    private int      mType;

    private void initContactInfomap(Map<String, String> map) {
        Cursor cursor = orm.queryOneUserPhoneEmail(AccountServiceUtils
                .getBorqsAccountID());
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String type = cursor.getString(cursor.getColumnIndex(QiupuORM.PhoneEmailColumns.TYPE));
                String info = cursor.getString(cursor.getColumnIndex(QiupuORM.PhoneEmailColumns.INFO));
                map.put(type, info);
            } while (cursor.moveToNext());
            cursor.close();
            cursor = null;
        }else {
            Log.d(TAG, "need load myself info from server");
        }
    }

    private void gotoUpdateContactInfo(String col, String data) {

        Map<String, String> map = new LinkedHashMap<String, String>();
        initContactInfomap(map);
        HashMap<String, String> contactInfoMap = QiupuHelper.organizationContactMap(col,
                data, map);

        HashMap<String, String> infoMap = new HashMap<String, String>();
        // String value = JSONUtil.createContactInfoJSONObject(contactInfoMap);
        infoMap.put("contact_info", JSONUtil.createContactInfoJSONObject(contactInfoMap));
        updateUserInfo(infoMap);
    }

    @Override
    public void acceptRequest(Requests request, int type) {
        Log.d(TAG, "acceptRequest() type = " + type);
        mRequest = request;
        mType = type;
        if (type == Requests.REQUEST_TYPE_EXCHANGE_VCARD) {
            // set to my circles 'privacy circle/default circle'
            setCircle(mRequest.user.uid,
                    CircleUtils.getDefaultCircleId(),
                    CircleUtils.getDefaultCircleName(getResources()));
        } else if (type == Requests.REQUEST_TYPE_CHANGE_PHONE_1) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_PHONE1, mRequest.data);
        } else if (type == Requests.REQUEST_TYPE_CHANGE_PHONE_2) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_PHONE2, mRequest.data);
        } else if (type == Requests.REQUEST_TYPE_CHANGE_PHONE_3) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_PHONE3, mRequest.data);
        } else if (type == Requests.REQUEST_TYPE_CHANGE_EMAIL_1) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_EMAIL1, mRequest.data);
        } else if (type == Requests.REQUEST_TYPE_CHANGE_EMAIL_2) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_EMAIL2, mRequest.data);
        } else if (type == Requests.REQUEST_TYPE_CHANGE_EMAIL_3) {
            gotoUpdateContactInfo(QiupuConfig.TYPE_EMAIL3, mRequest.data);
        } else if(type == Requests.REQUEST_EVENT_INVITE || type == Requests.REQUEST_EVENT_JOIN
                || type == Requests.REQUEST_PUBLIC_CIRCLE_INVITE
                || type == Requests.REQUEST_PUBLIC_CIRCLE_JOIN) {
            doneRequests(mRequest.rid, mRequest.type, mRequest.data, true);
        }
    }

    @Override
    public void refuseRequest(Requests request) {
        mRequest = request;
        if(request.type == Requests.REQUEST_EVENT_INVITE || request.type == Requests.REQUEST_EVENT_JOIN
                || request.type == Requests.REQUEST_PUBLIC_CIRCLE_INVITE
                || request.type == Requests.REQUEST_PUBLIC_CIRCLE_JOIN) {
            doneRequests(request.rid, request.type, request.data, false);
        }else {
            doneRequests(request.rid);
        }
    }

    private void doneRequests(final String requestid) {
        doneRequests(requestid, -1, "", false);
    }

    private void doneRequests(final String requestid, final int type, final String data, final boolean isAccept) {
        if (!AccountServiceUtils.isAccountReady()) {
            Log.d(TAG, "getRequests, mAccount is null exit");
            return;
        }

//		begin();
        asyncQiupu.doneRequests(AccountServiceUtils.getSessionID(), requestid, type, data, isAccept,
                new TwitterAdapter() {
                    public void doneRequests(boolean suc) {
                        Log.d(TAG, "finish doneRequests = " + suc);
                        orm.deleteDoneRequest(requestid, -1);
                        updateRequestCountUI();
//				Message mds = mHandler.obtainMessage(REQUEST_DONE_END);
//				mds.getData().putBoolean("RESULT", suc);
//				Log.d(TAG, "requestid = " + requestid);
//				mds.getData().putString("request_id", requestid);
//				mHandler.sendMessage(mds);

                    }

                    public void onException(TwitterException ex,
                                            TwitterMethod method) {
                        TwitterExceptionUtils.printException(TAG,
                                "doneRequests, server exception:", ex, method);

//				Message mds = mHandler.obtainMessage(REQUEST_DONE_END);
//				mds.getData().putBoolean("RESULT", false);
//				mHandler.sendMessage(mds);
                    }
                });
    }

    private void updateRequestCountUI() {
        synchronized (QiupuHelper.requestrefreshListener) {
            Log.d(TAG, "updateRequestCountUI: " + QiupuHelper.requestrefreshListener.size());
            Set<String> set = QiupuHelper.requestrefreshListener.keySet();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String key = it.next();
                WeakReference<RequestRefreshListner> ref = QiupuHelper.requestrefreshListener.get(key);
                if (ref != null && ref.get() != null) {
                    ref.get().refreshRequestUi();
                }
            }
        }
    }

    // RequestActionListener interface end

    // NotificationListener begin
    @Override
    public void onNotificationDownloadCallBack(final boolean isToMe, final int count) {
        Log.d(TAG, "onNotificationDownLoadCallback: isTome: " + isToMe + " count: " + count );
        mBasicHandler.post(new Runnable() {
            @Override
            public void run() {
                if(isToMe) {
                    refreshHeadTomeIcon(count);
                    // refresh to me ListView
                    if(mToMeQuickDialog != null) {
                        mToMeQuickDialog.OnRefreshComplete();
                        if(mToMeQuickDialog.isShow()) {
                            if(mInfomationAdapter == null) {
                                mInfomationAdapter = new InformationAdapter(BaseNotificationActivity.this);
                            }
                            mInfomationAdapter.alterDataList(mOperator.loadNtfToMe(""));
                            mToMeQuickDialog.setListAdapter(mInfomationAdapter);
                        }
                    }
                }else {
                    refreshHeadotherIcon(count);
                    // refresh Other ListView
                    if(mOtherNtfQuickDialog != null){
                        mOtherNtfQuickDialog.OnRefreshComplete();
                        if(mOtherNtfQuickDialog.isShow()) {
                            if(mInfomationAdapter == null) {
                                mInfomationAdapter = new InformationAdapter(BaseNotificationActivity.this);
                            }
                            mInfomationAdapter.alterDataList(mOperator.loadNtfWithOutToMe(""));
                            mOtherNtfQuickDialog.setListAdapter(mInfomationAdapter);
                        }
                    }
                }
            }
        });
    }
    // NotificationListener end

    // RequestRefreshListner begin
    @Override
    public void refreshRequestUi() {
        Log.d(TAG, "refreshRequestUi: " );
        mBasicHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mRequestQuickDialog != null) {
                    ImageView requestView = (ImageView) findViewById(R.id.head_request);
                    if(requestView == null) {
                        return;
                    }

                    ArrayList<Requests> requeslist = orm.buildRequestList("", getSceneId());
                    if(requeslist != null && requeslist.size() > 0) {
                        Drawable requestIcon = getResources().getDrawable(R.drawable.request_icon_light);
                        requestView.setImageBitmap(generatorTargetCountIcon(requestIcon, requeslist.size()));
                    }else {
                        requestView.setImageResource(R.drawable.request_icon);
                    }

                    if(mRequestAdapter == null) {
                        mRequestAdapter = new RequestsAdapter(BaseNotificationActivity.this);
                        mRequestQuickDialog.setListAdapter(mRequestAdapter);
                    }
                    mRequestAdapter.alterRequests(requeslist);
                }
            }
        });
    }
    // RequestRefreshListner end

    // RequestListener begin
    @Override
    public void requestUpdated(final ArrayList<Requests> data) {
        if(QiupuConfig.LOGD)Log.d(TAG, "requestUpdated, requests count: " + (data != null ? data.size() : 0 ));
        mBasicHandler.post(new Runnable() {
            @Override
            public void run() {
                ImageView requestView = (ImageView) findViewById(R.id.head_request);
                if(requestView == null) {
                    return ;
                }
                if(data != null && data.size() > 0) {
                    Drawable requestIcon = getResources().getDrawable(R.drawable.request_icon_light);
                    requestView.setImageBitmap(generatorTargetCountIcon(requestIcon, data.size()));
                }else {
                    requestView.setImageResource(R.drawable.request_icon);
                }

                //refresh request list
                if(mRequestQuickDialog != null) {
                    mRequestQuickDialog.OnRefreshComplete();
                    if(mRequestAdapter == null) {
                        mRequestAdapter = new RequestsAdapter(BaseNotificationActivity.this);
                    }
                    mRequestAdapter.alterRequests(data);
                    mRequestQuickDialog.setListAdapter(mRequestAdapter);
                }
            }
        });
    }
    // RequestListener end
}

