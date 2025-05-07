package com.aki.claimreporting;

import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class CaptureDamagedParts extends AppCompatActivity {
    public static DatabaseHelper mydb;
    public static String carviewcircle1, carviewcircle11, carviewcircle8, carviewcircle7, carviewcircle4;
    public static String carviewselectionid;
    public static boolean isActivityPaused = false;
    FirebaseCrashlytics mCrashlytics;
    ImageView frontViewSuccess, backViewSuccess, driverSideViewSuccess, passengerSideViewSuccess, roofViewSuccess;
    CardView frontViewCount, backViewCount, driverSideViewCount, passengerSideViewCount, roofViewCount;
    TextView frontViewtxt, backViewtxt, driverSideViewtxt, passengerSideViewtxt, roofViewtxt;
    TextView proceedid;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    PermissionHandler permissionManager;
    LinearLayout frontView, backView, driverSideView, passengerSideView, roofView;
    public static Activity activityCaptureDamagedParts;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityPaused = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_damagedparts);
        activityCaptureDamagedParts = this;
        //String methodName = Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();

        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Take photo of damaged Part");
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {
                    recreate();
                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialogC(permissions);
                }
            });
            if (permissionManager.hasPermissions(permissions)) {
                init();
            } else {
                permissionManager.requestPermissions(permissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    void init() {
        //String methodName = Objects.requireNonNull(new Object() {}.getClass().getEnclosingMethod()).getName();
        try {
            // Views
            proceedid = (TextView) findViewById(R.id.proceedBtn);


            frontView = (LinearLayout) findViewById(R.id.frontView);
            backView = (LinearLayout) findViewById(R.id.backView);
            driverSideView = (LinearLayout) findViewById(R.id.driverSideView);
            passengerSideView = (LinearLayout) findViewById(R.id.passengerSideView);
            roofView = (LinearLayout) findViewById(R.id.roofView);
            // Success Icons
            frontViewSuccess = (ImageView) findViewById(R.id.frontViewSuccessIc);
            backViewSuccess = (ImageView) findViewById(R.id.backViewSuccessIc);
            driverSideViewSuccess = (ImageView) findViewById(R.id.driverSideViewSuccessIc);
            passengerSideViewSuccess = (ImageView) findViewById(R.id.passengerSideViewSuccessIc);
            roofViewSuccess = (ImageView) findViewById(R.id.roofViewSuccessIc);

//            frontViewSuccess.setVisibility(View.GONE);
//            backViewSuccess.setVisibility(View.GONE);
//            driverSideViewSuccess.setVisibility(View.GONE);
//            passengerSideViewSuccess.setVisibility(View.GONE);
//            roofViewSuccess.setVisibility(View.GONE);

            // Count
            frontViewCount = (CardView) findViewById(R.id.frontViewCountIc);
            backViewCount = (CardView) findViewById(R.id.backViewCountIc);
            driverSideViewCount = (CardView) findViewById(R.id.driverSideViewCountIc);
            passengerSideViewCount = (CardView) findViewById(R.id.passengerSideViewCountIc);
            roofViewCount = (CardView) findViewById(R.id.roofViewCountIc);


            // frontViewCount.setVisibility(View.GONE);
            //    backViewCount.setVisibility(View.GONE);
            //   driverSideViewCount.setVisibility(View.GONE);
            //   passengerSideViewCount.setVisibility(View.GONE);
            //  roofViewCount.setVisibility(View.GONE);


            frontViewtxt = (TextView) findViewById(R.id.txtfrontView);
            backViewtxt = (TextView) findViewById(R.id.txtbackView);
            driverSideViewtxt = (TextView) findViewById(R.id.txtdriverSideView);
            passengerSideViewtxt = (TextView) findViewById(R.id.txtpassengerSideView);
            roofViewtxt = (TextView) findViewById(R.id.txtroofView);

            if (Objects.equals(carviewcircle1, "1") && (!(String.valueOf(MainActivity.frontViewcount)).equals("0"))) {
                frontViewSuccess.setVisibility(View.VISIBLE);
                frontViewCount.setVisibility(View.VISIBLE);
                frontViewtxt.setText(String.valueOf(MainActivity.frontViewcount));
                // frontViewCount.setVisibility(View.VISIBLE);
            } else {
                frontViewSuccess.setVisibility(View.GONE);
                frontViewCount.setVisibility(View.GONE);
            }
            if (Objects.equals(carviewcircle4, "1") && (!(String.valueOf(MainActivity.roofViewcount)).equals("0"))) {
                roofViewSuccess.setVisibility(View.VISIBLE);
                roofViewCount.setVisibility(View.VISIBLE);
                roofViewtxt.setText(String.valueOf(MainActivity.roofViewcount));
                // cirlcestate2 = R.drawable.carnoselected_2;
            } else {
                roofViewSuccess.setVisibility(View.GONE);
                roofViewCount.setVisibility(View.GONE);
            }
            if (Objects.equals(carviewcircle7, "1") && (!(String.valueOf(MainActivity.backViewcount)).equals("0"))) {
                backViewSuccess.setVisibility(View.VISIBLE);
                backViewCount.setVisibility(View.VISIBLE);
                backViewtxt.setText(String.valueOf(MainActivity.backViewcount));
                // cirlcestate3 = R.drawable.carnoselected_3;
            } else {
                backViewSuccess.setVisibility(View.GONE);
                backViewCount.setVisibility(View.GONE);
            }
            if (Objects.equals(carviewcircle8, "1") && (!(String.valueOf(MainActivity.passengerSideViewcount)).equals("0"))) {
                passengerSideViewSuccess.setVisibility(View.VISIBLE);
                passengerSideViewCount.setVisibility(View.VISIBLE);
                passengerSideViewtxt.setText(String.valueOf(MainActivity.passengerSideViewcount));
                //  cirlcestate4 = R.drawable.carnoselected_4;
            } else {
                passengerSideViewSuccess.setVisibility(View.GONE);
                passengerSideViewCount.setVisibility(View.GONE);
            }
            if (Objects.equals(carviewcircle11, "1") && (!(String.valueOf(MainActivity.driverSideViewcount)).equals("0"))) {
                driverSideViewSuccess.setVisibility(View.VISIBLE);
                driverSideViewCount.setVisibility(View.VISIBLE);
                driverSideViewtxt.setText(String.valueOf(MainActivity.driverSideViewcount));
                // cirlcestate5 = R.drawable.carnoselected_5;
            } else {
                driverSideViewSuccess.setVisibility(View.GONE);
                driverSideViewCount.setVisibility(View.GONE);
            }

            proceedid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkConnected()) {

                        mydb = new DatabaseHelper(CaptureDamagedParts.this);
                        int countcheck = mydb.getlocalimageattachment().getCount();
                        int countcheck1 = countcheck;
                        if (MainActivity.damagecountnew == 0) {
                            Toast.makeText(CaptureDamagedParts.this, getString(R.string.carproceed), Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            if (mydb.getlocalimageattachment().getCount() == MainActivity.damagecountnew) {
                                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                                if (incidenttypeval == "F82589E6-7344-47B2-A672-11013F538551") {
                                    startActivity(new Intent(CaptureDamagedParts.this, AccidentDescription.class));
                                    finish();
                                } else {
                                    Intent car = new Intent(CaptureDamagedParts.this, ThirdParty.class);
                                    startActivity(car);
                                    finish();
                                }
                            } else {
                                Intent car = new Intent(CaptureDamagedParts.this, ProgressDialogUpload.class);
                                startActivity(car);
                            }
                        }
                    } else {
                        startActivity(new Intent(CaptureDamagedParts.this, AccidentDescription.class));
                        finish();
                    }

                }
            });

            frontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (!permissionManager.hasPermissions(permissions)) {
                            permissionManager.requestPermissions(permissions);
                            return;
                        }
                        carviewcircle1 = "1";
                        carviewselectionid = "1";

                        SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                        //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putInt("selectcarid", 1);
                        editor.putString("selectcartitle", "Front bumper");
                        editor.putString("selectcardescription", "I’ll need to check the front bumper");
                        editor.apply();

                        SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = loadmodelPref.edit();
                        editor1.putString(MainActivity.ModelID, "2");
                        editor1.apply();

                        SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                        modeldoceditor.putString(CarView.doctypeid, "924409f8-45ee-4190-b528-45c423ab9b33");
                        modeldoceditor.apply();

                        SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                        SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                        thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                        thirdpartyoceditor.apply();
                        Intent intent = new Intent(CaptureDamagedParts.this, CameraDamage.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!permissionManager.hasPermissions(permissions)) {
                        permissionManager.requestPermissions(permissions);
                        return;
                    }
                    carviewcircle7 = "1";
                    carviewselectionid = "7";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 7);
                    editor.putString("selectcartitle", "Rear Bumper");
                    editor.putString("selectcardescription", "I’ll need to check the Rear Bumper");
                    editor.apply();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.apply();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "fb386688-c943-4217-8144-90512d5e0529");
                    modeldoceditor.apply();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.apply();
                    Intent intent = new Intent(CaptureDamagedParts.this, CameraDamage.class);
                    startActivity(intent);
                    finish();

                }
            });

            passengerSideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!permissionManager.hasPermissions(permissions)) {
                        permissionManager.requestPermissions(permissions);
                        return;
                    }
                    carviewcircle8 = "1";
                    carviewselectionid = "8";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 8);
                    editor.putString("selectcartitle", "Left front door");
                    editor.putString("selectcardescription", "I’ll need to check the Left front handle/door");
                    editor.apply();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.apply();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "f960785b-2f51-45cf-863b-e8206e395417");
                    modeldoceditor.apply();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.apply();
                    Intent intent = new Intent(CaptureDamagedParts.this, CameraDamage.class);
                    startActivity(intent);
                    finish();
                }
            });

            roofView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!permissionManager.hasPermissions(permissions)) {
                        permissionManager.requestPermissions(permissions);
                        return;
                    }
                    carviewcircle4 = "1";
                    carviewselectionid = "4";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 4);
                    editor.putString("selectcartitle", "Roof top");
                    editor.putString("selectcardescription", "I’ll need to check the Roof top");
                    editor.apply();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.apply();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "a6d93aa2-fed8-43f6-af59-128211a31722");
                    modeldoceditor.apply();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.apply();
                    Intent intent = new Intent(CaptureDamagedParts.this, CameraDamage.class);
                    startActivity(intent);
                    finish();
                }
            });

            driverSideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!permissionManager.hasPermissions(permissions)) {
                        permissionManager.requestPermissions(permissions);
                        return;
                    }
                    carviewcircle11 = "1";
                    carviewselectionid = "11";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 11);
                    editor.putString("selectcartitle", "Right Front door");
                    editor.putString("selectcardescription", "I’ll need to check the Right Front handle/door");
                    editor.apply();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.apply();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "a2927342-d89d-4cf0-80c0-43c7dc7a87b6");
                    modeldoceditor.apply();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.apply();
                    Intent intent = new Intent(CaptureDamagedParts.this, CameraDamage.class);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //  MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MainActivity.passengerSideViewcount = 0;
        MainActivity.backViewcount = 0;
        MainActivity.frontViewcount = 0;
        MainActivity.driverSideViewcount = 0;
        MainActivity.roofViewcount = 0;
        mydb = new DatabaseHelper(this);
        mydb.deletealllocalimage();
        mydb.close();
        finish();
    }
}