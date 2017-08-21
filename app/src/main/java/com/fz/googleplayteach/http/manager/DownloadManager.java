package com.fz.googleplayteach.http.manager;

import android.content.Intent;
import android.net.Uri;

import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.domain.DownloadInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.IOUtils;
import com.fz.googleplayteach.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 冯政 on 2017/7/5.
 * 下载管理者（单例模式----静态内部类）
 * DownloadManager ：被观察者，有责任通知所有的观察者状态和进度发生变化(观察者模式)
 */

public class DownloadManager {
    public static final int STATE_UNDO=1;
    public static final int STATE_WAITING=2;
    public static final int STATE_DOWNLOADING=3;
    public static final int STATE_PAUSE=4;
    public static final int STATE_ERROR=5;
    public static final int STATE_SUCCESS=6;

    /*
    *4. 观察者集合*/
    private ArrayList<DownloadObserver> mObservers=new ArrayList<>();

    //下载对象的集合
//    private HashMap<String,DownloadInfo> mDownloadInfoMap=new HashMap<>();
    private ConcurrentHashMap<String,DownloadInfo> mDownloadInfoMap=new ConcurrentHashMap<>();//考虑线程安全问题

    //下载任务的集合
    private ConcurrentHashMap<String,DownloadTask> mDownloadTaskMap=new ConcurrentHashMap<>();

    private DownloadManager(){

    }
    private static class DownloadManagerHolder{
        private static final DownloadManager mInstance=new DownloadManager();
    }
    public static final DownloadManager getInstance(){
        return DownloadManagerHolder.mInstance;
    }

    /*
    * 2. 注册观察者*/
    public void registerObserver(DownloadObserver observer){
        if (observer!=null&&!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    /*
    * 3. 注销观察者*/
    public void unRegisterObserver(DownloadObserver observer){
        if (observer!=null&&mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    /*
    * 1. 声明观察者的接口*/
    public interface DownloadObserver{
        void onDownloadStateChanged(DownloadInfo downloadInfo);//下载状态发生变化
        void onDownloadProgressChanged(DownloadInfo downloadInfo);//下载进度发生变化
    }

    /*
    * 5. 通知下载状态发生变化*/
    public void notifyDownloadStateChanged(DownloadInfo downloadInfo){
        for (DownloadObserver observer:mObservers) {
            observer.onDownloadStateChanged(downloadInfo);
        }
    }

    /*
    * 6. 通知下载进度发生变化*/
    public void notifyDownloadProgressChanged(DownloadInfo downloadInfo){
        for (DownloadObserver observer:mObservers) {
            observer.onDownloadProgressChanged(downloadInfo);
        }
    }

    //开始下载
    public synchronized void download(AppInfo appInfo){
        //如果对象是第一次下载，就需要创建一个新的DownloadInfo对象，从头下载
        //如果之前下载过，要接着下载，实现断点续传
        DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
        if (downloadInfo==null){
            downloadInfo=DownloadInfo.appInfoCopy(appInfo);//生成一个下载的对象
        }
        downloadInfo.currentState= DownloadManager.STATE_WAITING;//状态切换为等待下载
        notifyDownloadStateChanged(downloadInfo);

        System.out.println(downloadInfo.name+"等待下载");

        //将下载对象放入集合中
        mDownloadInfoMap.put(downloadInfo.id,downloadInfo);

        //初始化下载任务，并放入线程池中运行
        DownloadTask task=new DownloadTask(downloadInfo);
        ThreadManager.getInstance().execute(task);

        //将下载任务放入集合中
        mDownloadTaskMap.put(downloadInfo.id,task);
    }

    class DownloadTask implements Runnable{
        private DownloadInfo downloadInfo;
        private HttpHelper.HttpResult httpResult;

        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo=downloadInfo;
        }

        @Override
        public void run() {
            System.out.println(downloadInfo.name+"开始下载");

            downloadInfo.currentState=STATE_DOWNLOADING;//状态切换为正在下载
            notifyDownloadStateChanged(downloadInfo);

            File file=new File(downloadInfo.path);
            if (!file.exists()||file.length()!=downloadInfo.currentPos||downloadInfo.currentPos==0){
                //从头开始下载
                //删除无效文件
                file.delete();
                downloadInfo.currentPos=0;//当前位置置为0

                //从头开始下载
                httpResult = HttpHelper.download(HttpHelper.URL+"DownloadServlet?name="+downloadInfo.downloadUrl);
            }else {
                //断点续传  range 表示请求服务器从文件的哪个位置开始返回数据
                httpResult=HttpHelper.download(HttpHelper.URL+"DownloadServlet?name="+downloadInfo.downloadUrl+"&range="+file.length());

            }
            if (httpResult!=null&&httpResult.getInputStream()!=null){
                InputStream is=httpResult.getInputStream();
                FileOutputStream out = null;
                try {
                    out=new FileOutputStream(file,true);//要在原有文件基础上追加数据，参数2设为true
                    byte[] buffer=new byte[5*1024];
                    int len=0;
                    //只有状态是正在下载才继续轮询，解决下载过程中暂停的问题
                    while ((len=is.read(buffer))!=-1&&downloadInfo.currentState==STATE_DOWNLOADING){
                        out.write(buffer,0,len);
                        out.flush();//把剩余数据刷入本地

                        //更新下载进度
                        downloadInfo.currentPos+=len;
                        notifyDownloadProgressChanged(downloadInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    IOUtils.close(is);
                    IOUtils.close(out);
                }

                //文件下载结束
                if (file.length()==downloadInfo.size){
                    //文件完整，表示下载成功
                    downloadInfo.currentState=STATE_SUCCESS;
                    notifyDownloadStateChanged(downloadInfo);
                }else if(downloadInfo.currentState==STATE_PAUSE){
                    //中途暂停
                    notifyDownloadStateChanged(downloadInfo);
                }else {
                    //下载失败
                    file.delete();//删除无效文件
                    downloadInfo.currentState=STATE_ERROR;
                    downloadInfo.currentPos=0;
                    notifyDownloadStateChanged(downloadInfo);
                }

            }else {
                //网络异常
                file.delete();//删除无效文件
                downloadInfo.currentState=STATE_ERROR;
                downloadInfo.currentPos=0;
                notifyDownloadStateChanged(downloadInfo);
            }
            //从集合中移除下载任务
            mDownloadTaskMap.remove(downloadInfo.id);
        }

    }

    //下载暂停
    public synchronized void pause(AppInfo appInfo){
        DownloadInfo downloadInfo=mDownloadInfoMap.get(appInfo.id);//取出下载对象

        if (downloadInfo!=null){
            //只有正在下载和等待下载时才需要暂停
            if (downloadInfo.currentState==STATE_DOWNLOADING||downloadInfo.currentState==STATE_WAITING){
                downloadInfo.currentState=STATE_PAUSE;//将下载状态切换为暂停
                notifyDownloadStateChanged(downloadInfo);

                DownloadTask task=mDownloadTaskMap.get(downloadInfo.id);
                if (task!=null){
                    ThreadManager.getInstance().cancel(task);//移除下载任务
                }
            }
        }

    }

    //开始安装
    public synchronized void install(AppInfo appInfo){
        //跳转到系统的安装页面进行安装
        DownloadInfo downloadInfo=DownloadInfo.appInfoCopy(appInfo);
        if (downloadInfo!=null){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://"+downloadInfo.path),"application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    //根据应用信息返回下载对象
    public DownloadInfo getDownloadInfo(AppInfo appInfo){
        return mDownloadInfoMap.get(appInfo.id);
    }
}
