package com.borqs.qiupu.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.borqs.qiupu.R;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: b608
 * Date: 12-12-20
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */

public abstract class BasicNavigationActivity extends SlidingMenuOverlayActivity {
    // mock for BpcNewPostActivity to compatible with BaseResideMenuActivity
    protected void setUpMenu(Class<?> fragmentClass) {
        setContentView(R.layout.stream_fragment_activity);
    }

    protected void createLeftItem(int home_screen_menu_loop_icon_default, int tab_feed, Class<?> fragmentClass) {
    }
}

