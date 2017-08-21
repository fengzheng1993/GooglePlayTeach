package com.fz.googleplayteach.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.protocol.AppProtocol;
import com.fz.googleplayteach.ui.activity.HomeDetailActivity;
import com.fz.googleplayteach.ui.adapter.MyBaseAdapter;
import com.fz.googleplayteach.ui.holder.AppHolder;
import com.fz.googleplayteach.ui.holder.BaseHolder;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/24.
 * 应用
 */

public class AppFragment extends BaseFragment {
    private ArrayList<AppInfo> data;
    @Override
    public View onCreateSuccessView() {
        ListView view = new ListView(UIUtils.getContext());
        view.setDivider(null);//去掉分割线
        view.setAdapter(new AppAdapter(data));

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appData=data.get(position);
                if (appData!=null){
                    Intent intent=new Intent(UIUtils.getContext(), HomeDetailActivity.class);
                    intent.putExtra("packageName",appData.packageName);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        AppProtocol protocol=new AppProtocol();
        data= (ArrayList<AppInfo>) protocol.getData(0);
        return check(data);
    }
    class AppAdapter extends MyBaseAdapter{

        public AppAdapter(ArrayList data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            return new AppHolder();
        }

        @Override
        public ArrayList<AppInfo> onLoadMore() {
            AppProtocol protocol=new AppProtocol();
            ArrayList<AppInfo> moreData= (ArrayList<AppInfo>) protocol.getData(getListSize());
            return moreData;
        }
    }
}
