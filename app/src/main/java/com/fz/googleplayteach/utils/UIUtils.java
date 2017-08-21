package com.fz.googleplayteach.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.fz.googleplayteach.global.GooglePlayApplication;

/**
 * Created by 冯政 on 2017/6/24.
 */

public class UIUtils {

    public static Context getContext(){
        return GooglePlayApplication.getContext();
    }

    public static Handler getHandler(){
        return GooglePlayApplication.getHandler();
    }

    public static int getMainThreadId(){
        return GooglePlayApplication.getMainThreadTid();
    }

    /******************************加载资源文件*************************************/
    //获取字符串
    public static String getString(int id){
        return getContext().getResources().getString(id);
    }

    //获取字符串数组
    public static String[] getStringArray(int id){
        return getContext().getResources().getStringArray(id);
    }

    //获取图片
    public static Drawable getDrawable(int id){
        return getContext().getResources().getDrawable(id);
    }

    //获取颜色
    public static int getColor(int id){
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //获取尺寸
    public static int getDimen(int id){
        return getContext().getResources().getDimensionPixelSize(id);//返回具体的像素值
    }

    /*********************************dip和px转换*************************************/
    //dip转化为px
    public static int dip2px(float dip){
        float density=getContext().getResources().getDisplayMetrics().density;//设备密度
        return (int) (dip*density+0.5f);
    }

    //px转化为dip
    public static float px2dip(int px){
        float density=getContext().getResources().getDisplayMetrics().density;//设备密度
        return px/density;
    }

    /*************************************加载布局文件***********************************/
    public static View inflate(int id){
        return View.inflate(getContext(),id,null);
    }

    /********************************判断是否运行在主线程*********************************/
    public static boolean isRunOnUIThread(){
        //获取当前线程id
        int myTid=android.os.Process.myTid();
        if (myTid==getMainThreadId()){
            return true;
        }
        return false;
    }

    //运行在主线程
    public static void runOnUIThread(Runnable runnable){
        if (isRunOnUIThread()){
            runnable.run();//运行在主线程，直接运行
        }else {
            getHandler().post(runnable);//运行在子线程，借助handler让其运行在主线程
        }
    }

}
