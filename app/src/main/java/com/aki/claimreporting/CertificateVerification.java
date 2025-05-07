package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class CertificateVerification extends AppCompatActivity {
    LinearLayout motorInsure, lifeInsure, marineInsure, piInsure;
    PermissionHandler permissionManager;
    String[] permissions = new String[]{PERMISSION_LOCATION, PERMISSION_CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_verification);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            try {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Verify Certificate");
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                motorInsure = findViewById(R.id.motorInsure);
                lifeInsure = findViewById(R.id.lifeInsure);
                marineInsure = findViewById(R.id.marineInsure);
                piInsure = findViewById(R.id.piInsure);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                motorInsure.setOnClickListener(onClickMotorInsurance -> {
                    if (permissionManager.hasPermissions(permissions)) {
                        startActivity(new Intent(CertificateVerification.this, ScanQrForMotorInsure.class));
                    } else {
                        permissionManager.requestPermissions(permissions);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            SharedPreferences qrcheckPref = getSharedPreferences("QrCodeNavigation", MODE_PRIVATE);
            SharedPreferences.Editor editor = qrcheckPref.edit();
            marineInsure.setOnClickListener(onClickMarineInsure -> {
                if (permissionManager.hasPermissions(permissions)) {
                    Intent intent = new Intent(this, ScanCertificate.class);
                    editor.putString("QrCodeCheck", "3");
                    editor.apply();
                    startActivity(intent);
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            });

            piInsure.setOnClickListener(OnClickPiInsure -> {
                if (permissionManager.hasPermissions(permissions)) {
                    Intent intent = new Intent(this, ScanCertificate.class);
                    editor.putString("QrCodeCheck", "4");
                    editor.apply();
                    startActivity(intent);
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            });
            try {
                lifeInsure.setOnClickListener(onClickLifeInsurance -> {
                    if (permissionManager.hasPermissions(permissions)) {
                        startActivity(new Intent(CertificateVerification.this, ScanQrForLifeInsure.class));
                    } else {
                        permissionManager.requestPermissions(permissions);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}