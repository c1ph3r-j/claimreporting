package com.aki.claimreporting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TelematicsRedirect extends AppCompatActivity {

    public TextView apktext;
    Button updatenow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telematics_redirect);
        init();
    }

    public void init() {
        updatenow = (Button) findViewById(R.id.Btnmandator);
        updatenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://www.telematics.com/?id=12"));
//                startActivity(intent);

                PackageManager manager = getPackageManager();
                Intent intent = manager.getLaunchIntentForPackage("com.swiftant.telematics");
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intent);

//                Uri mUri = Uri.parse("market://details?id=" + "com.swiftant.telematics");
//                Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
//                startActivity(mIntent );
            }
        });
    }
}