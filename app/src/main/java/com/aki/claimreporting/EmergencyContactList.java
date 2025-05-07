package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;

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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

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

public class EmergencyContactList extends AppCompatActivity {

    public static FirebaseCrashlytics mCrashlytics;
    public static String reqidval;
    public ProgressDialog progressdialog;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public ListView list;
    public ImageView addemergency;
    SearchView searchView;
    DatabaseHelper mydb;
    EmergencyContactAdapter neardearadapter;
    ArrayList<EmergencyContactInfo> neardearlist = new ArrayList<>();
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_conact_list);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        activity = this;
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Emergency Contact");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            searchView = (SearchView) findViewById(R.id.searchView);
            addemergency = (ImageView) findViewById(R.id.imageAddemergency);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
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
                            neardearadapter.filter("");
                            list.clearTextFilter();
                        } else {
                            neardearadapter.filter(newText);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            addemergency.setOnClickListener(view -> {
                Intent login = new Intent(EmergencyContactList.this, AddEmergency.class);
                startActivity(login);
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        try {
            getemergencycontactinfo();
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

    public void getemergencycontactinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(EmergencyContactList.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
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
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/NearDear/GetAllNearDear";
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
                                runOnUiThread(() ->
                                        progressdialog = ProgressDialog.show(EmergencyContactList.this, "Loading", "Please wait...", true));
                                staticResponse = client.newCall(request).execute();
                                assert staticResponse.body() != null;
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    JSONArray nearResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllnearDears");
                                    for (int i = 0; i < nearResponseList.length(); i++) {
                                        JSONObject nearObj = nearResponseList.getJSONObject(i);
                                        EmergencyContactInfo element = new EmergencyContactInfo();
                                        try {
                                            element.nearid = nearObj.getString("nearDearsId");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        try {
                                            element.nearname = AESCrypt.decrypt(nearObj.getString("nearDearName"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        try {
                                            element.nearphone = "+254 " + AESCrypt.decrypt(nearObj.getString("nearDearPhone"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        try {
                                            element.nearemail = AESCrypt.decrypt(nearObj.getString("nearDearEmail"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        neardearlist.add(element);
                                    }
                                    runOnUiThread(() -> {
                                        neardearadapter = new EmergencyContactAdapter(EmergencyContactList.this, neardearlist, EmergencyContactList.this);
                                        list = (ListView) findViewById(R.id.listemergencycontact);
                                        list.setAdapter(neardearadapter);
                                        list.setTextFilterEnabled(true);
                                        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                        progressdialog.dismiss();
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
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                }
                            } catch (final Exception ex) {
                                progressdialog.dismiss();
                                ex.printStackTrace();
                                mCrashlytics.recordException(ex);
                                runOnUiThread(() -> Toast.makeText(EmergencyContactList.this,
                                        R.string.admin, Toast.LENGTH_LONG).show());
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EmergencyContactList.this);
                    dialog.setMessage("GPS locations is not enabled.Please enable it");
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }
            } else {
                Toast.makeText(EmergencyContactList.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            Toast.makeText(EmergencyContactList.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteNearDear(Context mcontext, Activity mactivity) {
        try {
            ConnectivityManager cm = (ConnectivityManager) mactivity.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {

                mydb = new DatabaseHelper(mactivity);
                if (mydb.getTokendetails().getCount() != 0) {
                    Cursor curseattachtoken = mydb.getTokendetails();
                    int counttoken = curseattachtoken.getCount();
                    if (counttoken >= 1) {
                        while (curseattachtoken.moveToNext()) {
                            MainActivity.stokenval = curseattachtoken.getString(1);
                        }
                    }
                }
                progressdialog = new ProgressDialog(mactivity);
                MainActivity.encryptedSHA = "";
                String sourceStr = MainActivity.InsertMobileParameters();
                try {
                    MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Thread(() -> {
                    MainActivity.postURL = mcontext.getString(R.string.uaturl) + "/app/NearDear/DeleteNearDear";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    SharedPreferences modelPref = mactivity.getSharedPreferences("NearDearDelete", Context.MODE_PRIVATE);
                    final String nearuserid = modelPref.getString(MainActivity.neardearUseridmap, null);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("nearDearId", nearuserid);
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
                        mactivity.runOnUiThread(() ->
                                progressdialog = ProgressDialog.show(mactivity, "Loading", "Please wait...", true));
                        staticResponse = client.newCall(request).execute();
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
                            mactivity.runOnUiThread(() -> {
                                progressdialog.dismiss();
                                Intent emergency = new Intent(mactivity, EmergencyContactList.class);
                                mactivity.startActivity(emergency);
                            });
                        } else if (staticJsonObj.getInt("rcode") == 401) {
                            mactivity.runOnUiThread(() -> {
                                progressdialog.dismiss();
                                unauthorize(EmergencyContactList.this);
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
                                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                        alert.setCancelable(false);
                                        alert.setMessage(errorText);
                                        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                        alert.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "deleteNearDear", e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "deleteNearDear", e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    } catch (final Exception ex) {
                        progressdialog.dismiss();
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(reqidval, "MyDriver-deletedriver", ex.toString());
                        mCrashlytics.recordException(ex);
                        mactivity.runOnUiThread(() -> Toast.makeText(mactivity,
                                ex.toString(), Toast.LENGTH_LONG).show());
                    }
                }).start();


            } else {
                Toast.makeText(mactivity, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            MainActivity.MobileErrorLog(reqidval, "MyDriver-deletedriver", ex.toString());
            Toast.makeText(mactivity, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    /*public void unauthorize() {
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(EmergencyContactList.this);
            dialog.setMessage("Your session have been expired. Please login again to continue");
            dialog.setPositiveButton("Ok", (dialog1, which) -> {
                mydb = new DatabaseHelper(EmergencyContactList.this);
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
                Intent login = new Intent(EmergencyContactList.this, Login.class);
                startActivity(login);
            });
            android.app.AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "unAuthorize", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/

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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//
//            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
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
//            supporteditor.putString(MainActivity.ReferrenceURL, "Emergency Contact Information");
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(EmergencyContactList.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//
//            // Do something
//
//        } else {
        try {
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EmergencyContactList.this, Dashboard.class));
    }
}