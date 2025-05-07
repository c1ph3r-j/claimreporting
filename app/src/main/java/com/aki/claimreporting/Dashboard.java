package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimLocation.progressdialog;
import static com.aki.claimreporting.MainActivity.checkGPSStatus;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.ScanCertificate.qrcodedone;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity {
    public static DatabaseHelper mydb;
    final String[] array = {"Certificate Expiry", "Driving Licence Expiry"};
    CardView reportClaimBtn, verifyCertificateBtn, otherServicesBtn, reportClaimOnWhatsapp;
    BottomNavigationView bottomNavigationView;
    CarouselView carouselView;
    String reqidval;
    int icarviewchange, icarviewchangenew;
    JSONArray dashboardNotificationList;
    int[] notificationimages = {R.drawable.dashboardicon1, R.drawable.dashboardicon2};
    public static String sToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            preventSizeChange(this, getSupportActionBar());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");
            // startActivity(new Intent(Dashboard.this, CaptureDamagedParts.class));
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        preventSizeChange(this, getSupportActionBar());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        preventSizeChange(this, getSupportActionBar());
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
        alert.setMessage("Do you want to exit?");
        alert.setTitle("Alert");
        alert.setCancelable(false);
        alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.setPositiveButton("Yes", (dialogInterface, i) -> finishAffinity());
        alert.show();
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            //TODO New Dashboard part.
            reportClaimBtn = findViewById(R.id.reportClaimBtn);
            reportClaimOnWhatsapp = findViewById(R.id.reportClaimOnWhatsapp);
            verifyCertificateBtn = findViewById(R.id.verifyCertificateBtn);
            otherServicesBtn = findViewById(R.id.otherServicesBtn);

            reportClaimOnWhatsapp.setOnClickListener(onClickReportClaim -> {
                try {
                    String phoneNumberWithCountryCode = "+254769782488";
                    String message = "Hi";

                    startActivity(
                            new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(
                                            String.format("https://api.whatsapp.com/send?phone=%s&text=%s", phoneNumberWithCountryCode, message)
                                    )
                            )
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            reportClaimBtn.setOnClickListener(onClickReportClaim -> {
                try {
                    startActivity(new Intent(Dashboard.this, ClaimType.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            verifyCertificateBtn.setOnClickListener(onClickVerifyCertificate -> {
                try {
                    startActivity(new Intent(Dashboard.this, CertificateVerification.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            otherServicesBtn.setOnClickListener(onClickOtherServices -> {
                try {
                    startActivity(new Intent(Dashboard.this, OtherServices.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });


            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            // Access the menu
            Menu menu = bottomNavigationView.getMenu();
            if(isUserSignedUp()){
                menu.add(Menu.NONE, R.id.profileView, Menu.NONE, "Account")
                        .setIcon(R.drawable.user_ic);
            }else{
                menu.removeItem(R.id.profileView);
            }


            icarviewchange = 0;
            icarviewchangenew = 0;

//            try {
//                bottomNavigationView.setBackground(null);
//                bottomNavigationView.getMenu().getItem(2).setEnabled(false);
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }


            try {
                bottomNavigationView.setOnItemSelectedListener(selectedItem -> {
                    try {
                        int itemId = selectedItem.getItemId();
                        qrcodedone = 0;
                        System.out.println(itemId + " \n\n\n\n\n");
                        if (itemId == R.id.homeView) {
                            System.out.println("HOME PRESSED");
                            // TODO Home Button Action.
//                            Intent redirect = new Intent(Dashboard.this, CertificateActivation.class);
//                            redirect.putExtra("intentFrom", "10");
//                            startActivity(redirect);
                        } else if (itemId == R.id.addVehicleView) {
                            selectedItem.setChecked(true);
                            Intent redirect1 = new Intent(Dashboard.this, VehicleOwnership.class);
                            startActivity(redirect1);
                        } else if (itemId == R.id.myClaimsView) {
                            selectedItem.setChecked(true);
                            startActivity(new Intent(Dashboard.this, MyClaims.class));
                        } else if (itemId == R.id.profileView) {
                            selectedItem.setChecked(true);
                            Intent redirect3 = new Intent(Dashboard.this, Profile.class);
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

            try {
                ViewListener viewListener = position -> {
                    View customView = getLayoutInflater().inflate(R.layout.custom_carousel, null);
                    TextView car_header = customView.findViewById(R.id.headertxt);
                    TextView car_title = customView.findViewById(R.id.tittletxt);
                    LinearLayout layout_for_notification = customView.findViewById(R.id.layoutForNotification);
                    ImageView car_image = customView.findViewById(R.id.imgviewcert);
                    String headerText = "Learn More About Bima Yangu";
                    car_header.setText(headerText);
                    car_image.setImageDrawable(AppCompatResources.getDrawable(Dashboard.this, R.drawable.learn_ic));
                    String titleText = "Click Here to Watch Videos";
                    car_title.setText(titleText);
                    try {
                        layout_for_notification.setOnClickListener(onClickNotificationLayout ->
                                startActivity(new Intent(Dashboard.this, HelpVideos.class)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    return customView;
                };
                carouselView = findViewById(R.id.carouselView);
                carouselView.setViewListener(viewListener);
                carouselView.setPageCount(1);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                getVehicleData();
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

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void getVehicleData() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    try {
                        mydb = new DatabaseHelper(Dashboard.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor firebaseUserId = mydb.getTokendetails();
                            if (firebaseUserId.getCount() >= 1) {
                                while (firebaseUserId.moveToNext()) {
                                    MainActivity.stokenval = firebaseUserId.getString(1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(MainActivity.postURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        try {
                            runOnUiThread(() -> progressdialog = ProgressDialog.show(Dashboard.this, "Loading", "Fetching the information. Please wait...", true));
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        staticResponse = client.newCall(request).execute();
                        if (staticResponse.code() == 401) {
                            try {
                                runOnUiThread(() -> {
                                    unauthorize(Dashboard.this);
                                    progressdialog.dismiss();
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        } else if (staticResponse.code() == 200) {
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {

                                try {
                                    mydb = new DatabaseHelper(Dashboard.this);
                                    Cursor curseAttachToken = mydb.getvehicledetails();
                                    if (curseAttachToken.getCount() > 0) {
                                        mydb.deletevehicledata();
                                        mydb.deletedriverdetails();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

                                JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");
                                for (int i = 0; i < vehicleResponseList.length(); i++) {
                                    JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                    if (vehicleObj.getString("isSubmitted").equals("true") || vehicleObj.getString("isSubmitted").equals("True")) {
                                        try {
                                            boolean isInsertedVehicle = mydb.insertvehicledetails(vehicleObj.getString("registrationNo"), vehicleObj.getString("certificateNo"), vehicleObj.getString("insurerName"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("id"), vehicleObj.getString("coverageType"), vehicleObj.getString("vINNumber"), vehicleObj.getString("vehicleRefID"), vehicleObj.getString("certificateType"), vehicleObj.getString("policyEndDate"), vehicleObj.getString("insuredName"), vehicleObj.getString("insuredPhoneNo"), vehicleObj.getString("insuredMailId"), vehicleObj.getString("policyNo"), vehicleObj.getString("insuredPIN"));
                                            if (isInsertedVehicle) {
                                                Log.i(null, "Insertion Done");
                                            } else {
                                                Log.i(null, "Not Insertion Done");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        if (!(vehicleObj.getString("drivers").equals("null") || vehicleObj.getString("drivers").equals(""))) {
                                            try {
                                                JSONArray driverList = vehicleObj.getJSONArray("drivers");
                                                for (int j = 0; j < driverList.length(); j++) {
                                                    try {
                                                        JSONObject driverObj = driverList.getJSONObject(j);
                                                        boolean isInsertedDriver = mydb.insertdriverinfo(driverObj.getString("driverUserId"), driverObj.getString("driverName"), driverObj.getString("driverDLCountry"), driverObj.getString("driverDLNum"), driverObj.getString("driverDLValidFrom"), driverObj.getString("driverDLValidTill"), driverObj.getString("mobileNo"), vehicleObj.getString("id"), String.valueOf(driverObj.getBoolean("isSelfDriver")), driverObj.getString("cRAID"));
                                                        if (isInsertedDriver) {
                                                            Log.i(null, "Insertion Done");
                                                        } else {
                                                            Log.i(null, "Not Insertion Done");
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                        mCrashlytics.recordException(e);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        }
                                    }
                                }
                                runOnUiThread(() -> {
                                    try {
                                        getSmsPhoneNumber();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });

                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                try {
                                    runOnUiThread(() -> {
                                        unauthorize(Dashboard.this);
                                        progressdialog.dismiss();
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            } else {
                                try {
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            progressdialog.dismiss();
                                            AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                            alert.show();
                                        } catch (JSONException e) {
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
                                    runOnUiThread(() -> progressdialog.dismiss());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        }

                    } catch (final IOException e) {
                        runOnUiThread(() -> progressdialog.dismiss());
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    } catch (JSONException ex) {
                        try {
                            runOnUiThread(() -> progressdialog.dismiss());
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                    }
                });
                thread.start();
            }
        } catch (Exception ex) {
            try {
                runOnUiThread(() -> progressdialog.dismiss());
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Dashboard.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(Dashboard.this);
            mydb.deletetokendata();
            mydb.deleteclaimstep2data();
            mydb.deleteregstep();
            mydb.deletethirdlocalimage();
            mydb.deletethirdpartydetails();
            mydb.deleteuserdata();
            mydb.deletetermsdata();
            mydb.deletetermsconditionsdata();
            mydb.deletedriverdetails();
            mydb.deletevehicledata();
            Intent login = new Intent(Dashboard.this, Login.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    public void getSmsPhoneNumber() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    try {
                        mydb = new DatabaseHelper(Dashboard.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor firebaseUserId = mydb.getTokendetails();
                            if (firebaseUserId.getCount() >= 1) {
                                while (firebaseUserId.moveToNext()) {
                                    MainActivity.stokenval = firebaseUserId.getString(1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    String vehicleURL = getString(R.string.uaturl) + "/app/MasterData/GetAllPhoneNoSMS";
                    OkHttpClient vehicle = new OkHttpClient();
                    JsonObject vehicleJson = new JsonObject();
                    String vehicleString = vehicleJson.toString();
                    RequestBody vehicleBody = RequestBody.create(JSON, vehicleString);
                    Request vehicleRequest = new Request.Builder()
                            .url(vehicleURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(vehicleBody)
                            .build();
                    Response vehicleResponse;
                    try {

                        vehicleResponse = vehicle.newCall(vehicleRequest).execute();
                        int statusCode = vehicleResponse.code();
                        if (statusCode == 401) {
                            try {
                                runOnUiThread(() -> {
                                    unauthorize(Dashboard.this);
                                    progressdialog.dismiss();
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        } else {
                            assert vehicleResponse.body() != null;
                            String vehicleResult = vehicleResponse.body().string();
                            final JSONObject staticJsonObj = new JSONObject(vehicleResult);
                            if (staticJsonObj.getInt("rcode") == 1) {
                                String phuNumVersionId = staticJsonObj.getJSONObject("rObj").getJSONObject("getAllPhoneNoSMS").getString("phoneNoVersion");
                                JSONArray sendSMSList = staticJsonObj.getJSONObject("rObj").getJSONObject("getAllPhoneNoSMS").getJSONArray("phoneNos");
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < sendSMSList.length(); i++) {

                                    try {
                                        String val = sendSMSList.getString(i);
                                        String encryptVal = AESCrypt.decrypt(val);
                                        if (sendSMSList.length() - 1 == i) {
                                            sb.append(encryptVal);
                                        } else {
                                            sb.append(encryptVal).append(";");
                                        }

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                    }
                                }
                                String commaseparatedlist = sb.toString();
                                String commasepencrypt = AESCrypt.encrypt(commaseparatedlist);

                                try {
                                    mydb = new DatabaseHelper(Dashboard.this);
                                    if (mydb.getsendsmsdetails().getCount() != 0) {
                                        mydb.deletesendsmsdata();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                boolean Isinserted = mydb.insertsendsms(phuNumVersionId, commasepencrypt);
                                if (Isinserted) {
                                    Log.i(null, "Insertion Done");
                                } else {
                                    Log.i(null, "Not Insertion Done");
                                }
                                runOnUiThread(() -> {
                                    try {
                                        getDashboardOption();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                try {
                                    runOnUiThread(() -> {
                                        unauthorize(Dashboard.this);
                                        progressdialog.dismiss();
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            } else {
                                try {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        }

                    } catch (Exception ex) {
                        runOnUiThread(() -> progressdialog.dismiss());
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                    }

                });

                thread.start();
            }

        } catch (Exception ex) {
            runOnUiThread(() -> progressdialog.dismiss());
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }

    }

    @SuppressLint("InflateParams")
    public void getDashboardOption() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    try {
                        mydb = new DatabaseHelper(Dashboard.this);
                        if (mydb.getfirebaseTokendetails().getCount() != 0) {
                            Cursor firebaseUserId = mydb.getTokendetails();
                            if (firebaseUserId.getCount() >= 1) {
                                while (firebaseUserId.moveToNext()) {
                                    MainActivity.stokenval = firebaseUserId.getString(1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Dashboard/GetDashboard";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(MainActivity.postURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        staticResponse = client.newCall(request).execute();
                        assert staticResponse.body() != null;
                        String staticRes = staticResponse.body().string();
                        Log.i(null, staticRes);
                        final JSONObject staticJsonObj = new JSONObject(staticRes);
                        try {
                            reqidval = staticJsonObj.getString("reqID");
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                        }
                        if (staticJsonObj.getInt("rcode") == 1) {
                            JSONArray dashboardResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("dashBoardData");
                            runOnUiThread(() -> {
                                try {
                                    dashboardNotificationList = new JSONArray();
                                    for (int i = 0; i < dashboardResponseList.length(); i++) {
                                        JSONObject obj = dashboardResponseList.getJSONObject(i);
                                        int responseNew = obj.getInt("typeID");
                                        if ((responseNew == 3 || responseNew == 5)) {
                                            dashboardNotificationList.put(obj);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                if (dashboardNotificationList.length() != 0) {
                                    ViewListener viewListener = position -> {
                                        View customView = getLayoutInflater().inflate(R.layout.custom_carousel, null);
                                        //set view attributes here
                                        TextView car_header = customView.findViewById(R.id.headertxt);
                                        TextView car_title = customView.findViewById(R.id.tittletxt);
//                                        LinearLayout layout_for_notification = customView.findViewById(R.id.layoutForNotification);
                                        ImageView car_image = customView.findViewById(R.id.imgviewcert);

                                        new Thread(() -> {
                                            try {
                                                JSONObject obj = dashboardNotificationList.getJSONObject(position);
                                                int responseNew = obj.getInt("typeID");
                                                if (responseNew == 3) {
                                                    car_header.setText(array[1]);
                                                    String message = "Hi "
                                                            + obj.getString("param1")
                                                            + ", Your Driving Licence Expiring On "
                                                            + (obj.getString("param6")).replace("DL Expires on ", "") + "\n"
                                                            + ((obj.getString("param4")).replace("-", "")).replace(" Only", "");

                                                    car_title.setText(message);

                                                } else if (responseNew == 5) {
                                                    car_header.setText(getString(R.string.claim_initiated));
                                                    String message =
                                                            "Incident Id  -  " + obj.getString("param1") + "\n"
                                                                    + "Vehicle No  -  " + (obj.getString("param3") + "\n"
                                                                    + "Initiated on -  " + (obj.getString("param6")).replace("Claim Initiated on ", ""));

                                                    car_title.setText(message);
                                                    car_image.setImageDrawable(AppCompatResources.getDrawable(Dashboard.this, notificationimages[0]));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        }).start();

                                        return customView;
                                    };

                                    carouselView.setViewListener(viewListener);
                                    carouselView.setPageCount((dashboardNotificationList.length() == 0) ? 1 : dashboardNotificationList.length());
                                }
                            });
                            try {
                                String from = getIntent().getStringExtra("from");
                                if (from != null && !from.isEmpty()) {
                                    if (from.equals("certificate_verification")) {
                                        startActivity(new Intent(this, CertificateVerification.class));
                                    } else if (from.equals("report_claim")) {
                                        startActivity(new Intent(this, ClaimType.class));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            runOnUiThread(() -> {
                                try {
                                    progressdialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            });
                        } else {
                            try {
                                runOnUiThread(progressdialog::dismiss);
                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                JSONObject index = rmsg.getJSONObject(0);
                                runOnUiThread(() -> {
                                    String errorText;
                                    try {
                                        errorText = index.getString("errorText");
                                        AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
                                        alert.setCancelable(false);
                                        alert.setMessage(errorText);
                                        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                        alert.show();
                                    } catch (JSONException e) {
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
                    } catch (final IOException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        runOnUiThread(() -> progressdialog.dismiss());
                    } catch (JSONException e) {
                        runOnUiThread(() -> progressdialog.dismiss());
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                });
                thread.start();
            }

        } catch (Exception e) {
            runOnUiThread(() -> progressdialog.dismiss());
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        MenuItem settingsItem = menu.findItem(R.id.log_feature_icon);
        settingsItem.setIcon(isUserSignedUp() ? R.drawable.signingouticon : R.drawable.signingupicon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_one) {
            Intent otpIntent = new Intent(Dashboard.this, Notification.class);
            startActivity(otpIntent);
            return true;
        } else if (id == R.id.action_item_two) {
            Intent otpIntent = new Intent(Dashboard.this, HelpVideos.class);
            startActivity(otpIntent);
            return true;
        } else if (id == R.id.log_feature_icon) {
            if (!isUserSignedUp()) {
                Intent otpIntent = new Intent(Dashboard.this, Registration.class);
                startActivity(otpIntent);

            } else {
                MainActivity.alertWarning("Alert", "Are you sure you want to sign out?", this)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            mydb = new DatabaseHelper(Dashboard.this);
                            mydb.deletetokendata();
                            mydb.deleteclaimstep2data();
                            mydb.deleteregstep();
                            mydb.deletethirdlocalimage();
                            mydb.deletethirdpartydetails();
                            mydb.deleteuserdata();
                            mydb.deletetermsdata();
                            mydb.deletetermsconditionsdata();
                            mydb.deletedriverdetails();
                            mydb.deletevehicledata();
                            mydb.deleteneardeardata();
                            mydb.deleteclaimofflineiddata();
                            loginUser();

                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    //mobile login API
    public void loginUser() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (MainActivity.isNetworkConnected(this)) {
                if (checkGPSStatus(this)) {
                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Account/GetToken";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        String imeiInput = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                        JsonObject detailsInput = new JsonObject();
                        try {
                            detailsInput.addProperty("uniqueID", imeiInput);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String insertString = detailsInput.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() -> progressdialog = ProgressDialog.show(this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    try {
                                        sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                        mydb = new DatabaseHelper(Dashboard.this);
                                        if (mydb.getTokendetails().getCount() != 0) {
                                            mydb.deletetokendata();
                                        }
                                        if (mydb.getUserdetails().getCount() != 0) {
                                            mydb.deleteuserdata();
                                        }
                                        mydb.inserttoken(sToken);
                                        boolean IsProfileinserted = mydb.insertuserdetails("", staticJsonObj.getJSONObject("rObj").optString("cRAID", ""), "", "");
                                        if (IsProfileinserted) {
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        Intent i = getIntent();
                                        finish();
                                        startActivity(i);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(() -> progressdialog.dismiss());
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                try {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            } else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> {
                                                dialog.dismiss();
                                                progressdialog.dismiss();
                                            });
                                            alert.show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            runOnUiThread(progressdialog::dismiss);
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    runOnUiThread(progressdialog::dismiss);
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        } catch (final IOException ex) {
                            runOnUiThread(() -> {
                                progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                Toast.makeText(Dashboard.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(Dashboard.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> progressdialog.dismiss());
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();
                } else {
                    try {
                        runOnUiThread(() -> progressdialog.dismiss());
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Dashboard.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    } catch (Exception e) {
                        runOnUiThread(() -> progressdialog.dismiss());
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            } else {
                try {
                    progressdialog.dismiss();
                    Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> progressdialog.dismiss());
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            runOnUiThread(() -> progressdialog.dismiss());
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(Dashboard.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        } finally {

        }

    }

}
