package com.borqs.wutong.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.borqs.account.service.AccountServiceUtils;
import com.borqs.account.service.BorqsAccount;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.ui.bpc.fragment.EventListFragment;

import java.util.ArrayList;

import twitter4j.PollInfo;
import twitter4j.QiupuAlbum;
import twitter4j.UserCircle;

/**
 * Created by yangfeng on 14-7-24.
 */
public class CacheHelper {
    private static CacheHelper _instance;
    private CacheHelper() {
        // no outside instance
    }
    private QiupuORM orm;

    public static CacheHelper getInstance(Context context) {
        if (null == getInstance().orm) {
            getInstance().orm = QiupuORM.getInstance(context.getApplicationContext());
        }
        return _instance;
    }
    public static CacheHelper getInstance() {
        if (_instance == null) {
            _instance = new CacheHelper();
        }
        return _instance;
    }

    public static UserCircle queryOneCircleWithGroup(long circleId) {
        return getInstance().orm.queryOneCircleWithGroup(circleId);
    }

    public static void attach(Context context) {
        getInstance(context);
    }

    // latency compatible begin, to be removed later
    public static QiupuORM getOrm(Context context) {
        return getInstance(context).orm;
    }

    public static QiupuORM getOrm()
    {
        return getInstance().orm;
    }

    public static String getSceneId() {
        return getInstance().orm.getSettingValue(QiupuORM.HOME_ACTIVITY_ID);
    }

    public static String getUserProfileImageUrl(long uid) {
        return getInstance().orm.getUserProfileImageUrl(uid);
    }

    public static String getCurrentApiUrl() {
        return getInstance().orm.getCurrentApiUrl();
    }

    public static boolean isUsingTestURL() {
        return getInstance().orm.isUsingTestURL();
    }

    public static boolean isOpenPublicCircle() {
        return getInstance().orm.isOpenPublicCircle();
    }

    public static void checkExpandCirCle() {
        getInstance().orm.checkExpandCirCle();
    }

    public static Cursor queryInCircleCircles(long circleid) {
        return getInstance().orm.queryInCircleCircles(circleid);
    }

    public static void insertOneCircle(UserCircle circle) {
        getInstance().orm.insertOneCircle(circle);
    }

    public static void insertOneCircleCircles(long parent_id, UserCircle circle) {
        getInstance().orm.insertOneCircleCircles(parent_id, circle);
    }

    public static void deleteCircleByCricleId(long uid, String circleId) {
        getInstance().orm.deleteCircleByCricleId(uid, circleId);
    }

    public static void deleteCacheCircleCircle(UserCircle circle) {
        getInstance().orm.deleteCacheCircleCircle(circle);
    }

    public static void updateUserInfoInCircle(long uid, String circleId, String circleName) {
        getInstance().orm.updateUserInfoInCircle(uid, circleId, circleName);
    }

    public static void updatePageInfo(long pageid, ContentValues values) {
        getInstance().orm.updatePageInfo(pageid, values);
    }

    public static Cursor queryAllCircleList() {
        return getInstance().orm.queryAllCircleList();
    }

    public static Cursor queryChildCircleList(long circleid) {
        return getInstance().orm.queryChildCircleList(circleid);
    }

    public static void insertQiupuAlbumList(ArrayList<QiupuAlbum> albums, long uid) {
        getInstance().orm.insertQiupuAlbumList(albums, uid);
    }

    public static long getCurrentUserId() {
        return AccountServiceUtils.getBorqsAccountID();
    }

    public static String getCurrentUserName() {
        String nickName = getInstance().orm.getUserName(getCurrentUserId());
        if (TextUtils.isEmpty(nickName)) {
            BorqsAccount account = AccountServiceUtils.getBorqsAccount();
            if (null == account) {
                nickName = "";
            } else {
                nickName = account.nickname;
            }
        }
        return TextUtils.isEmpty(nickName) ? "" : nickName;
    }

    public static void insertPollList(ArrayList<PollInfo> pollList, int type) {
        getInstance().orm.insertPollList(pollList, type);
    }

    public static void insertCirclePollList(ArrayList<PollInfo> pollList) {
        getInstance().orm.insertCirclePollList(pollList);
    }

    public static void deletePollInfo(String pollId) {
        getInstance().orm.deletePollnfo(pollId);
    }

    public static void insertEventsList(Context context, ArrayList<UserCircle> circles) {
        getInstance().orm.insertEventsList(context, circles);
    }

    public static Cursor queryUpcomingEvents() {
        return getInstance().orm.queryUpcomingEvents();
    }

    public static Cursor queryPastEvents() {
        return getInstance().orm.queryPastEvents();
    }

    // latency compatible end
}
