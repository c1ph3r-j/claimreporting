package com.aki.claimreporting;

import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;


public class WelcomeScreen extends AppCompatActivity {
    LinearLayout signUpBtn, loginBtn;
    FirebaseCrashlytics mCrashlytics;
    String[] permissions = new String[]{PERMISSION_LOCATION};
    PermissionHandler permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            getWindow().setStatusBarColor(getColor(R.color.lightdarkcolorgrey));
            setContentView(R.layout.activity_welcome_screen);
            init();

        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "onCreate", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            loginBtn = findViewById(R.id.login);
            signUpBtn = findViewById(R.id.signup);
            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {
                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialogC(permissions);
                }
            });


            try {
                loginBtn.setOnClickListener(onClickLogin -> {
                    try {
                        if (permissionManager.hasPermissions(permissions)) {
                            Intent i = new Intent(WelcomeScreen.this, Login.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            permissionManager.requestPermissions(permissions);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                signUpBtn.setOnClickListener(onClickSignUp -> {
                    try {
                        if (permissionManager.hasPermissions(permissions)) {
                            Intent i = new Intent(WelcomeScreen.this, Registration.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            permissionManager.requestPermissions(permissions);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}