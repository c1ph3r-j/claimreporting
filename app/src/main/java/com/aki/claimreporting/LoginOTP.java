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

public class LoginOTP extends AppCompatActivity {


    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public static String sToken;
    public ProgressDialog progressdialog;
    public CountDownTimer countdown;
    public TextView otptxtval, otptimer;
    public TextView resendotpclick;
    EditText loginotp;
    ImageView verify_otp;
    Activity activity;
    ImageView backimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            setContentView(R.layout.activity_login_otp);
            preventSizeChange(this, getSupportActionBar());
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            getWindow().setStatusBarColor(getColor(R.color.lightdarkcolorgrey));
            init();
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "onCreate", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }


    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            activity = this;
            mCrashlytics = FirebaseCrashlytics.getInstance();
            otptxtval = findViewById(R.id.otp);
            String emailSentText = "We have sent a verification code to your Email ID - " + Login.emailidval;
            otptxtval.setText(emailSentText);
            loginotp = findViewById(R.id.loginotp);
            otptimer = findViewById(R.id.txtotptimer);
            loginotp.setText(Login.otp);
            verify_otp = findViewById(R.id.verifyotp);
            resendotpclick = findViewById(R.id.resendotpclick);
            otptimer.setVisibility(View.VISIBLE);
            resendotpclick.setVisibility(View.GONE);
            backimg = findViewById(R.id.closeicon);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            resendotpclick.setOnClickListener(onClickResend -> resendOTP());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            verify_otp.setOnClickListener(onVerifyOtp -> verifyOtp());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        backimg.setOnClickListener(onClickBackImg -> {
            try {
                countdown.cancel();
                finish();
            } catch (Exception ex) {
                ex.printStackTrace();
                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                mCrashlytics.recordException(ex);
            }
        });


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
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Account/CRAAppLogin";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            Details.addProperty("email", Login.emailidval);
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
                            try {
                                runOnUiThread(() -> progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
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
                                        Login.otp = staticJsonObj.getJSONObject("rObj").getString("OTP");
                                        Login.otpid = staticJsonObj.getJSONObject("rObj").getString("OTPID");
                                        loginotp.setText(Login.otp);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginOTP.this);
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
                        } catch (final IOException ex) {
                            try {
                                runOnUiThread(() -> {
                                    try {
                                        progressdialog.dismiss();
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog("UserRegistration-getuserexistapi", ex.toString(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                        Toast.makeText(LoginOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                        } catch (JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(LoginOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(LoginOTP.this);
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
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(LoginOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }

    }

    public void verifyOtp() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    try {
                        if (loginotp.getText().toString().equals("") || loginotp.getText().toString().length() == 0) {
                            Toast.makeText(LoginOTP.this, getString(R.string.otpman), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = getString(R.string.uaturl) + "/app/Account/CRAAppLoginValidateOTP";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        try {
                            Details.addProperty("OTPToken", Login.otpid);
                            Details.addProperty("OTP", loginotp.getText().toString());
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
                            try {
                                runOnUiThread(() -> progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);

                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();

                                    try {
                                        sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                        mydb = new DatabaseHelper(LoginOTP.this);
                                        if (mydb.getTokendetails().getCount() != 0) {
                                            mydb.deletetokendata();
                                        }
                                        if (mydb.getUserdetails().getCount() != 0) {
                                            mydb.deleteuserdata();
                                        }
                                        mydb.inserttoken(sToken);
                                        boolean IsProfileinserted = mydb.insertuserdetails(AESCrypt.decrypt(staticJsonObj.getJSONObject("rObj").getJSONObject("cRALoginProfile").getString("firstName")), staticJsonObj.getJSONObject("rObj").getJSONObject("cRALoginProfile").getString("cRAID"), Registration.phonenumber, staticJsonObj.getJSONObject("rObj").getJSONObject("cRALoginProfile").getString("email"));
                                        if (IsProfileinserted) {
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        Intent i = new Intent(LoginOTP.this, Dashboard.class);
                                        startActivity(i);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(LoginOTP.this);
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
                                runOnUiThread(() -> {
                                    try {
                                        progressdialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                });
                            }
                        } catch (final IOException | JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(LoginOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(LoginOTP.this);
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
            Toast.makeText(LoginOTP.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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