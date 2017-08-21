package com.fz.googleplayteach.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.fz.googleplayteach.http.manager.ThreadManager;
import com.fz.googleplayteach.ui.holder.BaseHolder;
import com.fz.googleplayteach.ui.holder.MoreHolder;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/25.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    private ArrayList<T> data;
    private static final int TYPE_NORMAL=1;//正常布局类型
    private static final int TYPE_MORE=0;//更多布局类型
    private boolean isLoadMore=false;

    public MyBaseAdapter(ArrayList<T> data){
        this.data=data;
    }
    @Override
    public int getCount() {
        return data.size()+1;//增加加载更多布局
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    //返回当前位置展示的布局类型
    public int getItemViewType(int position) {
        if (position==getCount()-1){
            return TYPE_MORE;
        }else {
            return getInnerType(position);
        }
    }

    //子类可以重写此方法来更改返回的布局类型
    public int getInnerType(int position){
        return TYPE_NORMAL;//默认就是普通类型
    }

    @Override
    //返回布局类型个数
    public int getViewTypeCount() {
        return 2;//普通布局和加载更多布局
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder baseHolder;
        if (convertView==null){
            if (getItemViewType(position)==TYPE_MORE){
                //加载更多类型
                baseHolder=new MoreHolder(hasMore());
            }else {
                baseHolder=getHolder(position);
            }
        }else {
            baseHolder= (BaseHolder) convertView.getTag();
        }
        if (getItemViewType(position)!=TYPE_MORE) {
            baseHolder.setData(getItem(position));
        }else {
            //一旦加载更多布局展示出来，就开始加载更多
            if (((MoreHolder)baseHolder).getData()==MoreHolder.STATE_MORE_MORE){
                loadMore((MoreHolder) baseHolder);
            }
        }
        return baseHolder.getRootView();
    }

    //子类可以重写此方法来决定是否可以加载更多
    public boolean hasMore(){
        return true;//默认都是有更多数据的
    }

    public abstract BaseHolder getHolder(int position);

    //加载更多数据
    public void loadMore(final MoreHolder holder){
        if (!isLoadMore){
            isLoadMore=true;
            ThreadManager.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<T> moreData = onLoadMore();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreData != null) {
                                if (moreData.size()<20){//每一页有20条数据，如果返回的数据小于20条，就认为到了最后一页
                                    holder.setData(MoreHolder.STATE_MORE_NONE);
                                    Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                                }else {
                                    holder.setData(MoreHolder.STATE_MORE_MORE);//还有更多数据
                                }
                                //将更多数据追加到当前集合中
                                data.addAll(moreData);
                                MyBaseAdapter.this.notifyDataSetChanged();
                            } else {
                                //加载更多失败
                                holder.setData(MoreHolder.STATE_MORE_ERROR);
                            }
                            isLoadMore=false;
                        }
                    });
                }
            });
        }
    }

    //加载更多数据，必须由子类实现
    public abstract ArrayList<T> onLoadMore();

    public int getListSize(){
        return data.size();
    }
}
