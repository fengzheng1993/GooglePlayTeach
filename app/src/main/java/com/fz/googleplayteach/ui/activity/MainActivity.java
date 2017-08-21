package com.fz.googleplayteach.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.fz.googleplayteach.R;
import com.fz.googleplayteach.ui.fragment.BaseFragment;
import com.fz.googleplayteach.ui.fragment.FragmentFactory;
import com.fz.googleplayteach.ui.view.PagerTab;
import com.fz.googleplayteach.utils.UIUtils;

public class MainActivity extends BaseActivity {

    private PagerTab mPagerTab;
    private ViewPager mViewPager;
    private MyAdapter mAdapter;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionbar();

        mPagerTab = (PagerTab) findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mPagerTab.setViewPager(mViewPager);//将tab与viewpager绑定在一起
        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment=FragmentFactory.createFragment(position);
                fragment.loadData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initActionbar(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,R.string.drawer_close);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);//设置actionBar左侧的图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
//                View menu=findViewById(R.id.fl_drawer);
//                if (drawerLayout.isDrawerOpen(menu)){//判断侧边栏是否可见
//                    drawerLayout.closeDrawer(menu);//关闭
//                }else {
//                    drawerLayout.openDrawer(menu);//开启
//                }
                toggle.onOptionsItemSelected(item);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends FragmentPagerAdapter{

        private  String[] mTabNames;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mTabNames = UIUtils.getStringArray(R.array.tab_names);//加载页签标题数组
        }

        @Override
        /*返回当前页面位置的fragment对象*/
        public Fragment getItem(int position) {
            return FragmentFactory.createFragment(position);
        }

        @Override
        /*返回页签标题*/
        public CharSequence getPageTitle(int position) {
            return mTabNames[position];
        }

        @Override
        /*fragment的数量*/
        public int getCount() {
            return mTabNames.length;
        }
    }
}
