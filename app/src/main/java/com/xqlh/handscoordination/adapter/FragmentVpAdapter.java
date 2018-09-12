package com.xqlh.handscoordination.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/11.
 */

public class FragmentVpAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments;


    public FragmentVpAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}