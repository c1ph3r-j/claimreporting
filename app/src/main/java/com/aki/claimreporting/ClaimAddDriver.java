package com.aki.claimreporting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ClaimAddDriver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.getSupportActionBar().hide();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        setContentView(R.layout.activity_claim_add_driver);
    }
}