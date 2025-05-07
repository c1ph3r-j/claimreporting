package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
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
import androidx.cardview.widget.CardView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThirdPartyShare extends AppCompatActivity implements View.OnClickListener {

    public static String ShareCode;
    CardView viewForSend, viewForReceive;
    LinearLayout sendLayout, receiveLayout, nextButton, skipButton;
    TextView sendHeading, receiveHeading, sendText, receiveText;
    SharedPreferences selectedSharedPref;
    ProgressDialog progressdialog;
    ImageView selfinvcert, imgcollision;
    FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_share);
        getSupportActionBar().setTitle("Third Party Share");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        preventSizeChange(this, getSupportActionBar());

        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            viewForSend = findViewById(R.id.viewcollision);
            viewForReceive = findViewById(R.id.selfinvtxt);
            sendLayout = findViewById(R.id.sendLayout);
            receiveLayout = findViewById(R.id.receiveLayout);
            sendHeading = findViewById(R.id.collisionheadertxt);
            sendText = findViewById(R.id.collisiontittletxt);
            receiveHeading = findViewById(R.id.selfinvheadertxt);
            receiveText = findViewById(R.id.selfinvtittletxt);
            nextButton = findViewById(R.id.nextButton);
            skipButton = findViewById(R.id.SkipButton);
            imgcollision = findViewById(R.id.imgcollision);
            selfinvcert = findViewById(R.id.selfinvcert);

            selectedSharedPref = getSharedPreferences("shareInfoPref", MODE_PRIVATE);

            SharedPreferences.Editor editor = selectedSharedPref.edit();
            editor.putString("selectedOption", "null");
            editor.apply();

            try {
                viewForSend.setOnClickListener(this);
                viewForReceive.setOnClickListener(this);
                nextButton.setOnClickListener(this);
                skipButton.setOnClickListener(this);
                getQrCode();
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
    public void onClick(View view) {


        if (view.getId() == viewForSend.getId()) {
            clearSelectedOptions();
            selectedOption(sendLayout, sendHeading, sendText);
            SharedPreferences.Editor editor = selectedSharedPref.edit();
            editor.putString("selectedOption", "send");
            editor.apply();
            imgcollision.setImageDrawable(getResources().getDrawable(R.drawable.bluetoothsendwhite));
            selfinvcert.setImageDrawable(getResources().getDrawable(R.drawable.blueetoothreceive));

        } else if (view.getId() == viewForReceive.getId()) {
            clearSelectedOptions();
            selectedOption(receiveLayout, receiveHeading, receiveText);
            SharedPreferences.Editor editor = selectedSharedPref.edit();
            editor.putString("selectedOption", "receive");
            editor.apply();
            selfinvcert.setImageDrawable(getResources().getDrawable(R.drawable.bluetoothreceivewhite));
            imgcollision.setImageDrawable(getResources().getDrawable(R.drawable.blueetoothsend));
        } else if (view.getId() == nextButton.getId()) {
            String selectedOption = selectedSharedPref.getString("selectedOption", "null");
            if (selectedOption.equals("null")) {
                Toast.makeText(this, "Please select any of the option above to continue!", Toast.LENGTH_SHORT).show();
            } else if (selectedOption.equals("send")) {
                startActivity(new Intent(ThirdPartyShare.this, SenderShakeDetection.class));
            } else if (selectedOption.equals("receive")) {
                startActivity(new Intent(ThirdPartyShare.this, ReceiverShakeDetection.class));
            }
        } else if (view.getId() == skipButton.getId()) {
            startActivity(new Intent(ThirdPartyShare.this, ClaimVisualArtifacts.class));
        }
    }

    public void getQrCode() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            DatabaseHelper mydb = new DatabaseHelper(ThirdPartyShare.this);
            if (mydb.getTokendetails().getCount() != 0) {
                Cursor curseattachtoken = mydb.getTokendetails();
                int counttoken = curseattachtoken.getCount();
                if (counttoken >= 1) {
                    while (curseattachtoken.moveToNext()) {
                        MainActivity.stokenval = curseattachtoken.getString(1);
                    }
                }
            }

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
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Incidents/GetQRCode";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            SharedPreferences sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                            Details.addProperty("incidentUniqueCode", sharedpreferences.getString("CraIdval", ""));
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        String parms = MainActivity.InsertMobileParameters();
                        String stokenval = MainActivity.stokenval;
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .addHeader("MobileParameter", parms)
                                .post(body)
                                .build();

                        Response staticResponse = null;


                        try {
                            runOnUiThread(() -> progressdialog = ProgressDialog.show(ThirdPartyShare.this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);


                            if (staticJsonObj.getInt("rcode") == 1) {

                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    try {
                                        JSONObject getQrCode = staticJsonObj.getJSONObject("rObj");
                                        SharedPreferences sharedPreferences = getSharedPreferences("ShareCodePref", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        ShareCode = getQrCode.getString("qRCode");
                                        editor.putString("shareCode", ShareCode);
                                        editor.apply();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(progressdialog::dismiss);
                                try {
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ThirdPartyShare.this);
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
                        } catch (Exception ex) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(ThirdPartyShare.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ThirdPartyShare.this);
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
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
//            mCrashlytics.recordException(ex);
//            MainActivity.MobileErrorLog("UserRegistration-getuserexistapi",ex.toString(),ex.toString());
            Toast.makeText(ThirdPartyShare.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            //MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

    private void selectedOption(LinearLayout layout, TextView heading, TextView bodyText) {
        layout.setBackgroundColor(getColor(R.color.purple_500));
        heading.setTextColor(getColor(R.color.white));
        bodyText.setTextColor(getColor(R.color.white));
    }

    void clearSelectedOptions() {
        sendLayout.setBackgroundColor(getColor(R.color.white));
        sendText.setTextColor(getColor(R.color.black));
        sendHeading.setTextColor(getColor(R.color.purple_500));
        receiveLayout.setBackgroundColor(getColor(R.color.white));
        receiveText.setTextColor(getColor(R.color.black));
        receiveHeading.setTextColor(getColor(R.color.purple_500));
        ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_name) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                View screenView = rootView.getRootView();
                screenView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                screenView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
                SharedPreferences sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
                supporteditor.putString(MainActivity.ReferrenceURL, "Service Provider");
                supporteditor.apply();
                SharedPreferences sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.apply();
                Intent login = new Intent(ThirdPartyShare.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }

}