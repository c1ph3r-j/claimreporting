package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class VehicleOwnership extends AppCompatActivity {

    public static String ownVehicleval;
    public static String authorizedval;
    public static View authlinearview;
    public static TextView sucesscantxt;
    public static DatabaseHelper mydb;
    public static RadioGroup vehyoursGroup, vehothersGroup;
    public static RadioButton vehothersnoButton, vehyothersdriverButton, vehothersyesButton, vehyoursyesButton, vehyoursnoButton;
    public static LinearLayout authlinearlayval, vehsucesslinearlayval, vehfailelinearlayval;
    public String stokenval;
    public LinearLayout prcdscanbutton, backhomebutton;
    public FirebaseCrashlytics mCrashlytics;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_ownership);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Add Vehicle");
            preventSizeChange(this, getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity = this;
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
            {
                SharedPreferences envuatshared = getSharedPreferences("ENVUAT", Context.MODE_PRIVATE);
                String uatenvi = "1";
/*
        String uatenvi = envuatshared.getString(MainActivity.uatenvironment,"");
*/
       /* if(uatenvi.equals("0"))
        {
            uatenvirlinear.setVisibility(View.GONE);
        }
        else
        {
            uatenvirlinear.setVisibility(View.VISIBLE);
        }*/

                authlinearview = (View) findViewById(R.id.view24);

                sucesscantxt = (TextView) findViewById(R.id.vehcsucessscan);

                authlinearlayval = (LinearLayout) findViewById(R.id.authlinealay);
                vehsucesslinearlayval = (LinearLayout) findViewById(R.id.vehcsuceslinearlay);
                vehfailelinearlayval = (LinearLayout) findViewById(R.id.vehcfaillinearlay);
                prcdscanbutton = (LinearLayout) findViewById(R.id.btnproceedscan);
                backhomebutton = (LinearLayout) findViewById(R.id.btnproceedfailscan);
                vehsucesslinearlayval.setVisibility(View.GONE);
                vehfailelinearlayval.setVisibility(View.GONE);
                authlinearlayval.setVisibility(View.GONE);
                authlinearview.setVisibility(View.GONE);
                backhomebutton.setVisibility(View.GONE);
                prcdscanbutton.setVisibility(View.GONE);
                vehyoursGroup = (RadioGroup) findViewById(R.id.vehbelyouradiogroup);
                vehyoursyesButton = (RadioButton) findViewById(R.id.radioovehbelyour1);
                vehyoursnoButton = (RadioButton) findViewById(R.id.radiovehbelyour2);

                try {
                    vehyoursGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                        try {
                            if (i == R.id.radioovehbelyour1) {
                                ownVehicleval = getString(R.string.own_vehicle_val1);
                                sucesscantxt.setVisibility(View.VISIBLE);
                                sucesscantxt.setText(getString(R.string.for_audit_purposes));
                                vehsucesslinearlayval.setVisibility(View.VISIBLE);
                                vehfailelinearlayval.setVisibility(View.GONE);
                                authlinearlayval.setVisibility(View.GONE);
                                backhomebutton.setVisibility(View.GONE);
                                prcdscanbutton.setVisibility(View.VISIBLE);
                                authlinearview.setVisibility(View.GONE);
                            } else if (i == R.id.radiovehbelyour2) {
                                ownVehicleval = getString(R.string.own_vehicle_val2);
                                authlinearlayval.setVisibility(View.VISIBLE);
                                vehsucesslinearlayval.setVisibility(View.GONE);
                                vehfailelinearlayval.setVisibility(View.GONE);
                                authlinearview.setVisibility(View.VISIBLE);
                                vehothersyesButton.setChecked(false);
                                backhomebutton.setVisibility(View.GONE);
                                vehyothersdriverButton.setChecked(false);
                                prcdscanbutton.setVisibility(View.GONE);
                                vehothersnoButton.setChecked(false);
                                vehsucesslinearlayval.setVisibility(View.GONE);
                                vehfailelinearlayval.setVisibility(View.GONE);
                                sucesscantxt.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
                vehothersGroup = (RadioGroup) findViewById(R.id.youauthorizeradiogroup);
                vehothersyesButton = (RadioButton) findViewById(R.id.radioyouauthorize1);
                vehyothersdriverButton = (RadioButton) findViewById(R.id.radioyouauthorize2);
                vehothersnoButton = (RadioButton) findViewById(R.id.radioyouauthorize3);
                try {
                    vehothersGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                        try {
                            if (i == R.id.radioyouauthorize1) {
                                authorizedval = getString(R.string.authorizedval1);
                                vehsucesslinearlayval.setVisibility(View.VISIBLE);
                                vehfailelinearlayval.setVisibility(View.GONE);
                                prcdscanbutton.setVisibility(View.VISIBLE);
                                backhomebutton.setVisibility(View.GONE);
                                sucesscantxt.setVisibility(View.VISIBLE);
                                sucesscantxt.setText(getString(R.string.for_audit_purposes_recorded_and_shared));
                            } else if (i == R.id.radioyouauthorize2) {
                                authorizedval = getString(R.string.authorizedval2);
                                vehsucesslinearlayval.setVisibility(View.VISIBLE);
                                vehfailelinearlayval.setVisibility(View.GONE);
                                prcdscanbutton.setVisibility(View.VISIBLE);
                                backhomebutton.setVisibility(View.GONE);
                                sucesscantxt.setVisibility(View.VISIBLE);
                                sucesscantxt.setText(getString(R.string.for_audit_purposes_recorded_and_shared));
                            } else if (i == R.id.radioyouauthorize3) {
                                authorizedval = getString(R.string.authorizedval3);
                                vehsucesslinearlayval.setVisibility(View.GONE);
                                prcdscanbutton.setVisibility(View.GONE);
                                backhomebutton.setVisibility(View.VISIBLE);
                                vehfailelinearlayval.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

                try {
                    prcdscanbutton.setOnClickListener(onClickProceedScan -> {
                        Intent login = new Intent(VehicleOwnership.this, AddVehicle.class);
                        startActivity(login);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

                try {
                    backhomebutton.setOnClickListener(onClickBack -> {
                        Intent login = new Intent(VehicleOwnership.this, Dashboard.class);
                        startActivity(login);
                    });
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
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_name) {
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
//            supporteditor.putString(MainActivity.ReferrenceURL, getString(R.string.own_vehicle));
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(VehicleOwnership.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//
//            // Do something
//
//        } else {
        onBackPressed();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}