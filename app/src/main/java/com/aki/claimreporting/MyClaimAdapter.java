package com.aki.claimreporting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyClaimAdapter extends FragmentPagerAdapter {

    int totalTabs;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public MyClaimAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }


    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment getItem(int position) {
        try {
            switch (position) {
                case 0:
                    return new TimeLine();
                case 1:
                    return new ClaimInfo();
                case 2:
                    return new ClaimImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
