package com.fz.googleplayteach.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fz.googleplayteach.http.protocol.HotProtocol;
import com.fz.googleplayteach.ui.view.FlowLayout;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.utils.DrawableUtils;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 冯政 on 2017/6/24.
 * 排行
 */

public class HotFragment extends BaseFragment {
    private ArrayList<String> data;
    @Override
    public View onCreateSuccessView() {
        //支持上下滑动
        ScrollView scrollView=new ScrollView(UIUtils.getContext());
        FlowLayout flowLayout=new FlowLayout(UIUtils.getContext());

        int padding=UIUtils.dip2px(10);
        flowLayout.setPadding(padding,padding,padding,padding);//设置内边距

        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6));//水平间距
        flowLayout.setVerticalSpacing(UIUtils.dip2px(8));//竖直间距

        for (int i=0;i<data.size();i++){
            TextView textView=new TextView(UIUtils.getContext());
            final String keyWord=data.get(i);
            textView.setText(keyWord);

            textView.setTextColor(Color.WHITE);//文字颜色白色
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);//文字字体大小
            textView.setPadding(padding,padding,padding,padding);//设置文字内边距
            textView.setGravity(Gravity.CENTER);

            Random random=new Random();
            //随机颜色 r,g,b 0-255  -->30-230，颜色值不能太小或太大，从而避免整体颜色过亮或过暗
            int r=30+random.nextInt(200);
            int g=30+random.nextInt(200);
            int b=30+random.nextInt(200);
//            GradientDrawable bgNormal=DrawableUtils.getGradientDrawable(Color.rgb(r,g,b),UIUtils.dip2px(6));
//            textView.setBackground(bgNormal);//设置正常背景
//            GradientDrawable bgPress=DrawableUtils.getGradientDrawable(0xffcecece,UIUtils.dip2px(6));
//            textView.setBackground(bgNormal);//设置按下背景
//            StateListDrawable selector=DrawableUtils.getSelector(bgNormal,bgPress);
            StateListDrawable selector=DrawableUtils.getSelector(Color.rgb(r,g,b),0xffcecece,UIUtils.dip2px(6));
            textView.setBackground(selector);
            flowLayout.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });
        }
        scrollView.addView(flowLayout);
        return scrollView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HotProtocol protocol=new HotProtocol();
        data= (ArrayList<String>) protocol.getData(0);
        return check(data);
    }
}
