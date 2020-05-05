package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class FragmentsAccess extends FragmentPagerAdapter {
    public FragmentsAccess(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                GroupsFragment grpFragment = new GroupsFragment();
                return grpFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        switch(position){
            case 0:
                GroupsFragment grpFragment = new GroupsFragment();
                return "Groups";
            default:
                return null;
        }
    }
}
