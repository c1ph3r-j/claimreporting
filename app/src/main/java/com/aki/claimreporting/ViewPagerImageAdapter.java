package com.aki.claimreporting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerImageAdapter extends FragmentStateAdapter {

    ArrayList<ViewPagerImageFragment> listOfImageFragments;

    public ViewPagerImageAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<ViewPagerImageFragment> attachimglist) {
        super(fragmentActivity);
        this.listOfImageFragments = attachimglist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return listOfImageFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return listOfImageFragments.size();
    }
}
