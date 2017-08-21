package com.fz.googleplayteach.ui.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fz.googleplayteach.http.protocol.RecommendProtocol;
import com.fz.googleplayteach.ui.view.LoadingPage;
import com.fz.googleplayteach.ui.view.fly.ShakeListener;
import com.fz.googleplayteach.ui.view.fly.StellarMap;
import com.fz.googleplayteach.utils.UIUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 冯政 on 2017/6/24.
 * 推荐
 */

public class RecommendFragment extends BaseFragment {
    private ArrayList<String> data;
    @Override
    public View onCreateSuccessView() {
        final StellarMap stellar=new StellarMap(UIUtils.getContext());
        stellar.setAdapter(new RecommendAdapter());

        //随机方式,9行6列的随机方式
        stellar.setRegularity(6,9);

        //设置内边距
        int padding=UIUtils.dip2px(10);
        stellar.setInnerPadding(padding,padding,padding,padding);

        //设置默认页面,第一组数据
        stellar.setGroup(0,true);

        //设置摇晃跳到下一页
        ShakeListener shakeListener=new ShakeListener(UIUtils.getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellar.zoomIn();//跳到下一页
            }
        });
        return stellar;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        RecommendProtocol protocol=new RecommendProtocol();
        data= (ArrayList<String>) protocol.getData(0);
        return check(data);
    }

    class RecommendAdapter implements StellarMap.Adapter {

        @Override
        //返回组的个数
        public int getGroupCount() {
            return 2;
        }

        @Override
        //返回某组的item个数
        public int getCount(int group) {
            int count=data.size()/getGroupCount();
            if(group==getGroupCount()-1){
                //最后一页，将除不尽，余下来的数量追加在最后一页，保证数据不丢失
                count+=data.size()%getGroupCount();
            }
            return count;
        }

        @Override
        //初始化布局
        public View getView(int group, int position, View convertView) {
            //因为Position每组都会从0开始计数，所以需要将前面几组数据的个数加起来，才能确定当前组获取数据的角标位置
            position+=(group)*getCount(group-1);
            final String keyWord=data.get(position);
            System.out.println("文字为："+keyWord);
            TextView textView=new TextView(UIUtils.getContext());
            textView.setTextColor(Color.BLACK);
            textView.setText(keyWord);
            //随机大小,16-25
            Random random=new Random();
            int size=16+random.nextInt(10);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
            //随机颜色 r,g,b 0-255  -->30-230，颜色值不能太小或太大，从而避免整体颜色过亮或过暗
            int r=30+random.nextInt(200);
            int g=30+random.nextInt(200);
            int b=30+random.nextInt(200);
            textView.setTextColor(Color.rgb(r,g,b));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), keyWord, Toast.LENGTH_SHORT).show();
                }
            });
            return textView;
        }

        @Override
        //返回下一组的id
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            //往上滑加载下一页 isZoomIn=false
            //往下滑加载上一页 isZoomIn=true
            if (isZoomIn){
                //往下滑加载上一页
                if (group>0){
                    group--;
                }else {
                    //跳到最后一页
                    group=getGroupCount()-1;
                }
            }else {
                //往上滑加载下一页
                if (group<getGroupCount()-1){
                    group++;
                }else {
                    //跳到第一页
                    group=0;
                }
            }
            return group;
        }
    }
}
