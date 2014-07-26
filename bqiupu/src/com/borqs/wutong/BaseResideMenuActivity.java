package com.borqs.wutong;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.borqs.qiupu.R;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.ui.BasicActivity;
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

public abstract class BaseResideMenuActivity extends BasicActivity.SimpleBaseActivity {
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

        initCustomizedHeader(resideMenu);
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
//    public ResideMenu getResideMenu(){
//        return resideMenu;
//    }
    // reside menu end

    // hide the scope of parent's members that shuld be isolate, begin
    private QiupuORM orm;
    private AsyncQiupu asyncQiupu;
    // hidden code end.

    @Override
    protected void initCustomizedHeader(View parent) {
//        getPluginItemInfo();
        initHeadViews(parent);
        showSlideToggle(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(resideMenu.DIRECTION_LEFT);
            }
        });
        tryUpdateInitialDetect();

//        refreshRequestNtf();
//        refreshToMeNtf();
//        refreshOtherNtf();
    }

//    private void refreshRequestNtf() {
//        final long sceneId = getSceneId();
//        ImageView requestView = (ImageView) findViewById(R.id.head_request);
//        ArrayList<Requests> requeslist = orm.buildRequestList("", sceneId);
//        if(requeslist != null && requeslist.size() > 0) {
//            Drawable requestIcon = getResources().getDrawable(R.drawable.request_icon_light);
//            requestView.setImageBitmap(generatorTargetCountIcon(requestIcon, requeslist.size()));
//        }else {
//            requestView.setImageResource(R.drawable.request_icon);
//        }
//        requestView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mRequestQuickDialog == null ) {
//                    mRequestQuickDialog = new NtfQuickAction(SlidingMenuOverlayActivity.this,NtfQuickAction.VERTICAL, Notification.ntf_type_request);
//                    mRequestAdapter = new RequestsAdapter(SlidingMenuOverlayActivity.this);
//                    mRequestAdapter.setRequestActionListener(SlidingMenuOverlayActivity.this);
//                    mRequestQuickDialog.setListAdapter(mRequestAdapter);
//                    mRequestAdapter.alterRequests(orm.buildRequestList("", sceneId));
//                    mRequestQuickDialog.setOnDismissListener(SlidingMenuOverlayActivity.this);
//                    mRequestQuickDialog.show(v);
//                }else {
//                    if(mRequestAdapter == null) {
//                        mRequestAdapter = new RequestsAdapter(SlidingMenuOverlayActivity.this);
//                    }
//                    mRequestAdapter.alterRequests(orm.buildRequestList("", sceneId));
//                    mRequestQuickDialog.setListAdapter(mRequestAdapter);
//                    mRequestQuickDialog.show(v);
//                }
//            }
//        });
//    }
//
//    private void refreshToMeNtf() {
//        ImageView tomeView = (ImageView) findViewById(R.id.head_send_me);
//        int count = mOperator.loadUnReadToMeNtfCount();
//        if(count > 0) {
//            Drawable tomeicon = getResources().getDrawable(R.drawable.letter_icon_light);
//            tomeView.setImageBitmap(generatorTargetCountIcon(tomeicon, count));
//        }else {
//            tomeView.setImageResource(R.drawable.letter_icon);
//        }
//        tomeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mToMeQuickDialog == null ) {
//                    mToMeQuickDialog = new NtfQuickAction(SlidingMenuOverlayActivity.this,NtfQuickAction.VERTICAL, Notification.ntf_type_tome);
//                    mInfomationAdapter = new InformationAdapter(SlidingMenuOverlayActivity.this, SlidingMenuOverlayActivity.this);
//                    mToMeQuickDialog.setListAdapter(mInfomationAdapter);
//                    mToMeQuickDialog.setListItemClickListener(infomationItemClickListenter);
//                    mInfomationAdapter.alterDataList(mOperator.loadNtfToMe(""));
//                    mToMeQuickDialog.setOnDismissListener(SlidingMenuOverlayActivity.this);
//                    mToMeQuickDialog.show(v);
//                    //refresh title ToMe ntf icon
////					onNotificationDownloadCallBack(true, 0);
//
//                }else {
//                    if(mInfomationAdapter == null) {
//                        mInfomationAdapter = new InformationAdapter(SlidingMenuOverlayActivity.this);
//                    }
//                    mInfomationAdapter.alterDataList(mOperator.loadNtfToMe(""));
//                    mToMeQuickDialog.setListAdapter(mInfomationAdapter);
//                    mToMeQuickDialog.show(v);
//                }
//            }
//        });
//    }
//    private void refreshOtherNtf() {
//        ImageView otherNtfView = (ImageView) findViewById(R.id.head_ntf);
//        int count = mOperator.loadUnReadOtherNtfCount();
//        if(count > 0) {
//            Drawable otherntfIcon = getResources().getDrawable(R.drawable.notice_icon_light);
//            otherNtfView.setImageBitmap(generatorTargetCountIcon(otherntfIcon, count));
//        }else {
//            otherNtfView.setImageResource(R.drawable.notice_icon);
//        }
//        otherNtfView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // show quick dialog
//                if (mOtherNtfQuickDialog == null) {
//                    mOtherNtfQuickDialog = new NtfQuickAction(SlidingMenuOverlayActivity.this, NtfQuickAction.VERTICAL, Notification.ntf_type_other);
//                    mInfomationAdapter = new InformationAdapter(SlidingMenuOverlayActivity.this, SlidingMenuOverlayActivity.this);
//                    mOtherNtfQuickDialog.setListAdapter(mInfomationAdapter);
//                    mOtherNtfQuickDialog.setListItemClickListener(infomationItemClickListenter);
//                    mInfomationAdapter.alterDataList(mOperator.loadNtfWithOutToMe(""));
//                    mOtherNtfQuickDialog.setOnDismissListener(SlidingMenuOverlayActivity.this);
//                    mOtherNtfQuickDialog.show(v);
//
//                    //refresh title other ntf icon
////					onNotificationDownloadCallBack(false, 0);
//
//                } else {
//                    if (mInfomationAdapter == null) {
//                        mInfomationAdapter = new InformationAdapter(SlidingMenuOverlayActivity.this);
//                    }
//                    mInfomationAdapter.alterDataList(mOperator.loadNtfWithOutToMe(""));
//                    mOtherNtfQuickDialog.setListAdapter(mInfomationAdapter);
//                    mOtherNtfQuickDialog.show(v);
//                }
//            }
//        });
//    }
}

