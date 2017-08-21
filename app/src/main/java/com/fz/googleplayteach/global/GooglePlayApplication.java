package com.fz.googleplayteach.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;


/**
 * Created by 冯政 on 2017/6/24.
 * 进行全局初始化
 */

public class GooglePlayApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainThreadTid;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        handler = new Handler();

        //获取当前线程id，此处是主线程id
        mainThreadTid = android.os.Process.myTid();
    }

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadTid() {
        return mainThreadTid;
    }
}
