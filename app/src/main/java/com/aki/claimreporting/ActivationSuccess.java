package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ActivationSuccess extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String reqidval;
    public static int qrcodelay;
    public static FirebaseCrashlytics mCrashlytics;
    public ProgressDialog progressdialog;
    public String startdate, enddate;
    public Date datefrompolicy, datetopolicy;
    TextView btnbackhome;
    TextView activateid;
    Button btnaddvehi;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_success);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        preventSizeChange(this, getSupportActionBar());
        ColorDrawable colorDrawable
                = new ColorDrawable(getColor(R.color.purple_500));

        // Set BackgroundDrawable
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(colorDrawable);

        getSupportActionBar().setTitle("Activation Success");
        activity = this;
        mCrashlytics = FirebaseCrashlytics.getInstance();
        mydb = new DatabaseHelper(ActivationSuccess.this);
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            activateid = (TextView) findViewById(R.id.txtactivateid);
            activateid.setText(" Reference ID : " + CertificateActivation.activationidref);
            btnaddvehi = (Button) findViewById(R.id.Btnuactivataddveh);
            try {
                btnaddvehi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addvehicleinfo();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            btnbackhome = (TextView) findViewById(R.id.Btnactivatebacktohome);
            try {
                btnbackhome.setText(R.string.Back_to_home);
                btnbackhome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent redirect = new Intent(ActivationSuccess.this, Dashboard.class);
                        startActivity(redirect);
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

    public void addvehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    try {
                        mydb = new DatabaseHelper(ActivationSuccess.this);
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

                    try {

                        Thread thread = new Thread(new Runnable() {

                            public void run() {
//                            SharedPreferences certPref = getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = certPref.edit();
//                            editor.putString(certificatenumaddriver, certificateval);
//                            editor.putString(regnumadddriver, regno);
//                            editor.commit();
                                SharedPreferences certificatenum = getSharedPreferences("CertificateNum", Context.MODE_PRIVATE);
                                SharedPreferences.Editor certificatenumeeditor = certificatenum.edit();
                                certificatenumeeditor.putString(CertificateActivation.CertNum, CertificateActivation.certificatenumparam);
                                certificatenumeeditor.commit();

                                CertificateActivation.postURL = getString(R.string.uaturl) + "/app/Vehicle/AddVehicle";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");
                                String dtStart = CertificateActivation.policyBeginDateparam;
                                String dtend = CertificateActivation.expirydateparam;
                                SimpleDateFormat formatstart = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                SimpleDateFormat formatend = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                try {
                                    datefrompolicy = formatstart.parse(dtStart);
                                    datetopolicy = formatend.parse(dtend);
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
                                    startdate = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
                                    enddate = formatter.format(Date.parse(String.valueOf(datetopolicy)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                JsonObject Details = new JsonObject();
                                // Details.addProperty("imageAttachmentID",vechieattachid);
                                // Details.addProperty("ownVehicleID",OwnVehicleNot.ownVehicleval);
                                //   Details.addProperty("authorizedID",OwnVehicleNot.authorizedval);
                                Details.addProperty("certificateNo", CertificateActivation.certificatenumparam);
                                Details.addProperty("policyNo", CertificateActivation.policyNoparam);
                                if (CertificateActivation.certificateTypeparam == "null") {
                                    Details.addProperty("certificateType", "");
                                } else {
                                    Details.addProperty("certificateType", CertificateActivation.certificateTypeparam);
                                }
                                Details.addProperty("coverageType", CertificateActivation.coverageTypeparam);
                                Details.addProperty("policyBeginDate", startdate);
                                Details.addProperty("policyEndDate", enddate);
                                Details.addProperty("registrationNo", CertificateActivation.regnumparam);
                                Details.addProperty("vINNumber", CertificateActivation.vINNumberparam);
                                Details.addProperty("make", CertificateActivation.makeparam);
                                Details.addProperty("model", CertificateActivation.modelparam);
                                Details.addProperty("yearOfMfg", CertificateActivation.yearOfMfgparam);
                                Details.addProperty("insurerID", CertificateActivation.insurerIDparam);
                                Details.addProperty("insuredName", CertificateActivation.insuredNameparam);
                                Details.addProperty("insuredNationalID", "");
                                Details.addProperty("insuredPIN", CertificateActivation.insuredPINparam);
                                Details.addProperty("insurerName", "");
                                Details.addProperty("sumInsured", CertificateActivation.sumInsuredparam);
                                Details.addProperty("tonnage", CertificateActivation.tonnageparam);
                                Details.addProperty("passengersCount", CertificateActivation.passengersCountparam);
                                Details.addProperty("insuredMailId", CertificateActivation.insuredMailIdparam);
                                Details.addProperty("insuredPhoneNo", CertificateActivation.insuredPhoneNoparam);
                                Details.addProperty("insurerName", CertificateActivation.insurerNameparam);
                                Details.addProperty("engineNumber", CertificateActivation.engineNumberparam);
                                Details.addProperty("speedoMeterReading", "");
                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(CertificateActivation.postURL)
                                        .header("Authorization", "Bearer " + MainActivity.stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .post(body)
                                        .build();
                                Response staticResponse = null;

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(ActivationSuccess.this, "Loading", "Please wait...", true);
                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
                                        }
                                    });
                                    staticResponse = client.newCall(request).execute();
                                    int statuscode = staticResponse.code();
                                    if (statuscode == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(ActivationSuccess.this);
                                                return;
                                            }
                                        });
                                    } else {
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                }
                                            });
                                            qrcodelay = 0;
                                            SharedPreferences certPref = getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = certPref.edit();
                                            editor.putString(CertificateActivation.certificatenumaddriver, CertificateActivation.certificatenumparam);
                                            editor.putString(CertificateActivation.regnumadddriver, CertificateActivation.regnumparam);
                                            editor.commit();
//                                        SharedPreferences Vechnewpref = getSharedPreferences("VehicleNewID", Context.MODE_PRIVATE);
//                                        SharedPreferences.Editor vecheprefednew = Vechnewpref.edit();
//                                        vecheprefednew.putString(MainActivity.Vechidshow,staticJsonObj.getJSONObject("rObj").getString("vehicleRefID"));
//                                        vecheprefednew.commit();
//                                        SharedPreferences VechPreferencenew = getSharedPreferences("GenerateVehiclePDF", Context.MODE_PRIVATE);
//                                        SharedPreferences.Editor vecheditornew = VechPreferencenew.edit();
//                                        vecheditornew.putString(MainActivity.VechPDFID,staticJsonObj.getJSONObject("rObj").getString("vehicleID"));
//                                        vecheditornew.commit();

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog dialog = new AlertDialog.Builder(ActivationSuccess.this)
                                                            .setTitle(getString(R.string.vehicle_added_successful))
                                                            .setMessage(getString(R.string.your_vehicle_added_successfully))
                                                            .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    Intent redirect = new Intent(ActivationSuccess.this, Dashboard.class);
                                                                    startActivity(redirect);
                                                                }
                                                            }).show();
                                                }
                                            });


//                                        Intent redirect = new Intent(ActivationSuccess.this, VehicleActivationSuccess.class);
//                                        startActivity(redirect);
                                        } else if (staticJsonObj.getInt("rcode") == 401) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                    unauthorize(ActivationSuccess.this);
                                                }
                                            });
                                        } else {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText = null;
                                                String trnId = null;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    trnId = staticJsonObj.getString("trnID");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ActivationSuccess.this);
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
                                        }
                                    }
                                } catch (final IOException | JSONException e) {
                                    runOnUiThread(progressdialog::dismiss);
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(ActivationSuccess.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } //                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
                                //                                startActivity(redirect);

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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActivationSuccess.this);
                        dialog.setMessage(getString(R.string.location_not_enabled));
                        dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

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
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(ActivationSuccess.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    /*public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActivationSuccess.this);
            dialog.setMessage(getString(R.string.unauthMessge));
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(ActivationSuccess.this);
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
                    Intent login = new Intent(ActivationSuccess.this, Dashboard.class);
                    startActivity(login);
                }
            });
            dialog.setCancelable(false);
            android.app.AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        try {
            Intent redirect = new Intent(ActivationSuccess.this, Dashboard.class);
            startActivity(redirect);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent login = new Intent(ActivationSuccess.this, Dashboard.class);
        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
        startActivity(login);
        //finishAffinity(); // or finish();
    }
}