package com.fz.googleplayteach.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/24.
 */

public abstract class BaseFragment<T> extends Fragment{

    private LoadingPage mLoadingPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLoadingPage = new LoadingPage(UIUtils.getContext()) {

            @Override
            public ResultState onLoad() {
                return BaseFragment.this.onLoad();
            }

            @Override
            public View onCreateSuccessView() {
                return BaseFragment.this.onCreateSuccessView();
            }
        };
        return mLoadingPage;
    }

    //加载成功的布局，必须由子类来实现
    public abstract View onCreateSuccessView();

    //加载网络数据，必须由子类实现
    public abstract LoadingPage.ResultState onLoad();

    //开始加载数据
    public void loadData(){
        if (mLoadingPage!=null){
            mLoadingPage.loadData();
        }
    }
    //对网络返回数据的合法性进行校验
    public LoadingPage.ResultState check(Object object){
        if (object!=null){
            if (object instanceof ArrayList){
                ArrayList<T> list= (ArrayList<T>) object;
                if (list.isEmpty()){
                    return LoadingPage.ResultState.STATE_EMPTY;
                }else {
                    return LoadingPage.ResultState.STATE_SUCCESS;
                }
            }
        }
        return LoadingPage.ResultState.STATE_ERROR;
    }
}
