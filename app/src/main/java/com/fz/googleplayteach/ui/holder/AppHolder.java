package com.fz.googleplayteach.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.domain.DownloadInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.http.manager.DownloadManager;
import com.fz.googleplayteach.ui.view.ProgressArc;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

import java.io.File;

/**
 * Created by 冯政 on 2017/6/25.
 */

public class AppHolder extends BaseHolder<AppInfo> implements DownloadManager.DownloadObserver,View.OnClickListener {

    private TextView tvName,tvSize,tvDes;
    private RatingBar rbStar;
    private ImageView ivIcon;
    private FrameLayout flProgress;
    private ProgressArc pbProgress;
    private DownloadManager manager;
    private int mCurrentState;
    private float mCurrentProgress;
    private TextView tvDownload;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.list_item_home);
        tvName= (TextView) view.findViewById(R.id.tv_name);
        tvDes= (TextView) view.findViewById(R.id.tv_des);
        tvSize= (TextView) view.findViewById(R.id.tv_size);
        rbStar = (RatingBar) view.findViewById(R.id.rb_star);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvDownload = (TextView) view.findViewById(R.id.tv_download);

        flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
        flProgress.setOnClickListener(this);
        pbProgress = new ProgressArc(UIUtils.getContext());
        pbProgress.setArcDiameter(UIUtils.dip2px(26));//设置圆形进度条
        pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));//设置进度条颜色
        //设置进度条宽高
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(UIUtils.dip2px(28), UIUtils.dip2px(28));
        flProgress.addView(pbProgress, params);

        manager = DownloadManager.getInstance();
        manager.registerObserver(this);//注册观察者，监听状态和进度变化
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(),data.size));
        tvDes.setText(data.des);
        tvName.setText(data.name);
        rbStar.setRating(data.stars);
        BitmapHelper.getInstance().display(ivIcon, HttpHelper.URL+"ImageServlet?name="+data.iconUrl);

        //判断当前应用是否下载过
        DownloadInfo downloadInfo = manager.getDownloadInfo(data);

        if (downloadInfo != null) {

            //之前下载过
            mCurrentState = downloadInfo.currentState;
            mCurrentProgress = downloadInfo.getProgress();
        } else {
            //没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mCurrentProgress = 0;
        }

        //判断之前是否下载完成过
        DownloadInfo info=DownloadInfo.appInfoCopy(data);
        File file=new File(info.path);
        if (file.exists()&&file.length()==data.size){
            mCurrentState =DownloadManager.STATE_SUCCESS;
        }

        refreshUI(mCurrentState, mCurrentProgress,data.id);//根据状态和进度更新UI
    }

    private void refreshUI(int currentState, float currentProgress,String id) {
        if (!getData().id.equals(id)){
            return;
        }
        mCurrentState = currentState;
        mCurrentProgress = currentProgress;
        switch (currentState) {
            case DownloadManager.STATE_UNDO://未下载
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);//没有进度
                tvDownload.setText("下载");
                break;
            case DownloadManager.STATE_WAITING://等待下载
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);//等待模式
                tvDownload.setText("等待");
                break;
            case DownloadManager.STATE_DOWNLOADING://正在下载
                pbProgress.setBackgroundResource(R.drawable.ic_pause);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
                tvDownload.setText((int) (currentProgress * 100) + "%");
                pbProgress.setProgress(mCurrentProgress, true);//设置下载进度
                break;
            case DownloadManager.STATE_PAUSE://暂停下载
                pbProgress.setBackgroundResource(R.drawable.ic_resume);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("暂停");
                break;
            case DownloadManager.STATE_ERROR://下载失败
                pbProgress.setBackgroundResource(R.drawable.ic_redownload);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS://下载成功
                pbProgress.setBackgroundResource(R.drawable.ic_install);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tvDownload.setText("安装");
                break;
            default:
                break;
        }
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }
    //主线程更新UI
    private void refreshUIOnMainThread(final DownloadInfo downloadInfo) {
        //判断下载对象是否是当前应用
        AppInfo appInfo = getData();
        if (appInfo.id.equals(downloadInfo.id)) {
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(downloadInfo.currentState, downloadInfo.getProgress(),downloadInfo.id);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_progress:
                //根据当前状态来决定下一步操作
                if (mCurrentState==DownloadManager.STATE_UNDO
                        ||mCurrentState==DownloadManager.STATE_PAUSE
                        ||mCurrentState==DownloadManager.STATE_ERROR){
                    manager.download(getData());
                }else if(mCurrentState==DownloadManager.STATE_DOWNLOADING||mCurrentState==DownloadManager.STATE_WAITING){
                    manager.pause(getData());
                }else if (mCurrentState==DownloadManager.STATE_SUCCESS){
                    manager.install(getData());
                }
                break;
            default:
                break;
        }
    }

}
