package com.fz.googleplayteach.ui.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/7/4.
 */

public class DetailDesHolder extends BaseHolder<AppInfo> {

    private ImageView ivArrow;
    private TextView tvAuthor;
    private TextView tvDes;
    private RelativeLayout rlToggle;
    private boolean isOpen=false;
    private LinearLayout.LayoutParams params;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.layout_detail_desinfo);
        tvDes = (TextView) view.findViewById(R.id.tv_detail_des);
        tvAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
        ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        rlToggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);
        rlToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    private void toggle() {
        int shortHeight=getShortHeight();
        int longHeight=getLongHeight();
        ValueAnimator animator = null;
        if (!isOpen){
            isOpen=true;
            if (longHeight>shortHeight){
                animator=ValueAnimator.ofInt(shortHeight,longHeight);//只有描述信息大于7行，才启动动画
            }
        }else {
            isOpen=false;
            if (longHeight>shortHeight){
                animator=ValueAnimator.ofInt(longHeight,shortHeight);//只有描述信息大于7行，才启动动画
            }
        }
        if(animator!=null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int height = (int) animation.getAnimatedValue();

                    params.height = height;
                    tvDes.setLayoutParams(params);
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isOpen) {
                        ivArrow.setImageResource(R.drawable.arrow_up);
                    } else {
                        ivArrow.setImageResource(R.drawable.arrow_down);
                    }
                    //ScrollView要滑动到底部
                    final ScrollView scrollView=getScrollView();
                    //为了运行更加安全和稳定，可以将滚动到底部方法放在消息队列中执行
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);//滚动到最底部
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.setDuration(100);
            animator.start();
        }
    }

    @Override
    public void refreshView(AppInfo data) {
        tvDes.setText(data.des);
        tvAuthor.setText(data.author);

        //放在消息队列中运行，解决当只有3行描述时也是7行的bug
        tvDes.post(new Runnable() {
            @Override
            public void run() {
                int shortHeight=getShortHeight();
                //默认展示7行高度值
                params = (LinearLayout.LayoutParams) tvDes.getLayoutParams();
                params.height=shortHeight;
                tvDes.setLayoutParams(params);
            }
        });


    }

    //获取7行textView的高度
    private int getShortHeight(){
        //模拟一个textView，计算虚拟textView的高度，从而知道tvDes在展示7行时的高度
        int width=tvDes.getMeasuredWidth();//宽度

        TextView view=new TextView(UIUtils.getContext());
        view.setText(getData().des);//设置文字一致
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);//文字大小一致
        view.setMaxLines(7);//设置最大行数7行

        int widthMeasureSpec= View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);//高度包裹内容，wrap_content，当包裹内容时，参数1表示尺寸的最大值，暂写2000，也可以是屏幕高度
        view.measure(widthMeasureSpec,heightMeasureSpec);//开始测量

        return view.getMeasuredHeight();//返回测量后的高度
    }

    //获取完整textView的高度
    private int getLongHeight(){
        //模拟一个textView，计算虚拟textView的高度，从而知道tvDes在展示7行时的高度
        int width=tvDes.getMeasuredWidth();//宽度

        TextView view=new TextView(UIUtils.getContext());
        view.setText(getData().des);//设置文字一致
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);//文字大小一致
//        view.setMaxLines(7);//设置最大行数7行

        int widthMeasureSpec= View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);//高度包裹内容，wrap_content，当包裹内容时，参数1表示尺寸的最大值，暂写2000，也可以是屏幕高度
        view.measure(widthMeasureSpec,heightMeasureSpec);//开始测量

        return view.getMeasuredHeight();//返回测量后的高度
    }

    //寻找ScrollView对象,一层一层往上找，直到找到SrollView后才返回（注意：一定要保证父控件或者祖宗控件有ScrollView）
    private ScrollView getScrollView(){
        ViewParent parent=tvDes.getParent();
        while (!(parent instanceof ScrollView)){
            parent=parent.getParent();
        }
        return (ScrollView) parent;
    }
}
