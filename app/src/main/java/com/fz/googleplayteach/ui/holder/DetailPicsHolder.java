package com.fz.googleplayteach.ui.holder;

import android.view.View;
import android.widget.ImageView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/7/4.
 */

public class DetailPicsHolder extends BaseHolder<AppInfo> {
    private ImageView[] ivPics;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.layout_detail_picinfo);
        ivPics=new ImageView[5];
        ivPics[0]= (ImageView) view.findViewById(R.id.iv_pic1);
        ivPics[1]= (ImageView) view.findViewById(R.id.iv_pic2);
        ivPics[2]= (ImageView) view.findViewById(R.id.iv_pic3);
        ivPics[3]= (ImageView) view.findViewById(R.id.iv_pic4);
        ivPics[4]= (ImageView) view.findViewById(R.id.iv_pic5);
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        for (int i=0;i<5;i++){
            if (i<data.screen.size()){
                BitmapHelper.getInstance().display(ivPics[i], HttpHelper.URL+"ImageServlet?name="+data.screen.get(i));
            }else {
                ivPics[i].setVisibility(View.GONE);
            }
        }
    }
}
