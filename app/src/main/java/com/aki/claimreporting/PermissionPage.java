package com.aki.claimreporting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class PermissionPage extends AppCompatActivity {

    public LinearLayout permissionroceed;
    ScrollView scrollView;
    FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            getWindow().setStatusBarColor(getColor(R.color.lightdarkcolorgrey));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        setContentView(R.layout.activity_permission_page);

        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            scrollView = (ScrollView) findViewById(R.id.scrollView);
            permissionroceed = findViewById(R.id.Btnpermissionroceed);

            try {
                scrollView.getViewTreeObserver()
                        .addOnScrollChangedListener(() -> {
                            if (scrollView.getChildAt(0).getBottom()
                                    <= (scrollView.getHeight() + scrollView.getScrollY())) {
                                //scroll view is at bottom

                                permissionroceed.setBackgroundColor(getColor(R.color.purple_500));
                                permissionroceed.setClickable(true);
                            } else {
                                //scroll view is not at bottom
                                permissionroceed.setBackgroundColor(Color.GRAY);
                                permissionroceed.setClickable(false);
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                permissionroceed.setOnClickListener(onClickProceed -> {
                    Intent login;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        login = new Intent(PermissionPage.this, WelcomeScreen.class);
                        startActivity(login);
                    }
                });
                permissionroceed.setClickable(false);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    //    public void onScrollChanged(){
//        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
//        int topDetector = scrollView.getScrollY();
//        int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
//        if(bottomDetector == 0 ){
//
//            String test1 = "tes1";
//
//        }
//        if(topDetector <= 0){
//            String test2 = "tes2";
//        }
//    }
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return false;
//    }


}