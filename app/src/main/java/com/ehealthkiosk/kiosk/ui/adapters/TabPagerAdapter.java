package com.ehealthkiosk.kiosk.ui.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStatePagerAdapter {


    final List<Fragment> mFragmentList = new ArrayList<>();
    final List<String> mFragmentTitleList = new ArrayList<>();

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {

        return mFragmentList.size();

    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mFragmentTitleList.get(position);

    }
    /**
     * Get the current fragment
     */
    /*public Fragment getCurrentFragment() {
        return currentFragment;
    }*/

/*
    @Override
    public Parcelable saveState() {
        return null;
    }
*/

}