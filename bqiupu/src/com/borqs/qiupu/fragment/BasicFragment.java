package com.borqs.qiupu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.borqs.common.SelectionItem;
import com.borqs.common.util.DialogUtils;
import com.borqs.qiupu.QiupuConfig;
import com.borqs.qiupu.R;
import com.borqs.qiupu.cache.ImageRun;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.ui.BasicActivity;
import com.borqs.qiupu.ui.bpc.ProgressInterface;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: b608
 * Date: 12-9-7
 * Time: 下午6:49
 * To change this template use File | Settings | File Templates.
 */
public class BasicFragment extends Fragment {

    protected void begin() {
        Activity activity = getActivity();
        if (ProgressInterface.class.isInstance(activity)) {
            ProgressInterface pi = (ProgressInterface) activity;
            pi.begin();
        }
    }

    protected void end() {
        Activity activity = getActivity();
        if (ProgressInterface.class.isInstance(activity)) {
            ProgressInterface pi = (ProgressInterface) activity;
            pi.end();
        }
    }
    
    protected void setViewIcon(final String url, final ImageView view, boolean isIcon) {
		ImageRun imagerun = new ImageRun(null, url, 0);
		imagerun.default_image_index = QiupuConfig.DEFAULT_IMAGE_INDEX_USER;
		imagerun.width = getResources().getDisplayMetrics().widthPixels;
		imagerun.height = imagerun.width;
		imagerun.noimage = true;
		imagerun.addHostAndPath = true;
		if(isIcon)
			imagerun.setRoundAngle=true;
		imagerun.setImageView(view);
		imagerun.post(null);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if (null != activity) {
            inflater.inflate(R.menu.comment_option_menu, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    public static class UserFragment extends BasicFragment {
        protected Cursor musers;
        protected QiupuORM orm;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d("UserFragment", "onCreate");
            super.onCreate(savedInstanceState);
            ensureOrm();
        }

        private boolean ensureOrm() {
            if (null == orm) {
                Context context = getActivity();
                if (null != context) {
                    orm = QiupuORM.getInstance(context);
                }
            }
            return null != orm;
        }

        protected void queryAllSimpleUser() {
            if (ensureOrm()) {
                musers = orm.queryAllSimpleUserInfo();
            } else {
                Log.w("UserFragment", "queryAllSimpleUser skip with uninitialized orm.");
            }
        }
    }

    public static abstract class BaseExFragment extends BasicFragment {
        private View mRootView;

        @Override
        public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
            mRootView = inflater.inflate(getRootViewResourceId(), null);
            return mRootView;
        }

        protected abstract int getRootViewResourceId();

        protected View findViewById(int viewId) {
            if (null != mRootView) {
                return mRootView.findViewById(viewId);
            }
            return null;
        }

        protected Context thiz;
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            thiz = getActivity();
            checkToHideCustomizedBar(thiz);
        }

        private void checkToHideCustomizedBar(Context context) {
            if (context instanceof BasicActivity) {
                // do nothing
            } else {
                View hideView = (View)mRootView.findViewById(R.id.titlebar_container);
                if (null != hideView) {
                    hideView.setVisibility(View.GONE);
                }
            }
        }

        protected void setHeadTitle(int titleId) {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.setHeadTitle(titleId);
            }
        }

        protected void setHeadTitle(String title) {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.setHeadTitle(title);
            }
        }

        protected void setSubTitle(String subTitle) {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.setSubTitle(subTitle);
            }
        }

        protected Intent getIntent() {
            if (null != thiz && thiz instanceof Activity) {
                ((Activity)thiz).getIntent();
            }
            return null;
        }

        protected void overrideRightActionBtn(int iconId, View.OnClickListener listener) {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.overrideRightActionBtn(iconId, listener);
            }
        }

        protected void showTitleSpinnerIcon(boolean enabled) {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.showTitleSpinnerIcon(enabled);
            }
        }

        protected void enableLeftNav() {
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                activity.enableLeftNav();
            }
        }

        protected void showCorpusSelectionDialog(ArrayList<SelectionItem> items, AdapterView.OnItemClickListener listener) {
            int location[];
            if (null != thiz && thiz instanceof BasicActivity) {
                BasicActivity activity = (BasicActivity)thiz;
                location = activity.getCorpusSelectionLocation();
            } else {
                location = new int[2];
                location[0] = 0;
                location[1] = 0;
            }

//            if(mRightActionBtn != null) {
//                int location[] = new int[2];
//                mRightActionBtn.getLocationInWindow(location);
                int x = location[0];
                int y = getResources().getDimensionPixelSize(R.dimen.title_bar_height);

                DialogUtils.showCorpusSelectionDialog(getActivity(), x, y, items, listener);
//            }
        }

    }
}
