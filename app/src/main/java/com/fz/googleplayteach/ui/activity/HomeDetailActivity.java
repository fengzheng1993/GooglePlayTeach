package com.fz.googleplayteach.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.protocol.HomeDetailProtocol;
import com.fz.googleplayteach.ui.holder.DetailAppInfoHolder;
import com.fz.googleplayteach.ui.holder.DetailDesHolder;
import com.fz.googleplayteach.ui.holder.DetailDownloadHolder;
import com.fz.googleplayteach.ui.holder.DetailPicsHolder;
import com.fz.googleplayteach.ui.holder.DetailSafeHolder;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/7/2.
 */

public class HomeDetailActivity extends BaseActivity {

    private LoadingPage loadingPage;
    private ActionBar actionBar;
    private String packageName;
    private AppInfo data;
    private HorizontalScrollView hsvDetailPicInfo;
    private FrameLayout flDetailDesInfo;
    private FrameLayout flDetailDownInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);//设置actionBar左侧的图标
        actionBar.setDisplayHomeAsUpEnabled(true);//显示返回键

        packageName = getIntent().getStringExtra("packageName");

        loadingPage = new LoadingPage(this) {
            @Override
            public ResultState onLoad() {
                return HomeDetailActivity.this.onLoad();
            }

            @Override
            public View onCreateSuccessView() {
                return HomeDetailActivity.this.onCreateSuccessView();
            }
        };
        setContentView(loadingPage);

        //开始加载网络数据
        loadingPage.loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateSuccessView(){
        //初始化成功的布局
        View view= UIUtils.inflate(R.layout.page_home_detail);
        //初始化应用信息模块
        FrameLayout flDetailAppInfo= (FrameLayout) view.findViewById(R.id.fl_detail_appInfo);
        //给帧布局填充页面
        DetailAppInfoHolder appInfoHolder=new DetailAppInfoHolder();
        flDetailAppInfo.addView(appInfoHolder.getRootView());
        appInfoHolder.setData(data);

        //初始化安全描述模块
        FrameLayout flDetailSafeInfo= (FrameLayout) view.findViewById(R.id.fl_detail_safeInfo);
        //给帧布局填充页面
        DetailSafeHolder safeHolder=new DetailSafeHolder();
        flDetailSafeInfo.addView(safeHolder.getRootView());
        safeHolder.setData(data);

        //初始化截图模块
        hsvDetailPicInfo = (HorizontalScrollView) view.findViewById(R.id.hsv_detail_pics);
        //给HorizontalScrollView填充页面
        DetailPicsHolder picsHolder=new DetailPicsHolder();
        hsvDetailPicInfo.addView(picsHolder.getRootView());
        picsHolder.setData(data);

        //初始化描述模块
        flDetailDesInfo = (FrameLayout) view.findViewById(R.id.fl_detail_des);
        //给帧布局填充页面
        DetailDesHolder desHolder=new DetailDesHolder();
        flDetailDesInfo.addView(desHolder.getRootView());
        desHolder.setData(data);

        //初始化下载模块
        flDetailDownInfo = (FrameLayout) view.findViewById(R.id.fl_detail_download);
        //给帧布局填充页面
        DetailDownloadHolder downloadHolder=new DetailDownloadHolder();
        flDetailDownInfo.addView(downloadHolder.getRootView());
        downloadHolder.setData(data);
        return view;
    }

    public LoadingPage.ResultState onLoad(){
        //请求网络，加载数据
        HomeDetailProtocol protocol=new HomeDetailProtocol(packageName);
        data = protocol.getData(0);
        if (data !=null){
            return LoadingPage.ResultState.STATE_SUCCESS;
        }else {
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }
}
