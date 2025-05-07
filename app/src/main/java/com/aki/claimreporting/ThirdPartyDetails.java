package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThirdPartyDetails extends AppCompatActivity {

    ProgressDialog progressdialog;
    ListView thirdPartyLayout;
    TextView noRecords, title;
    ArrayList<ThirdPartyDetailsModel> listOfThirdParty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_details);
        preventSizeChange(this, getSupportActionBar());
        init();

        getThirdPartyMapping();

    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            noRecords = findViewById(R.id.noRecords);
            thirdPartyLayout = findViewById(R.id.layoutForThirdPartyDetails);
            title = findViewById(R.id.headingThirdPartyDetails);

            thirdPartyLayout.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            noRecords.setVisibility(View.VISIBLE);

            Objects.requireNonNull(getSupportActionBar()).setTitle("ThirdParty Details");

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


    public void getThirdPartyMapping() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    String rooteddevice;
                    if (RootUtil.isDeviceRooted()) {
                        rooteddevice = "1";
                    } else {
                        rooteddevice = "0";
                    }


                    String phnencrypted = "";

                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Incidents/GetThirdPartyMapping";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            SharedPreferences claimPref = getSharedPreferences("ClaimDetailsView", MODE_PRIVATE);
                            String incident_id = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
                            Details.addProperty("incidentUniqueCode", incident_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;

                        try {
                            ThirdPartyDetails.this.runOnUiThread(() -> progressdialog = ProgressDialog.show(ThirdPartyDetails.this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);


                            if (staticJsonObj.getInt("rcode") == 1) {

                                ThirdPartyDetails.this.runOnUiThread(() -> {
                                    progressdialog.dismiss();

                                    try {

                                        JSONObject responseBody = staticJsonObj.getJSONObject("rObj");
                                        JSONArray getThirdPartyMapping = responseBody.getJSONArray("getThirdpartyMapping");


                                        if (getThirdPartyMapping.length() != 0) {
                                            listOfThirdParty = new ArrayList<>();
                                            for (int j = 0; j < getThirdPartyMapping.length(); j++) {
                                                JSONObject item = getThirdPartyMapping.getJSONObject(j);
                                                String name = item.getString("createdByName");
                                                String mobileNo = item.getString("createdByMobileNo");
                                                String vehicleNo = item.getString("vehicleNo");
                                                String incidentId = "Incident_Id: " + item.getString("incidentUniqueCode");

                                                listOfThirdParty.add(new ThirdPartyDetailsModel(
                                                        name,
                                                        vehicleNo,
                                                        incidentId,
                                                        mobileNo
                                                ));
                                            }

                                            ThirdPartyDetailsAdapter adapter = new ThirdPartyDetailsAdapter(ThirdPartyDetails.this, listOfThirdParty);
                                            thirdPartyLayout.setAdapter(adapter);

                                            title.setVisibility(View.VISIBLE);
                                            noRecords.setVisibility(View.GONE);
                                            thirdPartyLayout.setVisibility(View.VISIBLE);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                ThirdPartyDetails.this.runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                });
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ThirdPartyDetails.this);
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
                        } catch (final IOException | JSONException ex) {

//                              MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                            ThirdPartyDetails.this.runOnUiThread(() -> {
                                progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                Toast.makeText(ThirdPartyDetails.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ThirdPartyDetails.this);
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
                Toast.makeText(ThirdPartyDetails.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(ThirdPartyDetails.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }

    }

}