package com.fz.googleplayteach.http.protocol;

import com.fz.googleplayteach.domain.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/6/28.
 * 应用网络请求
 */

public class AppProtocol extends BaseProtocol {
    @Override
    public String getKey() {
        return "AppServlet";
    }

    @Override
    public String getParams() {
        return "";//如果没有参数就传空字符串，不能传null
    }

    @Override
    public ArrayList<AppInfo> parseData(String result) {
        try {
            JSONArray ja = new JSONArray(result);

            ArrayList<AppInfo> list = new ArrayList<AppInfo>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                AppInfo info = new AppInfo();
                info.des = jo.getString("des");
                info.downloadUrl = jo.getString("downloadUrl");
                info.iconUrl = jo.getString("iconUrl");
                info.id = jo.getString("id");
                info.name = jo.getString("name");
                info.packageName = jo.getString("packageName");
                info.size = jo.getLong("size");
                info.stars = (float) jo.getDouble("stars");

                list.add(info);
            }

            return list;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
