package com.fz.googleplayteach.ui.fragment;

import android.view.View;
import android.widget.ListView;

import com.fz.googleplayteach.domain.SubjectInfo;
import com.fz.googleplayteach.http.protocol.SubjectProtocol;
import com.fz.googleplayteach.ui.adapter.MyBaseAdapter;
import com.fz.googleplayteach.ui.holder.BaseHolder;
import com.fz.googleplayteach.ui.holder.SubjectHolder;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/24.
 * 专题
 */

public class SubjectFragment extends BaseFragment {
    private ArrayList<SubjectInfo> data;

    @Override
    public View onCreateSuccessView() {
        ListView listView=new ListView(UIUtils.getContext());
        listView.setDivider(null);
        listView.setAdapter(new SubjectAdapter(data));
        return listView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        SubjectProtocol protocol=new SubjectProtocol();
        data= (ArrayList<SubjectInfo>) protocol.getData(0);
        return check(data);
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo>{

        public SubjectAdapter(ArrayList<SubjectInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder getHolder(int position) {
            return new SubjectHolder();
        }

        @Override
        public ArrayList<SubjectInfo> onLoadMore() {
            SubjectProtocol protocol=new SubjectProtocol();
            ArrayList<SubjectInfo> moreData= (ArrayList<SubjectInfo>) protocol.getData(getListSize());
            return moreData;
        }
    }
}
