package com.fz.googleplayteach.ui.fragment;


import android.support.v4.app.Fragment;

import java.util.HashMap;

/**
 * Created by 冯政 on 2017/6/24.
 * Fragment工厂类，用来生产fragment
 */

public class FragmentFactory {
    private static HashMap<Integer,BaseFragment> mFragmentMap=new HashMap<>();
    public static BaseFragment createFragment(int pos){
        //先从集合中取，如果没有才创建，提高性能
        BaseFragment fragment=mFragmentMap.get(pos);
        if (fragment==null){
            switch (pos){
                case 0:
                    fragment=new HomeFragment();
                    break;
                case 1:
                    fragment=new AppFragment();
                    break;
                case 2:
                    fragment=new GameFragment();
                    break;
                case 3:
                    fragment=new SubjectFragment();
                    break;
                case 4:
                    fragment=new RecommendFragment();
                    break;
                case 5:
                    fragment=new CategoryFragment();
                    break;
                case 6:
                    fragment=new HotFragment();
                    break;
                default:
                    break;
            }
        }
        mFragmentMap.put(pos,fragment);
        return fragment;
    }
}
