package com.fz.googleplayteach.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.SubjectInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/6/30.
 */

public class SubjectHolder extends BaseHolder<SubjectInfo> {

    private ImageView ivPic;
    private TextView tvTittle;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.list_item_subject);
        ivPic = (ImageView) view.findViewById(R.id.iv_pic);
        tvTittle = (TextView) view.findViewById(R.id.tv_tittle);
        return view;
    }

    @Override
    public void refreshView(SubjectInfo data) {
        tvTittle.setText(data.des);
        BitmapHelper.getInstance().display(ivPic, HttpHelper.URL+"ImageServlet?name="+data.url);
    }
}
