package com.borqs.qiupu.ui;

import com.borqs.qiupu.R;

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

    protected void createLeftItem(int icon, int label, Class<?> targetFragmentClass) {
    }
}

