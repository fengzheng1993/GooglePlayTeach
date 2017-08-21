package com.fz.googleplayteach.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.http.manager.ThreadManager;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/6/24.
 * 根据当前状态来显示不同页面的自定义控件
 *
 * - - 未加载
 * - - 加载中
 * - - 数据为空
 * - - 加载失败
 * - - 加载成功
 */

public abstract class LoadingPage extends FrameLayout {
    private static final int STATE_LOAD_UNDO=1;//未加载
    private static final int STATE_LOAD_LOADING=2;//加载中
    private static final int STATE_LOAD_FAILED=3;//加载失败
    private static final int STATE_LOAD_EMPTY=4;//数据为空
    private static final int STATE_LOAD_SUCCESS=5;//加载成功

    private int mCurrentState=STATE_LOAD_UNDO;//当前状态
    private View mLoadingPage;
    private View mFailedPage;
    private View mEmptyPage;
    private View mSuccessPage;

    public LoadingPage(Context context) {
        super(context);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //初始化加载中的布局
        if (mLoadingPage==null){
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            addView(mLoadingPage);
        }
        //初始化加载失败布局
        if (mFailedPage==null){
            mFailedPage = UIUtils.inflate(R.layout.page_failed);
            //点击重试事件
            Button btnRetry= (Button) mFailedPage.findViewById(R.id.btn_retry);
            btnRetry.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();//重新加载数据
                }
            });
            addView(mFailedPage);
        }
        //初始化数据为空
        if (mEmptyPage==null){
            mEmptyPage = UIUtils.inflate(R.layout.page_empty);
            addView(mEmptyPage);
        }
        showRightPage();
    }

    /*
    * 根据当前状态决定显示哪个布局*/
    private void showRightPage() {
        mLoadingPage.setVisibility((mCurrentState==STATE_LOAD_UNDO||mCurrentState==STATE_LOAD_LOADING)?VISIBLE:GONE);
        mFailedPage.setVisibility((mCurrentState==STATE_LOAD_FAILED)?VISIBLE:GONE);
        mEmptyPage.setVisibility((mCurrentState==STATE_LOAD_EMPTY)?VISIBLE:GONE);

        //当前成功布局为空，并且当前状态为成功，才初始化成功布局
        if (mSuccessPage==null&&mCurrentState==STATE_LOAD_SUCCESS){
            mSuccessPage = onCreateSuccessView();

            if (mSuccessPage!=null){
                addView(mSuccessPage);
            }
        }

        if (mSuccessPage!=null){
            mSuccessPage.setVisibility((mCurrentState==STATE_LOAD_SUCCESS?VISIBLE:GONE));
        }
    }

    //开始加载数据
    public void loadData(){
        if (mCurrentState!=STATE_LOAD_LOADING){//如果当前没有加载，就开始加载数据
            mCurrentState=STATE_LOAD_LOADING;
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final ResultState resultState = onLoad();
                    //运行在主线程
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState();//加载结束后，更新网络状态

                                //根据最新的状态来刷新界面
                                showRightPage();
                            }
                        }
                    });
                }
            });
        }
    }

    //加载网络数据,返回值表示请求网络结束后的状态,必须由子类实现
    public abstract ResultState onLoad();

    public abstract View onCreateSuccessView();//加载成功时的布局

    //网络请求后的状态（枚举类型）
    public enum ResultState{
        STATE_SUCCESS(STATE_LOAD_SUCCESS),
        STATE_EMPTY(STATE_LOAD_EMPTY),
        STATE_ERROR(STATE_LOAD_FAILED);

        private int state;

        private ResultState(int state){
            this.state=state;
        }

        public int getState(){
            return state;
        }
    }
}
