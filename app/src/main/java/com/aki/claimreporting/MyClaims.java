package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimType.CraIdval;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyClaims extends AppCompatActivity {

    public static int valincr;
    public static int val;
    public static int caridvalvalid;
    public static ProgressDialog progressdialog;
    public static DatabaseHelper mydb;
    public static String reqidval;
    private static FirebaseCrashlytics mCrashlytics;
    public View rootview;
    public ListView list;
    public LinearLayout claimprese;
    public LinearLayout uatenvirlinear;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    String[] permissions = new String[]{PERMISSION_CAMERA, PERMISSION_LOCATION, PERMISSION_STORAGE, PERMISSION_READ_STORAGE};
    PermissionHandler permissionManager;
    CustomClaimHistoryList customClaimListView;
    SearchView searchView;
    MyClaims activity;
    ArrayList<ClaimhistoryResponse> claimhistorylist = new ArrayList<>();
    Context context;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void GetStolenFlow(Activity activity) {
        /*Intent car = new Intent(activity, PolicyInformation.class);
        activity.startActivity(car);*/
    }

    public static void GetClaimAttachment(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mydb = new DatabaseHelper(activity);
            if (mydb.getTokendetails().getCount() != 0) {
                Cursor curseattachtoken = mydb.getTokendetails();
                int counttoken = curseattachtoken.getCount();
                if (counttoken >= 1) {
                    while (curseattachtoken.moveToNext()) {
                        MainActivity.stokenval = curseattachtoken.getString(1);
                    }
                }
            }

            // String newphnval =  phonenonew.getText().toString();
            MainActivity.encryptedSHA = "";
            String sourceStr = MainActivity.InsertMobileParameters();
            try {
                MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            Thread thread = new Thread(() -> {
                SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                String incident_id = incidePref.getString(CraIdval, "");
                // MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriver";
                MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Claim/GetAllAttachment";
                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                SharedPreferences modelPref = activity.getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                final String certid = modelPref.getString(MyVehicles.ViewCertificateno, null);
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();
                JsonObject Details = new JsonObject();
                try {
                    Details.addProperty("incidentUniqueCode", incident_id);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
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
                    activity.runOnUiThread(() -> {
                        progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                    });
                    staticResponse = client.newCall(request).execute();
                    String staticRes = staticResponse.body().string();
                    Log.i(null, staticRes);
                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                    try {
                        reqidval = staticJsonObj.getString("reqID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    if (staticJsonObj.getInt("rcode") == 1) {
                        activity.runOnUiThread(() -> {
                            try {
                                val = 1;
                                valincr = 0;
                                JSONArray attachmentResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllAttachment");
                                int testcountval = attachmentResponseList.length();
                                int testcountva1l = testcountval;

                                if (attachmentResponseList.length() == 0) {
                                    progressdialog.dismiss();
                                    DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                                    int densityDpi = dm.densityDpi;
                                    if (densityDpi >= 320 && densityDpi <= 390) {
                                        Intent car = new Intent(activity, CarView.class);
                                        activity.startActivity(car);
                                        return;
                                    } else if (densityDpi >= 280 && densityDpi <= 310) {
                                        Intent car = new Intent(activity, CarView280.class);
                                        activity.startActivity(car);
                                        return;
                                    } else if (densityDpi >= 400 && densityDpi <= 520) {
                                        Intent car = new Intent(activity, CarView400.class);
                                        activity.startActivity(car);
                                        return;
                                    }
                                } else {
                                    for (int i = 0; i < attachmentResponseList.length(); i++) {
                                        JSONObject claimattachmentObj = attachmentResponseList.getJSONObject(i);
                                        valincr = val;
                                        String carnamevalvalid = claimattachmentObj.getString("attachmentType");
                                        if (carnamevalvalid.equals("Front bumper")) {
                                            caridvalvalid = 1;
                                        } else if (carnamevalvalid.equals("Hood / Bonnet")) {
                                            caridvalvalid = 2;
                                        } else if (carnamevalvalid.equals("Windscreen / Windshield")) {
                                            caridvalvalid = 3;
                                        } else if (carnamevalvalid.equals("Roof top")) {
                                            caridvalvalid = 4;
                                        } else if (carnamevalvalid.equals("Rear window")) {
                                            caridvalvalid = 5;
                                        } else if (carnamevalvalid.equals("Boot/Trunk")) {
                                            caridvalvalid = 6;

                                        } else if (carnamevalvalid.equals("Rear Bumper")) {
                                            caridvalvalid = 7;
                                        } else if (carnamevalvalid.equals("Left front door")) {
                                            caridvalvalid = 8;
                                        } else if (carnamevalvalid.equals("Left rear door")) {
                                            caridvalvalid = 9;
                                        } else if (carnamevalvalid.equals("Rear Left Fender")) {
                                            caridvalvalid = 10;
                                        } else if (carnamevalvalid.equals("Right Front door")) {
                                            caridvalvalid = 11;
                                        } else if (carnamevalvalid.equals("Right rear door")) {
                                            caridvalvalid = 12;
                                        } else if (carnamevalvalid.equals("Rear Right Fender")) {
                                            caridvalvalid = 13;
                                        } else if (carnamevalvalid.equals("Front Left Fender")) {
                                            caridvalvalid = 14;
                                        } else if (carnamevalvalid.equals("Front Right Fender")) {
                                            caridvalvalid = 15;
                                        }

                                        // String carnamevalvalid = claimattachmentObj.getString("attachmentType");
                                        String cardescription = claimattachmentObj.getString("attachmentType");
                                        String mergenamedescr = carnamevalvalid + " @ " + cardescription + " @ " + caridvalvalid;

                                        String newimgurl = claimattachmentObj.getString("azureURL");
                                        String imageurl = newimgurl;
                                        InputStream in = null;

                                        try {
                                            Log.i("URL", imageurl);
                                            URL url = new URL(imageurl);
                                            URLConnection urlConn = url.openConnection();
                                            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                                            httpConn.connect();

                                            in = httpConn.getInputStream();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        Bitmap bmpimg = BitmapFactory.decodeStream(in);
                                        String filename = UUID.randomUUID().toString() + ".JPEG";
                                        File sd = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                                        File destfile = new File(sd, filename);

                                        Bitmap bitmapfile = bmpimg;
                                        try {
                                            FileOutputStream out = new FileOutputStream(destfile);
                                            bitmapfile.compress(Bitmap.CompressFormat.PNG, 80, out);
                                            out.flush();
                                            out.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        int attachid = 1;
                                        MainActivity.damagecountnew = valincr;
                                        MainActivity.Global.img = bitmapfile;
                                        String imgData = destfile.toString();
                                        String imagename = UUID.randomUUID().toString() + ".JPEG";
                                        String imagenew = imgData;
                                        String regnum = MainActivity.regnogetclaim;
                                        int attachmentid = 100;
                                        String uniqueID = claimattachmentObj.getString("captureAttachmentID");
                                        mydb = new DatabaseHelper(activity);
                                        boolean Isinserted = mydb.insertlocalimageattachment(imagename, attachid, imgData, caridvalvalid, carnamevalvalid, cardescription, mergenamedescr, uniqueID, attachmentid, regnum, imagenew);
                                        if (Isinserted) {
                                            boolean test = Isinserted;
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            boolean test = Isinserted;
                                            //Toast.makeText(MyClaims.this,"DataNotInserted", Toast.LENGTH_SHORT).show();
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        val = valincr + 1;
                                    }
                                    activity.runOnUiThread(progressdialog::dismiss);
                                    Intent car = new Intent(activity, ReviewOwnImages.class);
                                    activity.startActivity(car);

                                }


                            } catch (JSONException e) {
                                activity.runOnUiThread(() -> progressdialog.dismiss());
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        });
                    } else {
                        try {
                            activity.runOnUiThread(progressdialog::dismiss);
                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                            JSONObject index = rmsg.getJSONObject(0);
                            activity.runOnUiThread(() -> {
                                String errorText;
                                try {
                                    errorText = index.getString("errorText");
                                    androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(activity);
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
                } catch (final IOException | JSONException e) {
                    activity.runOnUiThread(() -> progressdialog.dismiss());
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });
            thread.start();


        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_history);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        context = this;
        activity = this;
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
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Claims");
            preventSizeChange(this, getSupportActionBar());
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            MyClaims.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            LinearLayout headingForClaimDocView = findViewById(R.id.claimDocInfoView);
            headingForClaimDocView.setVisibility(View.GONE);
            Intent intentFrom = getIntent();
            boolean isFromOtherServices = intentFrom.getBooleanExtra("isFromOtherServices", false);
            if (isFromOtherServices) {
                headingForClaimDocView.setVisibility(View.VISIBLE);
            } else {
                headingForClaimDocView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (TextUtils.isEmpty(newText)) {
                        try {
                            customClaimListView.filter("");
                            list.clearTextFilter();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    } else {
                        try {
                            customClaimListView.filter(newText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }
                    return true;

                }
            });
            claimprese = (LinearLayout) findViewById(R.id.claimpresent);
            claimprese.setVisibility(View.GONE);
            uatenvirlinear = (LinearLayout) findViewById(R.id.lineuatenv);
            uatenvirlinear.setVisibility(View.GONE);
            getClaimhistoryinfo();

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyClaims.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) MyClaims.this.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled;
    }

    public void getClaimhistoryinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(MyClaims.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    progressdialog = new ProgressDialog(MyClaims.this);
                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    try {
                        Thread thread = new Thread(() -> {
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/GetAllClaim";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            //Device unique code
                            String imeiInput = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            Details.addProperty("uniqueID", imeiInput);
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
                                MyClaims.this.runOnUiThread(() -> {
                                    progressdialog = ProgressDialog.show(MyClaims.this, "Loading", "Please wait...", true);
                                });
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    MyClaims.this.runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(MyClaims.this);
                                    });
                                } else {
                                    String staticRes = staticResponse.body().string();
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    try {
                                        reqidval = staticJsonObj.getString("reqID");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog("MyClaims-getClaimHistory", e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        JSONArray claimResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllClaim");
                                        if (claimResponseList.length() == 0) {
                                            runOnUiThread(() -> {
                                                claimprese.setVisibility(View.GONE);
                                                uatenvirlinear.setVisibility(View.VISIBLE);
                                            });

                                        } else {
                                            runOnUiThread(() -> {
                                                claimprese.setVisibility(View.VISIBLE);
                                                uatenvirlinear.setVisibility(View.GONE);
                                            });
                                            for (int i = 0; i < claimResponseList.length(); i++) {
                                                JSONObject claimObj = claimResponseList.getJSONObject(i);

                                                if (claimObj.getString("isSubmitted") == "true") {
                                                    if (AESCrypt.decrypt(claimObj.getString("certificateNo")) == "null" || AESCrypt.decrypt(claimObj.getString("certificateNo")) == null) {
                                                        String test = "testval";
                                                        String test1 = test;
                                                    } else {
                                                        if (claimResponseList.getJSONObject(i).optJSONObject("vehicle") != null) {
                                                            ClaimhistoryResponse element = new ClaimhistoryResponse(AESCrypt.decrypt(claimObj.getString("incidentUniqueCode")), AESCrypt.decrypt(claimObj.getString("certificateNo")), AESCrypt.decrypt(claimObj.getString("registrationNo")), claimObj.getString("claimType"), claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("make", ""), claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("model", ""), claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("yearOfMfg", ""), claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("vINNumber", ""), claimResponseList.optJSONObject(i).optJSONObject("vehicle").optString("certificateType", ""), claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("coverageType", ""), claimObj.getString("createdOn"), claimObj.getString("isSubmitted"), claimObj.getString("vehicleId"), claimObj.getString("incLocation"), claimObj.getString("incidentDatetime"));
                                                            element.ClaimRefID = AESCrypt.decrypt(claimObj.getString("incidentUniqueCode"));
                                                            element.CertificateNo = AESCrypt.decrypt(claimObj.getString("certificateNo"));
                                                            element.RegistrationNo = AESCrypt.decrypt(claimObj.getString("registrationNo"));
                                                            element.ClaimType = claimObj.getString("claimType");
                                                            element.Make = claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("make", "");
                                                            element.Model = claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("model", "");
                                                            element.YearOfRegistration = claimResponseList.optJSONObject(i).optJSONObject("vehicle").optString("yearOfMfg", "");
                                                            element.ChassisNo = claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("vINNumber", "");
                                                            element.TypeCertificate = claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("certificateType", "");
                                                            element.Coverage = claimResponseList.getJSONObject(i).optJSONObject("vehicle").optString("coverageType", "");
                                                            element.Claimdate = claimObj.getString("createdOn");
//                            element.TypeOfVehicleID =  vehicleObj.getInt("TypeOfVehicleID");
//                            element.CoverTypeID =  vehicleObj.getInt("CoverTypeID");
//                            element.YearOfManufacture = vehicleObj.getString("YearOfManufacture");
//                            element.VehicleMake =  vehicleObj.getString("VehicleMake");
//                            element.VehicleModel = vehicleObj.getString("VehicleModel");
//                            element.PolicyStartDate = vehicleObj.getString("PolicyStartDate");

                                                            //regspinner.add(vehicleObj.getString("RegistrationNo"));
                                                            claimhistorylist.add(element);
                                                        }

                                                    }
                                                } else {

                                                }

                                            }

                                            MyClaims.this.runOnUiThread(() -> {
                                                //   claimprese.setVisibility(View.VISIBLE);
                                                //  uatenvirlinear.setVisibility(View.GONE);

                                                customClaimListView = new CustomClaimHistoryList(MyClaims.this, claimhistorylist, MyClaims.this, permissionManager);
                                                list = (ListView) findViewById(R.id.listclaimhistory);
                                                list.setAdapter(customClaimListView);
                                                // setListViewHeightBasedOnItems(list);
                                                list.setTextFilterEnabled(true);
                                                list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                                if (claimhistorylist.isEmpty()) {
                                                    claimprese.setVisibility(View.GONE);
                                                    uatenvirlinear.setVisibility(View.VISIBLE);
                                                } else {
                                                    claimprese.setVisibility(View.VISIBLE);
                                                    uatenvirlinear.setVisibility(View.GONE);
                                                }
                                                MyClaims.this.runOnUiThread(() -> progressdialog.dismiss());
                                            });
                                        }
                                    }else if(staticJsonObj.getInt("rcode") == 2){
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            claimprese.setVisibility(View.GONE);
                                            uatenvirlinear.setVisibility(View.VISIBLE);
                                        });
                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        MyClaims.this.runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            unauthorize(MyClaims.this);
                                        });
                                    } else {
                                        try {
                                            runOnUiThread(() -> {
                                                progressdialog.dismiss();
                                                uatenvirlinear.setVisibility(View.GONE);
                                            });
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(MyClaims.this);
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
                                }

                            } catch (final Exception e) {
                                runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                MyClaims.this.runOnUiThread(() -> Toast.makeText(MyClaims.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                            }
                        });
                        thread.start();
                    }catch (Exception e){
                        runOnUiThread(() -> {
                            if(progressdialog.isShowing()){
                                progressdialog.dismiss();
                            }
                        });
                        e.printStackTrace();
                    }



                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MyClaims.this);
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText(MyClaims.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(MyClaims.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MyClaims.this);
        dialog.setMessage(getString(R.string.session_expired));
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(MyClaims.this);
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
            Intent login = new Intent(MyClaims.this, Login.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_name) {
//            View rootView = MyClaims.this.getWindow().getDecorView().findViewById(android.R.id.content);
//            View screenView = rootView.getRootView();
//            screenView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//            screenView.setDrawingCacheEnabled(false);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            sharedpreferences = MyClaims.this.getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//            supporteditor.putString(MainActivity.ReferrenceURL, "Claim History");
//            supporteditor.apply();
//            sharedpreferencesimg = MyClaims.this.getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(MyClaims.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//        }
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}