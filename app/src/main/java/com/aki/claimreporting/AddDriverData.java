package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddDriverData extends AppCompatActivity {

    public static String driveridapi, crauseridapi;
    public static ProgressDialog progressDialog;
    public boolean searchcraid;
    String baseUrl;
    String date1;
    Switch selfSwitch;
    String stokenval;
    RadioGroup driversearch;
    DatabaseHelper mydb;
    TextView pickDateFromSelf, pickDateTillSelf, pickDateFromNew, pickDateTillNew, driversearchtxt;
    EditText driveraddcraidedit, driveraddphnedit, drivingLicenseSelf, driverNumberInAddDriver, driverNameNew, drivingLicenseNew, nationalIDNew;
    EditText phoneNo, existphone, existcraid;
    LinearLayout self, newDriver, phoneNumberVerify;
    LinearLayout submitButton, verifyButton, addDriverBtn;
    LinearLayout driverNameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver_data);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setTitle("Add Driver Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set BackgroundDrawable

        init();
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            try {
                //baseUrl
                baseUrl = getString(R.string.uaturl);

                //phoneNo=findViewById(R.id.phoneNumberInAddDriver);
                driverNumberInAddDriver = findViewById(R.id.driverNumberInAddDriver);
                drivingLicenseSelf = findViewById(R.id.dlNoSelfInAddDriver);
                driverNameLayout = findViewById(R.id.driverNameLayout);
                driverNameNew = findViewById(R.id.driverNameNewInAddDriver);
                drivingLicenseNew = findViewById(R.id.dlNoNewInAddDriver);
                nationalIDNew = findViewById(R.id.nationalIDInAddDriver);
                selfSwitch = findViewById(R.id.switchInAddDriverData);
                self = findViewById(R.id.selfDriver);
                driversearch = findViewById(R.id.radioGroupForAddDriver);
                newDriver = findViewById(R.id.newOrExistingDriver);
                phoneNumberVerify = findViewById(R.id.mobileVerify);
                submitButton = findViewById(R.id.btnSubmitInAddDriverData);
                verifyButton = findViewById(R.id.btnVerifyInAddDriverData);
                pickDateFromSelf = findViewById(R.id.validityFromSelf);
                pickDateTillSelf = findViewById(R.id.validityTillSelf);
                pickDateFromNew = findViewById(R.id.validityFromNew);
                pickDateTillNew = findViewById(R.id.validityTillNew);
                addDriverBtn = findViewById(R.id.addDriverBtn);

                driversearchtxt = findViewById(R.id.txtdriveralready);

                driveraddphnedit = findViewById(R.id.editalreadycraphn);
                driveraddcraidedit = findViewById(R.id.editalreadycraid);
                driveraddphnedit.setVisibility(View.INVISIBLE);
                driveraddcraidedit.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);

            }

            try {
                //Making required Layout's to be Visible and Gone
                self.setVisibility(View.GONE);
                newDriver.setVisibility(View.GONE);
                driverNameLayout.setVisibility(View.GONE);
                phoneNumberVerify.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.GONE);
                verifyButton.setVisibility(View.VISIBLE);
                addDriverBtn.setVisibility(View.GONE);
                verifyMobile();
                searchcraid = false;
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                driversearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (i == R.id.radioaddcraid) {
                            searchcraid = true;
                            driversearchtxt.setText(R.string.enter_the_CRA_id_of_the_driver);
                            driveraddphnedit.setVisibility(View.GONE);
                            driveraddcraidedit.setVisibility(View.VISIBLE);
                        } else if (i == R.id.radioaddphnno) {
                            searchcraid = false;
                            driversearchtxt.setText(R.string.enter_the_phone_number_of_the_driver);
                            driveraddphnedit.setVisibility(View.VISIBLE);
                            driveraddcraidedit.setVisibility(View.GONE);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                //changing the layout based on Switch's On check changed.
                selfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            self.setVisibility(View.VISIBLE);
                            driverNameLayout.setVisibility(View.VISIBLE);
                            newDriver.setVisibility(View.GONE);
                            phoneNumberVerify.setVisibility(View.GONE);
                            submitButton.setVisibility(View.VISIBLE);
                            verifyButton.setVisibility(View.GONE);
                            addDriverBtn.setVisibility(View.GONE);
                            submitData();
                        } else {
                            self.setVisibility(View.GONE);
                            newDriver.setVisibility(View.GONE);
                            driverNameLayout.setVisibility(View.GONE);
                            phoneNumberVerify.setVisibility(View.VISIBLE);
                            submitButton.setVisibility(View.GONE);
                            verifyButton.setVisibility(View.VISIBLE);
                            addDriverBtn.setVisibility(View.GONE);
                            verifyMobile();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                mydb = new DatabaseHelper(AddDriverData.this);
                if (mydb.getTokendetails().getCount() != 0) {
                    Cursor curseattachtoken = mydb.getTokendetails();
                    int counttoken = curseattachtoken.getCount();
                    if (counttoken >= 1) {
                        while (curseattachtoken.moveToNext()) {
                            stokenval = curseattachtoken.getString(1);
                            System.out.println(stokenval);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                // Adding the Date Values for Validity for License for both Self and New User Data.
                pickDateFromSelf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePickerDialog(pickDateFromSelf, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                pickDateTillSelf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePickerDialog(pickDateTillSelf, 1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                pickDateFromNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePickerDialog(pickDateFromNew, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                pickDateTillNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePickerDialog(pickDateTillNew, 1);
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

    /*public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddDriverData.this);
            dialog.setMessage("Your session have been expired. Please login again to continue");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(AddDriverData.this);
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
                    Intent login = new Intent(AddDriverData.this, Dashboard.class);
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

    private void submitData() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {
                    if (checkGPSStatus()) {
                        //API url Required
                        try {
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (drivingLicenseSelf.length() > 0 && pickDateFromSelf.length() > 0 && pickDateTillSelf.length() > 0) {

                                        String uniqueidval = MainActivity.UniqueID;

                                        String deviceData = MainActivity.InsertMobileParameters();
                                        String postUrl = baseUrl + "/app/Driver/AddUserSelfDriver";
                                        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(120, TimeUnit.SECONDS)
                                                .writeTimeout(120, TimeUnit.SECONDS)
                                                .readTimeout(120, TimeUnit.SECONDS)
                                                .build();
                                        final MediaType JSON
                                                = MediaType.parse("application/json; charset=utf-8");
                                        JsonObject details = new JsonObject();
                                        try {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog = ProgressDialog.show(AddDriverData.this, "Loading", "Please wait...", true);
                                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                                }
                                            });
                                            System.out.println("Kenya" + "\t" + drivingLicenseSelf.getText().toString() +
                                                    "\t" + pickDateFromSelf.getText().toString() + "\t" +
                                                    pickDateTillSelf.getText().toString() + "\t" + "123456");
                                            details.addProperty("driverName", driverNameNew.getText().toString().trim());
                                            details.addProperty("driverDLCountry", "Kenya");
                                            details.addProperty("driverDLNum", drivingLicenseSelf.getText().toString());
                                            details.addProperty("driverDLValidFrom", pickDateFromSelf.getText().toString());
                                            details.addProperty("driverDLValidTill", pickDateTillSelf.getText().toString());
                                            details.addProperty("AttachmentID", "123456");
                                            String insertString = details.toString();


                                            RequestBody body = RequestBody.create(JSON, insertString);
                                            Request request = new Request.Builder()
                                                    .url(postUrl)
                                                    .header("Authorization", "Bearer " + stokenval)
                                                    .header("mobileParameter", deviceData)
                                                    .post(body)
                                                    .build();

                                            Response staticResponse = null;
                                            try {
                                                staticResponse = client.newCall(request).execute();
                                                int statuscode = staticResponse.code();
                                                if (statuscode == 401) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            unauthorize(AddDriverData.this);
                                                        }
                                                    });
                                                } else {
                                                    String staticRes = staticResponse.body().string();
                                                    Log.i(null, staticRes);
                                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                                    if (staticJsonObj.getInt("rcode") == 1) {
                                                        try {
                                                            JSONObject rObj = staticJsonObj.getJSONObject("rObj");

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                            mCrashlytics.recordException(e);
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                runOnUiThread(() -> {
                                                                    if(progressDialog.isShowing()){
                                                                        progressDialog.dismiss();
                                                                    }
                                                                });
                                                                Intent i = new Intent(AddDriverData.this, DriverMapping.class);
                                                                startActivity(i);
                                                            }
                                                        });

                                                    } else if (staticJsonObj.getInt("rcode") == 2) {
                                                        runOnUiThread(() -> {
                                                            if(progressDialog.isShowing()){
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        runOnUiThread(() -> {
                                                            String errorText;
                                                            try {
                                                                errorText = index.getString("errorText");
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                                alert.setCancelable(false);
                                                                alert.setMessage(errorText);
                                                                alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                                alert.show();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(() -> {
                                                            if(progressDialog.isShowing()){
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        String errorText = index.getString("errorText");
                                                        runOnUiThread(() -> {
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                            alert.setCancelable(false);
                                                            alert.setMessage(errorText);
                                                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                            alert.show();
                                                        });

                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                runOnUiThread(() -> {
                                                    if(progressDialog.isShowing()){
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            runOnUiThread(() -> {
                                                if(progressDialog.isShowing()){
                                                    progressDialog.dismiss();
                                                }
                                            });
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }


                                    } else {
                                        runOnUiThread(() -> {
                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                        });
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                alert.setMessage("Please Enter Your Details before clicking on Submit");
                                                alert.setCancelable(false);
                                                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alert.show();
                                            }
                                        });
                                    }

                                }
                            });
                            t.start();
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            });
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                    } else {
                        try {
                            runOnUiThread(() -> {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            });
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddDriverData.this);
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
                            runOnUiThread(() -> {
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            });
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }


                } else {
                    runOnUiThread(() -> {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    });
                    Toast.makeText(AddDriverData.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verifyMobile() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (checkGPSStatus()) {
                        try {
                            existphone = findViewById(R.id.editalreadycraphn);
                            existcraid = findViewById(R.id.editalreadycraid);
                            if (searchcraid == true) {
                                try {
                                    if (existcraid.getText().toString() == "" || existcraid.getText().toString() == null || existcraid.getText().toString().length() == 0) {
                                        Toast.makeText(AddDriverData.this, getString(R.string.existdrivcraid), Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (existcraid.getText().toString().length() > 10) {
                                        Toast.makeText(AddDriverData.this, getString(R.string.notavalidcraid), Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (existcraid.getText().toString().length() < 6) {
                                        Toast.makeText(AddDriverData.this, getString(R.string.notavalidcraid), Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    driveridapi = "";
                                    crauseridapi = existcraid.getText().toString();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            } else {
                                try {
                                    if (existphone.getText().toString() == "" || existphone.getText().toString() == null || existphone.getText().toString().length() == 0) {
                                        Toast.makeText(AddDriverData.this, getString(R.string.existdrivphnno), Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (existphone.getText().toString().length() != 9) {
                                        Toast.makeText(AddDriverData.this, getString(R.string.validphoneno), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    driveridapi = existphone.getText().toString();
                                    crauseridapi = "";

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }

//                            try {
//                                existphone = (EditText) findViewById(R.id.editalreadycraphn);
//                                if (existphone.getText().toString() == "" || existphone.getText().toString() == null || existphone.getText().toString().length() == 0) {
//                                    Toast.makeText(AddDriverData.this, getString(R.string.existdrivphnno), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                else if (existphone.getText().toString().length() > 9) {
//                                    Toast.makeText(AddDriverData.this, getString(R.string.notavalidcraid), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                else if (existphone.getText().toString().length() < 6) {
//                                    Toast.makeText(AddDriverData.this, getString(R.string.notavalidcraid), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                                else  if (existphone.getText().toString().length() != 9 ) {
//                                    Toast.makeText(AddDriverData.this, getString(R.string.existdrivvalidphoneno), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                MainActivity.MobileErrorLog( e.getStackTrace()[0].getFileName() + " - " + methodName,  e.getMessage(), e.toString());
//                                mCrashlytics.recordException(e);
//                            }
                            //API url Required
                            try {
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (existphone.length() != 0 || searchcraid) {

                                            // System.out.println(phoneNo.getText().toString());
                                            String uniqueidval = MainActivity.UniqueID;
                                            String deviceData = MainActivity.InsertMobileParameters();
                                            String postUrl = baseUrl + "/app/Driver/AddUserExistingDriver";
                                            OkHttpClient client = new OkHttpClient.Builder()
                                                    .connectTimeout(120, TimeUnit.SECONDS)
                                                    .writeTimeout(120, TimeUnit.SECONDS)
                                                    .readTimeout(120, TimeUnit.SECONDS)
                                                    .build();
                                            final MediaType JSON
                                                    = MediaType.parse("application/json; charset=utf-8");
                                            JsonObject details = new JsonObject();
                                            details.addProperty("driverDLNum", "");
                                            details.addProperty("driverPhoneNo", driveridapi);
                                            details.addProperty("cRAMemberID", crauseridapi);
//                                            details.addProperty("certificateNo", "B10006866"); //Dummy certnum for checking
                                            details.addProperty("certificateNo", AddVehicle.certificatenumaddriver);
                                            String insertString = details.toString();

                                            RequestBody body = RequestBody.create(JSON, insertString);
                                            Request request = new Request.Builder()
                                                    .url(postUrl)
                                                    .header("Authorization", "Bearer " + stokenval)
                                                    .header("mobileParameter", deviceData)
                                                    .post(body)
                                                    .build();

                                            Response staticResponse = null;
                                            try {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog = ProgressDialog.show(AddDriverData.this, "Loading", "Please wait...", true);
                                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                                    }
                                                });

                                                staticResponse = client.newCall(request).execute();
                                                int statuscode = staticResponse.code();
                                                if (statuscode == 401) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();
                                                            unauthorize(AddDriverData.this);
                                                        }
                                                    });
                                                } else {
                                                    String staticRes = staticResponse.body().string();
                                                    Log.i(null, staticRes);
                                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                                    if (staticJsonObj.getInt("rcode") == 1) {

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    progressDialog.dismiss();
                                                                    JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                                                    Toast.makeText(AddDriverData.this, "Existing Driver Found.", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });

                                                    } else if (staticJsonObj.getInt("rcode") == 2) {
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        String errorText = index.getString("errorText");
                                                        String trnId = staticJsonObj.getString("trnID");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                                alert.setCancelable(false);
                                                                alert.setMessage("Bima Yangu does not have a driver profile with the provided phone number. Please create a new one with the following information.");
                                                                alert.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();

                                                                        self.setVisibility(View.GONE);
                                                                        newDriver.setVisibility(View.VISIBLE);
                                                                        driverNameLayout.setVisibility(View.VISIBLE);
                                                                        phoneNumberVerify.setVisibility(View.GONE);
                                                                        driverNumberInAddDriver.setText(driveridapi);
                                                                        submitButton.setVisibility(View.GONE);
                                                                        verifyButton.setVisibility(View.GONE);
                                                                        addDriverBtn.setVisibility(View.VISIBLE);
                                                                        addNewDriver();
                                                                    }
                                                                });
                                                                alert.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                alert.show();
                                                            }
                                                        });

                                                    } else if (staticJsonObj.getInt("rcode") == 3) {
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        String errorText = index.getString("errorText");
                                                        String trnId = staticJsonObj.getString("trnID");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                                alert.setCancelable(false);
                                                                alert.setMessage(errorText + "?");
                                                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        selfSwitch.setChecked(true);
                                                                        phoneNo.setText("");
                                                                        dialog.dismiss();
                                                                        self.setVisibility(View.VISIBLE);
                                                                        driverNameLayout.setVisibility(View.VISIBLE);
                                                                        newDriver.setVisibility(View.GONE);
                                                                        phoneNumberVerify.setVisibility(View.GONE);
                                                                        submitButton.setVisibility(View.VISIBLE);
                                                                        verifyButton.setVisibility(View.GONE);
                                                                        addDriverBtn.setVisibility(View.GONE);
                                                                        submitData();
                                                                    }
                                                                });
                                                                alert.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                alert.show();
                                                            }
                                                        });
                                                    } else {
                                                        runOnUiThread(progressDialog::dismiss);
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        String errorText = index.getString("errorText");
                                                        String trnId = staticJsonObj.getString("trnID");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                                alert.setCancelable(false);
                                                                alert.setMessage(errorText);
                                                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                alert.show();
                                                            }
                                                        });

                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                        alert.setMessage("Please Enter Your Mobile Number or CRA ID First");
                                                        alert.setCancelable(false);
                                                        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                        alert.show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                        mCrashlytics.recordException(e);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                t.start();
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

                    } else {
                        try {
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddDriverData.this);
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
                    Toast.makeText(AddDriverData.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void addNewDriver() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        addDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (checkGPSStatus()) {

                        //API url Required
                        try {
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (driverNameNew.length() > 0 && drivingLicenseNew.length() > 0
                                            && pickDateFromNew.length() > 0 && pickDateTillNew.length() > 0
                                            && nationalIDNew.length() > 0) {

                                        String uniqueidval = MainActivity.UniqueID;
                                        String deviceData = MainActivity.InsertMobileParameters();
                                        String postUrl = baseUrl + "/app/Driver/AddUserDriver";
                                        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(120, TimeUnit.SECONDS)
                                                .writeTimeout(120, TimeUnit.SECONDS)
                                                .readTimeout(120, TimeUnit.SECONDS)
                                                .build();
                                        final MediaType JSON
                                                = MediaType.parse("application/json; charset=utf-8");
                                        JsonObject details = new JsonObject();
                                        try {
                                            details.addProperty("driverName", driverNameNew.getText().toString().trim());
                                            details.addProperty("driverDLCountry", "Kenya");
                                            details.addProperty("driverDLNum", drivingLicenseNew.getText().toString().trim());
                                            details.addProperty("driverDLValidFrom", pickDateFromNew.getText().toString());
                                            details.addProperty("driverEmail", Login.emailidval);
                                            details.addProperty("driverDLValidTill", pickDateTillNew.getText().toString());
                                            details.addProperty("driverPhoneNo", driverNumberInAddDriver.getText().toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        String insertString = details.toString();

                                        RequestBody body = RequestBody.create(JSON, insertString);
                                        Request request = new Request.Builder()
                                                .url(postUrl)
                                                .header("Authorization", "Bearer " + stokenval)
                                                .header("mobileParameter", deviceData)
                                                .post(body)
                                                .build();

                                        Response staticResponse = null;
                                        try {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog = ProgressDialog.show(AddDriverData.this, "Loading", "Please wait...", true);
                                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                                }
                                            });
                                            staticResponse = client.newCall(request).execute();
                                            int statuscode = staticResponse.code();
                                            if (statuscode == 401) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        unauthorize(AddDriverData.this);
                                                    }
                                                });
                                            } else {
                                                String staticRes = staticResponse.body().string();
                                                Log.i(null, staticRes);
                                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                                if (staticJsonObj.getInt("rcode") == 1) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressDialog.dismiss();

                                                        }
                                                    });
                                                    try {
                                                        JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                                        try {
                                                            String oTP = rObj.getString("OTP");
                                                            String driverUserId = rObj.getString("driverUserId");
                                                            System.out.println("qwertyuiop1 : \t" + oTP);
                                                            System.out.println("qwertyuiop2 : \t" + driverUserId);
                                                            driverActivated(oTP, driverUserId);
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
                                                } else if (staticJsonObj.getInt("rcode") == 2) {
                                                    runOnUiThread(progressDialog::dismiss);
                                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                    JSONObject index = rmsg.getJSONObject(0);
                                                    String errorText = index.getString("errorText");
                                                    String trnId = staticJsonObj.getString("trnID");
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                            alert.setCancelable(false);
                                                            alert.setMessage(errorText);
                                                            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                            alert.show();
                                                        }
                                                    });

                                                } else {
                                                    try {
                                                        runOnUiThread(progressDialog::dismiss);
                                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                        JSONObject index = rmsg.getJSONObject(0);
                                                        runOnUiThread(() -> {
                                                            String errorText;
                                                            try {
                                                                errorText = index.getString("errorText");
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
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
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
                                                    alert.setMessage("Please Enter Your Details before adding as a new Driver");
                                                    alert.setCancelable(false);
                                                    alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    alert.show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            t.start();

                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    } else {
                        try {
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddDriverData.this);
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
                    Toast.makeText(AddDriverData.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void driverActivated(String oTP, String driverUserId) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (isNetworkConnected()) {
            if (checkGPSStatus()) {
                //API url Required
                try {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String uniqueidval = MainActivity.UniqueID;

                            String deviceData = MainActivity.InsertMobileParameters();
                            String postUrl = baseUrl + "/app/Driver/DriverActivated";
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            JsonObject details = new JsonObject();
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog = ProgressDialog.show(AddDriverData.this, "Loading", "Please wait...", true);
                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                details.addProperty("oTP", oTP);
                                details.addProperty("driverUserId", driverUserId);
                                String insertString = details.toString();


                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(postUrl)
                                        .header("Authorization", "Bearer " + stokenval)
                                        .header("mobileParameter", deviceData)
                                        .post(body)
                                        .build();

                                Response staticResponse = null;
                                try {
                                    staticResponse = client.newCall(request).execute();
                                    int statuscode = staticResponse.code();
                                    if (statuscode == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                unauthorize(AddDriverData.this);
                                            }
                                        });
                                    } else {
                                        String staticRes = staticResponse.body().string();
                                        Log.i(null, staticRes);
                                        final JSONObject staticJsonObj = new JSONObject(staticRes);
                                        if (staticJsonObj.getInt("rcode") == 1) {
                                            try {
                                                JSONObject rObj = staticJsonObj.getJSONObject("rObj");

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    Intent i = new Intent(AddDriverData.this, DriverMapping.class);
                                                    startActivity(i);
                                                }
                                            });

                                        } else if (staticJsonObj.getInt("rcode") == 2) {
                                            try {
                                                runOnUiThread(progressDialog::dismiss);
                                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                JSONObject index = rmsg.getJSONObject(0);
                                                runOnUiThread(() -> {
                                                    String errorText;
                                                    try {
                                                        errorText = index.getString("errorText");
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
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

                                        } else {
                                            try {
                                                runOnUiThread(progressDialog::dismiss);
                                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                JSONObject index = rmsg.getJSONObject(0);
                                                runOnUiThread(() -> {
                                                    String errorText;
                                                    try {
                                                        errorText = index.getString("errorText");
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(AddDriverData.this);
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
                    });
                    t.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

            } else {
                try {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddDriverData.this);
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
            Toast.makeText(AddDriverData.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDatePickerDialog(TextView tv, int maxDate) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            DatePickerDialog d = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                d = new DatePickerDialog(AddDriverData.this);
                if (maxDate == 0) {
                    d.getDatePicker().setMaxDate(new Date().getTime());
                }
                d.setOnDateSetListener((view, year, month, date) -> {
                    Calendar cal = Calendar.getInstance();


                    cal.set(year, month + 1, date);
                    /*date1=date + "-" + (month + 1) + "-" + year;*/
                    SimpleDateFormat sf = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
                    Date mydate = new Date();
                    mydate.setYear(year - 1900);
                    mydate.setDate(date);
                    mydate.setMonth(month);
                    date1 = sf.format(mydate);
                    tv.setText(date1);
                });
                d.show();
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
        return gps_enabled || network_enabled;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            finish();
        } catch (Exception e) {

            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }

}