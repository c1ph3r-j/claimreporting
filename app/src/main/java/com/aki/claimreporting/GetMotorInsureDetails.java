package com.aki.claimreporting;

import static com.aki.claimreporting.ClaimLocation.progressdialog;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.ScanQrForMotorInsure.QrCode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

public class GetMotorInsureDetails extends AppCompatActivity {
    public static JSONObject motorInsuranceDetails;
    EditText chassisNoField;
    Button reScan, verify;
    FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_insure_details);
        preventSizeChange(this, getSupportActionBar());

        mCrashlytics = FirebaseCrashlytics.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Motor Insurance");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            try {
                reScan = findViewById(R.id.ReScanBtn);
                verify = findViewById(R.id.VerifyBtn);
                chassisNoField = findViewById(R.id.ChassisNoField);

                reScan.setOnClickListener(onClickReScan -> {
                    startActivity(new Intent(GetMotorInsureDetails.this, ScanQrForMotorInsure.class));
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                verify.setOnClickListener(onClickVerify -> {
                    try {
                        if (chassisNoField.getText().toString().isEmpty()) {
                            new AlertDialog.Builder(GetMotorInsureDetails.this)
                                    .setTitle("Chassis/Registration Number!")
                                    .setMessage("Please Enter a Valid Chassis/Registration Number to Continue.")
                                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                                    })
                                    .show();
                        } else {
                            getCertificateInformation();
                        }
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
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    void getCertificateInformation() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    try {
                        try {
                            DatabaseHelper mydb = new DatabaseHelper(GetMotorInsureDetails.this);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        MainActivity.stokenval = curseattachtoken.getString(1);
                                    }
                                }
                            }

                            mydb.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/MotorVerification/MotorCertificateVerification";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("CertificateNumber", "");
                        Details.addProperty("ReasonID", "1");
                        Details.addProperty("ReasonForValidation", "Verification Using CRA App");
                        Details.addProperty("VehicleRegORChassNumber", chassisNoField.getText().toString());
                        SharedPreferences sharedpreferences = getSharedPreferences("ShareValPref", Context.MODE_PRIVATE);
                        Details.addProperty("PrintCode", sharedpreferences.getString(QrCode, ""));
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
                            runOnUiThread(() -> progressdialog = ProgressDialog.show(GetMotorInsureDetails.this, "Loading", "Fetching the information. Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            String staticRes = Objects.requireNonNull(staticResponse.body()).string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {

                                motorInsuranceDetails = staticJsonObj.getJSONObject("rObj").getJSONObject("motorInsuranceCertificate");

                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    startActivity(new Intent(GetMotorInsureDetails.this, MotorInsureResult.class));
                                });

                            }
                            else if(staticJsonObj.getInt("rcode") == -2) {
                                try {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                    });
                                    Intent intent = new Intent(this, InvalidResultView.class);
                                    intent.putExtra("type", "motor");
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(() -> progressdialog.dismiss());
                            }
                            else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(GetMotorInsureDetails.this);
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
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            runOnUiThread(() -> progressdialog.dismiss());
                        } catch (JSONException ex) {
                            runOnUiThread(() -> progressdialog.dismiss());
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                });
                thread.start();
            } else {
                Toast.makeText(GetMotorInsureDetails.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            runOnUiThread(() -> progressdialog.dismiss());
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}