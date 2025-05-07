package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.CertificateActivation.reqidval;
import static com.aki.claimreporting.ClaimLocation.progressdialog;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.stokenval;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VehicleSelectionWithoutServices extends AppCompatActivity {
    public static ArrayList<String> sListpager;
    public static ArrayList<String> sMakeModel;
    public static int valuelistadpt;
    public static ArrayList<VehicleselectClaimResponse> listOfVehiclesDetail;
    public static LinearLayout selectedVehicleLayout;
    public static TextView selectedVehicleModel;
    public static String lattestfinal;
    public static String longtestfinal;
    public static String smsphnnumbers;
    public static String drivercraid;
    public String membercraid;
    public DatabaseHelper mydb;
    public String finalpushsmstext, finalpushsmssend, PINString;
    View selectedView = null;
    boolean isMessageSent = false;
    SharedPreferences sharedpreferences;
    ViewPager viewPager;
    TextView sendsmsid, nextid;

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public static String SHA1(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_selection_without_services);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Claim Vehicle Selection");
        preventSizeChange(this, getSupportActionBar());
        listOfVehiclesDetail = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            if (isNetworkConnected()) {
                getallvehicleinfo();
            } else {
                getnonetworkVehicleinfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        init();
    }

    public void getnonetworkVehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
            Cursor curseattachtoken = mydb.getvehicledetails();
            int counttoken = curseattachtoken.getCount();
            if (counttoken >= 1) {
                while (curseattachtoken.moveToNext()) {
                    VehicleselectClaimResponse element = new VehicleselectClaimResponse(AESCrypt.decrypt(curseattachtoken.getString(1)), AESCrypt.decrypt(curseattachtoken.getString(2)), curseattachtoken.getString(3), curseattachtoken.getString(4), curseattachtoken.getString(5), curseattachtoken.getString(6), curseattachtoken.getString(7), curseattachtoken.getString(8), curseattachtoken.getString(9), curseattachtoken.getString(10), curseattachtoken.getString(14), curseattachtoken.getString(14));
                    listOfVehiclesDetail.add(element);

                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initListView();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);

        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            viewPager = (ViewPager) findViewById(R.id.viewPager);
            nextid = (TextView) findViewById(R.id.nextid);
            sendsmsid = (TextView) findViewById(R.id.sendsmsid);


            MainActivity.ambulanceenabled = "No";
            MainActivity.policeinfoenabled = "No";
            MainActivity.towingagencyenabled = "No";


        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void alertsmsuser() {
        android.app.AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(VehicleSelectionWithoutServices.this);
        alertDialog2.setTitle("Offline Claim Support...");
        alertDialog2.setMessage("You don't have Internet on your mobile. So, please click the 'SEND' option so that the CRA app will send an SMS to the support team to get instant help immediately.");
        //alertDialog2.setIcon(R.drawable.delete);
        alertDialog2.setPositiveButton("SEND",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
                            Cursor curseattachsms = mydb.getsendsmsdetails();
                            int countsms = curseattachsms.getCount();
                            if (countsms >= 1) {
                                while (curseattachsms.moveToNext()) {
                                    smsphnnumbers = AESCrypt.decrypt(curseattachsms.getString(2));
                                }
                            }

                            Uri sms_uri = Uri.parse("smsto:" + smsphnnumbers);
                            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                            sms_intent.putExtra("sms_body", finalpushsmssend);
                            startActivity(sms_intent);

                            isMessageSent = true;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        alertDialog2.show();


    }


    public void getallvehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        postURL = getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
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
                                .url(postURL)
                                //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(VehicleSelectionWithoutServices.this, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // progressdialog.dismiss();
                                        unauthorize();
                                        return;
                                    }
                                });
                            } else {
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                try {
                                    String reqidval = staticJsonObj.getString("reqID");
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();

                                            //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
                                            // startActivity(step2);
                                        }
                                    });
                                    JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");

                                    if (vehicleResponseList.length() == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initListView();
                                            }
                                        });

                                    } else {

                                        for (int i = 0; i < vehicleResponseList.length(); i++) {
                                            JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                            if (vehicleObj.getString("isSubmitted").contains("false")) {
                                                //Toast.makeText(VehicleSelectionWithoutServices.this, "Your vehicle is not Submitted Yet, Please submit the vehicle and try later.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("VehicleResponse", vehicleResponseList.toString());
                                                VehicleselectClaimResponse element = new VehicleselectClaimResponse(AESCrypt.decrypt(vehicleObj.getString("registrationNo")), AESCrypt.decrypt(vehicleObj.getString("certificateNo")), vehicleObj.getString("insurerName"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("id"), vehicleObj.getString("coverageType"), vehicleObj.getString("vINNumber"), vehicleObj.getString("vehicleRefID"), vehicleObj.getString("insurerID"));
                                                listOfVehiclesDetail.add(element);
                                                runOnUiThread(() -> initListView());
                                            }

                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

//                                                mAdapter = new VehicleClaimAdapter(VehicleSelectionWithoutServices.this,regnumlist,VehicleSelectionWithoutServices.this);
//                                                ListView list = (ListView) findViewById(R.id.listclaimvechall);
//                                                list.setAdapter(mAdapter);
//                                                // setListViewHeightBasedOnItems(list);
//                                                list.setTextFilterEnabled(true);
//                                                list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                    @Override
//                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                                                        //mAdapter.setSelectedIndex(position);
//                                                        mAdapter.notifyDataSetChanged();
////                                                        VehicleselectClaimResponse responsenew = regnumlist.get(position);
////                                                        String regnovaltest = responsenew.getRegistrationNo();
////                                                        String regnovaltest1 = regnovaltest;
////                                                        SharedPreferences certpref = getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
////                                                        SharedPreferences.Editor certeditor = certpref.edit();
////                                                        certeditor.putString(ClaimRegFragment.CertificateID, responsenew.getCertificateNo());
////                                                        certeditor.putString(ClaimRegFragment.Vechilerefid, responsenew.getVehicleRefID());
////                                                        certeditor.commit();
////                                                        driverselection(mcontext, activity);
//                                                    }
//                                                });

                                            }
                                        });
                                    }


                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            unauthorize();
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(VehicleSelectionWithoutServices.this);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(VehicleSelectionWithoutServices.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //progressdialog.dismiss();
                }
            });
            ex.getStackTrace();
            //MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getnetworkVehicleinfo",ex.toString(),ex.toString());
            //mCrashlytics.recordException(ex);
        }

    }

    public void getclaimincidentid() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (isNetworkConnected()) {
            try {
                if (isNetworkConnected()) {

                    if (checkGPSStatus()) {

                        mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        progressdialog = new ProgressDialog(VehicleSelectionWithoutServices.this);
                        String encryptedSHA = "";
                        String sourceStr = MainActivity.InsertMobileParameters();
                        try {
                            encryptedSHA = AESCrypt.encrypt(sourceStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread thread = new Thread(new Runnable() {
                            SharedPreferences locPref = getSharedPreferences("LocationCurrent", MODE_PRIVATE);

                            public void run() {
                                SharedPreferences vecidpref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                                String vechid = vecidpref.getString("Vechilerefid", "");
                                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                String incidenttypeval = incitype.getString("typeidincident", "");
                                postURL = getString(R.string.uaturl) + "/app/Claim/CRARegistration";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");

                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                JsonObject Details = new JsonObject();

                                try {
                                    Details.addProperty("incLocation", locPref.getString("Address1", ""));
                                    //Details.addProperty("incLocation", "Mimosa Road Mucai Drive Off Ngong Road Next to Pram Court Apartments AKI Centre, Nairobi City, Kenya");
                                    Details.addProperty("VehicleId", AESCrypt.decrypt(vechid));
                                    Details.addProperty("incidentDatetime", ClaimLocation.incidentselecteddate);
                                    Details.addProperty("driverUserId", "");
                                    Details.addProperty("claimTypeID", incidenttypeval);
                                    Details.addProperty("isEmergencyContact", false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(postURL)
                                        .header("Authorization", "Bearer " + stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .post(body)
                                        .build();
                                Response staticResponse;

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(VehicleSelectionWithoutServices.this, "Loading", "Please wait...", true);
                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
                                        }
                                    });
                                    staticResponse = client.newCall(request).execute();
                                    int statuscode = staticResponse.code();
                                    if (statuscode == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize();
                                            }
                                        });
                                    } else {
                                        try {
                                            assert staticResponse.body() != null;
                                            String staticRes = staticResponse.body().string();
                                            Log.i(null, staticRes);
                                            final JSONObject staticJsonObj = new JSONObject(staticRes);

                                            try {
                                                reqidval = staticJsonObj.getString("reqID");
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                            if (staticJsonObj.getInt("rcode") == 1) {
                                                String craid = staticJsonObj.getJSONObject("rObj").getString("incidentUniqueCode");
                                                sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor craeeditor = sharedpreferences.edit();
                                                craeeditor.putString("CraIdval", craid);
                                                craeeditor.apply();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressdialog.dismiss();
                                                        SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
                                                        if (incidentsharedpreferences.getString(ClaimType.typeidincident, "") == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
                                                            startActivity(new Intent(VehicleSelectionWithoutServices.this, ClaimStolenTheft.class));
                                                        } else {
                                                            try {
                                                                DisplayMetrics dm = getResources().getDisplayMetrics();
                                                                int densityDpi = dm.densityDpi;
                                                                if (densityDpi >= 320 && densityDpi <= 390) {
                                                                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView.class);
                                                                    startActivity(car);
                                                                }
                                                                if (densityDpi >= 280 && densityDpi <= 300) {
                                                                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView.class);
                                                                    startActivity(car);
                                                                }
                                                                if (densityDpi >= 310 && densityDpi <= 395) {
                                                                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView280.class);
                                                                    startActivity(car);
                                                                }
                                                                if (densityDpi >= 400 && densityDpi <= 520) {
                                                                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView400.class);
                                                                    startActivity(car);
                                                                }
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressdialog.dismiss();
                                                        unauthorize();
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
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(VehicleSelectionWithoutServices.this);
                                                            alert.setCancelable(false);
                                                            alert.setMessage(errorText);
                                                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                            alert.show();
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//                                                    progressdialog.dismiss();
//                                                    try {
//                                                        Toast.makeText(VehicleSelectionWithoutServices.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException ex) {
//                                                        ex.printStackTrace();
//                                                        //MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//                                                    }
//                                                    return;
//                                                }
//                                            });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }

                                } catch (final Exception ex) {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
//                                MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//                                mCrashlytics.recordException(ex);
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(VehicleSelectionWithoutServices.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        thread.start();

                    } else {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(VehicleSelectionWithoutServices.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    }


                } else {
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                progressdialog.dismiss();
                ex.getStackTrace();
//            MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//            mCrashlytics.recordException(ex);
                // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

            }
        } else {
            try {
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int densityDpi = dm.densityDpi;
                if (densityDpi >= 320 && densityDpi <= 390) {
                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView.class);
                    startActivity(car);
                }
                if (densityDpi >= 280 && densityDpi <= 300) {
                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView.class);
                    startActivity(car);
                }
                if (densityDpi >= 310 && densityDpi <= 395) {
                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView280.class);
                    startActivity(car);
                }
                if (densityDpi >= 400 && densityDpi <= 520) {
                    Intent car = new Intent(VehicleSelectionWithoutServices.this, CarView400.class);
                    startActivity(car);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            return false;

        } else {
            return true;
        }

    }

    private void initListView() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        SharedPreferences certpref = getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
        SharedPreferences.Editor certeditor = certpref.edit();
        ListOfVehiclesAdapter listOfVehiclesAdapter = new ListOfVehiclesAdapter(this, listOfVehiclesDetail);
        ListView listOfVehiclesView = findViewById(R.id.listOfVehiclesView);
        listOfVehiclesView.setAdapter(listOfVehiclesAdapter);
        listOfVehiclesView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (listOfVehiclesDetail.size() != 0) {
                if (selectedView != null) {
                    TextView title = selectedView.findViewById(R.id.vehicleName);
                    ImageView carIcon = selectedView.findViewById(R.id.carIcon);
                    LinearLayout layout = selectedView.findViewById(R.id.vehicleDetailsView);
                    title.setTextColor(getColor(R.color.black));
                    carIcon.setColorFilter(getColor(R.color.black));
                    layout.setBackgroundColor(getColor(R.color.white));
                }
                TextView title = view.findViewById(R.id.vehicleName);
                ImageView carIcon = view.findViewById(R.id.carIcon);
                LinearLayout layout = view.findViewById(R.id.vehicleDetailsView);
                title.setTextColor(getColor(R.color.white));
                carIcon.setColorFilter(getColor(R.color.white));
                layout.setBackgroundColor(getColor(R.color.purple_500));
                selectedView = view;
                certeditor.putString("CertificateID", listOfVehiclesDetail.get(i).getCertificateNo());
                certeditor.putString("Vechilerefid", listOfVehiclesDetail.get(i).getVehicleRefID());
                certeditor.apply();
                if (isNetworkConnected() || isMessageSent) {
                    getclaimincidentid();
                } else {
                    SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                    String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");

                    mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
                    Cursor curseattachuser = mydb.getUserdetails();
                    int countuser = curseattachuser.getCount();
                    if (countuser >= 1) {
                        while (curseattachuser.moveToNext()) {
                            try {
                                membercraid = AESCrypt.decrypt(curseattachuser.getString(2));
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    }
                    // String vehnewid = MainActivity.vehicleoffline;
                    SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                    String lattest = locationPref.getString(MainActivity.Latitude, null);
                    String longtest = locationPref.getString(MainActivity.Longitude, null);
                    lattestfinal = lattest;
                    longtestfinal = longtest;

                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmm", Locale.US);
                    String dateTime = sdf.format(Calendar.getInstance().getTime());
//                SharedPreferences driverpref = getSharedPreferences("DriverSelection",MODE_PRIVATE);
//                String drivercraidnew = driverpref.getString(Claim.driverusercraid,"");
//                if(drivercraidnew.equals(""))
//                {
//                    try {
//                        drivercraid =  AESCrypt.decrypt(membercraid);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                else
//                {
//                    drivercraid = drivercraidnew;
//                }
                    try {
                        drivercraid = membercraid;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    SharedPreferences vecidpref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                    String vehnewid = vecidpref.getString("Vechilerefid", "");
                    String vehidnew = null;
                    try {
                        vehidnew = AESCrypt.decrypt(vehnewid).replace("-", "~");
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    String mergefinal = null;
                    try {
                        //mergefinal = dateTime+ AESCrypt.decrypt(membercraid)+ "-" + drivercraid +"-"+ vehidnew + ":" +lattestfinal+ ":" +longtestfinal;
                        mergefinal = dateTime + membercraid + "#" + drivercraid + "#" + vehidnew + ":" + lattestfinal + ":" + longtestfinal;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    String PasswordKey = "WeLove9SA";
                    String shakey = null;
                    try {
                        // shakey = dateTime+"-"+lattestfinal+"-"+longtestfinal+"-"+vehidnew+"-"+ AESCrypt.decrypt(membercraid);
                        // shakey = dateTime+"#"+lattestfinal+"#"+longtestfinal+"#"+vehidnew+"#"+ AESCrypt.decrypt(membercraid);
                        shakey = dateTime + "#" + lattestfinal + "#" + longtestfinal + "#" + vehidnew + "#" + membercraid;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    String newval = SHA1(PasswordKey + shakey);

                    //  String newval = SHA1(mergefinal);
                    String firstthreeChars = newval.substring(0, 3);
                    String lastthreeChars = newval.substring(newval.length() - 3);
                    String checkdigit = firstthreeChars + lastthreeChars;
                    mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
                    int randomPIN = (int) (Math.random() * 9000) + 1000;
                    PINString = String.valueOf(randomPIN);
                    finalpushsmstext = checkdigit + 0 + 0 + 0 + 0 + 0 + mergefinal;
                    finalpushsmssend = "Do not change the text " + finalpushsmstext + " " + PINString;
                    if (mydb.getclaimofflineiddetails().getCount() != 0) {
                        mydb.deleteclaimofflineiddata();
                    }
                    boolean Isinserted = mydb.insertclaimofflineid(finalpushsmstext);
                    if (Isinserted) {
                        boolean test = Isinserted;
                        Log.i(null, "Insertion Done");
                    } else {
                        boolean test = Isinserted;
                        Log.i(null, "Not Insertion Done");
                    }

                    alertsmsuser();
                }
            }
        });
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(VehicleSelectionWithoutServices.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mydb = new DatabaseHelper(VehicleSelectionWithoutServices.this);
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
                Intent login = new Intent(VehicleSelectionWithoutServices.this, Login.class);
                startActivity(login);
            }
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}