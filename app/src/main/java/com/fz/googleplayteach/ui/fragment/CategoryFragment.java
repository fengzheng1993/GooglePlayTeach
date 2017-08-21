package com.fz.googleplayteach.ui.fragment;

import android.view.View;
import android.widget.ListView;

import com.fz.googleplayteach.domain.CategoryInfo;
import com.fz.googleplayteach.http.protocol.CategoryProtocol;
import com.fz.googleplayteach.ui.adapter.MyBaseAdapter;
import com.fz.googleplayteach.ui.holder.BaseHolder;
import com.fz.googleplayteach.ui.holder.CategoryHolder;
import com.fz.googleplayteach.ui.holder.TittleHolder;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/24.
 * 分类
 */

public class CategoryFragment extends BaseFragment {
    private ArrayList<CategoryInfo> data;
    @Override
    public View onCreateSuccessView() {
        ListView listView=new ListView(UIUtils.getContext());
        listView.setDivider(null);//去掉分割线
        listView.setAdapter(new CategoryAdapter(data));
        return listView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        CategoryProtocol protocol=new CategoryProtocol();
        data= (ArrayList<CategoryInfo>) protocol.getData(0);
        return check(data);
    }

    class CategoryAdapter extends MyBaseAdapter{

        public CategoryAdapter(ArrayList data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            //判断是标题类型还是普通分类类型，来返回不同的Holder
            CategoryInfo categoryData=data.get(position);
            if (categoryData.isTitle){
                return new TittleHolder();
            }else {
                return new CategoryHolder();
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+1;//在原来的基础上加一种布局类型
        }

        @Override
        public int getInnerType(int position) {
            //判断是否是标题类型还是普通分类类型
            if (data.get(position).isTitle){
                return super.getInnerType(position)+1;//标题类型
            }else {
                return super.getInnerType(position);//普通类型
            }
        }

        @Override
        public ArrayList onLoadMore() {
            return null;
        }

        @Override
        public boolean hasMore() {
            return false;//没有更多数据，需要隐藏加载更多布局
        }
    }
}
