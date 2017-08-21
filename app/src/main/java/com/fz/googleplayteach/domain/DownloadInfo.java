package com.fz.googleplayteach.domain;

import android.os.Environment;

import com.fz.googleplayteach.http.manager.DownloadManager;

import java.io.File;

/**
 * Created by 冯政 on 2017/7/5.
 */

public class DownloadInfo {
    public String id;
    public String downloadUrl;
    public String packageName;
    public String name;
    public long size;

    public long currentPos;//当前下载位置
    public int currentState;//当前下载状态
    public String path;//文件下载到本地的路径

    public static final String GOOGLE_PLAY_MARKET="GooglePlay";
    public static final String DOWNLOAD="download";

    //获取下载进度（0-1）
    public float getProgress(){
        if (size==0){
            return 0;
        }
        float progress=currentPos/(float)size;
        return progress;
    }

    //获取下载路径
    public String getFilePath(){
        StringBuffer sb=new StringBuffer();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath()).
                append(File.separator).append(GOOGLE_PLAY_MARKET).
                append(File.separator).append(DOWNLOAD);
        if (createDir(sb.toString())){
            //文件夹存在或者已经创建完成
            return sb.toString()+File.separator+name+".apk";//返回文件路径
        }
        return null;
    }

    public boolean createDir(String dirPath){
        File file=new File(dirPath);
        if (!file.exists()||!file.isDirectory()){//文件不存在或者不是一个文件夹
            return file.mkdirs();//创建文件夹
        }
        return true;//文件夹存在
    }

    //从AppInfo对象中拷贝一个DownloadInfo对象
    public static DownloadInfo appInfoCopy(AppInfo appInfo){
        DownloadInfo downloadInfo=new DownloadInfo();
        downloadInfo.name=appInfo.name;
        downloadInfo.size=appInfo.size;
        downloadInfo.downloadUrl=appInfo.downloadUrl;
        downloadInfo.packageName=appInfo.packageName;
        downloadInfo.id=appInfo.id;

        downloadInfo.currentPos=0;
        downloadInfo.currentState= DownloadManager.STATE_UNDO;
        downloadInfo.path=downloadInfo.getFilePath();
        return downloadInfo;
    }

}
