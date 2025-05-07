package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_RECORD_AUDIO;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class AccidentDescription extends AppCompatActivity {
    final int IMAGE_TAKE_CODE = 1234;
    FirebaseCrashlytics mCrashlytics;
    VideoView videoViewEvidence;
    ImageView videoEvidenceClose;
    TextView videoEvidence;
    PermissionHandler permissionManager;
    String[] permissions = new String[]{PERMISSION_CAMERA, PERMISSION_LOCATION, PERMISSION_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_RECORD_AUDIO};
    LinearLayout proceedBtn, SkipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_description);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Accident Description (Optional)");
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            preventSizeChange(this, getSupportActionBar());

            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {
                    videoEvidence.performClick();
                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialogC(PERMISSION_CAMERA_AND_STORAGE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            videoEvidenceClose = findViewById(R.id.videoevidencecloseicon);
            videoEvidence = findViewById(R.id.videoevidencebtntxt);
            videoViewEvidence = findViewById(R.id.videoViewevidence);
            proceedBtn = findViewById(R.id.nextid);

            SkipButton = findViewById(R.id.SkipButton);


            try {
                videoEvidence.setOnClickListener(onClickVideoEvidence -> {
                    if (permissionManager.hasPermissions(PERMISSION_CAMERA_AND_STORAGE)) {
                        Intent login = new Intent(AccidentDescription.this, RecordVideoEvidence.class);
                        login.putExtra("LocationVal", 2);
                        startActivity(login);
                        finish();
                    } else {
                        permissionManager.requestPermissions(PERMISSION_CAMERA_AND_STORAGE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                videoEvidenceClose.setOnClickListener(onClickVideoEvidenceClose -> {
                    videoViewEvidence.setVisibility(View.GONE);
                    videoEvidenceClose.setVisibility(View.GONE);
                    videoEvidence.setVisibility(View.VISIBLE);
                    SkipButton.setVisibility(View.VISIBLE);
//                    proceedBtn.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                proceedBtn.setOnClickListener(onClickProceed -> {
                    SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                    String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                    if (incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3") {

                        Intent signUpIntent = new Intent(AccidentDescription.this, StolenImageDeclaration.class);
                        startActivity(signUpIntent);
                    } else {
                        Intent signUpIntent = new Intent(AccidentDescription.this, ImageDeclaration.class);
                        startActivity(signUpIntent);
                    }
                });

                SkipButton.setOnClickListener(onClickProceed -> {
                    SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                    String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                    if (incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3") {

                        Intent signUpIntent = new Intent(AccidentDescription.this, StolenImageDeclaration.class);
                        startActivity(signUpIntent);
                    } else {
                        Intent signUpIntent = new Intent(AccidentDescription.this, ImageDeclaration.class);
                        startActivity(signUpIntent);
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            SharedPreferences sharedPreference = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
            String filepath = sharedPreference.getString("videofilepathlocation", "");
            try {
                if (!Objects.equals(filepath, "")) {
                    videoViewEvidence.setVisibility(View.VISIBLE);
                    videoEvidence.setVisibility(View.GONE);
                    videoEvidenceClose.setVisibility(View.VISIBLE);
                    SkipButton.setVisibility(View.GONE);
//                    proceedBtn.setVisibility(View.VISIBLE);

                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoViewEvidence);
                    Uri uri = Uri.parse(filepath);
                    videoViewEvidence.setMediaController(mediaController);
                    videoViewEvidence.setVideoURI(uri);
                    videoViewEvidence.requestFocus();
                    videoViewEvidence.start();
                }
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (permissionManager.hasPermissions(PERMISSION_CAMERA_AND_STORAGE)) {
                if (requestCode == IMAGE_TAKE_CODE) {
                    //TODO: action

                    SharedPreferences sharedPreference = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                    String filepath = sharedPreference.getString("videofilepathlocation", "");
                    try {
                        if (!Objects.equals(filepath, "")) {
                            videoViewEvidence.setVisibility(View.VISIBLE);
                            videoEvidence.setVisibility(View.GONE);
                            videoEvidenceClose.setVisibility(View.VISIBLE);
                            SkipButton.setVisibility(View.GONE);
//                            proceedBtn.setVisibility(View.VISIBLE);
                            MediaController mediaController = new MediaController(this);
                            mediaController.setAnchorView(videoViewEvidence);
                            Uri uri = Uri.parse(filepath);
                            videoViewEvidence.setMediaController(mediaController);
                            videoViewEvidence.setVideoURI(uri);
                            videoViewEvidence.requestFocus();
                            videoViewEvidence.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}