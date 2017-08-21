package com.fz.googleplayteach.ui.holder;

import android.provider.Contacts;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/7/2.
 * 首页轮播条holder
 */

public class HomeHeaderHolder extends BaseHolder<ArrayList<String>> {

    private ArrayList<String> data;
    private ViewPager mViewPager;
    private LinearLayout llContainer;
    private int mPreviousPos;

    @Override
    public View initView() {
        //创建根布局 相对布局
        RelativeLayout rlRoot=new RelativeLayout(UIUtils.getContext());
        //初始化布局参数，根布局上层控件是listView，所以要使用listView定义的LayoutParams
        AbsListView.LayoutParams params=new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,UIUtils.dip2px(180));
        rlRoot.setLayoutParams(params);

        //ViewPager
        mViewPager = new ViewPager(UIUtils.getContext());
        RelativeLayout.LayoutParams vpParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlRoot.addView(mViewPager,vpParams);//将ViewPager设置给相对布局

        //初始化指示器
        llContainer = new LinearLayout(UIUtils.getContext());
        llContainer.setOrientation(LinearLayout.HORIZONTAL);

        int padding =UIUtils.dip2px(10);
        llContainer.setPadding(padding,padding,padding,padding);

        RelativeLayout.LayoutParams llParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//底部对齐
        llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//右对齐

        rlRoot.addView(llContainer,llParams);
        return rlRoot;
    }

    @Override
    public void refreshView(final ArrayList<String> data) {
        this.data=data;
        //填充viewpager数据
        mViewPager.setAdapter(new HeaderAdapter());
        mViewPager.setCurrentItem(data.size()*10000);

        //初始化指示器
        for (int i=0;i<data.size();i++){
            ImageView point=new ImageView(UIUtils.getContext());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i==0){//第一个默认选中
                point.setImageResource(R.drawable.indicator_selected);
            }else {
                point.setImageResource(R.drawable.indicator_normal);
                params.leftMargin=UIUtils.dip2px(6);//左边距
            }
            point.setLayoutParams(params);
            llContainer.addView(point);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position=position%data.size();

                //当前点被选中
                ImageView point= (ImageView) llContainer.getChildAt(position);
                point.setImageResource(R.drawable.indicator_selected);

                //上个点变为不选中
                ImageView prePoint= (ImageView) llContainer.getChildAt(mPreviousPos);
                prePoint.setImageResource(R.drawable.indicator_normal);

                mPreviousPos=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //启动轮播条
        HomeHeaderTask task=new HomeHeaderTask();
        task.start();
    }

    class HomeHeaderTask implements Runnable{

        public void start(){
            UIUtils.getHandler().removeCallbacksAndMessages(null);//移除之前发送的所有消息，避免重复消息
            UIUtils.getHandler().postDelayed(this,3000);
        }

        @Override
        public void run() {
            int currentPos=mViewPager.getCurrentItem();
            currentPos++;
            mViewPager.setCurrentItem(currentPos);
            //继续发消息，实现内循环
            UIUtils.getHandler().postDelayed(this,3000);
        }
    }

    class HeaderAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position=position%data.size();
            String url=data.get(position);
            ImageView imageView= new ImageView(UIUtils.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            BitmapHelper.getInstance().display(imageView, HttpHelper.URL+"ImageServlet?name="+url);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
