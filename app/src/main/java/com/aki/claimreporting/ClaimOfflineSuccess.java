package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class ClaimOfflineSuccess extends AppCompatActivity {

    Button backhome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Success");
        setContentView(R.layout.activity_claim_offline_success);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        preventSizeChange(this, getSupportActionBar());
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            backhome = findViewById(R.id.Btnuofflineback);
            backhome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent otpIntent = new Intent(ClaimOfflineSuccess.this, Dashboard.class);
                    startActivity(otpIntent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }
}