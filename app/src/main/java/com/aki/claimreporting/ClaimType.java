package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimVehicleSelection.selectedVehicleLayout;
import static com.aki.claimreporting.ClaimVehicleSelection.setNewVehicleSelected;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class ClaimType extends AppCompatActivity implements View.OnClickListener {

    public static String CraIdval = "CraIdval";
    public static String Thirdpartyavailable = "Thirdpartyavailable";
    public static String typeidincident = "typeidincident";
    public static String CertificateID = "CertificateID";
    LinearLayout collisionLayout, selfInvolvingLayout, stolenOrTheftLayout, otherServicesLayout;
    TextView collisionText, selfInvolvingText, stolenOrTheftText, otherServicesText, collisionBodyText, selfInvolvingBodyText, stolenOrTheftBodyText, otherServicesBodyText;
    CardView otherservinanimate, stoleninanimate, selfinvtxt, viewcollision;
    Animation animSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Claim Type");

        mCrashlytics = FirebaseCrashlytics.getInstance();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_claim_type);
        preventSizeChange(this, getSupportActionBar());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            try {
                try {
                    selectedVehicleLayout = null;
                    setNewVehicleSelected = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                collisionLayout = findViewById(R.id.CollisionLayout);
                collisionLayout.setOnClickListener(this);
                stolenOrTheftLayout = findViewById(R.id.StolenOrTheftLayout);
                stolenOrTheftLayout.setOnClickListener(this);
                selfInvolvingLayout = findViewById(R.id.SelfInvolvingLayout);
                selfInvolvingLayout.setOnClickListener(this);
                otherServicesLayout = findViewById(R.id.OtherServicesLayout);
                otherServicesLayout.setOnClickListener(this);
                collisionText = findViewById(R.id.collisionheadertxt);
                stolenOrTheftText = findViewById(R.id.stolenheadertxt);
                selfInvolvingText = findViewById(R.id.selfinvheadertxt);
                otherServicesText = findViewById(R.id.otherservheadertxt);
                collisionBodyText = findViewById(R.id.collisiontittletxt);
                selfInvolvingBodyText = findViewById(R.id.selfinvtittletxt);
                stolenOrTheftBodyText = findViewById(R.id.stolentittletxt);
                otherServicesBodyText = findViewById(R.id.otherservtittletxt);

                selfinvtxt = (CardView) findViewById(R.id.selfinvtxt);
                viewcollision = (CardView) findViewById(R.id.viewcollision);

                ClaimLocation.locationupdate = "";

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            clearSelectedClaimType();


            animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinleft);
            viewcollision.startAnimation(animSlide);

            animSlide.setDuration(800);


            animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinleft);

            selfinvtxt.startAnimation(animSlide);

            animSlide.setDuration(1300);


            stoleninanimate = (CardView) findViewById(R.id.stoleninanimate);
            animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinleft);

            stoleninanimate.startAnimation(animSlide);
            animSlide.setDuration(1900);

            otherservinanimate = (CardView) findViewById(R.id.otherservinanimate);
            animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideinleft);

            otherservinanimate.startAnimation(animSlide);
            animSlide.setDuration(2400);

            DatabaseHelper mydb = new DatabaseHelper(this);
            Cursor curseattachnear = mydb.getclaimofflineiddetails();
            int countnear = curseattachnear.getCount();
            try {
                if (countnear >= 1) {
                    Intent car = new Intent(ClaimType.this, OfflineClaimPending.class);
                    startActivity(car);
                } else {

                    CaptureDamagedParts.carviewcircle1 = "0";
                    CaptureDamagedParts.carviewcircle11 = "0";
                    CaptureDamagedParts.carviewcircle8 = "0";
                    CaptureDamagedParts.carviewcircle7 = "0";
                    CaptureDamagedParts.carviewcircle4 = "0";


                    MainActivity.damagecountnew = 0;
                    CarView.carviewcircle1 = "0";
                    CarView.carviewcircle2 = "0";
                    CarView.carviewcircle3 = "0";
                    CarView.carviewcircle4 = "0";
                    CarView.carviewcircle5 = "0";
                    CarView.carviewcircle6 = "0";
                    CarView.carviewcircle7 = "0";
                    CarView.carviewcircle8 = "0";
                    CarView.carviewcircle9 = "0";
                    CarView.carviewcircle10 = "0";
                    CarView.carviewcircle11 = "0";
                    CarView.carviewcircle12 = "0";
                    CarView.carviewcircle13 = "0";
                    CarView.carviewcircle14 = "0";
                    CarView.carviewcircle15 = "0";
                    CarView280.carviewcircle1 = "0";
                    CarView280.carviewcircle2 = "0";
                    CarView280.carviewcircle3 = "0";
                    CarView280.carviewcircle4 = "0";
                    CarView280.carviewcircle5 = "0";
                    CarView280.carviewcircle6 = "0";
                    CarView280.carviewcircle7 = "0";
                    CarView280.carviewcircle8 = "0";
                    CarView280.carviewcircle9 = "0";
                    CarView280.carviewcircle10 = "0";
                    CarView280.carviewcircle11 = "0";
                    CarView280.carviewcircle12 = "0";
                    CarView280.carviewcircle13 = "0";
                    CarView280.carviewcircle14 = "0";
                    CarView280.carviewcircle15 = "0";
                    CarView400.carviewcircle1 = "0";
                    CarView400.carviewcircle2 = "0";
                    CarView400.carviewcircle3 = "0";
                    CarView400.carviewcircle4 = "0";
                    CarView400.carviewcircle5 = "0";
                    CarView400.carviewcircle6 = "0";
                    CarView400.carviewcircle7 = "0";
                    CarView400.carviewcircle8 = "0";
                    CarView400.carviewcircle9 = "0";
                    CarView400.carviewcircle10 = "0";
                    CarView400.carviewcircle11 = "0";
                    CarView400.carviewcircle12 = "0";
                    CarView400.carviewcircle13 = "0";
                    CarView400.carviewcircle14 = "0";
                    CarView400.carviewcircle15 = "0";
                    SharedPreferences certpref = getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
                    SharedPreferences.Editor certeditor = certpref.edit();
                    certeditor.putString(ClaimType.CertificateID, "");
                    certeditor.putString("Vechilerefid", "");
                    certeditor.commit();

                    SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor incidenttypeditor = incidentsharedpreferences.edit();
                    incidenttypeditor.putString("typeidincident", "");
                    incidenttypeditor.apply();

                    mydb = new DatabaseHelper(ClaimType.this);
                    Cursor curseattachimage = mydb.getlocalimageattachment();
                    int countimage = curseattachimage.getCount();

                    if (countimage > 0) {
                        mydb.deletealllocalimage();
                    }
                    Cursor cursethirdattachimage = mydb.getthirdlocalimages();
                    int countthirdimage = cursethirdattachimage.getCount();

                    if (countthirdimage > 0) {
                        mydb.deletethirdlocalimage();
                    }
                    Cursor cursethirdpartdetails = mydb.getthirdpartydetails();
                    int countthirddetail = cursethirdpartdetails.getCount();

                    if (countthirddetail > 0) {
                        mydb.deletethirdpartydetails();
                    }

                    Cursor cursestolenpartdetails = mydb.getClaimImgmore();
                    int countstolendetail = cursestolenpartdetails.getCount();

                    if (countstolendetail > 0) {
                        mydb.deleteClaimImgmor();
                    }


                    SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("visimagefilepath", "");
                    editor.commit();

                    SharedPreferences sharedPreference1 = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreference1.edit();
                    editor1.putString("visaudiofilepath", "");
                    editor1.commit();

                    SharedPreferences sharedPreference2 = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreference2.edit();
                    editor2.putString("videofilepath", "");
                    editor2.commit();


                    SharedPreferences sharedPreference3 = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor3 = sharedPreference3.edit();
                    editor3.putString("videofilepathlocation", "");
                    editor3.commit();

                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public void onClick(View view) {
        clearSelectedClaimType();
        //startActivity(new Intent(ClaimType.this, ClaimLocation.class));
        // storing selected claim type id in shared pref.
        SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
        SharedPreferences.Editor incidenttypeditor = incidentsharedpreferences.edit();

        if (view.getId() == collisionLayout.getId()) {
            // On Click of CollisionLayout.
            selectedClaimType(collisionLayout, collisionText, collisionBodyText);
            // storing id to shared pref.
            incidenttypeditor.putString(typeidincident, "630CF0B1-C91C-48D5-BD09-2F23D6C3AAB8");
            incidenttypeditor.apply();
        } else if (view.getId() == selfInvolvingLayout.getId()) {
            // On click of SelfInvolving Layout.
            selectedClaimType(selfInvolvingLayout, selfInvolvingText, selfInvolvingBodyText);
            // storing id to shared pref.
            incidenttypeditor.putString(typeidincident, "F82589E6-7344-47B2-A672-11013F538551");
            incidenttypeditor.apply();
        } else if (view.getId() == stolenOrTheftLayout.getId()) {
            // On click of StolenOrTheftLayout.
            selectedClaimType(stolenOrTheftLayout, stolenOrTheftText, stolenOrTheftBodyText);
            // storing id to shared pref.
            incidenttypeditor.putString(typeidincident, "B2EC755A-88EF-4F53-8911-C13688D226D3");
            incidenttypeditor.apply();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
        String claimType = sharedPreferences.getString("typeidincident", "");
        if (claimType == "630CF0B1-C91C-48D5-BD09-2F23D6C3AAB8" || claimType == "F82589E6-7344-47B2-A672-11013F538551") {
            startActivity(new Intent(ClaimType.this, MapsView.class));
        } else {
            startActivity(new Intent(ClaimType.this, ClaimVehicleSelection.class));
        }


//        else if (view.getId() == otherServicesLayout.getId()) {
//            // On click of OtherServicesLayout.
//            selectedClaimType(otherServicesLayout, otherServicesText, otherServicesBodyText);
//            incidenttypeditor.putString("typeidincident", "");
//            incidenttypeditor.apply();
//        }

//        SharedPreferences sharedPreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
//        String claimType = sharedPreferences.getString("typeidincident", "");
//        if (claimType.isEmpty()) {
//            Toast.makeText(ClaimType.this, "Please select any one of the option above!", Toast.LENGTH_SHORT).show();
//        } else {
//            startActivity(new Intent(ClaimType.this, ClaimLocation.class));
//        }
    }

    // Change the color for selected layout.
    private void selectedClaimType(LinearLayout selectedLayout, TextView selectedLayoutHeader, TextView selectedLayoutTitle) {
        // Changing colors for the selected layout.
        selectedLayout.setBackgroundColor(getColor(R.color.purple_500));
        selectedLayoutHeader.setTextColor(getColor(R.color.white));
        selectedLayoutTitle.setTextColor(getColor(R.color.white));
    }

    private void clearSelectedClaimType() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Setting Default Colors for Collision Layout.
            collisionLayout.setBackgroundColor(getColor(R.color.white));
            collisionText.setTextColor(getColor(R.color.purple_500));
            collisionBodyText.setTextColor(getColor(R.color.black));
            // Setting Default Colors for SelfInvolving Layout .
            selfInvolvingLayout.setBackgroundColor(getColor(R.color.white));
            selfInvolvingText.setTextColor(getColor(R.color.purple_500));
            selfInvolvingBodyText.setTextColor(getColor(R.color.black));
            // Setting Default Colors for StolenOrTheft Layout.
            stolenOrTheftLayout.setBackgroundColor(getColor(R.color.white));
            stolenOrTheftText.setTextColor(getColor(R.color.purple_500));
            stolenOrTheftBodyText.setTextColor(getColor(R.color.black));
            // Setting Default Colors for OtherServices Layout.
            otherServicesLayout.setBackgroundColor(getColor(R.color.white));
            otherServicesText.setTextColor(getColor(R.color.purple_500));
            otherServicesBodyText.setTextColor(getColor(R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}