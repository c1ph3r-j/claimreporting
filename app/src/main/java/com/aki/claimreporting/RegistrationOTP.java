package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationOTP extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public static String sToken;
    public ProgressDialog progressdialog;
    public CountDownTimer countdown;
    public TextView otptxtval, otptimer;
    public TextView resendotpclick;
    EditText registerotp;
    ImageView verify_otp;
    Activity activity;
    ImageView backimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            setContentView(R.layout.activity_registration_otp);
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            preventSizeChange(this, getSupportActionBar());
            init();
            getWindow().setStatusBarColor(getColor(R.color.lightdarkcolorgrey));
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }

    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        activity = this;
        mCrashlytics = FirebaseCrashlytics.getInstance();
        otptxtval = findViewById(R.id.otp);
        String emailSentMsg = "We have sent a verification code to your Email ID - " + Registration.emailid;
        otptxtval.setText(emailSentMsg);
        backimg = findViewById(R.id.closeicon);
        registerotp = findViewById(R.id.registerotp);
        otptimer = findViewById(R.id.txtotptimer);
        registerotp.setText(Registration.otp);
        verify_otp = findViewById(R.id.verifyotp);
        resendotpclick = findViewById(R.id.resendotpclick);
        otptimer.setVisibility(View.VISIBLE);
        resendotpclick.setVisibility(View.GONE);
        try {
            resendotpclick.setOnClickListener(onClickReSendOtp -> resendOTP());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            verify_otp.setOnClickListener(onClickVerifyOtp -> verifyOtp());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            backimg.setOnClickListener(onClickBackImg -> {
                countdown.cancel();
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            timerFunction();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void timerFunction() {
        countdown = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long timerVal = (millisUntilFinished / 1000);
                String timer;
                if (timerVal <= 9) {
                    timer = "00:0" + (millisUntilFinished / 1000);
                } else {
                    timer = "00:" + (millisUntilFinished / 1000);
                }
                otptimer.setText(timer);
            }

            public void onFinish() {
                resendotpclick.setVisibility(View.VISIBLE);
                otptimer.setVisibility(View.GONE);
            }
        }.start();
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

    public void resendOTP() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Account/CRAAppRegistrationSendOTP";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            Details.addProperty("firstName", Registration.fullname);
                            Details.addProperty("email", Registration.emailid);
                            Details.addProperty("phoneNo", Registration.phonenumber);
                            Details.addProperty("referralCode", Registration.referalcode);
                            //Device unique code
                            String imeiInput = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            Details.addProperty("uniqueID", imeiInput);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() -> {
                                try {
                                    progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);

                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(() -> {
                                    try {
                                        progressdialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    try {
                                        Registration.otp = staticJsonObj.getJSONObject("rObj").getString("OTP");
                                        Registration.otpid = staticJsonObj.getJSONObject("rObj").getString("TokenID");
                                        registerotp.setText(Registration.otp);
                                        resendotpclick.setVisibility(View.GONE);
                                        otptimer.setVisibility(View.VISIBLE);
                                        timerFunction();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationOTP.this);
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
                        } catch (final IOException | JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(RegistrationOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
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
                    });
                    thread.start();
                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RegistrationOTP.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
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
                try {
                    Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(RegistrationOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }

    }

    public void verifyOtp() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    try {
                        if (registerotp.getText().toString().equals("") || registerotp.getText().toString().length() == 0) {
                            Toast.makeText(RegistrationOTP.this, getString(R.string.otpman), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    Thread thread = new Thread(() -> {
                        try {
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            String url = getString(R.string.uaturl) + "/app/Account/CRAAppRegistrationValidateOTP";
                            JsonObject Details = new JsonObject();
                            try {
                                Details.addProperty("OTPToken", Registration.otpid);
                                Details.addProperty("OTP", registerotp.getText().toString().trim());
                                //Device unique code
                                String imeiInput = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                Details.addProperty("uniqueID", imeiInput);
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }

                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse;

                            try {
                                runOnUiThread(() -> progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true));
                                staticResponse = client.newCall(request).execute();
                                assert staticResponse.body() != null;
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);

                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(() -> {
                                        try {
                                            progressdialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        try {
                                            sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                            mydb = new DatabaseHelper(RegistrationOTP.this);
                                            if (mydb.getTokendetails().getCount() != 0) {
                                                mydb.deletetokendata();
                                            }
                                            if (mydb.getUserdetails().getCount() != 0) {
                                                mydb.deleteuserdata();
                                            }
                                            mydb.inserttoken(sToken);
                                            boolean isProfileInserted = mydb.insertuserdetails(staticJsonObj.getJSONObject("rObj")
                                                            .getJSONObject("cRALoginProfile").getString("firstName"),
                                                    staticJsonObj.getJSONObject("rObj").getJSONObject("cRALoginProfile").getString("cRAID"),
                                                    Registration.phonenumber, staticJsonObj.getJSONObject("rObj").getJSONObject("cRALoginProfile").getString("email"));
                                            if (isProfileInserted) {
                                                Log.i(null, "Insertion Done");
                                            } else {
                                                Log.i(null, "Not Insertion Done");
                                            }
                                            boolean signUpPreference = getIntent().getBooleanExtra("ImageDeclaration", false);
                                            boolean grievance = getIntent().getBooleanExtra("grievance", false);
                                            boolean addVehicle = getIntent().getBooleanExtra("add-vehicle",false);
                                            if (signUpPreference) {
                                                Intent i = new Intent(RegistrationOTP.this, ClaimSuccess.class);
                                                startActivity(i);
                                            } else {
                                                if (grievance || addVehicle) {
                                                    Intent i = new Intent(RegistrationOTP.this, Grievance.class);
                                                    i.putExtra("grievance",grievance);
                                                    i.putExtra("add-vehicle",addVehicle);
                                                    startActivity(i);

                                                } else {
                                                    Intent i = new Intent(RegistrationOTP.this, Dashboard.class);
                                                    startActivity(i);
                                                }

                                            }
                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                        } catch (Exception e) {
                                            e.printStackTrace();
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationOTP.this);
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
                                runOnUiThread(() -> {
                                    try {
                                        progressdialog.dismiss();
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                        Toast.makeText(RegistrationOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();
                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RegistrationOTP.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
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
                try {
                    Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(RegistrationOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            countdown.cancel();
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "onBackPressed", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }
}