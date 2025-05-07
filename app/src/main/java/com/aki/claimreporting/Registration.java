package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.CheckBox;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Registration extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public static String fbstokenval;
    public static String otp;
    public static String otpid;
    public static String fullname;
    public static String emailid;
    public static String reqidval = "";
    public static String phonenumber;
    public static String referalcode;
    public ProgressDialog progressdialog;
    public ArrayList<String> myPrefixlist;
    EditText lastnametxt, emailidtxt, phnumtxt;
    CheckBox termsagree;
    ImageView nextbtn, closeBtn;
    Activity activity;
    TextView termsandconditionsclick;
    TextView privacyconditionsclick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            activity = this;
            setContentView(R.layout.activity_registration);
            preventSizeChange(this, getSupportActionBar());
            init();
            getWindow().setStatusBarColor(getColor(R.color.lightdarkcolorgrey));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            lastnametxt = findViewById(R.id.name);
            emailidtxt = findViewById(R.id.emailid);
            phnumtxt = findViewById(R.id.phonenumber);
            closeBtn = findViewById(R.id.closeicon);
            nextbtn = findViewById(R.id.next);
            termsagree = findViewById(R.id.checkbox_registerpage);
            termsandconditionsclick = findViewById(R.id.termsconditionclick);

            try {
                termsandconditionsclick.setOnClickListener(onClickTermsAndConditions -> {
                    Intent terms = new Intent(Registration.this, TermsConditions.class);
                    startActivity(terms);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            privacyconditionsclick = findViewById(R.id.privacyclick);

            try {
                privacyconditionsclick.setOnClickListener(onClickTermsAndConditions -> {
                    Intent terms = new Intent(Registration.this, TermsConditions.class);
                    startActivity(terms);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                nextbtn.setOnClickListener(onClickAgreeTerms -> {
                    if (termsagree.isChecked()) {
                        try {
                            registeruser();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    } else {
                        try {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Registration.this);
                            alert.setMessage(getString(R.string.agree_terms_and_conditions));
                            alert.setCancelable(false);
                            alert.setNegativeButton("OK", (dialogInterface, i) ->
                                    dialogInterface.cancel());
                            alert.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }

                });

                try {
                    closeBtn.setOnClickListener(onClickClose -> finish());
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
            try {
                if (isNetworkConnected()) {
                    getNumberKenyans();
                }
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

    public void getNumberKenyans() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    MainActivity.appurl = getString(R.string.uaturl) + "/app/MasterData/GetAllTelephoneKenya";
                    // OkHttpClient client = new OkHttpClient();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = RequestBody.create(mediaType, "");
                    Request request = new Request.Builder()
                            .url(MainActivity.appurl)
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        try {
                            runOnUiThread(() -> {
                                progressdialog = ProgressDialog.show(Registration.this, "Loading", "Please wait...", true);
                                // progressdialog.show(activity, "Loading", "Please wait...", true);
                            });
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

                            try {
                                myPrefixlist = new ArrayList<>();
                                JSONArray versionapiver = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllTelephoneKenya");
                                for (int i = 0; i < versionapiver.length(); i++) {
                                    JSONObject versionobj = versionapiver.getJSONObject(i);
                                    myPrefixlist.add(versionobj.getString("prefix"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
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
                                        AlertDialog.Builder alert = new AlertDialog.Builder(Registration.this);
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
                    } catch (final IOException e) {
                        try {
                            runOnUiThread(() -> {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                progressdialog.dismiss();
                                Toast.makeText(Registration.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        runOnUiThread(() -> progressdialog.dismiss());
                    }
                });
                thread.start();
            }
        } catch (Exception ex) {
            progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

    public void registeruser() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    String emailInput = emailidtxt.getText().toString().trim().toLowerCase();
                    try {
                        if (lastnametxt.getText().toString().equals("") || lastnametxt.getText().toString().length() == 0) {
                            Toast.makeText(Registration.this, getString(R.string.nameman), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (emailidtxt.getText().toString().equals("") || emailidtxt.getText().toString().length() == 0) {
                            Toast.makeText(Registration.this, getString(R.string.emailman), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!emailInput.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                            Toast.makeText(Registration.this, "Invalid Email ID", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (phnumtxt.getText().toString().equals("") || phnumtxt.getText().toString().length() == 0) {
                            Toast.makeText(Registration.this, getString(R.string.phonenumman), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (phnumtxt.getText().toString().length() != 9) {
                            Toast.makeText(Registration.this, getString(R.string.validphoneno), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!(myPrefixlist.contains(phnumtxt.getText().toString().substring(0, 2)) || myPrefixlist.contains(phnumtxt.getText().toString().substring(0, 3)))) {
                            Toast.makeText(Registration.this, getString(R.string.validnetworkphoneno), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    mydb = new DatabaseHelper(Registration.this);
                    if (mydb.getfirebaseTokendetails().getCount() != 0) {
                        Cursor firebaseToken = mydb.getfirebaseTokendetails();
                        if (firebaseToken.getCount() >= 1) {
                            while (firebaseToken.moveToNext()) {
                                fbstokenval = firebaseToken.getString(1);
                            }
                        }
                    }
                    try {
                        Thread thread = new Thread(() -> {
                            /*MainActivity.appurl = getString(R.string.uaturl) + "/app/Account/EmailRegistrtaion";*/
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
                                try {
                                    String phoneNo = phnumtxt.getText().toString();
                                    String email = emailidtxt.getText().toString();
                                    String firstName = lastnametxt.getText().toString();
                                    Details.addProperty("phoneNo", phoneNo);
                                    Details.addProperty("email", emailInput);
                                    Details.addProperty("firstName", firstName);
                                    Details.addProperty("referralCode", "");
                                    //Device unique code
                                    String imeiInput = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                    Details.addProperty("uniqueID",imeiInput);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                String insertString = Details.toString();

                                String url = getString(R.string.uaturl) + "/app/Account/CRAAppRegistrationSendOTP";


                                String mobileParameter = MainActivity.InsertMobileParameters();

                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(url)
                                        .header("MobileParameter", mobileParameter)
                                        .post(body)
                                        .build();
                                Response staticResponse;

                                try {
                                    runOnUiThread(() ->
                                            progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true));
                                    staticResponse = client.newCall(request).execute();
                                    assert staticResponse.body() != null;
                                    String staticRes = staticResponse.body().string();
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);

                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            try {
                                                otp = staticJsonObj.getJSONObject("rObj").getString("OTP");
                                                otpid = staticJsonObj.getJSONObject("rObj").getString("TokenID");
                                                fullname = lastnametxt.getText().toString();
                                                phonenumber = phnumtxt.getText().toString();
                                                emailid = emailidtxt.getText().toString();

                                                Intent i = new Intent(Registration.this, RegistrationOTP.class);
                                                Intent intent = getIntent();
                                                boolean signUpPreference = intent.getBooleanExtra("ImageDeclaration",false);
                                                boolean createGrievance = intent.getBooleanExtra("grievance",false);
                                                boolean addVehicle = getIntent().getBooleanExtra("add-vehicle",false);
                                                i.putExtra("grievance",createGrievance);
                                                i.putExtra("add-vehicle",addVehicle);
                                                i.putExtra("ImageDeclaration",signUpPreference);
                                                startActivity(i);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(Registration.this);
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
                                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                                mCrashlytics.recordException(ex);
                                                Toast.makeText(Registration.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                                }
                            } catch (JSONException ex) {
                                try {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                        Toast.makeText(Registration.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Registration.this);
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
            try {
                ex.printStackTrace();
                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                mCrashlytics.recordException(ex);
                Toast.makeText(Registration.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }
    }
}