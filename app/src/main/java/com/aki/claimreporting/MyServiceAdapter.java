package com.aki.claimreporting;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class MyServiceAdapter extends FragmentPagerAdapter {

    int totalTabs;
    private Context myContext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public MyServiceAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }


    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {

        if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {
            switch (position) {


                case 0:
                    Hospital hosFragment = new Hospital();
                    return hosFragment;
                case 1:
                    Towing towFragment = new Towing();
                    return towFragment;
                case 2:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("No")) {
            switch (position) {

                case 0:
                    Hospital hosFragment = new Hospital();
                    return hosFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("No")) {
            switch (position) {
                case 0:
                    Towing towFragment = new Towing();
                    return towFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("Yes")) {

            switch (position) {


                case 0:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }

        } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("No")) {
            switch (position) {


                case 0:
                    Hospital hosFragment = new Hospital();
                    return hosFragment;
                case 1:
                    Towing towFragment = new Towing();
                    return towFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {
            switch (position) {


                case 0:
                    Towing towFragment = new Towing();
                    return towFragment;
                case 1:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("Yes")) {
            switch (position) {

                case 0:
                    Hospital hosFragment = new Hospital();
                    return hosFragment;
                case 1:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }
        } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {
            switch (position) {

                case 0:
                    Towing towFragment = new Towing();
                    return towFragment;
                case 1:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    Hospital hosFragment = new Hospital();
                    return hosFragment;
                case 1:
                    Towing towFragment = new Towing();
                    return towFragment;
                case 2:
                    Police polFragment = new Police();
                    return polFragment;
                default:
                    return null;
            }
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}