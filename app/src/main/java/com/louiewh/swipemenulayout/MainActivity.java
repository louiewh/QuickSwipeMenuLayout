package com.louiewh.swipemenulayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TabLayout;


import com.louiewh.swipemenulayout.fragemnt.ListViewFragment;
import com.louiewh.swipemenulayout.fragemnt.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.table_layout);

        mFragmentList.add(new ListViewFragment());
        mFragmentList.add(new RecyclerViewFragment());

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;
        private String[] mTitleArray={
                "ListView",
                "RecyclerView"
        };

        private ViewPagerAdapter(FragmentManager fm,  List<Fragment> list) {
            super(fm);
            mFragmentList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleArray[position];
        }
    }
}
