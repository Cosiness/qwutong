package com.borqs.wutong;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.borqs.qiupu.R;
import com.borqs.qiupu.ui.SlidingMenuOverlayActivity;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

/**
 * Created with IntelliJ IDEA.
 * User: b608
 * Date: 12-12-20
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */

public abstract class BaseResideMenuActivity extends SlidingMenuOverlayActivity {
    // reside menu begin
    private ResideMenu resideMenu;
    //    private BpcPostsNewActivity mContext;
//    private ResideMenuItem itemCalendar;
//    private ResideMenuItem itemSettings;

    protected void setUpMenu(Class<?> fragmentClass) {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.img1);
        resideMenu.attachToActivity(this, R.layout.stream_fragment_activity);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        createLeftMenuItems();

        createRightMenuItems();

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

//        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//            }
//        });
//        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });
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

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }
    // reside menu end
}

