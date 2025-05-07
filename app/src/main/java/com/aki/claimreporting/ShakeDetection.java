package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ShakeDetection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_detection);
        preventSizeChange(this, getSupportActionBar());
    }
}