package com.fz.googleplayteach.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.CategoryInfo;
import com.fz.googleplayteach.utils.UIUtils;


/**
 * Created by 冯政 on 2017/7/2.
 * 分类标题holder
 */

public class TittleHolder extends BaseHolder<CategoryInfo> {

    private TextView tvTittle;

    @Override
    public View initView() {
        View view=UIUtils.inflate(R.layout.list_item_title);
        tvTittle = (TextView) view.findViewById(R.id.tv_tittle);
        return view;
    }

    @Override
    public void refreshView(CategoryInfo data) {
        tvTittle.setText(data.title);
    }
}
