package com.aki.claimreporting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ImageDeclarationAdapter extends FragmentStateAdapter {

    ArrayList<ImageDeclarationFragment> listOfImageFragments;

    public ImageDeclarationAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<ImageDeclarationFragment> attachimglist) {
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
