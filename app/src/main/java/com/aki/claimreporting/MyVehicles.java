package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyVehicles extends AppCompatActivity {

    public static String reqidval;
    public static CustomVehicleHistoryList customHumanListView;
    public static ListView list;
    public static String ViewCertificateno, ViewRegno;
    public static String Viewvehicleref;
    public static ArrayList<VehicleselectResponse> vehiclehislist = new ArrayList<>();
    public static DatabaseHelper mydb;
    public static ProgressDialog progressdialog;
    public static String stokenval, encryptedSHA;
    public static MyVehicles activity;
    public static LinearLayout vehicleprese;
    public static LinearLayout vehiclenorec;
    private static FirebaseCrashlytics mCrashlytics;
    public View rootview;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public ImageView addvehicle;
    PermissionHandler permissionManager;
    String[] permissions = new String[]{PERMISSION_LOCATION, PERMISSION_STORAGE, PERMISSION_READ_STORAGE};
    SearchView searchView;

    public static void getNewVehicleinfo(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            try {
                mydb = new DatabaseHelper(activity);
                if (mydb.getTokendetails().getCount() != 0) {
                    Cursor curseattachtoken = mydb.getTokendetails();
                    int counttoken = curseattachtoken.getCount();
                    if (counttoken >= 1) {
                        while (curseattachtoken.moveToNext()) {
                            stokenval = curseattachtoken.getString(1);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            progressdialog = new ProgressDialog(activity);
            encryptedSHA = "";
            String sourceStr = MainActivity.InsertMobileParameters();
            try {
                encryptedSHA = AESCrypt.encrypt(sourceStr);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                Thread thread = new Thread(() -> {
                    vehiclehislist.clear();
                    MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
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
                            .header("Authorization", "Bearer " + stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;

                    try {
                        activity.runOnUiThread(() -> progressdialog = ProgressDialog.show(activity, activity.getString(R.string.loading), activity.getString(R.string.please_wait), true));
                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            activity.runOnUiThread(() -> {
                                progressdialog.dismiss();
                                /*unauthorize(MyVehicles.this);*/
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
                                    if(progressdialog.isShowing()){
                                        progressdialog.dismiss();
                                    }
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");
                                    if (vehicleResponseList.length() == 0) {
                                        activity.runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            vehiclenorec.setVisibility(View.VISIBLE);
                                        });
                                    } else {
                                        vehiclehislist = new ArrayList<>();
                                        for (int i = 0; i < vehicleResponseList.length(); i++) {
                                            JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                            VehicleselectResponse element = new VehicleselectResponse(AESCrypt.decrypt(vehicleObj.getString("registrationNo")), AESCrypt.decrypt(vehicleObj.getString("certificateNo")), vehicleObj.getString("id"), vehicleObj.getString("insurerID"), vehicleObj.getString("insurerName"), vehicleObj.getString("insuredName"), vehicleObj.getString("certificateType"), vehicleObj.getString("coverageType"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), vehicleObj.getString("policyNo"), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("policyEndDate"), vehicleObj.getString("vINNumber"), vehicleObj.getString("isOwnVehicle"), vehicleObj.getString("isSubmitted"), vehicleObj.getString("ownVehicleID"), AESCrypt.decrypt(vehicleObj.getString("vehicleRefID")));
                                            vehiclehislist.add(element);
                                        }

                                        activity.runOnUiThread(() -> {
                                            vehicleprese.setVisibility(View.VISIBLE);
//                                            customHumanListView = new CustomVehicleHistoryList(activity, vehiclehislist, activity, );
//                                            list = (ListView) activity.findViewById(R.id.listvehiclehistory);
//                                            list.setAdapter(customHumanListView);
//                                            list.setTextFilterEnabled(true);
//                                            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                            progressdialog.dismiss();
                                        });
                                    }

                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    activity.runOnUiThread(() -> {
                                       /* unauthorize(MyVehicles.this);*/
                                        progressdialog.dismiss();
                                    });
                                } else {
                                    try {
                                        activity.runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            vehiclenorec.setVisibility(View.VISIBLE);
                                        });
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        activity.runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
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
                                        activity.runOnUiThread(progressdialog::dismiss);
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                }
                            } catch (Exception e) {
                                activity.runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    } catch (final Exception ex) {
                        activity.runOnUiThread(progressdialog::dismiss);
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                        activity.runOnUiThread(() -> Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show());
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    /*public static void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyVehicles.activity);
            dialog.setMessage(MyVehicles.activity.getString(R.string.session_expired));
            dialog.setPositiveButton("Ok", (dialog1, which) -> {
                mydb = new DatabaseHelper(MyVehicles.activity);
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
                Intent login = new Intent(MyVehicles.activity, Login.class);
                MyVehicles.activity.startActivity(login);
            });
            AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/

    public static void Deleteuservehicle(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
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
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

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
                    MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Vehicle/DeleteVehicle";
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
                    Details.addProperty("certificateNo", certid);
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
                        activity.runOnUiThread(() ->
                                progressdialog = ProgressDialog.show(activity, activity.getString(R.string.loading), activity.getString(R.string.please_wait), true));
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
                            activity.runOnUiThread(() -> {
                                progressdialog.dismiss();
                                activity.recreate();
//                                getNewVehicleinfo(activity);
                            });

                        } else if (staticJsonObj.getInt("rcode") == 2) {
                            try {
                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                JSONObject index = rmsg.getJSONObject(0);
                                activity.runOnUiThread(() -> {
                                    progressdialog.dismiss();
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
                                activity.runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }

                        } else {
                            try {
                                activity.runOnUiThread(progressdialog::dismiss);
                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                JSONObject index = rmsg.getJSONObject(0);
                                activity.runOnUiThread(() -> {
                                    String errorText;
                                    try {
                                        errorText = index.getString("errorText");
                                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
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
                                activity.runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    } catch (final Exception ex) {
                        activity.runOnUiThread(progressdialog::dismiss);
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                    }
                });
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Vehicles");
            preventSizeChange(this, getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        vehicleprese = (LinearLayout) findViewById(R.id.vehiclepresent);
        vehiclenorec = (LinearLayout) findViewById(R.id.vehiclenorecords);
        searchView = (SearchView) findViewById(R.id.searchView);
        try {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        if (TextUtils.isEmpty(newText)) {
                            customHumanListView.filter("");
                            list.clearTextFilter();
                        } else {
                            customHumanListView.filter(newText);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        return true;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        try {
            vehicleprese.setVisibility(View.GONE);
            vehiclenorec.setVisibility(View.GONE);
            getVehicleinfo();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            e.printStackTrace();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyVehicles.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) MyVehicles.this.getSystemService(Context.LOCATION_SERVICE);
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

//    public void getVehicleinfo() {
//
//        try {
//            if(isNetworkConnected()) {
//
//                if (checkGPSStatus())
//                {
//
//                    mydb = new DatabaseHelper(getActivity());
//                    if(mydb.getTokendetails().getCount() != 0) {
//                        Cursor curseattachtoken = mydb.getTokendetails();
//                        int counttoken = curseattachtoken.getCount();
//                        if (counttoken >= 1) {
//                            while (curseattachtoken.moveToNext()) {
//                                //stokenval = curseattachtoken.getString(1);
//                                stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
//                            }
//                        }
//                    }
//                    progressdialog = new ProgressDialog(getActivity());
//                    encryptedSHA = "";
//                    String sourceStr = MainActivity.InsertMobileparameters();
//                    try {
//                        encryptedSHA = AESUtils.encrypt(sourceStr);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Thread thread = new Thread(new Runnable() {
//
//                        public void run() {
//                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
//                            final MediaType JSON
//                                    = MediaType.parse("application/json; charset=utf-8");
//                            OkHttpClient client = new OkHttpClient();
//                            JsonObject Details = new JsonObject();
//                            String insertString = Details.toString();
//                            RequestBody body = RequestBody.create(JSON, insertString);
//                            Request request = new Request.Builder()
//                                    .url(MainActivity.postURL)
//                                    .header("Authorization", "Bearer " +stokenval)
//                                    .header("MobileParameters", MainActivity.InsertMobileparameters())
//                                    .post(body)
//                                    .build();
//                            Response staticResponse = null;
//
//                            try {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                    }
//                                });
//                                staticResponse = client.newCall(request).execute();
//                                String staticRes = staticResponse.body().string();
//                                Log.i(null,staticRes);
//                                final JSONObject staticJsonObj = new JSONObject(staticRes);
//                                if (staticJsonObj.getInt("rcode") == 1)
//                                {
//                                    //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
//                                    // sOTP= staticJsonObj.getJSONObject("rObj").getString("OTP");
//                                    //  sOTPID= staticJsonObj.getJSONObject("rObj").getString("OTPID");
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog.dismiss();
//
//                                            //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
//                                            // startActivity(step2);
//                                        }
//                                    });
//
////
//                                    JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");
//                                    for (int i = 0; i < vehicleResponseList.length(); i++) {
//                                        JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
//                                        VehicleselectResponse element = new VehicleselectResponse(vehicleObj.getString("registrationNo"),vehicleObj.getString("certificateNo"),vehicleObj.getString("insuredName"),vehicleObj.getString("make"),vehicleObj.getString("model"),vehicleObj.getString("yearOfMfg"),vehicleObj.getString("policyBeginDate"),vehicleObj.getString("id"),vehicleObj.getString("userID"),vehicleObj.getString("id"),vehicleObj.getString("userID"),vehicleObj.getString("vINNumber"),vehicleObj.getString("doctype"));
//                                        // element.VehicleRefID = vehicleObj.getInt("VehicleRefID");
//                                        element.RegistrationNo = vehicleObj.getString("registrationNo");
//                                        element.CertificateNo = vehicleObj.getString("certificateNo");
////                            element.InsuranceCompanyID =  vehicleObj.getInt("InsuranceCompanyID");
//                                        element.InsuranceCompanyName =  vehicleObj.getString("insuredName");
//                                        element.TypeOfVehicleName =vehicleObj.getString("vINNumber");
////                            element.TypeOfVehicleID =  vehicleObj.getInt("TypeOfVehicleID");
////                            element.CoverTypeID =  vehicleObj.getInt("CoverTypeID");
////                            element.YearOfManufacture = vehicleObj.getString("YearOfManufacture");
////                            element.VehicleMake =  vehicleObj.getString("VehicleMake");
////                            element.VehicleModel = vehicleObj.getString("VehicleModel");
////                            element.PolicyStartDate = vehicleObj.getString("PolicyStartDate");
//
//                                        //regspinner.add(vehicleObj.getString("RegistrationNo"));
//                                        vehiclehislist.add(element);
//                                    }
//
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            // setInvisible();
//                                            customHumanListView = new CustomVehicleHistoryList(getActivity(), vehiclehislist);
//                                            list = (ListView) rootview.findViewById(R.id.listvehiclehistory);
//                                            list.setAdapter(customHumanListView);
//                                            // setListViewHeightBasedOnItems(list);
//                                            list.setTextFilterEnabled(true);
//                                            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//
//                                        }
//                                    });
//
//
//                                }
//                                else
//                                {
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            try {
//                                                Toast.makeText(getActivity(), staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") , Toast.LENGTH_SHORT).show();
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                            return;
//                                        }
//                                    });
//                                }
//                            } catch (final IOException ex)
//                            {
//                                progressdialog.dismiss();
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                getActivity().runOnUiThread(new Runnable() {
//                                    public void run() {
//
//                                        Toast.makeText(getActivity(),
//                                                ex.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                            } catch (JSONException ex)
//                            {
//                                progressdialog.dismiss();
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                Toast.makeText(getActivity(),
//                                        ex.toString(), Toast.LENGTH_LONG).show();
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//                            }
//                        }
//                    });
//                    thread.start();
//
//
//
//                } else
//                {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                    dialog.setMessage("GPS locations is not enabled.Please enable it");
//                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //this will navigate user to the device location settings screen
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    });
//                    AlertDialog alert = dialog.create();
//                    alert.show();
//                }
//
//
//            }
//            else
//            {
//                Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (Exception ex)
//        {
//            //progressdialog.dismiss();
//            ex.getStackTrace();
//            mCrashlytics.recordException(ex);
//            Toast.makeText(getActivity(),ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//        }
//    }

//    public void getVehicleinfo() {
//        try {
//            if(isNetworkConnected()) {
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                mydb = new DatabaseHelper(getActivity());
////                Cursor curseattach = mydb.getTokendetails();
////                int counttest1 = curseattach.getCount();
////                if (counttest1 >= 1) {
////                    while (curseattach.moveToNext()) {
////                        token = curseattach.getString(6);
////                    }
//                        //  }
//                        SharedPreferences locationPref = getContext().getSharedPreferences("LocationPref", MODE_PRIVATE);
//                        final SharedPreferences.Editor locationeditor = locationPref.edit();
//                        final String latitude = locationPref.getString(MainActivity.Latitude, null);
//                        final String longitude = locationPref.getString(MainActivity.Longitude, null);
//                        SharedPreferences vehicleRegPref = getContext().getSharedPreferences("ClaimRegisterInitialization", MODE_PRIVATE);
//                        final String vehInstance = vehicleRegPref.getString(VehicleInstanceID, "758AD97F-519C-4C90-8FE2-2CCF6DB6729F");
//                        final String instanceType = vehicleRegPref.getString(InstanceTypeID, "1");
//                        String vehicleURL = getString(R.string.uaturl) + "/api/v1/Vehicle/GetAllVehicle";
//                        OkHttpClient vehicle = new OkHttpClient();
//                        JsonObject vehicleJson = new JsonObject();
//                        vehicleJson.addProperty("typeofvehicleid", (String) null);
//                        String vehicleString = vehicleJson.toString();
//                        RequestBody vehicleBody = RequestBody.create(JSON, vehicleString);
//                        Request vehicleRequest = new Request.Builder()
//                                .url(vehicleURL)
//                                .header("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsImtpZCI6IkJEMUU1NUUzNjAwNkY5ODY1ODRBNzQ1NzU5RDY2REYxNTYxQTkzRjEiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiVVNFUiIsInByaW1hcnlzaWQiOiJGQ0E3NEE3OS0wNTBDLTQ3N0YtODVCQS05MEE1Q0MxRjlEREIiLCJMb2dpbkluSGlzdG9yeUlEIjoiOTk0IiwiTG9nZ2VkSW5Sb2xlSUQiOiJVU0VSIiwiTG9nZ2VkSW5FbWFpbCI6InByZWV0aGlAc3dpZnRhbnQuY29tIiwiTG9nZ2VkSW5OYW1lIjoiUHJlZXRoaSBQYW5kaSIsIm5iZiI6MTU5NTIyNjU2MCwiZXhwIjoxNTk1ODMxMzYwLCJpYXQiOjE1OTUyMjY1NjAsImlzcyI6Imh0dHBzOi8vZGlnaXRhbGNsYWltYXBpLmF6dXJld2Vic2l0ZXMubmV0LyIsImF1ZCI6Imh0dHBzOi8vZGlnaXRhbGNsYWltYXBpLmF6dXJld2Vic2l0ZXMubmV0LyJ9.iv5zqG55f7ZQ_iY3hOV18bUxoPXzWWTXnVqWxEQLwcvFXb_O6hXbrk4UQqasg2uT7TansJ2D65ZEe3iyTqwFzpMHAnN6S4PUjqbq_rL2MAg6PvdTi2X5KvFFSsBehyiCXx0YpVioJugX-4P2sG2CktZbihQDBf-Ws0Gy1xjlHTpGn2JGNQpiwV4O-A4PzOn8iie-AAls2VWSaVBygar-4KG54KPHeQqctP_GFB0W7KUagvlziJMkTt1uw0-a7dA_OLk0kA_C8dJSgZsqysD5TR1VYxVBlWor1cXn2NIqJR1vhCKKB6A-az8gbqMBYTcIpSiZLEhVy8wEqZorkwo8ew")
//                                .header("Latitude", latitude)
//                                .header("Longitude", longitude)
//                                .header("VehicleInstanceID", vehInstance)
//                                .header("InstanceTypeID", instanceType)
//                                .post(vehicleBody)
//                                .build();
//                        Response vehicleResponse = null;
//                        try {
//                            getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    //setVisible();
//                                    //Toast.makeText(getActivity(),"Check your network settings!", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            vehicleResponse = vehicle.newCall(vehicleRequest).execute();
//                            String vehicleResult = vehicleResponse.body().string();
//                            JSONObject vehicleJsonObj = new JSONObject(vehicleResult);
//                            Log.i(null, "Vehicle type " + vehicleJsonObj.getString("message") + " fetched");
//                            if (vehicleJsonObj.getInt("code") == 1) {
//                                //JSONArray vehicleResponseList = vehicleJsonObj.getJSONObject("callBackObj").getJSONArray("getAllVehicle");
//                                JSONArray vehicleResponseList = vehicleJsonObj.getJSONObject("callBackObj").getJSONArray("getAllVehicle");
//                                for (int i = 0; i < vehicleResponseList.length(); i++) {
//                                    JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
    // VehicleselectResponse element = new VehicleselectResponse(vehicleObj.getInt("VehicleRefID"), vehicleObj.getString("RegistrationNo"), vehicleObj.getString("CertificateNo"), vehicleObj.getInt("InsuranceCompanyID"), vehicleObj.getString("InsuranceCompanyName"), vehicleObj.getString("TypeOfVehicleName"), vehicleObj.getInt("TypeOfVehicleID"), vehicleObj.getInt("CoverTypeID"), vehicleObj.getString("YearOfManufacture"), vehicleObj.getString("VehicleMake"), vehicleObj.getString("VehicleModel"), vehicleObj.getString("PolicyStartDate"));
////                            element.VehicleRefID = vehicleObj.getInt("VehicleRefID");
////                            element.RegistrationNo = vehicleObj.getString("RegistrationNo");
////                            element.CertificateNo = vehicleObj.getString("CertificateNo");
////                            element.InsuranceCompanyID =  vehicleObj.getInt("InsuranceCompanyID");
////                            element.InsuranceCompanyName =  vehicleObj.getString("InsuranceCompanyName");
////                            element.TypeOfVehicleName =vehicleObj.getString("TypeOfVehicleName");
////                            element.TypeOfVehicleID =  vehicleObj.getInt("TypeOfVehicleID");
////                            element.CoverTypeID =  vehicleObj.getInt("CoverTypeID");
////                            element.YearOfManufacture = vehicleObj.getString("YearOfManufacture");
////                            element.VehicleMake =  vehicleObj.getString("VehicleMake");
////                            element.VehicleModel = vehicleObj.getString("VehicleModel");
////                            element.PolicyStartDate = vehicleObj.getString("PolicyStartDate");
//
//                                    // regspinner.add(vehicleObj.getString("RegistrationNo"));
//                                 //   vehiclehislist.add(element);
//                                }
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // setInvisible();
//                                        customHumanListView = new CustomVehicleHistoryList(getActivity(), vehiclehislist);
//                                        list = (ListView) rootview.findViewById(R.id.listvehiclehistory);
//                                        list.setAdapter(customHumanListView);
//                                        // setListViewHeightBasedOnItems(list);
//                                        list.setTextFilterEnabled(true);
//                                        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//
//                                    }
//                                });
//
//                            } else {
//                                //Toast.makeText(getActivity(),"Check API!", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (Exception ex) {
//                            //setInvisible();
//                            ex.printStackTrace();
//                            mCrashlytics.recordException(ex);
//
//                            //Toast.makeText(getActivity(),"Check your network settings!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//
//                thread.start();
//            }
//            else
//            {
//
//
//                Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (Exception ex)
//        {
//            ex.getStackTrace();
//            mCrashlytics.recordException(ex);
//        }
//    }
//    public static boolean setListViewHeightBasedOnItems(ListView listView) {
//
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter != null) {
//
//            int numberOfItems = listAdapter.getCount();
//
//            // Get total height of all items.
//            int totalItemsHeight = 0;
//            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
//                View item = listAdapter.getView(itemPos, null, listView);
//                float px = 500 * (listView.getResources().getDisplayMetrics().density);
//                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                totalItemsHeight += item.getMeasuredHeight();
//            }
//
//            // Get total height of all item dividers.
//            int totalDividersHeight = listView.getDividerHeight() *
//                    (numberOfItems - 1);
//            // Get padding
//            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();
//
//            // Set list height.
//            ViewGroup.LayoutParams params = listView.getLayoutParams();
//            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
//            listView.setLayoutParams(params);
//            listView.requestLayout();
//            return true;
//
//        } else {
//            return false;
//        }
//
//    }
//
//    public void setInvisible() {
//        progressOverlay.setVisibility(View.INVISIBLE);
//    }
//    public void setVisible() {
//        progressOverlay.setVisibility(View.VISIBLE);
//    }

    public void getVehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    try {
                        mydb = new DatabaseHelper(MyVehicles.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    progressdialog = new ProgressDialog(MyVehicles.this);
                    encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
//                    String certnumencrypt = null;
//                    try {
//                        certnumencrypt = AESCrypt.encrypt("G/KTD/0700/286146");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        String encryptest = AESCrypt.decrypt(certnumencrypt);
//                        String encryptest2 = encryptest;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    try {
                        encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    Thread thread = new Thread(() -> {
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
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(MyVehicles.this, getString(R.string.loading), getString(R.string.please_wait), true));
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(MyVehicles.this);
                                });
                            } else {
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
                                    //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                    // sOTP= staticJsonObj.getJSONObject("rObj").getString("OTP");
                                    //  sOTPID= staticJsonObj.getJSONObject("rObj").getString("OTPID");


                                    JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");
                                    if (vehicleResponseList.length() == 0) {
                                        runOnUiThread(() -> {
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                            vehiclenorec.setVisibility(View.VISIBLE);
                                        });

                                    } else {
                                        try {
                                            vehiclehislist.clear();
                                            vehiclehislist = new ArrayList<>();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        for (int i = 0; i < vehicleResponseList.length(); i++) {
                                            JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                            VehicleselectResponse element = new VehicleselectResponse(AESCrypt.decrypt(vehicleObj.getString("registrationNo")), AESCrypt.decrypt(vehicleObj.getString("certificateNo")), vehicleObj.getString("id"), vehicleObj.getString("insurerID"), vehicleObj.getString("insurerName"), vehicleObj.getString("insuredName"), vehicleObj.getString("certificateType"), vehicleObj.getString("coverageType"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), AESCrypt.decrypt(vehicleObj.getString("policyNo")), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("policyEndDate"), vehicleObj.getString("vINNumber"), vehicleObj.getString("isOwnVehicle"), vehicleObj.getString("isSubmitted"), vehicleObj.getString("ownVehicleID"), AESCrypt.decrypt(vehicleObj.getString("vehicleRefID")));
                                            vehiclehislist.add(element);
                                        }
                                        runOnUiThread(() -> {
                                            // setInvisible();
                                            vehicleprese.setVisibility(View.VISIBLE);
                                            customHumanListView = new CustomVehicleHistoryList(MyVehicles.this, vehiclehislist, MyVehicles.this, permissionManager);
                                            list = (ListView) findViewById(R.id.listvehiclehistory);
                                            list.setAdapter(customHumanListView);
                                            // setListViewHeightBasedOnItems(list);
                                            list.setTextFilterEnabled(true);
                                            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                            progressdialog.dismiss();
                                        });
                                    }


                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(MyVehicles.this);
                                    });
                                } else {
                                    try {
                                        runOnUiThread(progressdialog::dismiss);
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                vehiclenorec.setVisibility(View.VISIBLE);
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
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
                                        runOnUiThread(progressdialog::dismiss);
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                }
                            }
                        } catch (final Exception ex) {
                            runOnUiThread(progressdialog::dismiss);
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            runOnUiThread(() -> Toast.makeText(MyVehicles.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                        }
                    });
                    thread.start();


                } else {
                    try {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MyVehicles.this);
                        dialog.setMessage(getString(R.string.gps_not_enabled));
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        AlertDialog alert = dialog.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            } else {
                Toast.makeText(MyVehicles.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(MyVehicles.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

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
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//            View rootView = MyVehicles.this.getWindow().getDecorView().findViewById(android.R.id.content);
//            View screenView = rootView.getRootView();
//            screenView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//            screenView.setDrawingCacheEnabled(false);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//            supporteditor.putString(MainActivity.ReferrenceURL, "Vehicle History");
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(MyVehicles.this, SupportTicket.class);
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