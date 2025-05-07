package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimType.CraIdval;
import static com.aki.claimreporting.ClaimType.typeidincident;
import static com.aki.claimreporting.MainActivity.claiminmiddleflow;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClaimSuccess extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String reqidval;
    public static Handler handler;
    public static Runnable myRunnable;
    public static String loaderappear;
    LinearLayout bimaYanguDocBtn, companyClaimDocBtn;
    public static FirebaseCrashlytics mCrashlytics;
    public TextView craid, backhome;
    public Button viewpdf;
    public TextView txtphno, txtsuccess;
    public TextView videoclaimhis;
    public LinearLayout viewclaimloading;
    public ProgressDialog progressdialog;
    public android.app.AlertDialog.Builder dialog;
    public android.app.AlertDialog alert;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public String insurernumber;
    String tollfreecompany, tollfreenum;
    Activity activity;
    LinearLayout claimSuccessLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_success);
        activity = this;
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Incident Confirmation");
        mCrashlytics = FirebaseCrashlytics.getInstance();
        /*ThirdPartyCarView.carviewcircle1 = "0";
        ThirdPartyCarView.carviewcircle2 = "0";
        ThirdPartyCarView.carviewcircle3 = "0";
        ThirdPartyCarView.carviewcircle4 = "0";
        ThirdPartyCarView.carviewcircle5 = "0";
        ThirdPartyCarView.carviewcircle6 = "0";
        ThirdPartyCarView.carviewcircle7 = "0";
        ThirdPartyCarView.carviewcircle8 = "0";
        ThirdPartyCarView.carviewcircle9 = "0";
        ThirdPartyCarView.carviewcircle10= "0";
        ThirdPartyCarView.carviewcircle11= "0";
        ThirdPartyCarView.carviewcircle12= "0";
        ThirdPartyCarView.carviewcircle13= "0";
        ThirdPartyCarView.carviewcircle14= "0";
        ThirdPartyCarView.carviewcircle15 = "0";
        MainActivity.vehicleaccidentflow = "1";*/
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
             bimaYanguDocBtn = findViewById(R.id.bimaYanguDocBtn);
             companyClaimDocBtn = findViewById(R.id.companyClaimDocBtn);
             claimSuccessLayout = findViewById(R.id.claimSuccessLayout);
             claimSuccessLayout.setVisibility(View.GONE);
             bimaYanguDocBtn.setOnClickListener(onClickBimaYanguDoc -> {
                 try {
                     getclaimpdf(0);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             });

             companyClaimDocBtn.setOnClickListener(onClickCompanyClaimForm -> {
                 try {
                     getclaimpdf(1);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             });
             insertclaimfinal();
            // viewclaimloading = (LinearLayout) findViewById(R.id.pdfclaimloading);

            try {

                //   String jsonString = "[{\"EntityID\": \"11\",\"InsurerID\" : \"ed71b8e7-7df0-4b59-a9ee-e9de0686e84e\",\"PhoneNo\": \"+254 202 204 000\",\"EntityName\": \"Africa Merchant Assurance Company Ltd.\"},{\"EntityID\": \"12\", \"InsurerID\" : \"4b97447d-21d5-45af-9731-d57bc54485d2\",\"PhoneNo\": \"+254 723 600 400\",\"EntityName\": \"AIG Kenya Insurance Company Ltd.\"},{\"EntityID\": \"46\",\"InsurerID\" : \"344ab0c8-d46c-4d27-b2c1-5a596c14e5e5\",\"PhoneNo\": \"+254 700 111 999\",\"EntityName\": \"Xplico Insurance Company Ltd.\"}]";
                String jsonString = "[{\"EntityID\":\"11\",\"InsurerID\":\"ed71b8e7-7df0-4b59-a9ee-e9de0686e84e\",\"PhoneNo\":\"+254 202 204 000\",\"EntityName\":\"Africa Merchant Assurance Company Ltd.\"},{\"EntityID\":\"12\",\"InsurerID\":\"4b97447d-21d5-45af-9731-d57bc54485d2\",\"PhoneNo\":\"+254 723 600 400\",\"EntityName\":\"AIG Kenya Insurance Company Ltd.\"},{\"EntityID\":\"13\",\"InsurerID\":\"d7ba811d-82ba-4e53-8567-51e56a6580a2\",\"PhoneNo\":\"+254 709 566 000\",\"EntityName\":\"Allianz Insurance Company of Kenya\"},{\"EntityID\":\"14\",\"InsurerID\":\"eb499de5-b95c-4981-b39c-3213213cc8c2\",\"PhoneNo\":\"020 286 200\",\"EntityName\":\"APA Insurance Company Ltd.\"},{\"EntityID\":\"15\",\"InsurerID\":\"00d3ce4a-9540-4986-bf9f-a7b658b4a807\",\"PhoneNo\":\"+254 705 100 100\",\"EntityName\":\"Britam General Insurance Company\"},{\"EntityID\":\"16\",\"InsurerID\":\"0d4c83ff-5ef3-4847-8077-8fbeea25a872\",\"PhoneNo\":\"+254 722 204 572\",\"EntityName\":\"CIC General Insurance Company\"},{\"EntityID\":\"17\",\"InsurerID\":\"f7882e8a-eddb-4614-991b-9de9b1640fc8\",\"PhoneNo\":\"+254 7287 000 93\",\"EntityName\":\"Corporate Insurance Company Ltd.\"},{\"EntityID\":\"18\",\"InsurerID\":\"0fd0f1a2-b75e-4754-b31c-8e861aeac451\",\"PhoneNo\":\"+254 711 030 000\",\"EntityName\":\"Directline Assurance Company Ltd.\"},{\"EntityID\":\"19\",\"InsurerID\":\"3d0f6afc-ccf3-41f8-b5cb-fac48b8d2e20\",\"PhoneNo\":\"+254 709 988 000\",\"EntityName\":\"Fidelity Shield Insurance Company Ltd.\"},{\"EntityID\":\"20\",\"InsurerID\":\"1b18639e-d317-4061-9424-70242b5c7564\",\"PhoneNo\":\"020 290 000\",\"EntityName\":\"First Assurance Company Ltd.\"},{\"EntityID\":\"21\",\"InsurerID\":\"5a7fa593-38bc-4a4e-9e35-ca99303803f5\",\"PhoneNo\":\"+254 709 626 000\",\"EntityName\":\"GA Kenya Insurance Company Ltd.\"},{\"EntityID\":\"22\",\"InsurerID\":\"2b4c80aa-aaeb-46c0-94c5-6435b7bda0f2\",\"PhoneNo\":\"+254 709 551 000\",\"EntityName\":\"Geminia Insurance Company Ltd.\"},{\"EntityID\":\"23\",\"InsurerID\":\"b975918e-865e-45d5-bcf7-2609987ea380\",\"PhoneNo\":\"+254 719 071 000\",\"EntityName\":\"ICEA LION General Insurance Company\"},{\"EntityID\":\"24\",\"InsurerID\":\"2dfdb3e7-e76a-4078-8d83-fc1916e59d55\",\"PhoneNo\":\"+254 722 205 050\",\"EntityName\":\"Intra Africa Assurance Company Ltd.\"},{\"EntityID\":\"25\",\"InsurerID\":\"0ba1650f-a63b-4902-8f1f-dcd79a0c8076\",\"PhoneNo\":\"+254 730 180 000\",\"EntityName\":\"Invesco Assurance Company Ltd.\"},{\"EntityID\":\"26\",\"InsurerID\":\"160c76ea-4e49-42ca-8a5a-f2420803bb99\",\"PhoneNo\":\"+254 709 949 000\",\"EntityName\":\"Jubilee Insurance Company Ltd.\"},{\"EntityID\":\"27\",\"InsurerID\":\"05f5b786-46d7-47ff-84ed-b721d505bcf3\",\"PhoneNo\":\"+254 722 205 923\",\"EntityName\":\"Kenindia Assurance Company Ltd.\"},{\"EntityID\":\"28\",\"InsurerID\":\"644c5afd-4319-4693-94ef-9ca1a6f5f482\",\"PhoneNo\":\"+254 719 042 000\",\"EntityName\":\"Kenya Orient Insurance Company Ltd.\"},{\"EntityID\":\"29\",\"InsurerID\":\"f64d317f-2911-4fe7-a32c-3504ed76740d\",\"PhoneNo\":\"+254 795 111 123\",\"EntityName\":\"Kenyan Alliance Insurance Company \"},{\"EntityID\":\"30\",\"InsurerID\":\"680171f7-4f1c-4de7-8231-36dda3bf9061\",\"PhoneNo\":\"+254 709 922 000\",\"EntityName\":\"Madison Insurance Company Ltd.\"},{\"EntityID\":\"31\",\"InsurerID\":\"a5021a5f-321a-4990-9445-984b6faf210c\",\"PhoneNo\":\"+254 724 256 925\",\"EntityName\":\"Mayfair Insurance Company Ltd.\"},{\"EntityID\":\"32\",\"InsurerID\":\"66f4e43f-931c-48e0-9ebb-cb537efeee2e\",\"PhoneNo\":\"+254 723 342 150\",\"EntityName\":\"Metropolitan Cannon General Insurance Ltd.\"},{\"EntityID\":\"33\",\"InsurerID\":\"d0c24cea-10e0-402f-a0d9-d7eef4e36176\",\"PhoneNo\":\"+254 709 896 000\",\"EntityName\":\"Occidental Insurance Company Ltd.\"},{\"EntityID\":\"34\",\"InsurerID\":\"2eb14ff6-8f13-418f-9621-a67e9d3043b4\",\"PhoneNo\":\"+254 730 677 000\",\"EntityName\":\"Pacis Insurance Company Ltd.\"},{\"EntityID\":\"35\",\"InsurerID\":\"f15e4652-8507-470d-a675-88727302f248\",\"PhoneNo\":\"+254 757 741 399\",\"EntityName\":\"Phoenix of East Africa Assurance Company Ltd.\"},{\"EntityID\":\"36\",\"InsurerID\":\"e11cfe8d-be6c-46d9-a010-404e54befa1d\",\"PhoneNo\":\"+254 987 654 321\",\"EntityName\":\"Pioneer General Insurance Company Ltd.\"},{\"EntityID\":\"37\",\"InsurerID\":\"4cb026fc-0929-45fd-ad19-d6c0e46cdd46\",\"PhoneNo\":\"+254 709 990 000\",\"EntityName\":\"Resolution Insurance Ltd.\"},{\"EntityID\":\"38\",\"InsurerID\":\"98c4b0a6-4538-49c8-82c2-cb472d6e6e64\",\"PhoneNo\":\"+254 718 979 236\",\"EntityName\":\"Saham Assurance Company\"},{\"EntityID\":\"39\",\"InsurerID\":\"fb2a68fb-b037-4b00-ab6d-b54ca270af7d\",\"PhoneNo\":\"+254 719 035 000\",\"EntityName\":\"Sanlam General Insurance Ltd.\"},{\"EntityID\":\"40\",\"InsurerID\":\"52691690-10db-4b08-9f49-298e96803d25\",\"PhoneNo\":\"+254 703 808 010\",\"EntityName\":\"Takaful Insurance of Africa Ltd.\"},{\"EntityID\":\"41\",\"InsurerID\":\"82bff067-7ba4-45b4-b5f9-881aa1bdb984\",\"PhoneNo\":\"+254 709 914 000\",\"EntityName\":\"Tausi Assurance Company Ltd.\"},{\"EntityID\":\"42\",\"InsurerID\":\"95149040-c43f-4b5b-a5f0-5ac5fa2a2beb\",\"PhoneNo\":\"+254 711 039 000\",\"EntityName\":\"The Heritage Insurance Company Kenya Ltd.\"},{\"EntityID\":\"43\",\"InsurerID\":\"64f25408-1755-4352-958b-4755d1b05e47\",\"PhoneNo\":\"+254 705 426 931\",\"EntityName\":\"The Monarch Insurance Company Ltd.\"},{\"EntityID\":\"44\",\"InsurerID\":\"83dd280c-d5e8-4c09-a66d-f19feffa320b\",\"PhoneNo\":\"+254 740 477 028\",\"EntityName\":\"Trident Insurance Company Ltd.\"},{\"EntityID\":\"45\",\"InsurerID\":\"e7646e54-9d89-4462-83fa-eb68e978c002\",\"PhoneNo\":\"+254 711 010 000\",\"EntityName\":\"Old Mutual General Insurance Kenya Limited\"},{\"EntityID\":\"46\",\"InsurerID\":\"344ab0c8-d46c-4d27-b2c1-5a596c14e5e5\",\"PhoneNo\":\"+254 700 111 999\",\"EntityName\":\"Xplico Insurance Company Ltd.\"}]";
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    //int entityIDToFind = 12; // EntityID to search for
                    String idToFind = ClaimVehicleSelection.insuranceid;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // int entityID = jsonObject.getInt("EntityID");
                        String id = jsonObject.getString("InsurerID");

                        if (id.equals(idToFind)) {
                            //String insurerID = jsonObject.getString("InsurerID");
                            insurernumber = jsonObject.getString("PhoneNo");
                            //  String entityName = jsonObject.getString("EntityName");
                            // Do something with the matching object
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            tollfreenum = insurernumber;
            tollfreecompany = ClaimVehicleSelection.insurancename;

            videoclaimhis = findViewById(R.id.videoclaimhistory);
//        videoclaimhis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent otpIntent = new Intent(ClaimSuccess.this, HomePage.class);
//                startActivity(otpIntent);
//            }
//        });
            txtsuccess = findViewById(R.id.txtregsuccess);
            txtphno = findViewById(R.id.txtphnoval);
            try {
                txtsuccess.setText("Your claim is reported successfully to " + tollfreecompany + " claim support, dial "); //couldn't find the variable tollfreecompany so hardcoded the data
                txtphno.setText(tollfreenum); //couldn't find the variable tollfreenumber so hardcoded the data

                txtphno.setOnClickListener(onClickTxtPhoneNo -> {
                    Intent viewDetails = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tollfreenum));
                    startActivity(viewDetails);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            backhome = findViewById(R.id.Btnuclaimbacktohome);
            try {
                backhome.setOnClickListener(onClickBack -> {
                    Intent otpIntent = new Intent(ClaimSuccess.this, Dashboard.class);
                    startActivity(otpIntent);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            craid = findViewById(R.id.txtcraid);
            SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
            String incident_id = incidePref.getString(CraIdval, "");
            craid.setText("Incident Reference ID : " + incident_id);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

   /* OLD
    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    progressdialog = new ProgressDialog(this);
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
                        SharedPreferences driverPref = activity.getSharedPreferences("DriverID", MODE_PRIVATE);
                        String driver_id = driverPref.getString("DriverUniqueID", "");
                        SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                        String certnum = certifPref.getString("CertificateID", "");
                        String vehicrefid = certifPref.getString("Vechilerefid", "");
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                        String incidenttypeval = incitype.getString(typeidincident, "");
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("incidentUniqueCode", incident_id);
                        Details.addProperty("driverUserId", driver_id);
                        Details.addProperty("certificateNo", certnum);
                        Details.addProperty("claimTypeID", incidenttypeval);
                        Details.addProperty("VehicleId", vehicrefid);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() -> {
                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);

                                // progressdialog.show(activity, "Loading", "Please wait...", true);
                            });
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            if (staticRes.equals("")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        Toast.makeText(ClaimSuccess.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                    }
                                });
                            } else {
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
//                                        mydb = new DatabaseHelper(ImageDeclaration.this);
//                                        if(mydb.getclaimstep().getCount() != 0)
//                                        {
//                                            mydb.deleteclaimstep();
//                                        }
//                                        boolean claimstepinserted = mydb.insertclaimstep("ClaimCompleted");
//                                        if(claimstepinserted == true)
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Insertion Done");
//                                        }
//                                        else
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Not Insertion Done");
//                                        }
                                    //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
//                                    final JSONArray staticjsonval = staticJsonObj.getJSONArray("rmsg").getJSONArray(0);
//                                    String rmsgval2 = staticjsonval.getString(1);
//                                    String rmsgval3 = rmsgval2;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            //insertclaimpdf();
//                                                Intent car = new Intent(ClaimSuccess.this, ClaimSuccess.class);
//                                                startActivity(car);

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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ClaimSuccess.this);
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

                        } catch (final IOException | JSONException e) {
                            progressdialog.dismiss();
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Toast.makeText(ClaimSuccess.this,
                                            e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } //                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
                        //                                startActivity(redirect);

                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimSuccess.this);
                    dialog.setMessage("GPS locations is not enabled.Please enable it");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ClaimSuccess.this, e.getMessage() + " ", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }*/

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public void downloadfile(String filepath) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Show downloading dialog
            dialog = new android.app.AlertDialog.Builder(ClaimSuccess.this);
            dialog.setMessage("Your Claim registration report is downloading");
            alert = dialog.create();
            alert.show();

            // Fetch the incident ID from SharedPreferences
            SharedPreferences incidentPref = getSharedPreferences("CRAID", MODE_PRIVATE);
            String incidentId = incidentPref.getString(CraIdval, "");
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/";
            String fileName = incidentId + ".pdf";
            destination += fileName;

            // Open URL connection to download the file
            URL url = new URL(filepath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Read the input stream
            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }

            // Close streams
            inputStream.close();
            fileOutputStream.close();
            urlConnection.disconnect();

            // Download complete, dismiss the dialog and show PDF
            alert.dismiss();
            showPdf();

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }



    public void showPdf() {

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
            String incident_id = incidePref.getString(CraIdval, "");
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + incident_id + ".pdf");
            //File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/C-AA0074.pdf");
            Uri uri = FileProvider.getUriForFile(this, "com.aki.claimreporting.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public void getclaimpdf(int currentSelectedBtn) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(ClaimSuccess.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }

                        SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                        String incident_id = incidePref.getString(CraIdval, "");
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/ViewClaimPDF";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("incidentUniqueCode", incident_id);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        String companyClaimFormUrl = null;
                                        String bimaYanguDocUrl = null;
                                        try {
                                            companyClaimFormUrl = staticJsonObj.getJSONObject("rObj").getString("blobDownloadURL");
                                            bimaYanguDocUrl = staticJsonObj.getJSONObject("rObj").getString("memberblobDownloadURL");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }

                                        SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                                        String incident_id = incidePref.getString(CraIdval, "");
                                        File file1 = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + incident_id + ".pdf");
                                        boolean deleted = file1.delete();
                                        downloadfile((currentSelectedBtn == 0)? bimaYanguDocUrl : companyClaimFormUrl);
                                        // viewpdf.setEnabled(true);
                                        // viewclaimloading.setVisibility(View.GONE);
//                                        if(loaderappear.equals("1"))
//                                        {
//                                            handler.removeCallbacks(myRunnable);
//                                        }

                                        // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfurldoc));
                                        // startActivity(browserIntent);
                                    }
                                });

                            } else if (staticJsonObj.getInt("rcode") == 2) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        //  viewclaimloading.setVisibility(View.VISIBLE);
                                        // loaderappear = "1";
                                        //  getloadingfunction();
                                        //  viewpdf.setEnabled(false);
                                        Toast.makeText(ClaimSuccess.this, "PDF is generating", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                return;


                            } else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText = null;
                                        String trnId = null;
                                        try {
                                            errorText = index.getString("errorText");
                                            trnId = staticJsonObj.getString("trnID");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimSuccess.this);
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

                            progressdialog.dismiss();
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            //   Toast.makeText(ClaimSuccess.this,ex.toString(), Toast.LENGTH_LONG).show();
                            // getActivity().runOnUiThread(new Runnable() {
//                                        public void run() {
//
//                                            Toast.makeText(getActivity(),
//                                                    ex.toString(), Toast.LENGTH_LONG).show();
//                                        }
//                                    });
                        } // Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                        //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                    }
                });
                thread.start();
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {

                if (checkGPSStatus() == true) {

                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            SharedPreferences locPref = activity.getSharedPreferences("LocationCurrent", MODE_PRIVATE);
                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                            SharedPreferences driverPref = activity.getSharedPreferences("DriverID", MODE_PRIVATE);
                            String driver_id = driverPref.getString("DriverUniqueID", "");
                            SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                            String certnum = certifPref.getString("CertificateID", "");
                            String vehicrefid = certifPref.getString("Vechilerefid", "");
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
                            SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                            String incidenttypeval = incitype.getString("typeidincident", "");
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(360, TimeUnit.SECONDS)
                                    .writeTimeout(360, TimeUnit.SECONDS)
                                    .readTimeout(360, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            //Details.addProperty("incidentUniqueCode",incident_id);
                            Details.addProperty("incidentUniqueCode", incident_id);
                            Details.addProperty("incLocation", locPref.getString(MainActivity.Address1, ""));
                            Details.addProperty("driverUserId", driver_id);
                            Details.addProperty("certificateNo", certnum);
                            Details.addProperty("claimTypeID", incidenttypeval);
                            Details.addProperty("VehicleId", vehicrefid);
                            //Device unique code
                            String imeiInput = Settings.Secure.getString(ClaimSuccess.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            Details.addProperty("uniqueID",imeiInput);
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .method("POST", body)
                                    .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .build();
                            Response staticResponse = null;

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);

                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                if (staticRes.equals("")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            Toast.makeText(ClaimSuccess.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    try {
                                        reqidval = staticJsonObj.getString("reqID");
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        runOnUiThread(() -> {
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }

                                            claimSuccessLayout.setVisibility(View.VISIBLE);
                                        });
//                                        mydb = new DatabaseHelper(ImageDeclaration.this);
//                                        if(mydb.getclaimstep().getCount() != 0)
//                                        {
//                                            mydb.deleteclaimstep();
//                                        }
//                                        boolean claimstepinserted = mydb.insertclaimstep("ClaimCompleted");
//                                        if(claimstepinserted == true)
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Insertion Done");
//                                        }
//                                        else
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Not Insertion Done");
//                                        }
                                        //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
//                                    final JSONArray staticjsonval = staticJsonObj.getJSONArray("rmsg").getJSONArray(0);
//                                    String rmsgval2 = staticjsonval.getString(1);
//                                    String rmsgval3 = rmsgval2;
                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                //insertclaimpdf();
                                                mydb = new DatabaseHelper(ClaimSuccess.this);
                                                if (mydb.getclaimofflineiddetails().getCount() != 0) {
                                                    mydb.deleteclaimofflineiddata();
                                                }
                                                MainActivity.VideoEvidence = false;
                                                MainActivity.VideoDeclaration = false;
                                                SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreference.edit();
                                                editor.putString("visimagefilepath", "");
                                                editor.apply();

                                                SharedPreferences sharedPreference1 = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedPreference1.edit();
                                                editor1.putString("visaudiofilepath", "");
                                                editor1.apply();

                                                SharedPreferences sharedPreference2 = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor2 = sharedPreference2.edit();
                                                editor2.putString("videofilepath", "");
                                                editor2.apply();


                                                SharedPreferences sharedPreference3 = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor3 = sharedPreference3.edit();
                                                editor3.putString("videofilepathlocation", "");
                                                editor3.apply();
                                                runOnUiThread(progressdialog::dismiss);
                                               *//* if(isUserSignedUp()){
                                                    Intent car = new Intent(ImageDeclaration.this, ClaimSuccess.class);
                                                    startActivity(car);
                                                }else{
                                                    try {
                                                        showCustomDialog();
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }*//*
                                            }
                                        });*/
                                    } else {
                                        try {
                                            runOnUiThread(() -> {
                                                try {
                                                    claimSuccessLayout.setVisibility(View.GONE);
                                                    if(progressdialog.isShowing()){
                                                        progressdialog.dismiss();
                                                    }
                                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                    JSONObject index = rmsg.getJSONObject(0);
                                                    String errorText = index.getString("errorText");
                                                    Intent intent = new Intent(ClaimSuccess.this,ClaimHandler.class);
                                                    intent.putExtra("errorMsg",errorText);
                                                    startActivity(intent);
                                                   /* AlertDialog.Builder alert = new AlertDialog.Builder(ClaimSuccess.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressdialog.dismiss();
                                                            }
                                                        });
                                                        dialog.dismiss();
                                                    });
                                                    alert.show();*/
                                                } catch (Exception e) {
                                                    runOnUiThread(() -> {
                                                        if(progressdialog.isShowing()){
                                                            progressdialog.dismiss();
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }finally {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(progressdialog != null){
                                                                if(progressdialog.isShowing()){
                                                                    progressdialog.dismiss();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }

                                    }
                                }

                            } catch (final IOException ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
                                mCrashlytics.recordException(ex);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });
                            } catch (JSONException ex) {
                                progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
                                mCrashlytics.recordException(ex);
                                runOnUiThread(() -> Toast.makeText(ClaimSuccess.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());

                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                            finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });// Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    thread.start();

                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimSuccess.this);
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText((Context) this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            runOnUiThread(() -> {
                if(progressdialog.isShowing()){
                    progressdialog.dismiss();
                }
            });
            ex.getStackTrace();
            MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

//    public void getloadingfunction()
//    {
//        handler =  new Handler();
//        myRunnable = new Runnable() {
//            public void run() {
//                // Things to be done
//                getclaimpdf();
//            }
//        };
//
//        handler.postDelayed(myRunnable, 10000);
//
//
////        handler = new Handler();
////        handler.postDelayed(new Runnable() {
////            public void run() {
////
////                getclaimpdf();
////                handler.postDelayed(this, 5000); //now is every 2 minutes
////            }
////        }, 5000);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        //noinspection SimplifiableIfStatement
//        try {
//            if (id == R.id.action_name) {
//
//                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//                View screenView = rootView.getRootView();
//                screenView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//                screenView.setDrawingCacheEnabled(false);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//                supporteditor.putString(MainActivity.ReferrenceURL, "Claim Success");
//                supporteditor.apply();
//                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//                supporteditorimg.apply();
//                Intent login = new Intent(ClaimSuccess.this, SupportTicket.class);
//                startActivity(login);
//                return true;
//
//                // Do something
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(ClaimSuccess.this, Dashboard.class);
        startActivity(login);
    }
}