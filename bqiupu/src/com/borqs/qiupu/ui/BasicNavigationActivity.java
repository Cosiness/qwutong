package com.borqs.qiupu.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.borqs.qiupu.R;
import com.borqs.qiupu.fragment.FriendsListFragment;
import com.borqs.qiupu.fragment.StreamListFragment;
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
    // reside menu begin
    private ResideMenu resideMenu;
    //    private BpcPostsNewActivity mContext;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemSettings;

    protected void setUpMenu() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this, R.layout.stream_fragment_activity);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        createLeftMenuItems();

        itemCalendar = new ResideMenuItem(this, R.drawable.icon_album, "Calendar");
        itemSettings = new ResideMenuItem(this, R.drawable.menu_setting, "Settings");

        itemCalendar.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (view == itemCalendar){
            changeFragment(new FriendsListFragment());
        }else if (view == itemSettings){
            changeFragment(new StreamListFragment());
        }

        resideMenu.closeMenu();
    }

    private void createItem(int iconId, int labelId, final Class<?> fragmentClass) {
        final ResideMenuItem item = new ResideMenuItem(this, iconId, labelId);
        resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == item) {
                    try {
                        changeFragment((Fragment)fragmentClass.newInstance());
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

    private void createLeftMenuItems() {
        createItem(R.drawable.home_screen_menu_loop_icon_default, R.string.tab_feed, StreamListFragment.class);
        createItem(R.drawable.home_screen_photo_icon_default, R.string.home_album, StreamListFragment.class);
        createItem(R.drawable.friend_group_icon, R.string.tab_friends, StreamListFragment.class);
        createItem(R.drawable.home_screen_menu_people_icon_default, R.string.user_circles, StreamListFragment.class);
        createItem(R.drawable.home_screen_event_icon, R.string.event, StreamListFragment.class);
        createItem(R.drawable.home_screen_voting_icon_default, R.string.poll, StreamListFragment.class);
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
//            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
//            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
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

