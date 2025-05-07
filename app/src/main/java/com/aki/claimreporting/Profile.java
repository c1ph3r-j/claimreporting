package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimLocation.progressdialog;
import static com.aki.claimreporting.MainActivity.checkGPSStatus;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.ScanCertificate.QrCode;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

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

public class Profile extends AppCompatActivity {

    public static String reqidval;
    public ProgressDialog progressdialog;
    public String stokenval, encryptedSHA;
    public View rootview;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    DatabaseHelper mydb;
    Profile activity;
    Button BtnLogout;
    private FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preventSizeChange(this, getSupportActionBar());
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
//        uatenvirlinear = (LinearLayout) findViewById(R.id.lineuatenv);
//        SharedPreferences envuatshared = Profile.this.getSharedPreferences("ENVUAT",Context.MODE_PRIVATE);
//        String uatenvi = envuatshared.getString(MainActivity.uatenvironment,"");
//        if(uatenvi.equals("0"))
//        {
//            uatenvirlinear.setVisibility(View.GONE);
//        }
//        else
//        {
//            uatenvirlinear.setVisibility(View.VISIBLE);
//        }
            BtnLogout = (Button) findViewById(R.id.BtnLogout);

            try {

                BtnLogout.setOnClickListener(view -> {
                    AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Profile.this);
                    alertDialog2.setTitle("Logout...");
                    alertDialog2.setMessage("Are you sure you want to logout?");
                    alertDialog2.setPositiveButton("YES",
                            (dialog, which) -> {
                                mydb = new DatabaseHelper(Profile.this);
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
                            });
                    alertDialog2.setNegativeButton("NO",
                            (dialog, which) -> dialog.cancel());
                    alertDialog2.show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            getuserprofile();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }
    //mobile login API
    public void loginUser() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (MainActivity.isNetworkConnected(this)) {
                if (checkGPSStatus()) {
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
                                       String sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                        mydb = new DatabaseHelper(Profile.this);
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
                                        Intent otpIntent = new Intent(Profile.this, Dashboard.class);
                                        startActivity(otpIntent);
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
                                            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
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
                                Toast.makeText(Profile.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(Profile.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Profile.this);
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
            Toast.makeText(Profile.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        } finally {

        }

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Profile.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) Profile.this.getSystemService(Context.LOCATION_SERVICE);
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

    public void getuserprofile() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(Profile.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    progressdialog = new ProgressDialog(Profile.this);
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
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/UAD/GetUserProfile";
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
                                Profile.this.runOnUiThread(() ->
                                        progressdialog = ProgressDialog.show(Profile.this, getString(R.string.loading), getString(R.string.please_wait), true));
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    Profile.this.runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(Profile.this);
                                    });
                                } else {
                                    assert staticResponse.body() != null;
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
                                        final JSONObject userResponselist = staticJsonObj.getJSONObject("rObj").getJSONObject("getUserProfile");
                                        Profile.this.runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            try {
                                                TextView memberid = (TextView) findViewById(R.id.txtmemberidval);
                                                memberid.setText(getString(R.string.cra_idval) + " : " + AESCrypt.decrypt(userResponselist.getString("cRAID")));
                                                TextView firstname = (TextView) findViewById(R.id.txtfirstlastnameval);
                                                firstname.setText(AESCrypt.decrypt(userResponselist.getString("firstName")));
                                                TextView mobileno = (TextView) findViewById(R.id.txtmobilenoval);
                                                String mobile = AESCrypt.decrypt(userResponselist.getString("mobileNo"));
                                                if (mobile.contains("+254")) {
                                                    mobileno.setText(mobile);
                                                } else {
                                                    mobileno.setText(getString(R.string.prefix254) + " " + mobile);
                                                }
                                                if (AESCrypt.decrypt(userResponselist.getString("email")) == "null" || AESCrypt.decrypt(userResponselist.getString("email")) == "" || AESCrypt.decrypt(userResponselist.getString("email")) == null) {
                                                    LinearLayout emailvalnul = (LinearLayout) findViewById(R.id.emailnullval);
                                                    View emailview = (View) findViewById(R.id.emailnullvalview3);
                                                    emailvalnul.setVisibility(View.VISIBLE);
                                                    emailview.setVisibility(View.VISIBLE);
                                                    ImageView imgview = (ImageView) findViewById(R.id.imgphone);
                                                    TextView emailtxt = (TextView) findViewById(R.id.txtemailval);
                                                    imgview.setVisibility(View.GONE);
                                                    emailtxt.setVisibility(View.GONE);
                                                } else {
                                                    LinearLayout emailvalnul = (LinearLayout) findViewById(R.id.emailnullval);
                                                    View emailview = (View) findViewById(R.id.emailnullvalview3);
                                                    emailvalnul.setVisibility(View.VISIBLE);
                                                    emailview.setVisibility(View.VISIBLE);
                                                    TextView email = (TextView) findViewById(R.id.txtemailval);
                                                    email.setText(AESCrypt.decrypt(userResponselist.getString("email")));
                                                }
                                            } catch (Exception e) {
                                                progressdialog.dismiss();
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        });


                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        Profile.this.runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            unauthorize(Profile.this);
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
                                }
                            } catch (final Exception e) {
                                progressdialog.dismiss();
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                Profile.this.runOnUiThread(() ->
                                        Toast.makeText(Profile.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        progressdialog.dismiss();
                    }

                }
            } else {
                Toast.makeText(Profile.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(Profile.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Profile.this);
        dialog.setMessage(getString(R.string.session_expired));
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(Profile.this);
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
            ClearDB();
            Intent login = new Intent(Profile.this, Login.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    void ClearDB() {
        SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = modelPref.edit();
        editor.putString(QrCode, " ");
        editor.apply();
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
//            View rootView = Profile.this.getWindow().getDecorView().findViewById(android.R.id.content);
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
//            Intent login = new Intent(Profile.this, SupportTicket.class);
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