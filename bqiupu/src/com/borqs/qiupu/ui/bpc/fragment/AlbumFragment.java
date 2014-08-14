package com.borqs.qiupu.ui.bpc.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.borqs.account.service.AccountServiceUtils;
import com.borqs.common.adapter.AlbumGridLayoutAdapter;
import com.borqs.common.util.IntentUtil;
import com.borqs.qiupu.R;
import com.borqs.qiupu.db.QiupuORM;
import com.borqs.qiupu.fragment.BasicFragment.BaseExFragment;
import com.borqs.qiupu.ui.bpc.AlbumActivity;
import com.borqs.wutong.utils.CacheHelper;
import com.borqs.wutong.utils.ServiceHelper;

import java.util.ArrayList;

import twitter4j.QiupuAlbum;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

public class AlbumFragment extends BaseExFragment {
    private static final String TAG = "AlbumActivity";
    private long uid = 0;
    private AlbumGridLayoutAdapter gridAdapter;
    private ProgressBar  progressBar1;
    private TextView tv_msg;
    private GridView album_grid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.album_grid_view, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
//        boolean isSupportLeftNavigation  = getIntent().getBooleanExtra("supportLeftNavigation", false);
//        enableLeftNav(isSupportLeftNavigation);

        super.onActivityCreated(savedInstanceState);
//        setContentView(R.layout.album_grid_view);
        progressBar1 = (ProgressBar)findViewById(R.id.progressBar1);
        tv_msg = (TextView)findViewById(R.id.tv_msg);

        overrideRightActionBtn(R.drawable.actionbar_icon_refresh_normal, refreshListener);
        album_grid = (GridView)findViewById(R.id.album_grid);
        uid = getIntent().getLongExtra("uid", 0);
        String username = null;
        username = getIntent().getStringExtra("nick_name");
        setHeadTitle(R.string.home_album);
        setSubTitle(username);
        mAlbums = QiupuORM.queryQiupuAlbums(thiz, uid);
        gridAdapter = new AlbumGridLayoutAdapter(thiz,480,mAlbums);
        album_grid.setAdapter(gridAdapter);
        album_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                IntentUtil.startGridPicIntent(thiz, mAlbums.get(position).album_id, uid,getIntent().getStringExtra("nick_name"),false);
            }

        });
        getAllAlbums();

        onConfigurationChanged(getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation !=  Configuration.ORIENTATION_LANDSCAPE)
        {
            if (getResources().getConfiguration().orientation ==  Configuration.ORIENTATION_PORTRAIT)
            {
                album_grid.setNumColumns(2);
            }
        }
        else
        {
            album_grid.setNumColumns(4);
        }
    }

    View.OnClickListener refreshListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            getAllAlbums();
        }
    };

//    @Override
//    protected void loadRefresh() {
//        super.loadRefresh();
//    };
//
//
//    @Override
//    protected void createHandler() {
//        mHandler = new AlbumsHandler();
//    }
//
//    final int GET_ALBUM_SUCCESS           = 0;
//    final int GET_ALBUM_FAILED           = 1;
//
//    private class AlbumsHandler extends Handler
//    {
//        public AlbumsHandler()
//        {
//            super();
//            Log.d(TAG, "new AlbumsHandler");
//        }
//
//        @Override
//        public void handleMessage(Message msg)
//        {
//            end();
//            progressBar1.setVisibility(View.GONE);
//            switch(msg.what)
//            {
//                case GET_ALBUM_SUCCESS:
//                    if(mAlbums == null) {
//                        return ;
//                    }
//                    gridAdapter.notifyDataSetChanged();
//                    if(mAlbums.size() > 0) {
//                        tv_msg.setVisibility(View.GONE);
//                    }else {
//                        tv_msg.setVisibility(View.VISIBLE);
//                    }
//                    break;
//                case GET_ALBUM_FAILED:
//                    showCustomToast(R.string.loading_failed);
//                    break;
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlbums.clear();
        mAlbums = null;
    }


    ArrayList<QiupuAlbum> mAlbums = new ArrayList<QiupuAlbum>();
    private void getAllAlbums()
    {
        begin();
        tv_msg.setVisibility(View.GONE);
        progressBar1.setVisibility(View.VISIBLE);
        ServiceHelper.getAllAlbums(AccountServiceUtils.getSessionID(), uid, false, new TwitterAdapter() {
            @Override
            public void getAllAlbums(ArrayList<QiupuAlbum> albums) {
                if (mAlbums != null) {

                    for (int i = 0; i < albums.size(); i++) {
                        for (int j = 0; j < mAlbums.size(); j++) {
                            if (albums.get(i).album_id == mAlbums.get(j).album_id) {
                                albums.get(i).have_expired = albums.get(i).compareTo(mAlbums.get(j)) == 1;
                            }

                        }
                    }
                    mAlbums.clear();
                    mAlbums.addAll(albums);

                }
                CacheHelper.insertQiupuAlbumList(albums, uid);
                Log.d(TAG, "finish getAllAlbums=" + albums.size());
                onAlbumFetchOk();

            }

            public void onException(TwitterException ex, TwitterMethod method) {
                onAlbumFetchFail(ex.getMessage());
            }
        });
    }

//    private void onAlbumFetchFail(String text) {
//        Message mds = mHandler.obtainMessage(GET_ALBUM_FAILED);
//        mds.getData().putBoolean(RESULT, false);
//        mds.getData().putString(ERROR_MSG, text);
//        mHandler.sendMessage(mds);
//    }
//
//    private void onAlbumFetchOk() {
//        Message mds = mHandler.obtainMessage(GET_ALBUM_SUCCESS);
//        mds.getData().putBoolean(RESULT, true);
//        mHandler.sendMessage(mds);
//    }

//    @Override
//    protected void uiLoadBegin() {
//        showProgressBtn(true);
//        showRightActionBtn(false);
//
//        if(isUsingActionBar() && getActionBar() != null)
//        {
//            setProgress(500);
//        }
//    }
//
//    @Override
//    protected void uiLoadEnd() {
//        showProgressBtn(false);
//        showRightActionBtn(true);
//
//        if(isUsingActionBar() && getActionBar() != null)
//        {
//            setProgress(10000);
//        }
//    }

    private void onAlbumFetchFail(String text) {
        if (null != thiz && thiz instanceof AlbumActivity) {
            AlbumActivity activity = (AlbumActivity)thiz;
            activity.onAlbumFetchFail(text);
        }
    }

    private void onAlbumFetchOk() {
        if (null != thiz && thiz instanceof AlbumActivity) {
            AlbumActivity activity = (AlbumActivity)thiz;
            activity.onAlbumFetchOk();
        }
    }

    public void onDataUpdated(boolean success) {
        progressBar1.setVisibility(View.GONE);

        if (success) {
            if(mAlbums == null) {
                return ;
            }
            gridAdapter.notifyDataSetChanged();
            if(mAlbums.size() > 0) {
                tv_msg.setVisibility(View.GONE);
            }else {
                tv_msg.setVisibility(View.VISIBLE);
            }
        } else {
            // do nothing here.
        }
    }
}