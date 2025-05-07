package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.qrcodelay;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aki.claimreporting.marineinsurance.MarineInsurance;
import com.aki.claimreporting.professionalindemnityinsurance.PIInsurance;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

public class ScanCertificate extends AppCompatActivity {


    public static final String QrCode = "QrCode";
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    public static int qrcodedone = 0;
    public FirebaseCrashlytics mCrashlytics;
    public android.hardware.Camera camera;
    SharedPreferences sharedpreferences;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    ImageView flash;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;
    android.hardware.Camera.Parameters params;
    ImageView falshoff;
    ImageView flashon;
    private String qrCode;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_certificate);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();

        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().setTitle("Scan QR");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        initViews();
    }

    private void initViews() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            surfaceView = findViewById(R.id.surfaceView);
            falshoff = (ImageView) findViewById(R.id.imageViewflasfoff);
            flashon = (ImageView) findViewById(R.id.imageViewflason);


            try {
                flashon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Field[] declaredFields = CameraSource.class.getDeclaredFields();

                        for (Field field : declaredFields) {
                            if (field.getType() == android.hardware.Camera.class) {
                                field.setAccessible(true);
                                try {
                                    android.hardware.Camera camera = (android.hardware.Camera) field.get(cameraSource);
                                    if (camera != null) {
                                        android.hardware.Camera.Parameters params = camera.getParameters();
                                        params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
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

                    }


                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                falshoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Field[] declaredFields = CameraSource.class.getDeclaredFields();

                        for (Field field : declaredFields) {
                            if (field.getType() == android.hardware.Camera.class) {
                                field.setAccessible(true);
                                try {
                                    android.hardware.Camera camera = (android.hardware.Camera) field.get(cameraSource);
                                    if (camera != null) {
                                        android.hardware.Camera.Parameters params = camera.getParameters();
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


    private void initialiseDetectorsAndSources() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            barcodeDetector = new BarcodeDetector.Builder(ScanCertificate.this)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();

            cameraSource = new CameraSource.Builder(ScanCertificate.this, barcodeDetector)
                    .setRequestedPreviewSize(1920, 1080)
                    .setAutoFocusEnabled(true) //you should add this feature
                    .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(ScanCertificate.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraSource.start(surfaceView.getHolder());
                        } else {
                            ActivityCompat.requestPermissions(ScanCertificate.this, new
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
                    //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {
                        intentData = barcodes.valueAt(0).displayValue;
                        String qrCode = intentData;
                        if (qrCode.startsWith("AKI")) {
                            sharedpreferences = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor qrcodeeditor = sharedpreferences.edit();
                            qrcodeeditor.putString(QrCode, qrCode);
                            qrcodeeditor.commit();
                            qrcodedone = 1;
                            qrcodelay = 1;
                            SharedPreferences qrcheckPref = getSharedPreferences("QrCodeNavigation", MODE_PRIVATE);
                            String qrcheck = qrcheckPref.getString("QrCodeCheck", "");
                            if (qrcheck.equals("0")) {
                                Intent login = new Intent(ScanCertificate.this, AddVehicle.class);
                                startActivity(login);
                            } else if (qrcheck.equals("2")) {
                                Intent login = new Intent(ScanCertificate.this, CertificateActivation.class);
                                startActivity(login);
                            } else if (qrcheck.equals("3")) {
                                Intent intent = new Intent(ScanCertificate.this, MarineInsurance.class);
                                intent.putExtra("QrCodeValue", intentData);
                                startActivity(intent);
                            } else if (qrcheck.equals("4")) {
                                Intent intent = new Intent(ScanCertificate.this, PIInsurance.class);
                                intent.putExtra("QrCodeValue", intentData);
                                startActivity(intent);
                            } else {
                                Intent login = new Intent(ScanCertificate.this, VerifyCertificate.class);
                                startActivity(login);
                            }
                            finish();

                        } else {
                            sharedpreferences = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor qrcodeeditor = sharedpreferences.edit();
                            qrcodeeditor.putString(QrCode, qrCode);
                            qrcodeeditor.commit();
                            qrcodedone = 1;
                            qrcodelay = 1;
                            // RegistrationStep1.ocrdetectphysical();
                            //  finish();

                            //  RegistrationStep1.physicaldigital(QrCodeScanner.this);
                            SharedPreferences qrcheckPref = getSharedPreferences("QrCodeNavigation", MODE_PRIVATE);
                            String qrcheck = qrcheckPref.getString("QrCodeCheck", "");
                            Intent login;
                            if (qrcheck.equals("0")) {
                                login = new Intent(ScanCertificate.this, AddVehicle.class);
                            } else if (qrcheck.equals("2")) {
                                login = new Intent(ScanCertificate.this, CertificateActivation.class);
                            } else if (qrcheck.equals("3")) {
                                login = new Intent(ScanCertificate.this, MarineInsurance.class);
                                login.putExtra("QrCodeValue", intentData);
                            } else if (qrcheck.equals("4")) {
                                login = new Intent(ScanCertificate.this, PIInsurance.class);
                                login.putExtra("QrCodeValue", intentData);
                            } else {

                                login = new Intent(ScanCertificate.this, VerifyCertificate.class);
                            }
                            startActivity(login);
                            finish();
                            //}
                            //Intent login = new Intent(QrCodeScanner.this, RegistrationStep1.class);
                            // startActivity(login);
                            //     Intent login = new Intent(QrCodeScanner.this, CertificateScan.class);
                            //     startActivity(login);
                        }

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