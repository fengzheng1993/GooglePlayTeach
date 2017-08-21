package com.fz.googleplayteach.ui.holder;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/25.
 */

public abstract class BaseHolder<T> {
    private View mRootView;//一个item布局
    private T data;
    public BaseHolder(){
        mRootView=initView();
        //3.设置tag
        mRootView.setTag(this);
    }

    public View getRootView(){
        return mRootView;
    }

    //1.初始化布局文件
    //2.初始化控件 findViewById
    public abstract View initView();

    //设置当前item的数据
    public void setData(T data){
        this.data=data;
        refreshView(data);
    }

    //获取当前item的数据
    public T getData(){
        return data;
    }

    //4.根据数据刷新界面
    public abstract void refreshView(T data);
}
