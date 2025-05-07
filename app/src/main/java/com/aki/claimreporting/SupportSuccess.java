package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;


public class SupportSuccess extends AppCompatActivity {

    TextView txtcraidtxt, txtcraid1txt;
    Button backdash;
    FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Success");
        mCrashlytics = FirebaseCrashlytics.getInstance();
        setContentView(R.layout.activity_support_success);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            preventSizeChange(this, getSupportActionBar());
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
            backdash = (Button) findViewById(R.id.backdashproceed);
            try {
                backdash.setOnClickListener(onClickBack -> {
                    Intent otpIntent = new Intent(SupportSuccess.this, Dashboard.class);
                    startActivity(otpIntent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            txtcraidtxt = (TextView) findViewById(R.id.txtcraid);
            txtcraid1txt = (TextView) findViewById(R.id.txtcraid1);

            try {
                txtcraidtxt.setText("Your Reference Number :" + InsuranceCompanySupport.supportRefiD);
                txtcraid1txt.setText("Thanks for your request. We have submitted your request to " + InsuranceCompanySupport.membercompany + " and you can expect a response within 1 working day to your mail address.");
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
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            Intent redirect = new Intent(SupportSuccess.this, Dashboard.class);
            startActivity(redirect);
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
        Intent login = new Intent(SupportSuccess.this, Dashboard.class);
        startActivity(login);
    }
}