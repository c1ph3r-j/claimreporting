package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class ConsumerHelpVideos extends AppCompatActivity {
    public static final String ConsumerHelpVideoName = "ConsumerHelpVideoName";
    public ProgressDialog progressdialog;
    SharedPreferences sharedpreferences;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_help_videos);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Consumer Help Videos");
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        init();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            TableLayout vehiclereg = findViewById(R.id.txtvehicle);
            try {
                vehiclereg.setOnClickListener(onClickVehicleReg -> {
                    if (isNetworkConnected()) {
                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                        vimeoeditor.putString(ConsumerHelpVideoName, "whatIsInsurance");
                        vimeoeditor.apply();
                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
                        startActivity(regsiter);
                    } else {
                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout craregister = findViewById(R.id.txtcra);
            try {
                craregister.setOnClickListener(craRegister -> {
                    if (isNetworkConnected()) {
                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                        vimeoeditor.putString(ConsumerHelpVideoName, "UnderstandYourPolicy");
                        vimeoeditor.apply();
                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
                        startActivity(regsiter);
                    } else {
                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout cracollision = findViewById(R.id.txtclaimCollision);
            try {
                cracollision.setOnClickListener(carCollision -> {
                    if (isNetworkConnected()) {
                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                        vimeoeditor.putString(ConsumerHelpVideoName, "InsuranceClaimExplained");
                        vimeoeditor.apply();
                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
                        startActivity(regsiter);
                    } else {
                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout craselfacci = findViewById(R.id.txtself);
            try {
                craselfacci.setOnClickListener(view -> {
                    if (isNetworkConnected()) {
                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                        vimeoeditor.putString(ConsumerHelpVideoName, "UnderstandLifeInsurance");
                        vimeoeditor.apply();
                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
                        startActivity(regsiter);
                    } else {
                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

//            TableLayout craastolentacc = findViewById(R.id.txtclaimstolen);
//            try {
//                craastolentacc.setOnClickListener(craStolenTheft -> {
//                    if (isNetworkConnected()) {
//                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
//                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
//                        vimeoeditor.putString(ConsumerHelpVideoName, "VehicleValuation");
//                        vimeoeditor.apply();
//                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
//                        startActivity(regsiter);
//                    } else {
//                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
//                    }
//
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }

//            TableLayout craoffline = findViewById(R.id.txtofflineclaim);
//            try {
//                craoffline.setOnClickListener(craOffline -> {
//                    if (isNetworkConnected()) {
//                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
//                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
//                        vimeoeditor.putString(ConsumerHelpVideoName, "VehicleWriteOff");
//                        vimeoeditor.apply();
//                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
//                        startActivity(regsiter);
//                    } else {
//                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }

            TableLayout customsupport = findViewById(R.id.txtcustomersupport);
            try {
                customsupport.setOnClickListener(customerSupport -> {
                    if (isNetworkConnected()) {
                        sharedpreferences = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
                        SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                        vimeoeditor.putString(ConsumerHelpVideoName, "UnderstandingMotorInsurance");
                        vimeoeditor.apply();
                        Intent regsiter = new Intent(ConsumerHelpVideos.this, CustomerVimeVideo.class);
                        startActivity(regsiter);
                    } else {
                        Toast.makeText(ConsumerHelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "OnOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Intent login = new Intent(ConsumerHelpVideos.this, Dashboard.class);
//        startActivity(login);
    }
}