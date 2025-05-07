package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ProgressDialogUpload extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static TextView txtsuc;
    FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dialog_upload);
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setTitle("Damage Detection");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));
        mCrashlytics = FirebaseCrashlytics.getInstance();
        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    mydb = new DatabaseHelper(ProgressDialogUpload.this);
                    Cursor curseattachtoken = mydb.getinsertloceattachment();
                    int counttoken = curseattachtoken.getCount();
                    int counttoken1 = MainActivity.damagecountnew;
                    if (counttoken == MainActivity.damagecountnew) {
                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                        String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                        if (incidenttypeval == "F82589E6-7344-47B2-A672-11013F538551") {
                            startActivity(new Intent(ProgressDialogUpload.this, AccidentDescription.class));
                            finish();
                        } else {
                            Intent car = new Intent(ProgressDialogUpload.this, ThirdParty.class);
                            startActivity(car);
                            finish();
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "OnCreate", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

                handler.postDelayed(this, 5000); //now is every 2 minutes
            }
        }, 5000); //Every 120000 ms (2 minutes)
    }
}