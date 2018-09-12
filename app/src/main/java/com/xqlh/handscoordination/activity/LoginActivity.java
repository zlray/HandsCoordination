package com.xqlh.handscoordination.activity;


import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.adapter.FragmentVpAdapter;
import com.xqlh.handscoordination.base.BaseActivity;
import com.xqlh.handscoordination.fragment.StudentFragment;
import com.xqlh.handscoordination.fragment.TeacherFragment;

import java.util.ArrayList;

import butterknife.Bind;

public class LoginActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.vp_main)
    ViewPager mViewPager;
    @Bind(R.id.rg_main)
    RadioGroup mRadioGroup;
    @Bind(R.id.rb_student)
    RadioButton rb_student;
    @Bind(R.id.rb_teacher)
    RadioButton rb_teacher;
    //fragment集合
    private ArrayList<Fragment> mFragments;

    //fragment的适配器  ViewpagerAdapter
    private FragmentVpAdapter mPagerAdapter;

    private StudentFragment studnetFragment;
    private TeacherFragment teacherFragment;

    @Override
    public int setContent() {
        return R.layout.activity_login;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        /**
         * 加载单选组，使用单选组不必给单选组内的每个RadioButton 设置监听
         */
        /**
         * 给单选按钮设置监听
         */
        mRadioGroup.setOnCheckedChangeListener(this);
        //
        studnetFragment = new StudentFragment();
        teacherFragment = new TeacherFragment();
        //实例化fragmentlist
        mFragments = new ArrayList<>();
        //fragment集合中添加fragment
        mFragments.add(studnetFragment);
        mFragments.add(teacherFragment);
        //适配器
        mPagerAdapter = new FragmentVpAdapter(getSupportFragmentManager(), mFragments);
        //viewPager设置适配器
        mViewPager.setAdapter(mPagerAdapter);

        //当前为第一个页面
//        mViewPager.setCurrentItem(0);

        Intent intent = getIntent();
        String accept = intent.getStringExtra("admin");
        if ("admin".equals(accept)) {
            mRadioGroup.check(R.id.rb_teacher);
        } else {
            mRadioGroup.check(R.id.rb_student);
        }
        //ViewPager的页面改变监听
        mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_student:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_teacher:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            //获取当前页面用于改变对应RadioButton的状态
            int current = mViewPager.getCurrentItem();

            switch (current) {
                case 0:
                    mRadioGroup.check(R.id.rb_student);
                    break;
                case 1:
                    mRadioGroup.check(R.id.rb_teacher);
                    break;

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
