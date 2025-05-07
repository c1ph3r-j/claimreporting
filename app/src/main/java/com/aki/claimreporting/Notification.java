package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
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


public class Notification extends AppCompatActivity {

    public static final String notificationidmap = "notificationidmap";
    public static final String notificationtitle = "notificationtitle";
    public static final String notificationdescription = "notificationdescription";
    public static final String notificationcreatedOn = "notificationcreatedOn";
    public static DatabaseHelper mydb;
    public static String reqidval;
    public static FirebaseCrashlytics mCrashlytics;
    static TextView noNewNotificationView;
    static LinearLayout notificationView;
    static ArrayList<NotificationDataModel> notificationlist = new ArrayList<>();
    public String stokenval, encryptedSHA;
    public ProgressDialog progressdialog;
    Activity activity;
    ListView listView;
    private NotificationAdapter adapter;

    public static void Viewnotification(Context activity) {
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

            try {
                Thread thread = new Thread(new Runnable() {

                    public void run() {
                        // MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriver";
                        MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Notification/ViewNotification";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        // SharedPreferences modelPref = activity.getSharedPreferences("ViewNotification", Context.MODE_PRIVATE);
                        SharedPreferences viewnotifyPref = activity.getSharedPreferences("ViewNotification", MODE_PRIVATE);

                        String notificationid = viewnotifyPref.getString(notificationidmap, "");


                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("notificationID", notificationid);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;

                        try {

                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            try {
                                reqidval = staticJsonObj.getString("reqID");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            if (staticJsonObj.getInt("rcode") == 1) {

                            } else {

                            }
                        } catch (final IOException ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    public void run() {
//
//                                        Toast.makeText(getActivity(),
//                                                ex.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                Toast.makeText(getActivity(),
//                                        ex.toString(), Toast.LENGTH_LONG).show();
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                        }
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
            //progressdialog.dismiss();
//            ex.getStackTrace();
//            mCrashlytics.recordException(ex);
//            Toast.makeText(getActivity(),ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    public static void Deletenotification(Context activity) {
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


            try {
                Thread thread = new Thread(new Runnable() {

                    public void run() {
                        // MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriver";
                        MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Notification/DeleteNotification";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        SharedPreferences modelPref = activity.getSharedPreferences("Notification", Context.MODE_PRIVATE);
                        final String notificationid = modelPref.getString(notificationidmap, null);

                        OkHttpClient client = new OkHttpClient();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("notificationID", notificationid);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;

                        try {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressdialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
//                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                    }
//                                });
                            staticResponse = client.newCall(request).execute();
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


                            } else {

                            }
                        } catch (final IOException ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    public void run() {
//
//                                        Toast.makeText(getActivity(),
//                                                ex.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                Toast.makeText(getActivity(),
//                                        ex.toString(), Toast.LENGTH_LONG).show();
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                        }
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
            //progressdialog.dismiss();
//            ex.getStackTrace();
//            mCrashlytics.recordException(ex);
//            Toast.makeText(getActivity(),ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    public static void checkVisibility() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (notificationlist.size() > 0) {
                notificationView.setVisibility(View.VISIBLE);
                noNewNotificationView.setVisibility(View.GONE);
            } else {
                notificationView.setVisibility(View.GONE);
                noNewNotificationView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_notification);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            listView = (ListView) findViewById(R.id.listnotification);
            noNewNotificationView = findViewById(R.id.noNewNotificationView);
            notificationView = findViewById(R.id.notificationView);
            checkVisibility();

            GetAllNotificationlist();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public void GetAllNotificationlist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    try {
                        mydb = new DatabaseHelper(Notification.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                    // stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwicHJpbWFyeXNpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsInByaW1hcnlncm91cHNpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsIlJvbGUiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwibmJmIjoxNjEwNDM0NjA5LCJleHAiOjE2NDE1Mzg2MDksImlhdCI6MTYxMDQzNDYwOSwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.XuA0cKX5--jrvYfabh8xKYE4G74_EJYcbFwKfscj2Yiihe13BpWmw7xbeQSd7qZEjOIZ6qeNyIBmYa4D6AdNa50yZ-y3oefBBcUd7nsO24oZ0vzOO2P7uZV2NAhQkJZd83VJbNaHRjxFCwa8bTP6gaD8emA6aY2NSVt7TzTCNxnz_4059eDckHPg_0_BUQ3v4oMIAsC-Mn6mUUx6jWiK-ggaxbxQMY_TEHADMlLrqHkFgbi1PALLKQb8DNIfhHu-Iw6RtOzfMfM28_SUgpZZIEHftcg_XhUZmbNBYD-943T8Guq8xHAC_JJx1VaJIIwJEIGIuWvhlE1TU1_h-gjK9pKknfFcBOJorHXe4vI1YTMCe2AqGKqoRSAeYrtk7qgaD4UmWen1Y1h-gGHBWbu7VuXiA758j7RRLHfmdoZuGBITTe8Wl8taSacwtRPjxHjnz5vzqYlOvsKMTOjxxc-thXIBZWblPElK-0l0O1F9SiTQSEJi6C6UxL4qJNsUPIN0a4sHkbln8cJ6VLrB4VZIeOhuY5CFuyAy0DwYJbFyXZjnNF_39jYkCpOTN6dUVJUG152VAlONMUc29hJ_MeQHs2mRDxEKNxp-BAUtzZrZhcz8iEAI9R-Itc88tpm1U8BTNu6k_MnULKGZlrB8y1HRQ4-H1imSG5UNt6bg9l_W0IU";
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

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
                        Thread thread = new Thread(new Runnable() {


                            public void run() {
                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Notification/GetAllNotification";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");

                                OkHttpClient client = new OkHttpClient();
                                JsonObject Details = new JsonObject();
                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(MainActivity.postURL)
                                        .header("Authorization", "Bearer " + stokenval)
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
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                    }
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        JSONArray notificationList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllNotification");
                                        for (int i = 0; i < notificationList.length(); i++) {
                                            JSONObject notificationObj = notificationList.getJSONObject(i);
                                            NotificationDataModel element = new NotificationDataModel(
                                                    notificationObj.getString("notificationID"),
                                                    notificationObj.getString("userID"),
                                                    notificationObj.getString("title"),
                                                    notificationObj.getString("description"),
                                                    notificationObj.getString("notificationTypeID"),
                                                    notificationObj.getString("notificationInfo"),
                                                    notificationObj.getString("androidFireBaseID"),
                                                    notificationObj.getString("email"),
                                                    notificationObj.getString("phoneNo"),
                                                    notificationObj.getString("typeID")

                                            );

                                            notificationlist.add(element);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                adapter = new NotificationAdapter(Notification.this, notificationlist);
                                                listView.setAdapter(adapter);
                                                // setListViewHeightBasedOnItems(list);
                                                listView.setTextFilterEnabled(true);
                                                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                                progressdialog.dismiss();
                                                checkVisibility();
                                            }

                                        });

//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            progressdialog.dismiss();
//                                                        }
//                                                    });
//                                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
//                                                    startActivity(redirect);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(Notification.this);
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
                                        }
                                    }
                                } catch (final IOException ex) {
                                    // progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(Notification.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (JSONException ex) {
                                    //   progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
//                                                Toast.makeText(RegistrationStep1.this,
//                                                        ex.toString(), Toast.LENGTH_LONG).show();
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                }
                            }


                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }


                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Notification.this);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }


            } else {
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //finishAffinity(); // or finish();
    }
}
