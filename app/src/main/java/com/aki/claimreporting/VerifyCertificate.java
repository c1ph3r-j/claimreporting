package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class VerifyCertificate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Validate Certificate");
        preventSizeChange(this, getSupportActionBar());
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_verify_certificate);
    }
}