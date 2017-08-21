package com.fz.googleplayteach.ui.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.domain.DownloadInfo;
import com.fz.googleplayteach.http.manager.DownloadManager;
import com.fz.googleplayteach.ui.view.ProgressHorizontal;
import com.fz.googleplayteach.utils.UIUtils;

import java.io.File;

/**
 * Created by 冯政 on 2017/7/4.
 */

public class DetailDownloadHolder extends BaseHolder<AppInfo> implements DownloadManager.DownloadObserver,View.OnClickListener{

    private FrameLayout flProgress;
    private DownloadManager manager;
    private int mCurrentState;
    private float mCurrentProgress;
    private Button btnDownload;
    private ProgressHorizontal pbProgress;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.layout_detail_download);
        btnDownload = (Button) view.findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);
        //初始化自定义进度条
        flProgress = (FrameLayout) view.findViewById(R.id.fl_download);
        flProgress.setOnClickListener(this);
        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);//进度条背景
        pbProgress.setProgressResource(R.drawable.progress_normal);//进度条图片
        pbProgress.setProgressTextSize(UIUtils.dip2px(16));//进度条文字大小
        pbProgress.setProgressTextColor(Color.WHITE);

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        flProgress.addView(pbProgress,params);//给帧布局添加进度条

        manager = DownloadManager.getInstance();
        manager.registerObserver(this);//注册观察者，监听状态和进度变化

        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        //判断当前应用是否下载过
        DownloadInfo downloadInfo=manager.getDownloadInfo(data);
        if (downloadInfo!=null){
            //之前下载过
            mCurrentState = downloadInfo.currentState;
            mCurrentProgress = downloadInfo.getProgress();
        }else {
            //没有下载过
            mCurrentState= DownloadManager.STATE_UNDO;
            mCurrentProgress=0;
        }

        //判断之前是否下载完成过
        DownloadInfo info=DownloadInfo.appInfoCopy(data);
        File file=new File(info.path);
        if (file.exists()&&file.length()==data.size){
            mCurrentState=DownloadManager.STATE_SUCCESS;
        }

        refreshUI(mCurrentState,mCurrentProgress);//根据状态和进度更新UI
    }

    //根据状态和进度更新UI界面
    private void refreshUI(int currentState, float currentProgress) {
        mCurrentState=currentState;
        mCurrentProgress=currentProgress;
        switch (currentState){
            case DownloadManager.STATE_UNDO://未下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载");
                break;
            case DownloadManager.STATE_WAITING://等待下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("等待中...");
                break;
            case DownloadManager.STATE_DOWNLOADING://正在下载
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("");
                pbProgress.setProgress(mCurrentProgress);//设置下载进度
                break;
            case DownloadManager.STATE_PAUSE://暂停下载
                flProgress.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setCenterText("暂停");
                pbProgress.setProgress(mCurrentProgress);//设置下载进度
                break;
            case DownloadManager.STATE_ERROR://下载失败
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS://下载成功
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("安装");
                break;
            default:
                break;
        }
    }

    @Override
    //状态更新
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    //进度更新，子线程运行
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    //主线程更新UI
    private void refreshUIOnMainThread(final DownloadInfo downloadInfo){
        //判断下载对象是否是当前应用
        AppInfo appInfo=getData();
        if (appInfo.id.equals(downloadInfo.id)){
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(downloadInfo.currentState,downloadInfo.getProgress());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        System.out.println("当前状态："+mCurrentState);
        switch (v.getId()){
            case R.id.btn_download:
            case R.id.fl_download:
                //根据当前状态来决定下一步操作
                if (mCurrentState==DownloadManager.STATE_UNDO
                        ||mCurrentState==DownloadManager.STATE_PAUSE
                        ||mCurrentState==DownloadManager.STATE_ERROR){
                    manager.download(getData());
                }else if(mCurrentState==DownloadManager.STATE_DOWNLOADING||mCurrentState==DownloadManager.STATE_WAITING){
                    manager.pause(getData());
                }else if (mCurrentState==DownloadManager.STATE_SUCCESS){
                    System.out.println("开始安装");
                    manager.install(getData());
                }
                break;
            default:
                break;
        }
    }
}
