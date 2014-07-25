package com.borqs.qiupu.fragment;

import android.support.v4.app.Fragment;

import twitter4j.UserCircle;

public interface OrganizationExtraCallBack {
    public void getStreamRightFlipperFragment(Fragment fragment);
    public UserCircle getCircleInfo();
    public void startSearch();
    public void hidSearch();
}
