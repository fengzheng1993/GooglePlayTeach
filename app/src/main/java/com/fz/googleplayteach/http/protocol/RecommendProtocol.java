package com.fz.googleplayteach.http.protocol;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by 冯政 on 2017/7/1.
 */

public class RecommendProtocol extends BaseProtocol {
    @Override
    public String getKey() {
        return "RecommendServlet";
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public ArrayList<String> parseData(String result) {
        try {
            JSONArray ja = new JSONArray(result);

            ArrayList<String> list = new ArrayList<String>();

            for (int i = 0; i < ja.length(); i++) {
                String keyword = ja.getString(i);
                list.add(keyword);
            }

            return list;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
