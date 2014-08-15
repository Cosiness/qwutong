package com.borqs.wutong.utils;

import android.os.Message;
import android.util.Log;

import com.borqs.account.service.AccountServiceUtils;
import com.borqs.common.util.AsyncApiUtils;
import com.borqs.qiupu.AccountListener;

import java.util.HashMap;

import twitter4j.AsyncQiupu;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationContext;

/**
 * Created by yangfeng on 14-7-24.
 * collect the server requests here with FACADE pattern to isolate the dependency.
 */
public class ServiceHelper {
    private static final String TAG = ServiceHelper.class.getSimpleName();

    private static ServiceHelper _instance;
    private ServiceHelper() {
        // no instance
        asyncQiupu = new AsyncQiupu(ConfigurationContext.getInstance(), null, null);

    }

    private AsyncQiupu asyncQiupu;
    public static AsyncQiupu getAsyncHandle() {
        return getInstance().asyncQiupu;
    }

    public static ServiceHelper getInstance(AccountListener listener) {
        getInstance();
        _instance.asyncQiupu.attachAccountListener(listener);
        return _instance;
    }

    public static ServiceHelper getInstance() {
        if (null == _instance) {
            _instance = new ServiceHelper();
        }

        return _instance;
    }

    public static void getApkDetailInformation(String ticket, String pkgName, boolean needSubversion, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getApkDetailInformation(ticket, pkgName, needSubversion, twitterAdapter);
    }

    public static void attachAccountListener(AccountListener listener) {
        getInstance().asyncQiupu.attachAccountListener(listener);
    }

    public static void sendApproveRequest(final long uid, final String message,
                                          final AsyncApiUtils.AsyncApiSendRequestCallBackListener callback) {

        callback.sendRequestCallBackBegin();
        getInstance().asyncQiupu.sendApproveRequest(AccountServiceUtils.getSessionID(), String.valueOf(uid), message, new TwitterAdapter() {
            public void sendApproveRequest(boolean result) {
                Log.d(TAG, "finish sendApproveRequest :" + result);
                callback.sendRequestCallBackEnd(result, uid);
            }

            public void onException(TwitterException ex, TwitterMethod method) {
                Message msg = new Message();
                msg.getData().putBoolean(AsyncApiUtils.RESULT, false);
                callback.sendRequestCallBackEnd(false, uid);
            }
        });
    }

    public static void postRemoveFavorite(String savedTicket, String objectId, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.postRemoveFavorite(savedTicket, objectId, twitterAdapter);
    }

    public static void postAddFavorite(String savedTicket, String objectId, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.postAddFavorite(savedTicket, objectId, twitterAdapter);
    }

    public static void postLike(String savedTicket, String targetId, String type, TwitterAdapter adapter) {
        getInstance().asyncQiupu.postLike(savedTicket, targetId, type, adapter);
    }

    public static void postUnLike(String savedTicket, String targetId, String type, TwitterAdapter adapter) {
        getInstance().asyncQiupu.postUnLike(savedTicket, targetId, type, adapter);
    }

    public static void getUserCircle(String sessionID, long uid, String circles, boolean withMember,
                                     TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getUserCircle(sessionID, uid, circles, withMember, twitterAdapter);
    }

    public static void setTopList(String savedTicket, String group_id, String stream_id, boolean setTop,
                                  TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.setTopList(savedTicket, group_id, stream_id, setTop, twitterAdapter);
    }

    public static void postRetweet(String savedTicket, String post_id, String tos, String addedContent,
                                   boolean canComment, boolean canLike, boolean canShare, boolean privacy,
                                   TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.postRetweet(savedTicket, post_id, tos, addedContent,
                canComment, canLike, canShare, privacy, twitterAdapter);
    }

    public static void inviteWithMail(String savedTicket, String phoneNumber, String email, String name,
                                      String message, boolean exchange_vcard, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.inviteWithMail(savedTicket, phoneNumber, email, name, message, exchange_vcard, twitterAdapter);
    }

    public static void setCircle(String savedTicket, long uid, String circleid, TwitterAdapter adapter) {
        getInstance().asyncQiupu.setCircle(savedTicket, uid, circleid, adapter);
    }

    public static void exchangeVcard(String savedTicket, long uid, boolean send_request, String circleid, TwitterAdapter adapter) {
        getInstance().asyncQiupu.exchangeVcard(savedTicket, uid, send_request, circleid, adapter);
    }

    public static void getRecommendCategoryList(String savedTicket, boolean isSuggest, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getRecommendCategoryList(savedTicket, isSuggest, twitterAdapter);
    }

    public static void getFriendsListPage(String sessionID, long uid, String circles, int page,
                                          int count, boolean isfollowing, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getFriendsListPage(sessionID, uid, circles, page, count, isfollowing, twitterAdapter);
    }

    public static void createCircle(String sessionID, String circleName, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.createCircle(sessionID, circleName, twitterAdapter);
    }

    public static void usersSet(String savedTicket, String uid, String circleid, boolean isadd, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.usersSet(savedTicket, uid, circleid, isadd, twitterAdapter);
    }

    public static void updateUserInfo(String sessionID, HashMap<String, String> coloumsMap, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.updateUserInfo(sessionID, coloumsMap, twitterAdapter);
    }

    public static void getPostTimeLine(String sessionID, long uid, long circleId,
                                       int pageSize, String s, boolean isNew, String appKey,
                                       int filterType, long categoryId, int fromHome,
                                       TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getPostTimeLine(sessionID, uid, circleId, pageSize, s, isNew, appKey,
                filterType, categoryId, fromHome, twitterAdapter);
    }

    public static void syncPublicCirclInfo(String sessionID, String circleId, boolean with_member,
                                           boolean isEvent, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.syncPublicCirclInfo(sessionID, circleId, with_member, isEvent, twitterAdapter);
    }

    public static void deletePublicCirclePeople(String sessionID, long circleId, String uid,
                                                String admins, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.deletePublicCirclePeople(sessionID, circleId, uid, admins, twitterAdapter);
    }

    public static void deleteCircle(String sessionID, String circleId, int type, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.deleteCircle(sessionID, circleId, type, twitterAdapter);
    }

    public static void getPostTop(String sessionID, long id, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getPostTop(sessionID, id, twitterAdapter);
    }

    public static void getAllAlbums(String sessionID, long uid, boolean withPhotoId, TwitterAdapter twitterAdapter) {
        getInstance().asyncQiupu.getAllAlbums(sessionID, uid, withPhotoId, twitterAdapter);
    }

    public static void getPublicPollList(String sessionID, int page, int count, TwitterListener listener) {
        getInstance().asyncQiupu.getPublicPollList(sessionID, page, count, listener);
    }

    public static void getUserPollList(String ticket, int type, int page, int count, long userId, TwitterListener listener) {
        getInstance().asyncQiupu.getUserPollList(ticket, type, page, count, userId, listener);
    }

    public static void syncEventInfo(String ticket, String circleIds, boolean withMember, TwitterListener listener) {
        getInstance().asyncQiupu.syncEventInfo(ticket, circleIds, withMember, listener);
    }
}
