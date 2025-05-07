package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class VehicleInformationView extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    TabLayout tabLayout;
    ViewPager viewPager;
    Context context;
    private int[] tabIcons = {
            R.drawable.phone,
            R.drawable.addgrey
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_information_view);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences vechPref = getSharedPreferences("ViewVehicle", MODE_PRIVATE);
            String certid = vechPref.getString(MyVehicles.ViewCertificateno, "");
            SharedPreferences regPref = getSharedPreferences("ViewRegNum", MODE_PRIVATE);
            String regidid = regPref.getString(MyVehicles.ViewRegno, "");
            Objects.requireNonNull(getSupportActionBar()).setTitle(certid + " : " + regidid);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            preventSizeChange(this, getSupportActionBar());
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));

            // Set BackgroundDrawable
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            context = this;
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.pager);
            tabLayout.addTab(tabLayout.newTab().setText("Information"));
            tabLayout.addTab(tabLayout.newTab().setText("Drivers"));
            //setupTabIcons();
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            final MyVehicleAdapter adapter = new MyVehicleAdapter(context, this.getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

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
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//
//            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//            View screenView = rootView.getRootView();
//            screenView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//            screenView.setDrawingCacheEnabled(false);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//            supporteditor.putString(MainActivity.ReferrenceURL, "Vehicle Information");
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(VehicleInformationView.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//
//            // Do something
//
//        } else {
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Fragment frag;
//        FragmentManager fm1 = VehicleInformationView.this.getSupportFragmentManager();
//        FragmentTransaction ft1 = fm1.beginTransaction();
//        frag = new MyDriiver();
//        ft1.replace(R.id.activity_main_content_fragment, frag);
//        ft1.apply();
//        Fragment fragment = new MyDriiver();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_mydriver, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).apply();

//        Intent login = new Intent(VehicleInformationView.this, MyDriiver.class);
//        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
//        startActivity(login);
//        MyVehicles scanCertificatephysical = new MyVehicles();
//        FragmentManager manager = VehicleInformationView.this.getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.nav_myvehicle,scanCertificatephysical,scanCertificatephysical.getTag()).apply();
//        Fragment fragment = new MyDriiver();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_myvehicle, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).apply();

//        MyDriiver fragmentS1 = new MyDriiver();
//        getSupportFragmentManager().beginTransaction().replace(R.id.nav_myvehicle, fragmentS1).apply();

        //finish();
//        Intent login = new Intent(VehicleInformationView.this, HomePage.class);
//        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
//        startActivity(login);


    }

}