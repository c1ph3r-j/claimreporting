package com.aki.claimreporting.termsandconditions;

import static com.aki.claimreporting.MainActivity.checkGPSStatus;
import static com.aki.claimreporting.MainActivity.isNetworkConnected;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aki.claimreporting.AESCrypt;
import com.aki.claimreporting.Dashboard;
import com.aki.claimreporting.DatabaseHelper;
import com.aki.claimreporting.LoginOTP;
import com.aki.claimreporting.MainActivity;
import com.aki.claimreporting.PermissionHandler;
import com.aki.claimreporting.R;
import com.aki.claimreporting.Registration;
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


public class TermsAndConditions extends AppCompatActivity {


    //first name
    public String firstName;
    //button
    Button button;
    //check box
    CheckBox isTermsAndConditions;

    //text view
    TextView termsAndConditionsNav;
    //shared preference
    SharedPreferences sharedPreferences;
    //progress dialog
    ProgressDialog progressdialog;
    FirebaseCrashlytics mCrashlytics;
    String[] permissions = new String[]{PERMISSION_LOCATION};
    PermissionHandler permissionManager;
    public static DatabaseHelper mydb;
    public static String sToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        getWindow().setStatusBarColor(getColor(R.color.appColor));
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            progressdialog = progressDialog();
            //initialize variables
            initializeVariables();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }

    //to initialize variables
    private void initializeVariables() {
        try {
            String methodName = Objects.requireNonNull(new Object() {
            }.getClass().getEnclosingMethod()).getName();
            button = findViewById(R.id.continueTermsAndCondition);
            isTermsAndConditions = findViewById(R.id.checkBoxTermsAndConditions);
            termsAndConditionsNav = findViewById(R.id.termsAndConditionsNav);
            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {
                    // Create and show the progress dialog
                    ProgressDialog progressDialog = new ProgressDialog(TermsAndConditions.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false); // Prevents the user from dismissing the dialog
                    progressDialog.show();

                    // Use a Handler to dismiss the dialog after 2 seconds (2000 milliseconds)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss(); // Dismiss the progress dialog
                        }
                    }, 2000);
                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialogC(permissions);
                }
            });


            button.setOnClickListener(view -> {
                try {
                    if (permissionManager.hasPermissions(permissions)) {
                        if (isTermsAndConditions.isChecked()) {
                            loginUser();
                        } else {
                            MainActivity.alertTheUser(getString(R.string.alert), getString(R.string.terms_and_condition_validation), this);
                        }
                    } else {
                        permissionManager.requestPermissions(permissions);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }


            });
            termsAndConditionsNav.setOnClickListener(view -> {
                if (!progressdialog.isShowing()) {
                    progressdialog.show();
                    Intent intent = new Intent(TermsAndConditions.this, TermsAndConditionsWebView.class);
                    startActivity(intent);
                    progressdialog.dismiss();
                }

            });

            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    MainActivity.alertWarning(getString(R.string.alert), "Are you sure you want to exist the App.", TermsAndConditions.this)
                            .setPositiveButton("Yes", (dialogInterface, i) -> finishAffinity())
                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).show();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //progress dialog
    private ProgressDialog progressDialog() {
        try {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            return progressDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return new ProgressDialog(this);
        }
    }

    //mobile login API
    public void loginUser() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected(this)) {
                if (checkGPSStatus(this)) {
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
                                        sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                        mydb = new DatabaseHelper(TermsAndConditions.this);
                                        if (mydb.getTokendetails().getCount() != 0) {
                                            mydb.deletetokendata();
                                        }
                                        if (mydb.getUserdetails().getCount() != 0) {
                                            mydb.deleteuserdata();
                                        }
                                        mydb.inserttoken(sToken);
                                        boolean IsProfileinserted = mydb.insertuserdetails("", staticJsonObj.getJSONObject("rObj").optString("cRAID",""), "","");
                                        if (IsProfileinserted) {
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        Intent i = new Intent(TermsAndConditions.this, Dashboard.class);
                                        startActivity(i);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(TermsAndConditions.this);
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
                                Toast.makeText(TermsAndConditions.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (JSONException ex) {
                            runOnUiThread(() -> {
                                try {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(TermsAndConditions.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(TermsAndConditions.this);
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
            Toast.makeText(TermsAndConditions.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        } finally {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}