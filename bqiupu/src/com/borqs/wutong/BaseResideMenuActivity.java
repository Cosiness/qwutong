package com.borqs.wutong;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.borqs.qiupu.R;
import com.borqs.qiupu.db.QiupuORM;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import twitter4j.AsyncQiupu;

/**
 * Created with IntelliJ IDEA.
 * User: b608
 * Date: 12-12-20
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */

public abstract class BaseResideMenuActivity extends BaseNotificationActivity {
    // reside menu begin
    private ResideMenu resideMenu;

    protected void setUpMenu(Class<?> fragmentClass) {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.img1);
        resideMenu.attachToActivity(this, R.layout.stream_fragment_activity);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        createLeftMenuItems();

        createRightMenuItems();

        initCustomizedHeader(resideMenu);
        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        try {
            changeFragment((Fragment)fragmentClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    protected void createLeftItem(int iconId, int labelId, final Class<?> fragmentClass) {
        createItem(iconId, labelId, ResideMenu.DIRECTION_LEFT, fragmentClass);
    }
    protected void createRightItem(int iconId, int labelId, final Class<?> fragmentClass) {
        createItem(iconId, labelId, ResideMenu.DIRECTION_RIGHT, fragmentClass);
    }
    private void createItem(int iconId, int labelId, int type, final Class<?> fragmentClass) {
        final ResideMenuItem item = new ResideMenuItem(this, iconId, labelId);
        resideMenu.addMenuItem(item, type);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == item) {
                    try {
                        Fragment fragment = (Fragment)fragmentClass.newInstance();
                        changeFragment(fragment);
                        resideMenu.closeMenu();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void createRightMenuItems() {
    }

    protected void createLeftMenuItems() {
    }

    protected void onResideMenuOpen() {
    }

    protected void onResideMenuClose() {
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            onResideMenuOpen();
        }

        @Override
        public void closeMenu() {
            onResideMenuClose();
        }
    };

    protected void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // hide the scope of parent's members that shuld be isolate, begin
    private QiupuORM orm;
    private AsyncQiupu asyncQiupu;
    // hidden code end.

    @Override
    protected void initCustomizedHeader(View parent) {
        initHeadViews(parent);
        showSlideToggle(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(resideMenu.DIRECTION_LEFT);
            }
        });
        tryUpdateInitialDetect();
    }
}

