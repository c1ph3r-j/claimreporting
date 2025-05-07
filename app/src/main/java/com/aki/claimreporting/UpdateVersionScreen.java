package com.aki.claimreporting;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class UpdateVersionScreen extends AppCompatActivity {

    TextView newAppVersion;
    TextView currentVersion;
    Button updateAppBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Update Version");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ColorDrawable colorDrawable
                    = new ColorDrawable(getColor(R.color.purple_500));

            // Set BackgroundDrawable
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        setContentView(R.layout.activity_update_version_screen);
        init();
    }

    public void init() {
        try {
            currentVersion = findViewById(R.id.stepoldverid);

            updateAppBtn = findViewById(R.id.updateAppBtn);
            newAppVersion = findViewById(R.id.stepnewverid);

            currentVersion.setText(MainActivity.latestVersion);
            newAppVersion.setText(getString(R.string.app_version));

            updateAppBtn.setOnClickListener(onClickUpdate -> {
                //Handle update redirect here.
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(MainActivity.versionapkurl));
                startActivity(intent);
            });

            // updatelinkurl.setText("https://swiftant0-my.sharepoint.com/:u:/g/personal/arun_s_swiftant_com/EdwxESz5WFdCsgzGH5etxe4BxuIYS6NLbXMmwxf4XJ17IA?e=iws7Vu");
//        updatenow = (Button) findViewById(R.id.Btnmandator);
//        updatenow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.aki.cralite"));
//                startActivity(intent);
//            }
//        });

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "init", e.getMessage(), e.toString());
            //mCrashlytics.recordException(e);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        try {

            finishAffinity();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}