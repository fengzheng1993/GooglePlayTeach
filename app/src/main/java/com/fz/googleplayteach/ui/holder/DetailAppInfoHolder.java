package com.fz.googleplayteach.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.domain.AppInfo;
import com.fz.googleplayteach.http.HttpHelper;
import com.fz.googleplayteach.utils.BitmapHelper;
import com.fz.googleplayteach.utils.UIUtils;

/**
 * Created by 冯政 on 2017/7/2.
 */

public class DetailAppInfoHolder extends BaseHolder<AppInfo> {

    private RatingBar rbStar;
    private TextView tvDate;
    private TextView tvSize;
    private TextView tvDownloadNum;
    private TextView tvName;
    private ImageView ivIcon;
    private TextView tvVersion;

    @Override
    public View initView() {
        View view= UIUtils.inflate(R.layout.layout_detail_appinfo);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvDownloadNum = (TextView) view.findViewById(R.id.tv_download_num);
        tvSize = (TextView) view.findViewById(R.id.tv_size);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvVersion = (TextView) view.findViewById(R.id.tv_version);
        rbStar = (RatingBar) view.findViewById(R.id.rb_star);
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        BitmapHelper.getInstance().display(ivIcon, HttpHelper.URL+"ImageServlet?name="+data.iconUrl);
        tvDownloadNum.setText("下载量:"+data.downloadNum);
        tvName.setText(data.name);
        tvVersion.setText("版本号:"+data.version);
        tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(),data.size));
        tvDate.setText(data.date);
        rbStar.setRating(data.stars);
    }
}
