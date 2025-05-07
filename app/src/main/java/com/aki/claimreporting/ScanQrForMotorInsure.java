package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

public class ScanQrForMotorInsure extends AppCompatActivity {

    public static final String QrCode = "QrCode";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    public static int qrcodedone = 0;
    public FirebaseCrashlytics mCrashlytics;
    SharedPreferences sharedpreferences;
    SurfaceView surfaceView;
    String intentData = "";
    ImageView falshoff;
    ImageView flashon;
    boolean isForwarded = false;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_for_motor_insure);
        preventSizeChange(this, getSupportActionBar());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Scan QR");

        mCrashlytics = FirebaseCrashlytics.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    private void initViews() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            sharedpreferences = getSharedPreferences("ShareValPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor qrcodeeditor = sharedpreferences.edit();
            qrcodeeditor.putString(QrCode, "");
            qrcodeeditor.putInt("status", qrcodedone);
            qrcodeeditor.apply();

            surfaceView = findViewById(R.id.surfaceView);
            falshoff = findViewById(R.id.imageViewflasfoff);
            flashon = findViewById(R.id.imageViewflason);

            flashon.setOnClickListener(ToOnTheFlashLight -> {

                Field[] declaredFields = CameraSource.class.getDeclaredFields();

                for (Field field : declaredFields) {
                    if (field.getType() == Camera.class) {
                        field.setAccessible(true);
                        try {
                            Camera camera = (Camera) field.get(cameraSource);
                            if (camera != null) {
                                Camera.Parameters params = camera.getParameters();
                                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(params);
                                falshoff.setVisibility(View.VISIBLE);
                                flashon.setVisibility(View.GONE);
                                flashon.setImageResource(R.drawable.flashoff);
                            }


                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        break;
                    }
                }

            });
            falshoff.setOnClickListener(ToOffTheFlashLight -> {

                Field[] declaredFields = CameraSource.class.getDeclaredFields();

                for (Field field : declaredFields) {
                    if (field.getType() == Camera.class) {
                        field.setAccessible(true);
                        try {
                            Camera camera = (Camera) field.get(cameraSource);
                            if (camera != null) {
                                Camera.Parameters params = camera.getParameters();
                                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(params);
                                flashon.setVisibility(View.VISIBLE);
                                falshoff.setVisibility(View.GONE);
                                flashon.setImageResource(R.drawable.flashon);
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        break;
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void initialiseDetectorsAndSources() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {


            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(ScanQrForMotorInsure.this)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();

            cameraSource = new CameraSource.Builder(ScanQrForMotorInsure.this, barcodeDetector)
                    .setRequestedPreviewSize(1920, 1080)
                    .setAutoFocusEnabled(true) //you should add this feature
                    .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(ScanQrForMotorInsure.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraSource.start(surfaceView.getHolder());
                        } else {
                            ActivityCompat.requestPermissions(ScanQrForMotorInsure.this, new
                                    String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });


            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    try {
                        if (barcodes.size() != 0) {
                            if (!isForwarded) {
                                isForwarded = true;
                                intentData = barcodes.valueAt(0).displayValue;
                                String qrCode = intentData;
                                qrcodedone = 1;

                                SharedPreferences.Editor qrcodeeditor = sharedpreferences.edit();
                                qrcodeeditor.putString(QrCode, qrCode);
                                qrcodeeditor.putInt("status", qrcodedone);
                                qrcodeeditor.apply();

                                startActivity(new Intent(ScanQrForMotorInsure.this, GetMotorInsureDetails.class));
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        SharedPreferences.Editor qrcodeeditor = sharedpreferences.edit();
                        qrcodeeditor.putString(QrCode, "");
                        qrcodeeditor.putInt("status", qrcodedone);
                        qrcodeeditor.apply();
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}