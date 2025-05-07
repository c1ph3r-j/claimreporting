package com.aki.claimreporting;

import androidx.cardview.widget.CardView;

public interface CardAdapter {
    public final int MAX_ELEVATION_FACTOR = ClaimVehicleSelection.valuelistadpt;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}