package com.aki.claimreporting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class StolenImageDeclarationAdapter extends FragmentStateAdapter {

    ArrayList<StolenImageViewFragment> listOfImageFragments;

    public StolenImageDeclarationAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<StolenImageViewFragment> attachimglist) {
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