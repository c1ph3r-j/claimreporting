package com.aki.claimreporting;

import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class OtherServices extends AppCompatActivity {
    FirebaseCrashlytics mCrashlytics;
    CardView claimDocAdditionBtn, myClaimsBtn, myVehiclesBtn, reportClaimWhatsappBtn;
    CardView verifyMotorCertificateBtn, verifyLifeCertificateBtn, verifyMarineCertificateBtn, verifyPICertificateBtn;
    PermissionHandler permissionManager;
    String[] permissions = new String[]{PERMISSION_LOCATION, PERMISSION_CAMERA};
    CardView supportBtn, consumerVideosBtn, reportGrievanceBtn, ambulanceServicesBtn, policeServicesBtn, towingServiceBtn;
    BottomNavigationView bottomNavigationView;
    DatabaseHelper mydb;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_services);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();
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
            Objects.requireNonNull(getSupportActionBar()).setTitle("Other Services");
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Claim Services.
            claimDocAdditionBtn = findViewById(R.id.claimDocumentationBtn);
            myClaimsBtn = findViewById(R.id.myClaimsBtn);
            myVehiclesBtn = findViewById(R.id.myVehiclesBtn);
            //  reportClaimWhatsappBtn = findViewById(R.id.reportClaimWhatsappBtn);
            ambulanceServicesBtn = findViewById(R.id.ambulanceServicesBtn);
            policeServicesBtn = findViewById(R.id.policeServicesBtn);
            towingServiceBtn = findViewById(R.id.towingServicesBtn);
            // Verification Services.
            verifyMotorCertificateBtn = findViewById(R.id.verifyMotorCertificateBtn);
            verifyLifeCertificateBtn = findViewById(R.id.verifyLifeCertificateBtn);
            verifyMarineCertificateBtn = findViewById(R.id.verifyMarineCertificate);
            verifyPICertificateBtn = findViewById(R.id.verifyPiCertificate);

            // Support Services.
            supportBtn = findViewById(R.id.supportBtn);
            consumerVideosBtn = findViewById(R.id.consumerVideosBtn);
            reportGrievanceBtn = findViewById(R.id.reportGrievanceBtn);

//            reportClaimWhatsappBtn.setOnClickListener(onClickReportClaim ->{
//                try {
//                    String phoneNumberWithCountryCode = "+254769782488";
//                    String message = "Hi";
//
//                    startActivity(
//                            new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse(
//                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
//                                    )
//                            )
//                    );
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                    mCrashlytics.recordException(e);
//                }
//            });

            // Bottom Navigation View.
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            // Access the menu
            Menu menu = bottomNavigationView.getMenu();
            if(isUserSignedUp()){
                menu.add(Menu.NONE, R.id.profileView, Menu.NONE, "Account")
                        .setIcon(R.drawable.user_ic);
            }else{
                menu.removeItem(R.id.profileView);
            }
            try {
                bottomNavigationView.setOnItemSelectedListener(selectedItem -> {
                    try {
                        int itemId = selectedItem.getItemId();
                        if (itemId == R.id.homeView) {
                            finish();
                        } else if (itemId == R.id.addVehicleView) {
                            selectedItem.setChecked(true);
                            Intent redirect1 = new Intent(OtherServices.this, VehicleOwnership.class);
                            startActivity(redirect1);
                        } else if (itemId == R.id.myClaimsView) {
                            selectedItem.setChecked(true);
                            startActivity(new Intent(OtherServices.this, MyClaims.class));
                        } else if (itemId == R.id.profileView) {
                            selectedItem.setChecked(true);
                            Intent redirect3 = new Intent(OtherServices.this, Profile.class);
                            startActivity(redirect3);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    return true;
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            claimDocAdditionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startMyClaimsForClaimDoc = new Intent(OtherServices.this, MyClaims.class);
                    startMyClaimsForClaimDoc.putExtra("isFromOtherServices", true);
                    startActivity(startMyClaimsForClaimDoc);
                }
            });


            myClaimsBtn.setOnClickListener(onClickMyClaims -> {
                try {
                    startActivity(new Intent(OtherServices.this, MyClaims.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            myVehiclesBtn.setOnClickListener(onClickMyVehicles -> {
                try {
                    startActivity(new Intent(OtherServices.this, MyVehicles.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            verifyMotorCertificateBtn.setOnClickListener(onClickVerifyMotor -> {
                try {
                    if (permissionManager.hasPermissions(permissions)) {
                        startActivity(new Intent(OtherServices.this, ScanQrForMotorInsure.class));
                    } else {
                        permissionManager.requestPermissions(permissions);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            verifyLifeCertificateBtn.setOnClickListener(onClickVerifyLife -> {
                try {
                    if (permissionManager.hasPermissions(permissions)) {
                        startActivity(new Intent(OtherServices.this, ScanQrForLifeInsure.class));
                    } else {
                        permissionManager.requestPermissions(permissions);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });
            SharedPreferences qrcheckPref = getSharedPreferences("QrCodeNavigation", MODE_PRIVATE);
            SharedPreferences.Editor editor = qrcheckPref.edit();

            verifyMarineCertificateBtn.setOnClickListener(onClickMarine -> {
                if (permissionManager.hasPermissions(permissions)) {
                    Intent intent = new Intent(this, ScanCertificate.class);
                    editor.putString("QrCodeCheck", "3");
                    editor.apply();
                    startActivity(intent);
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            });

            verifyPICertificateBtn.setOnClickListener(onClickPI -> {
                if (permissionManager.hasPermissions(permissions)) {
                    Intent intent = new Intent(this, ScanCertificate.class);
                    editor.putString("QrCodeCheck", "4");
                    editor.apply();
                    startActivity(intent);
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            });

            supportBtn.setOnClickListener(onClickConnectWithCompany -> {
                try {
                    startActivity(new Intent(OtherServices.this, InsuranceCompanySupport.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            consumerVideosBtn.setOnClickListener(onClickConsumerSupport -> {
                try {
                    startActivity(new Intent(OtherServices.this, ConsumerHelpVideos.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            reportGrievanceBtn.setOnClickListener(onClickReportGrievance -> {
                try {
                    if (isUserSignedUp()) {
                        Intent intent = new Intent(this,Grievance.class);
                        intent.putExtra("grievance",true);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(OtherServices.this, Registration.class);
                        intent.putExtra("grievance", true);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            ambulanceServicesBtn.setOnClickListener(onClickAmbulanceService -> {
                try {
                    MainActivity.ambulanceenabled = "Yes";
                    MainActivity.policeinfoenabled = "No";
                    MainActivity.towingagencyenabled = "No";
                    Intent serviceProviderIntent = new Intent(OtherServices.this, ServiceProvider.class);
                    serviceProviderIntent.putExtra("isFromOtherServices", true);
                    startActivity(serviceProviderIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            policeServicesBtn.setOnClickListener(onClickPoliceService -> {
                try {
//                    MainActivity.ambulanceenabled = "No";
//                    MainActivity.policeinfoenabled = "Yes";
//                    MainActivity.towingagencyenabled = "No";
//                    Intent serviceProviderIntent = new Intent(OtherServices.this, ServiceProvider.class);
//                    serviceProviderIntent.putExtra("OtherService", true);
//                    startActivity(serviceProviderIntent);

                    Intent intentmap = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=near%20by%20police%20stations"));
                    startActivity(intentmap);

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            towingServiceBtn.setOnClickListener(onClickTowingService -> {
                try {
                    MainActivity.ambulanceenabled = "No";
                    MainActivity.policeinfoenabled = "No";
                    MainActivity.towingagencyenabled = "Yes";
                    Intent serviceProviderIntent = new Intent(OtherServices.this, ServiceProvider.class);
                    serviceProviderIntent.putExtra("isFromOtherServices", true);
                    startActivity(serviceProviderIntent);
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
    }

    private boolean isUserSignedUp() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mydb = new DatabaseHelper(this);
            if (mydb.getTokendetails().getCount() != 0 && mydb.getUserPhoneNumber().length() == 9) {
                Cursor firebaseUserId = mydb.getTokendetails();
                return firebaseUserId.getCount() >= 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(this,Dashboard.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(OtherServices.this, Dashboard.class);
        startActivity(login);
        //  Intent login = new Intent(Grievance.this, AddVehicle.class);
        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
        // startActivity(login);
        //finishAffinity(); // or finish();
    }
}