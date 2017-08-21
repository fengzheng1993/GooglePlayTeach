package com.fz.googleplayteach.utils;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by 冯政 on 2017/6/30.
 */

public class BitmapHelper {
    public static BitmapUtils bitmapUtilsInstance=null;

    public static BitmapUtils getInstance(){
        if (bitmapUtilsInstance==null){
            synchronized (BitmapHelper.class){
                if (bitmapUtilsInstance==null){
                    bitmapUtilsInstance=new BitmapUtils(UIUtils.getContext());
                }
            }
        }
        return bitmapUtilsInstance;
    }
}
