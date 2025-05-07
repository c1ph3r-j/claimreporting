package com.aki.claimreporting;

import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    ClaimVehicleSelection claimVehicleSelection;
    private List<CardFragment> fragments;
    private float baseElevation;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, ClaimVehicleSelection claimVehicleSelection) {
        super(fm);
        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;
        this.claimVehicleSelection = claimVehicleSelection;

        try {
            for (int i = 0; i < ClaimVehicleSelection.valuelistadpt; i++) {
                addCardFragment(new CardFragment());
            }
//            if (ClaimVehicleSelection.valuelistadpt != 1) {
//
//            } else {
//                addCardFragment(new CardFragment());
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClaimVehicleSelection getClaimVehicleSelection() {
        return this.claimVehicleSelection;
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return CardFragment.getInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (CardFragment) fragment);

        return fragment;
    }

    public void addCardFragment(CardFragment fragment) {
        fragments.add(fragment);
    }

}