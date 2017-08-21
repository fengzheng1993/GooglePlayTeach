package com.fz.googleplayteach.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.protocol.HomeProtocol;
import com.fz.googleplayteach.ui.activity.HomeDetailActivity;
import com.fz.googleplayteach.ui.adapter.MyBaseAdapter;
import com.fz.googleplayteach.ui.holder.BaseHolder;
import com.fz.googleplayteach.ui.holder.HomeHeaderHolder;
import com.fz.googleplayteach.ui.holder.HomeHolder;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/24.
 * 主页
 */

public class HomeFragment extends BaseFragment {
    private ArrayList<AppInfo> data;
    private ArrayList<String> pictureData;

    @Override
    /*运行在子线程，可以直接执行网络耗时操作*/
    public LoadingPage.ResultState onLoad() {
        HomeProtocol protocol=new HomeProtocol();
        data= (ArrayList<AppInfo>) protocol.getData(0);//加载第一页数据
        pictureData = protocol.getPictureData();

        //请求网络
        return check(data);
    }

    @Override
    /*如果加载数据成功，就回调此方法,运行在主线程*/
    public View onCreateSuccessView() {
        ListView view = new ListView(UIUtils.getContext());
        view.setDivider(null);//去掉分割线

        //给listview增加头布局展示轮播条
        HomeHeaderHolder header=new HomeHeaderHolder();
        view.addHeaderView(header.getRootView());
        if (pictureData!=null){
            header.setData(pictureData);//设置轮播条数据
        }
        view.setAdapter(new HomeAdapter(data));

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appData=data.get(position-1);//去掉头布局
                if (appData!=null){
                    Intent intent=new Intent(UIUtils.getContext(), HomeDetailActivity.class);
                    intent.putExtra("packageName",appData.packageName);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    class HomeAdapter extends MyBaseAdapter<AppInfo> {

        public HomeAdapter(ArrayList<AppInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            return new HomeHolder();
        }

        //子线程中调用
        @Override
        public ArrayList<AppInfo> onLoadMore() {
            HomeProtocol protocol=new HomeProtocol();
            ArrayList<AppInfo> moreData= (ArrayList<AppInfo>) protocol.getData(getListSize());
            return moreData;
        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder=null;
//            if (convertView==null){
//                  //1.加载布局文件
                    //2.初始化控件 findViewById
//                convertView=UIUtils.inflate(R.layout.list_item_home);
//                viewHolder=new ViewHolder();
//                viewHolder.textView= (TextView) convertView.findViewById(R.id.tv_tittle);
                     //3.设置tag
//                convertView.setTag(viewHolder);
//            }else {
//                viewHolder= (ViewHolder) convertView.getTag();
//            }
                    //4.根据数据刷新界面
//            viewHolder.textView.setText(getItem(position));
//            return convertView;
//        }
//    }
//    static class ViewHolder{
//        TextView textView;
//    }
    }

}
