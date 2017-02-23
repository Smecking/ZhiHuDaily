package com.dyman.zhihudaily.module.news;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.dyman.zhihudaily.R;
import com.dyman.zhihudaily.ZhiHuDailyApp;
import com.dyman.zhihudaily.adapter.CommentAdapter;
import com.dyman.zhihudaily.adapter.listener.AdapterItemClickListener;
import com.dyman.zhihudaily.base.BaseActivity;
import com.dyman.zhihudaily.base.IntentKeys;
import com.dyman.zhihudaily.entity.CommentsInfo;
import com.dyman.zhihudaily.network.RetrofitHelper;
import com.dyman.zhihudaily.utils.DividerItemDecoration;
import com.dyman.zhihudaily.utils.ToastUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CommentActivity extends BaseActivity {

    private static final String TAG = CommentActivity.class.getSimpleName();

    private Toolbar toolbar;

    private RecyclerView longCommentRv;

    private RecyclerView shortCommentRv;

    private CommentAdapter longCAdapter;
    private CommentAdapter shortCAdapter;

    private int newID;

    private int sCommentNum = 0;
    private int lCommentNum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initToolbar();
        initView();
        initData();
        loadData();
    }


    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("0条点评");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private void initView() {

        longCAdapter = new CommentAdapter(ZhiHuDailyApp.getInstance());
        shortCAdapter = new CommentAdapter(ZhiHuDailyApp.getInstance());

        longCommentRv = (RecyclerView) findViewById(R.id.longComment_rv_activity_comment);
        longCommentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        longCommentRv.setItemAnimator(new DefaultItemAnimator());
        longCommentRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        longCommentRv.setAdapter(longCAdapter);
        longCAdapter.setAdapterItemClickListener(new AdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(int position) {
                ToastUtil.ShortToast("点击了长评论 第"+position+"个");
            }
        });

        shortCommentRv = (RecyclerView) findViewById(R.id.shortComment_rv_activity_comment);
        shortCommentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        shortCommentRv.setItemAnimator(new DefaultItemAnimator());
        shortCommentRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        shortCommentRv.setAdapter(shortCAdapter);
        shortCAdapter.setAdapterItemClickListener(new AdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(int position) {
                ToastUtil.ShortToast("点击了短评论 第"+position+"个");
            }
        });
    }


    private void initData() {

        newID = getIntent().getIntExtra(IntentKeys.NEWS_ID, 0);
    }


    private void loadData() {

        RetrofitHelper.getZhiHuAPI()
                .getCommentsLong(String.valueOf(newID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentsInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "--load long comment complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //TODO: set EmptyView
                    }

                    @Override
                    public void onNext(CommentsInfo commentsInfo) {
                        longCAdapter.updateAdapter(commentsInfo.getComments());
                        lCommentNum = commentsInfo.getComments().size();
                        updateActionbar();
                    }
                });

        RetrofitHelper.getZhiHuAPI()
                .getCommentsShort(String.valueOf(newID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CommentsInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "--load short comment complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //TODO: set EmptyView
                    }

                    @Override
                    public void onNext(CommentsInfo commentsInfo) {
                        shortCAdapter.updateAdapter(commentsInfo.getComments());
                        sCommentNum = commentsInfo.getComments().size();
                        updateActionbar();
                    }
                });
    }

    /**
     *  更新 Actionbar显示的数据
     */
    private void updateActionbar() {

        getSupportActionBar().setTitle((lCommentNum+sCommentNum) + "条点评");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}