package com.fz.googleplayteach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by 冯政 on 2017/7/1.
 */

public class RatioLayout extends FrameLayout {

    private float ratio;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        String nameSpace="http://schemas.android.com/apk/com.fz.googleplayteach.ui.view.RatioLayout";
        ratio = attrs.getAttributeFloatValue(nameSpace,"ratio",-1);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //1.获取控件宽度  mode : MeasureSpec.AT_MOST 至多模式，控件有多大就显示多大，wrap_content
        //                       MeasureSpec.EXACTLY 类似宽高写成dp，match_parent
        //                       MeasureSpec.UNSPECIFIED 未指定模式
        // widthMeasureSpec和heightMeasureSpec并不是控件真实的宽高，里面包含模式信息
        //2.根据宽度和比例ratio，计算控件的高度
        //3.重新测量控件
        System.out.println("widthMeasureSpec:"+widthMeasureSpec);//  1073742602  转化为二进制 ，1000000000000000000001100001010 拆分开：100000000000000000000表示模式值；1100001010表示实际的宽度，转化为十进制为778px
        int width=MeasureSpec.getSize(widthMeasureSpec);//获取宽度值
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);//获取宽度模式
        int height=MeasureSpec.getSize(heightMeasureSpec);//获取高度值
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);//获取高度模式
        if (widthMode==MeasureSpec.EXACTLY&&heightMode!=MeasureSpec.EXACTLY&&ratio>0){
            //图片宽度=控件宽度-左侧内边距-右侧内边距
            int imageWidth=width-getPaddingLeft()-getPaddingRight();
            int imageHeight=(int)(imageWidth/ratio+0.5f);
            //控件高度=图片高度+上侧内边距+下侧内边距
            height=imageHeight+getPaddingTop()+getPaddingBottom();
            //根据最新的高度来重新生成heightMeasureSpec(高度模式是确定模式)
            heightMeasureSpec=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        }
        //按照最新的高度模式测量控件
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
