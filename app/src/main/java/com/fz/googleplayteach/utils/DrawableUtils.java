package com.fz.googleplayteach.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by 冯政 on 2017/7/2.
 */

public class DrawableUtils {
    //获取shape对象
    public static GradientDrawable getGradientDrawable(int color, int radius){
        // xml中定义的shape标签对应此类
        GradientDrawable shape=new GradientDrawable();

        shape.setShape(GradientDrawable.RECTANGLE);//画矩形
        shape.setCornerRadius(radius);//圆角半径
        shape.setColor(color);//颜色

        return shape;
    }

    //获取图片状态选择器
    public static StateListDrawable getSelector(Drawable normal, Drawable press){
        StateListDrawable selector=new StateListDrawable();//图片状态选择器
        selector.addState(new int[]{android.R.attr.state_pressed},press);//按下图片
        selector.addState(new int[]{},normal);
        return selector;
    }

    //获取图片状态选择器
    public static StateListDrawable getSelector(int normalColor,int pressColor,int radius){
        GradientDrawable bgNormal=getGradientDrawable(normalColor,radius);
        GradientDrawable bgPress=getGradientDrawable(pressColor,radius);
        StateListDrawable selector=getSelector(bgNormal,bgPress);
        return selector;
    }
}
