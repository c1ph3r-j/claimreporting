package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ClaimInformationView extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String eventtype = "eventype";
    public static String eventName = "eventName";
    public static FirebaseCrashlytics mCrashlytics;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    TabLayout tabLayout;
    ViewPager viewPager;
    Context context;
    private int[] tabIcons = {
            R.drawable.phone,
            R.drawable.addgrey,
            R.drawable.attachblue
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_information_view);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        preventSizeChange(this, getSupportActionBar());
        SharedPreferences claimPref = getSharedPreferences("ClaimDetailsView", MODE_PRIVATE);
        String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Incident : " + incidenid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        context = this;
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.pager);
            tabLayout.addTab(tabLayout.newTab().setText("Timeline Events"));
            tabLayout.addTab(tabLayout.newTab().setText("Claim Details"));
            tabLayout.addTab(tabLayout.newTab().setText("Add Information"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            try {
                final MyClaimAdapter adapter = new MyClaimAdapter(this.getSupportFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
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

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        //noinspection SimplifiableIfStatement
        try {
            if (id == R.id.action_name) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                View screenView = rootView.getRootView();
                screenView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                screenView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
                sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
                supporteditor.putString(MainActivity.ReferrenceURL, "Claim Information");
                supporteditor.apply();
                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.apply();
                Intent login = new Intent(ClaimInformationView.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                try {
                    mydb = new DatabaseHelper(ClaimInformationView.this);
                    mydb.deleteClaimImgmor();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mydb = new DatabaseHelper(ClaimInformationView.this);
        mydb.deleteClaimImgmor();
        finish();
    }

}