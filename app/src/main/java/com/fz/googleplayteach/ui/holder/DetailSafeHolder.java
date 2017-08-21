package com.fz.googleplayteach.ui.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/7/3.
 */

public class DetailSafeHolder extends BaseHolder<AppInfo> {
    private ImageView[] mSafeIcons;//安全标识图片
    private ImageView[] mDesIcons;//安全描述图片
    private TextView[] mSafeDes;//安全描述文字
    private LinearLayout[] mSafeDesBar;//安全描述条目
    private RelativeLayout rlDesRoot;
    private LinearLayout llDesRoot;
    private int mDesHeight;
    private boolean isOpen=false;//标记安全描述开关状态，默认关
    private LinearLayout.LayoutParams params;
    private ImageView ivArrow;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.layout_detail_safeinfo);
        mSafeIcons=new ImageView[4];
        mSafeIcons[0]= (ImageView) view.findViewById(R.id.iv_safe1);
        mSafeIcons[1]= (ImageView) view.findViewById(R.id.iv_safe2);
        mSafeIcons[2]= (ImageView) view.findViewById(R.id.iv_safe3);
        mSafeIcons[3]= (ImageView) view.findViewById(R.id.iv_safe4);

        mDesIcons=new ImageView[4];
        mDesIcons[0]= (ImageView) view.findViewById(R.id.iv_des1);
        mDesIcons[1]= (ImageView) view.findViewById(R.id.iv_des2);
        mDesIcons[2]= (ImageView) view.findViewById(R.id.iv_des3);
        mDesIcons[3]= (ImageView) view.findViewById(R.id.iv_des4);

        mSafeDes=new TextView[4];
        mSafeDes[0]= (TextView) view.findViewById(R.id.tv_des1);
        mSafeDes[1]= (TextView) view.findViewById(R.id.tv_des2);
        mSafeDes[2]= (TextView) view.findViewById(R.id.tv_des3);
        mSafeDes[3]= (TextView) view.findViewById(R.id.tv_des4);

        mSafeDesBar=new LinearLayout[4];
        mSafeDesBar[0]= (LinearLayout) view.findViewById(R.id.ll_des1);
        mSafeDesBar[1]= (LinearLayout) view.findViewById(R.id.ll_des2);
        mSafeDesBar[2]= (LinearLayout) view.findViewById(R.id.ll_des3);
        mSafeDesBar[3]= (LinearLayout) view.findViewById(R.id.ll_des4);

        rlDesRoot = (RelativeLayout) view.findViewById(R.id.rl_des_root);
        llDesRoot = (LinearLayout) view.findViewById(R.id.ll_des_root);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        rlDesRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    private void toggle() {
        ValueAnimator animator;
        //属性动画
        if (isOpen){
            isOpen=false;
            animator = ValueAnimator.ofInt(mDesHeight,0);
        }else {
            isOpen=true;
            animator=ValueAnimator.ofInt(0,mDesHeight);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            //启动动画之后，会不断回调此方法来获取最新的值
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取最新的高度值
                int height= (int) animation.getAnimatedValue();

                //重新修改布局高度
                params.height=height;
                llDesRoot.setLayoutParams(params);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束事件
                //更新小箭头的方向
                if (isOpen){
                    ivArrow.setImageResource(R.drawable.arrow_up);
                }else {
                    ivArrow.setImageResource(R.drawable.arrow_down);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(100);//动画时间
        animator.start();//启动动画
    }

    @Override
    public void refreshView(AppInfo data) {
        for (int i=0;i<4;i++){
            if (i<data.safe.size()){
                AppInfo.SafeInfo safeInfo=data.safe.get(i);//安全标识图片
                BitmapHelper.getInstance().display(mSafeIcons[i], HttpHelper.URL+"ImageServlet?name="+safeInfo.safeUrl);
                mSafeDes[i].setText(safeInfo.safeDes);//安全描述文字
                BitmapHelper.getInstance().display(mDesIcons[i],HttpHelper.URL+"ImageServlet?name="+safeInfo.safeDesUrl);//安全描述图片
            }else {
                //剩下不该显示的图片
                mSafeIcons[i].setVisibility(View.GONE);
                mSafeDesBar[i].setVisibility(View.GONE);
            }
        }
        //获取安全描述的完整高度
        llDesRoot.measure(0,0);
        mDesHeight = llDesRoot.getMeasuredHeight();

        //隐藏安全描述
        params = (LinearLayout.LayoutParams) llDesRoot.getLayoutParams();
        params.height=0;
        llDesRoot.setLayoutParams(params);
    }
}
