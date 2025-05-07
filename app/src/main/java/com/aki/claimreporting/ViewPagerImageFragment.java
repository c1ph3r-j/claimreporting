package com.aki.claimreporting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;


public class ViewPagerImageFragment extends Fragment {
    String image;
    CardView cardView;
    FirebaseCrashlytics mCrashlytics;

    public ViewPagerImageFragment() {
        // Required empty public constructor
    }


    public ViewPagerImageFragment(String img) {
        this.image = img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager_image, container, false);
        cardView = view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            ImageView imageView = view.findViewById(R.id.imageViewForViewPager);
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getView", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return view;
    }
}