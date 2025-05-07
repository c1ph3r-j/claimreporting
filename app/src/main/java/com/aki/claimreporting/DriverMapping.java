package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DriverMapping extends AppCompatActivity {

    public static String reqidval;
    public String stokenval, encryptedSHA;
    public ProgressDialog progressdialog;
    public TextView adddriver;
    public TextView norecdriver;
    public LinearLayout prcbutton;
    public String loginphn;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    String baseUrl;
    DatabaseHelper mydb;
    /*public static FirebaseCrashlytics mCrashlytics;*/
    Activity activity;
    Context context;
    TextView prcButtontxt;
    ArrayList<DriverMappingModel> driverlist = new ArrayList<DriverMappingModel>();
    ListView listView;
    private DriverMappingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_mapping);
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Driver");
        mCrashlytics = FirebaseCrashlytics.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        /*mCrashlytics = FirebaseCrashlytics.getInstance();*/
        activity = this;
        context = this;
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            norecdriver = (TextView) findViewById(R.id.txtdriverrecordtype);
            adddriver = findViewById(R.id.adddriverid);
            prcbutton = findViewById(R.id.driverproceed);
            prcButtontxt = findViewById(R.id.prcButtonTxt);
            SharedPreferences driverPref = getSharedPreferences("IsCreateDriver", MODE_PRIVATE);
            String driveredit = driverPref.getString(MainActivity.CreateDriverRedirect, "");
            if (driveredit.equals("1")) {
                prcButtontxt.setText("SKIP");
            } else if (driveredit.equals("4")) {
                prcButtontxt.setText("SKIP");
            } else {
                prcButtontxt.setText("DONE");
            }

            try {
                adddriver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPreference = getSharedPreferences("IsCreateDriver", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putString(MainActivity.CreateDriverRedirect, "1");
                        editor.commit();
                        Intent intent = new Intent(DriverMapping.this, AddDriverData.class);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            prcbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (DriverMappingAdapter.drivermaplist.size() == 0) {
//                         Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
//                         startActivity(login);
//                        Toast.makeText(DriverMapping.this, getString(R.string.drivermapreq), Toast.LENGTH_SHORT).show();
//                        return;


                        mydb = new DatabaseHelper(DriverMapping.this);
                        Cursor curseattachuser = mydb.getUserdetails();
                        int countuser = curseattachuser.getCount();
                        if (countuser >= 1) {
                            while (curseattachuser.moveToNext()) {
                                try {
                                    loginphn = curseattachuser.getString(3);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

                            }
                        }
                        Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
                        startActivity(login);
//                        String lgnphnupdate = loginphn.replace("+254","");
//                        String lgnphnupdatenew = lgnphnupdate.replace("-","");
//                        if(lgnphnupdatenew.trim().equals(Registration.phonenumber))
//                        {
//                            Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                            startActivity(login);
//                        }
//                        else
//                        {
//                            Intent login = new Intent(DriverMapping.this, ActivateUpdatePhone.class);
//                            startActivity(login);
//                        }
                    } else {
                        /*Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
                        startActivity(login);*/
                        insertdrivermap();
                        // DriverMappingAdapter.drivermaplist.clear();

                        // Intent login = new Intent(DriverMapping.this, RegistrationStep4.class);
                        // startActivity(login);

                    }

//                SharedPreferences driverPref = getSharedPreferences("IsCreateDriver",MODE_PRIVATE);
//                String driveredit = driverPref.getString(MainActivity.CreateDriverRedirect,"");
//                if(driveredit.equals("1"))
//                {
//
//                    if(DriverMappingAdapter.drivermaplist.size() == 0)
//                    {
////                         Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
////                         startActivity(login);
////                        Toast.makeText(DriverMapping.this, getString(R.string.drivermapreq), Toast.LENGTH_SHORT).show();
////                        return;
//
//
//                        mydb = new DatabaseHelper(DriverMapping.this);
//                        Cursor curseattachuser = mydb.getUserdetails();
//                        int countuser = curseattachuser.getCount();
//                        if (countuser >= 1) {
//                            while (curseattachuser.moveToNext()) {
//                                try {
//                                    loginphn =  curseattachuser.getString(3);
//                                }
//                                catch (Exception ex)
//                                {
//                                    ex.printStackTrace();
//                                }
//
//                            }
//                        }
//                        Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                        startActivity(login);
////                        String lgnphnupdate = loginphn.replace("+254","");
////                        String lgnphnupdatenew = lgnphnupdate.replace("-","");
////                        if(lgnphnupdatenew.trim().equals(Registration.phonenumber))
////                        {
////                            Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
////                            startActivity(login);
////                        }
////                        else
////                        {
////                            Intent login = new Intent(DriverMapping.this, ActivateUpdatePhone.class);
////                            startActivity(login);
////                        }
//                    }
//                    else
//                    {
//                        /*Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                        startActivity(login);*/
//                        insertdrivermap();
//                        // DriverMappingAdapter.drivermaplist.clear();
//
//                        // Intent login = new Intent(DriverMapping.this, RegistrationStep4.class);
//                        // startActivity(login);
//
//                    }
//
//                }
//                else if(driveredit.equals("4"))
//                {
//                    if(DriverMappingAdapter.drivermaplist.size() == 0)
//                    {
////                        Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
////                        startActivity(login);
//
//                        mydb = new DatabaseHelper(DriverMapping.this);
//                        Cursor curseattachuser = mydb.getUserdetails();
//                        int countuser = curseattachuser.getCount();
//                        if (countuser >= 1) {
//                            while (curseattachuser.moveToNext()) {
//                                try {
//                                    loginphn =  curseattachuser.getString(4);
//                                }
//                                catch (Exception ex)
//                                {
//                                    ex.printStackTrace();
//                                }
//
//
//                            }
//                        }
//                        Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                        startActivity(login);
////                        String lgnphnupdate = loginphn.replace("+254","");
////                        String lgnphnupdatenew = lgnphnupdate.replace("-","");
////                        if(lgnphnupdatenew.equals(Registration.phonenumber))
////                        {
////                            Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
////                            startActivity(login);
////                        }
////                        else
////                        {
////                            Intent login = new Intent(DriverMapping.this, ActivateUpdatePhone.class);
////                            startActivity(login);
////                        }
////                        Toast.makeText(DriverMapping.this, getString(R.string.drivermapreq), Toast.LENGTH_SHORT).show();
////                        return;
//                    }
//                    else
//                    {
//                        insertdrivermap();
//                        // DriverMappingAdapter.drivermaplist.clear();
//
//                        // Intent login = new Intent(DriverMapping.this, RegistrationStep4.class);
//                        // startActivity(login);
//
//                    }
//                }
//                else {
//                    DriverMappingAdapter.drivermaplist.clear();
//                    insertdrivevehiclermap();
//                    /*insertdrivermap();*/
//                    //DriverMappingAdapter.drivermaplist.clear();
//
//
////                    finish();
////                    Intent login = new Intent(DriverMapping.this, VehicleInformationView.class);
////                    startActivity(login);
//
//
//                }
////                Intent login = new Intent(DriverMapping.this, RegistrationStep4.class);
////                startActivity(login);
                }
            });
            listView = (ListView) findViewById(R.id.listdrivermapping);
            GetAllDriverlist();


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
            ex.printStackTrace();
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled;

    }

    public void insertdrivermap() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    SharedPreferences driverPref = getSharedPreferences("IsCreateDriver", MODE_PRIVATE);
                    String driveredit = driverPref.getString(MainActivity.CreateDriverRedirect, "");
                    System.out.println(driveredit + "\tqwertyuiopoiuygfdswa");
                    if (driveredit.equals("1")) {
                        mydb = new DatabaseHelper(DriverMapping.this);
                        if (mydb.getregstep().getCount() != 0) {
                            mydb.deleteregstep();
                        }
                        boolean regstepinserted = mydb.insertregstep("RegCompleted");
                        if (regstepinserted) {
                            boolean test = regstepinserted;
                            Log.i(null, "Insertion Done");
                        } else {
                            boolean test = regstepinserted;
                            Log.i(null, "Not Insertion Done");
                        }
                    }
                } else {
                    mydb = new DatabaseHelper(DriverMapping.this);
                    if (mydb.getregstep().getCount() != 0) {
                        mydb.deleteregstep();
                    }
                    boolean regstepinserted = mydb.insertregstep("RegCompleted");
                    if (regstepinserted) {
                        boolean test = regstepinserted;
                        Log.i(null, "Insertion Done");
                    } else {
                        boolean test = regstepinserted;
                        Log.i(null, "Not Insertion Done");
                    }
                }

//                      while (DriverMappingAdapter.drivermaplist.next()) {
//
//
                try {
                    Thread thread = new Thread(new Runnable() {

                        public void run() {

                            try {
                                int arraysize = DriverMappingAdapter.drivermaplist.size() - 1;
                                int ival = 0;
                                boolean isvalreached = true;

                                while (isvalreached) {
                                    int ivalincr = ival;
                                    DriverMappingSendAPI drivermapInfo = DriverMappingAdapter.drivermaplist.get(ivalincr);
                                    String certnum = drivermapInfo.getCertnum();
                                    String driverid = drivermapInfo.getDriverUserId();
                                    mydb = new DatabaseHelper(DriverMapping.this);
                                    if (mydb.getTokendetails().getCount() != 0) {
                                        Cursor curseattachtoken = mydb.getTokendetails();
                                        int counttoken = curseattachtoken.getCount();
                                        if (counttoken >= 1) {
                                            while (curseattachtoken.moveToNext()) {
                                                stokenval = curseattachtoken.getString(1);
                                            }
                                        }
                                    }


                                    String postURL = getString(R.string.uaturl) + "/app/Driver/AddDriverMapping";
                                    final MediaType JSON
                                            = MediaType.parse("application/json; charset=utf-8");

                                    OkHttpClient client = new OkHttpClient();
                                    JsonObject Details = new JsonObject();
                                    Details.addProperty("certificateNo", certnum);
                                    Details.addProperty("driverUserID", driverid);
                                    String insertString = Details.toString();
                                    RequestBody body = RequestBody.create(JSON, insertString);
                                    Request request = new Request.Builder()
                                            .url(postURL)
                                            .header("Authorization", "Bearer " + stokenval)
                                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                                            .post(body)
                                            .build();
                                    Response staticResponse = null;

                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog = new ProgressDialog(DriverMapping.this);
                                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                                // progressdialog.show(activity, "Loading", "Please wait...", true);
                                            }
                                        });
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        staticResponse = client.newCall(request).execute();
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
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    progressdialog.dismiss();
//                                                }
//                                            });
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                }
                                            });
                                            DriverMappingAdapter.drivermaplist.clear();
                                            Thread.sleep(2000);
                                            if (arraysize == ivalincr) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressdialog.dismiss();
                                                    }
                                                });
                                                isvalreached = false;
                                                SharedPreferences driverPrefnew = getSharedPreferences("IsCreateDriver", MODE_PRIVATE);
                                                String drivereditnew = driverPrefnew.getString(MainActivity.CreateDriverRedirect, "");
//                                            if(drivereditnew.equals("1"))
//                                            {
////                                                                          Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
////                                                                          startActivity(login);
//
//                                                mydb = new DatabaseHelper(DriverMapping.this);
//                                                Cursor curseattachuser = mydb.getUserdetails();
//                                                int countuser = curseattachuser.getCount();
//                                                if (countuser >= 1) {
//                                                    while (curseattachuser.moveToNext()) {
//                                                        try {
//                                                            loginphn =  curseattachuser.getString(4);
//                                                        }
//                                                        catch (Exception ex)
//                                                        {
//                                                            ex.printStackTrace();
//                                                        }
//
//
//                                                    }
//                                                }
//                                                Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                                                startActivity(login);
////                                                            String lgnphnupdate = loginphn.replace("+254","");
////                                                            String lgnphnupdatenew = lgnphnupdate.replace("-","");
////                                                            if(lgnphnupdatenew.equals(AddVehicle.phnumberdmvic))
////                                                            {
////                                                                Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
////                                                                startActivity(login);
////                                                            }
////                                                            else
////                                                            {
////                                                                Intent login = new Intent(DriverMapping.this, ActivateUpdatePhone.class);
////                                                                startActivity(login);
////                                                            }
//                                            }
//                                            else if(drivereditnew.equals("4"))
//                                            {
////                                                                          Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
////                                                                          startActivity(login);
//
//                                                mydb = new DatabaseHelper(DriverMapping.this);
//                                                Cursor curseattachuser = mydb.getUserdetails();
//                                                int countuser = curseattachuser.getCount();
//                                                if (countuser >= 1) {
//                                                    while (curseattachuser.moveToNext()) {
//                                                        try {
//                                                            loginphn =  curseattachuser.getString(4);
//                                                        }
//                                                        catch (Exception ex)
//                                                        {
//                                                            ex.printStackTrace();
//                                                        }
//
//
//                                                    }
//                                                }
//
//                                                Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
//                                                startActivity(login);
//
////                                                if(loginphn.equals(AddVehicle.phnumberdmvic))
////                                                {
////                                                    Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
////                                                    startActivity(login);
////                                                }
////                                                else
////                                                {
////                                                    Intent login = new Intent(DriverMapping.this, ActivateUpdatePhone.class);
////                                                    startActivity(login);
////                                                }
//                                            }
//                                            else
//                                            {
//                                                finish();
////                                                            Intent login = new Intent(DriverMapping.this, VehicleInformationView.class);
////                                                            startActivity(login);
//                                            }
                                                Intent login = new Intent(DriverMapping.this, AddVehicleSuccess.class);
                                                startActivity(login);
                                            }
                                            ival = ivalincr + 1;
                                        }
//                                else if(staticJsonObj.getInt("rcode") == 2)
//                                {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            userexist();
//
//                                        }
//                                    });
//
                                        else if (staticJsonObj.getInt("rcode") == 401) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                    unauthorize(DriverMapping.this);
                                                }
                                            });
                                        } else {
                                            String errorMsg = staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText");

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(DriverMapping.this);
                                                    alert.setMessage(errorMsg);
                                                    alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    alert.show();

                                                }
                                            });
                                            // Thread.sleep(2000);
//                                                    if (arraysize == ivalincr) {
//                                                        runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                progressdialog.dismiss();
//                                                            }
//                                                        });
//                                                        isvalreached = false;
//                                                        SharedPreferences driverPrefnew = getSharedPreferences("IsCreateDriver",MODE_PRIVATE);
//                                                        String drivereditnew = driverPrefnew.getString(MainActivity.CreateDriverRedirect,"");
//                                                        if(drivereditnew.equals("1"))
//                                                        {
////                                                                          Intent login = new Intent(DriverMapping.this, VehicleTermsConditions.class);
////                                                                          startActivity(login);
//
//                                                            mydb = new DatabaseHelper(DriverMapping.this);
//                                                            Cursor curseattachuser = mydb.getUserdetails();
//                                                            int countuser = curseattachuser.getCount();
//                                                            if (countuser >= 1) {
//                                                                while (curseattachuser.moveToNext()) {
//                                                                    try {
//                                                                        loginphn =  curseattachuser.getString(4);
//                                                                    }
//                                                                    catch (Exception ex)
//                                                                    {
//                                                                        ex.printStackTrace();
//                                                                    }
//
//                                                                }
//                                                            }
//                                                            String lgnphnupdate = loginphn.replace("+254","");
//                                                            String lgnphnupdatenew = lgnphnupdate.replace("-","");
//                                                            if(lgnphnupdatenew.equals(RegistrationStep1.phnumberdmvic))
//                                                            {
//                                                                Intent login = new Intent(DriverMapping.this, VehicleSuccess.class);
//                                                                startActivity(login);
//                                                            }
//                                                            else
//                                                            {
//                                                                Intent login = new Intent(DriverMapping.this, UpdatePhoneNumber.class);
//                                                                startActivity(login);
//                                                            }
//                                                        }
//                                                        else
//                                                        {
//                                                            finish();
//                                                            Intent login = new Intent(DriverMapping.this, VehicleInformationView.class);
//                                                            startActivity(login);
//                                                        }
//                                                    }
                                            ival = ivalincr + 1;
                                        }
                                    } catch (final IOException e) {
                                        // progressdialog.dismiss();
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);

                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                Toast.makeText(DriverMapping.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        //  progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }


                                }

                            } catch (Exception ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                    }
                                });
                            }
                        }

                    });
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
//                        }
//                    for (int id = 0; id < DriverMappingAdapter.drivermaplist.size(); id++) {
//                        DriverMappingSendAPI drivermapInfo = DriverMappingAdapter.drivermaplist.get(id);
//                        String certnum = drivermapInfo.getCertnum();
//                        String driverid = drivermapInfo.getDriverUserId();
//                        mydb = new DatabaseHelper(DriverMapping.this);
//                        if (mydb.getTokendetails().getCount() != 0) {
//                            Cursor curseattachtoken = mydb.getTokendetails();
//                            int counttoken = curseattachtoken.getCount();
//                            if (counttoken >= 1) {
//                                while (curseattachtoken.moveToNext()) {
//                                    stokenval = curseattachtoken.getString(1);
//                                }
//                            }
//                        }
////                            progressdialog = new ProgressDialog(this);
//                        encryptedSHA = "";
//                        String sourceStr = MainActivity.InsertMobileparameters();
//                        try {
//                            encryptedSHA = AESUtils.encrypt(sourceStr);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        Thread thread = new Thread(new Runnable() {
//
//                            public void run() {
//
//                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriverMapping";
//                                final MediaType JSON
//                                        = MediaType.parse("application/json; charset=utf-8");
//
//                                OkHttpClient client = new OkHttpClient();
//                                JsonObject Details = new JsonObject();
//                                Details.addProperty("certificateNo", certnum);
//                                Details.addProperty("driverUserID", driverid);
//                                String insertString = Details.toString();
//                                RequestBody body = RequestBody.create(JSON, insertString);
//                                Request request = new Request.Builder()
//                                        .url(MainActivity.postURL)
//                                        .header("Authorization", "Bearer " + stokenval)
//                                        .header("MobileParameter", MainActivity.InsertMobileparameters())
//                                        .post(body)
//                                        .build();
//                                Response staticResponse = null;
//
//                                try {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                        }
//                                    });
//                                    try {
//                                        Thread.sleep(10000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    staticResponse = client.newCall(request).execute();
//                                    String staticRes = staticResponse.body().string();
//                                    Log.i(null, staticRes);
//                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
//                                    try {
//                                        reqidval = staticJsonObj.getString("reqID");
//                                    }
//                                    catch (JSONException ex)
//                                    {
//                                        ex.printStackTrace();
//                                    }
//                                    if (staticJsonObj.getInt("rcode") == 1) {
////                                            runOnUiThread(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    progressdialog.dismiss();
////                                                }
////                                            });
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                progressdialog.dismiss();
//                                            }
//                                        });
//                                        DriverMappingAdapter.drivermaplist.clear();
//
//                                    }
////                                else if(staticJsonObj.getInt("rcode") == 2)
////                                {
////                                    runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            progressdialog.dismiss();
////                                            userexist();
////
////                                        }
////                                    });
////                                }
//                                    else {
////                                                runOnUiThread(new Runnable() {
////                                                    @Override
////                                                    public void run() {
////                                                        progressdialog.dismiss();
////
////                                                    }
////                                                });
//                                    }
//                                } catch (final IOException ex) {
//                                    // progressdialog.dismiss();
//                                    ex.printStackTrace();
//                                    MainActivity.MobileErrorLog(reqidval,"DriverMapping-insertdrivermap",ex.toString(),ex.toString());
//                                    mCrashlytics.recordException(ex);
//                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                    runOnUiThread(new Runnable() {
//                                        public void run() {
//
//                                            Toast.makeText(DriverMapping.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                } catch (JSONException ex) {
//                                    //  progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                    ex.printStackTrace();
//                                    MainActivity.MobileErrorLog(reqidval,"DriverMapping-insertdrivermap",ex.toString(),ex.toString());
//                                    mCrashlytics.recordException(ex);
//                                    //  Toast.makeText(DriverMapping.this,ex.toString(), Toast.LENGTH_LONG).show();
//                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//                                }
//                            }
//                        });
//                        thread.start();
//                    }

//                    SharedPreferences driverPrefnew = getSharedPreferences("IsCreateDriver",MODE_PRIVATE);
//                    String drivereditnew = driverPrefnew.getString(MainActivity.CreateDriverRedirect,"");
//                    if(drivereditnew.equals("1"))
//                    {
//                        Intent login = new Intent(DriverMapping.this, RegistrationStep4.class);
//                        startActivity(login);
//                    }
//                    else
//                    {
//                        finish();
//                        Intent login = new Intent(DriverMapping.this, VehicleInformationView.class);
//                        startActivity(login);
//                    }
            } else {
                try {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DriverMapping.this);
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


        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }


    //Old before change to Synchornus method

//    public void insertdrivermap()
//    {
//
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    if (isNetworkConnected()) {
//
//                        if (checkGPSStatus())
//                        {
//
//                            SharedPreferences driverPref = getSharedPreferences("IsCreateDriver",MODE_PRIVATE);
//                            String driveredit = driverPref.getString(MainActivity.CreateDriverRedirect,"");
//                            if(driveredit.equals("1"))
//                            {
//                                mydb = new DatabaseHelper(DriverMapping.this);
//                                if(mydb.getregstep().getCount() != 0)
//                                {
//                                    mydb.deleteregstep();
//                                }
//                                boolean regstepinserted = mydb.insertregstep("Step2");
//                                if(regstepinserted)
//                                {
//                                    boolean test = regstepinserted;
//                                    Log.i(null,"Insertion Done");
//                                }
//                                else
//                                {
//                                    boolean test = regstepinserted;
//                                    Log.i(null,"Not Insertion Done");
//                                }
//                            }
//                            else
//                            {
//                                mydb = new DatabaseHelper(DriverMapping.this);
//                                if(mydb.getregstep().getCount() != 0)
//                                {
//                                    mydb.deleteregstep();
//                                }
//                                boolean regstepinserted = mydb.insertregstep("RegCompleted");
//                                if(regstepinserted)
//                                {
//                                    boolean test = regstepinserted;
//                                    Log.i(null,"Insertion Done");
//                                }
//                                else
//                                {
//                                    boolean test = regstepinserted;
//                                    Log.i(null,"Not Insertion Done");
//                                }
//                            }
//
//
//                            for (int id = 0; id < DriverMappingAdapter.drivermaplist.size(); id++) {
//                                DriverMappingSendAPI drivermapInfo = DriverMappingAdapter.drivermaplist.get(id);
//                                String certnum = drivermapInfo.getCertnum();
//                                String driverid = drivermapInfo.getDriverUserId();
//                                mydb = new DatabaseHelper(DriverMapping.this);
//                                if (mydb.getTokendetails().getCount() != 0) {
//                                    Cursor curseattachtoken = mydb.getTokendetails();
//                                    int counttoken = curseattachtoken.getCount();
//                                    if (counttoken >= 1) {
//                                        while (curseattachtoken.moveToNext()) {
//                                            stokenval = curseattachtoken.getString(1);
//                                        }
//                                    }
//                                }
////                            progressdialog = new ProgressDialog(this);
//                                encryptedSHA = "";
//                                String sourceStr = MainActivity.InsertMobileparameters();
//                                try {
//                                    encryptedSHA = AESUtils.encrypt(sourceStr);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                Thread thread = new Thread(new Runnable() {
//
//                                    public void run() {
//                                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriverMapping";
//                                        final MediaType JSON
//                                                = MediaType.parse("application/json; charset=utf-8");
//
//                                        OkHttpClient client = new OkHttpClient();
//                                        JsonObject Details = new JsonObject();
//                                        Details.addProperty("certificateNo", certnum);
//                                        Details.addProperty("driverUserID", driverid);
//                                        String insertString = Details.toString();
//                                        RequestBody body = RequestBody.create(JSON, insertString);
//                                        Request request = new Request.Builder()
//                                                .url(MainActivity.postURL)
//                                                .header("Authorization", "Bearer " + stokenval)
//                                                .header("MobileParameter", MainActivity.InsertMobileparameters())
//                                                .post(body)
//                                                .build();
//                                        Response staticResponse = null;
//
//                                        try {
////                                        runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
////                                                // progressdialog.show(activity, "Loading", "Please wait...", true);
////                                            }
////                                        });
//                                            staticResponse = client.newCall(request).execute();
//                                            String staticRes = staticResponse.body().string();
//                                            Log.i(null, staticRes);
//                                            final JSONObject staticJsonObj = new JSONObject(staticRes);
//                                            try {
//                                                reqidval = staticJsonObj.getString("reqID");
//                                            }
//                                            catch (JSONException ex)
//                                            {
//                                                ex.printStackTrace();
//                                            }
//                                            if (staticJsonObj.getInt("rcode") == 1) {
////                                            runOnUiThread(new Runnable() {
////                                                @Override
////                                                public void run() {
////                                                    progressdialog.dismiss();
////                                                }
////                                            });
//                                                DriverMappingAdapter.drivermaplist.clear();
//
//                                            }
////                                else if(staticJsonObj.getInt("rcode") == 2)
////                                {
////                                    runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            progressdialog.dismiss();
////                                            userexist();
////
////                                        }
////                                    });
////                                }
//                                            else {
////                                                runOnUiThread(new Runnable() {
////                                                    @Override
////                                                    public void run() {
////                                                        progressdialog.dismiss();
////
////                                                    }
////                                                });
//                                            }
//                                        } catch (final IOException ex) {
//                                            // progressdialog.dismiss();
//                                            ex.printStackTrace();
//                                            MainActivity.MobileErrorLog(reqidval,"DriverMapping-insertdrivermap",ex.toString(),ex.toString());
//                                            mCrashlytics.recordException(ex);
//                                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//
//                                                    Toast.makeText(DriverMapping.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        } catch (JSONException ex) {
//                                            //  progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                            ex.printStackTrace();
//                                            MainActivity.MobileErrorLog(reqidval,"DriverMapping-insertdrivermap",ex.toString(),ex.toString());
//                                            mCrashlytics.recordException(ex);
//                                            //  Toast.makeText(DriverMapping.this,ex.toString(), Toast.LENGTH_LONG).show();
//                                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//                                        }
//                                    }
//                                });
//                                thread.start();
//                            }
//
//
//                        }
//
//                        else {
//                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DriverMapping.this);
//                            dialog.setMessage("GPS locations is not enabled.Please enable it");
//                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //this will navigate user to the device location settings screen
//                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                    startActivity(intent);
//                                }
//                            });
//                            android.app.AlertDialog alert = dialog.create();
//                            alert.show();
//                        }
//
//
//                    } else {
////                        Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                catch (Exception ex) {
//                    //progressdialog.dismiss();
//                    ex.getStackTrace();
//                    MainActivity.MobileErrorLog(reqidval,"DriverMapping-insertdrivermap",ex.toString(),ex.toString());
//                    mCrashlytics.recordException(ex);
////                    Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                    // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                }
//            }
//
//        });
//
//    }

    public void insertdrivevehiclermap() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
//                            mydb = new DatabaseHelper(DriverMapping.this);
//                            if(mydb.getregstep().getCount() != 0)
//                            {
//                                mydb.deleteregstep();
//                            }
//                            boolean regstepinserted = mydb.insertregstep("Step2");
//                            if(regstepinserted)
//                            {
//                                boolean test = regstepinserted;
//                                Log.i(null,"Insertion Done");
//                            }
//                            else
//                            {
//                                boolean test = regstepinserted;
//                                Log.i(null,"Not Insertion Done");
//                            }

                    for (int id = 0; id < DriverMappingAdapter.drivermaplist.size(); id++) {
                        DriverMappingSendAPI drivermapInfo = DriverMappingAdapter.drivermaplist.get(id);
                        String certnum = drivermapInfo.getCertnum();
                        String driverid = drivermapInfo.getDriverUserId();
                        mydb = new DatabaseHelper(DriverMapping.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
//                            progressdialog = new ProgressDialog(this);
                        String sourceStr = MainActivity.InsertMobileParameters();

                        Thread thread = new Thread(new Runnable() {

                            public void run() {
                                String postURL = getString(R.string.uaturl) + "/app/Driver/AddDriverMapping";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");

                                OkHttpClient client = new OkHttpClient();
                                JsonObject Details = new JsonObject();
                                Details.addProperty("certificateNo", certnum);
                                Details.addProperty("driverUserID", driverid);
                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(postURL)
                                        .header("Authorization", "Bearer " + stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .post(body)
                                        .build();
                                Response staticResponse = null;

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
                                        }
                                    });
                                    staticResponse = client.newCall(request).execute();
                                    String staticRes = staticResponse.body().string();
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
//                                                    DriverMappingAdapter.drivermaplist.clear();
//                                                    finish();
//                                                    Intent login = new Intent(DriverMapping.this, VehicleInformationView.class);
//                                                    startActivity(login);
                                            }
                                        });


                                    } else if (staticJsonObj.getInt("rcode") == 2) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                /*userexist();*/

                                            }
                                        });
                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(DriverMapping.this);
                                            }
                                        });
                                    } else {
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        progressdialog.dismiss();
//
//                                                    }
//                                                });
                                    }
                                } catch (final IOException ex) {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    /*mCrashlytics.recordException(ex);*/
//                                      MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(DriverMapping.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (JSONException e) {
                                    progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                    /*mCrashlytics.recordException(ex);*/
                                    //  Toast.makeText(DriverMapping.this,ex.toString(), Toast.LENGTH_LONG).show();
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                }
                            }
                        });
                        thread.start();
                    }


                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DriverMapping.this);
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
//                        Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            /*mCrashlytics.recordException(ex);*/
//                    Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }


    }

    public void GetAllDriverlist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    mydb = new DatabaseHelper(DriverMapping.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                                // stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwicHJpbWFyeXNpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsInByaW1hcnlncm91cHNpZCI6IjAwMDAwMDAwLTAwMDAtMDAwMC0wMDAwLTAwMDAwMDAwMDAwMCIsIlJvbGUiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDAiLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiMDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwIiwibmJmIjoxNjEwNDM0NjA5LCJleHAiOjE2NDE1Mzg2MDksImlhdCI6MTYxMDQzNDYwOSwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.XuA0cKX5--jrvYfabh8xKYE4G74_EJYcbFwKfscj2Yiihe13BpWmw7xbeQSd7qZEjOIZ6qeNyIBmYa4D6AdNa50yZ-y3oefBBcUd7nsO24oZ0vzOO2P7uZV2NAhQkJZd83VJbNaHRjxFCwa8bTP6gaD8emA6aY2NSVt7TzTCNxnz_4059eDckHPg_0_BUQ3v4oMIAsC-Mn6mUUx6jWiK-ggaxbxQMY_TEHADMlLrqHkFgbi1PALLKQb8DNIfhHu-Iw6RtOzfMfM28_SUgpZZIEHftcg_XhUZmbNBYD-943T8Guq8xHAC_JJx1VaJIIwJEIGIuWvhlE1TU1_h-gjK9pKknfFcBOJorHXe4vI1YTMCe2AqGKqoRSAeYrtk7qgaD4UmWen1Y1h-gGHBWbu7VuXiA758j7RRLHfmdoZuGBITTe8Wl8taSacwtRPjxHjnz5vzqYlOvsKMTOjxxc-thXIBZWblPElK-0l0O1F9SiTQSEJi6C6UxL4qJNsUPIN0a4sHkbln8cJ6VLrB4VZIeOhuY5CFuyAy0DwYJbFyXZjnNF_39jYkCpOTN6dUVJUG152VAlONMUc29hJ_MeQHs2mRDxEKNxp-BAUtzZrZhcz8iEAI9R-Itc88tpm1U8BTNu6k_MnULKGZlrB8y1HRQ4-H1imSG5UNt6bg9l_W0IU";
                            }
                        }
                    }
                    String data = MainActivity.InsertMobileParameters();
                    System.out.println(data);
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            String postURL = getString(R.string.uaturl) + "/app/Driver/GetAllDriver";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient();
                            JsonObject Details = new JsonObject();
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .header("Authorization", "Bearer " + stokenval)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse = null;

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                System.out.println(staticRes);

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
                                    JSONArray driverResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllDriver");
                                    if (driverResponseList.length() == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    progressdialog.dismiss();
                                                    norecdriver = (TextView) findViewById(R.id.txtdriverrecordtype);
                                                    norecdriver.setText("You don't have a driver profile associated with your Bima Yangu account. Click Add Driver Profile to create one");

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                    progressdialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    progressdialog.dismiss();
                                                    prcButtontxt.setText("NEXT");
                                                    norecdriver = (TextView) findViewById(R.id.txtdriverrecordtype);
                                                    norecdriver.setText("Choose one or more driver profiles from the following list to link with your vehicle.");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                    progressdialog.dismiss();
                                                }
                                            }
                                        });

                                        for (int i = 0; i < driverResponseList.length(); i++) {
                                            JSONObject driverObj = driverResponseList.getJSONObject(i);
                                            DriverMappingModel element = new DriverMappingModel(
                                                    driverObj.getString("driverUserId"),
                                                    driverObj.getString("driverName"),
                                                    driverObj.getString("driverDLCountry"),
                                                    driverObj.getString("driverDLNum"),
                                                    driverObj.getString("driverDLValidFrom"),
                                                    driverObj.getString("driverDLValidTill"),
                                                    driverObj.getString("mobileNo"),
                                                    // the data from API doesnt have a boolean for first driver which may lead to exception
                                                    /*driverObj.getBoolean("isSelfDriver")*/
                                                    false,
                                                    driverObj.getString("driverStatus")
                                            );

                                            driverlist.add(element);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    adapter = new DriverMappingAdapter(DriverMapping.this, driverlist);
                                                    listView = (ListView) findViewById(R.id.listdrivermapping);
                                                    listView.setAdapter(adapter);
                                                    // setListViewHeightBasedOnItems(list);
                                                    listView.setTextFilterEnabled(true);
                                                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                                    progressdialog.dismiss();

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                    progressdialog.dismiss();
                                                }
//                                            adapter = new DriverMappingAdapter(getApplicationContext(), dataModels);
//
//                                            adapter = new DriverMappingAdapter(dataModels, getApplicationContext());
//                                            listView.setAdapter(adapter);
//                                            Spinner insurSpinnerVal = (Spinner) findViewById(R.id.spinnerinsurance);
//                                            insurSpinnerVal.setOnItemSelectedListener(RegistrationStep1.this);
//                                            ArrayAdapter insunameAdapter = new ArrayAdapter(RegistrationStep1.this,android.R.layout.simple_list_item_1,insurcomspinner);
//                                            insurSpinnerVal.setAdapter(insunameAdapter);

                                            }
                                        });
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            progressdialog.dismiss();
//                                                        }
//                                                    });
//                                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
//                                                    startActivity(redirect);
                                    }

                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
//                                            unauthorize();
                                        }
                                    });
                                } else if (staticJsonObj.getInt("rcode") == 2) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressdialog.dismiss();
                                            try {
                                                norecdriver = (TextView) findViewById(R.id.txtdriverrecordtype);
                                                norecdriver.setText("You don't have a driver profile associated with your Bima Yangu account. Click 'Add Driver Profile' to create one.");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                            return;
                                        }
                                    });
                                } else {
                                    try {
                                        runOnUiThread(progressdialog::dismiss);
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText = null;
                                            String trnId = null;
                                            try {
                                                errorText = index.getString("errorText");
                                                trnId = staticJsonObj.getString("trnID");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(DriverMapping.this);
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
                            } catch (final IOException e) {
                                // progressdialog.dismiss();
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(DriverMapping.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                //   progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DriverMapping.this);
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
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.getStackTrace();
        }
    }

    /*public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DriverMapping.this);
            dialog.setMessage("Your session have been expired. Please login again to continue");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(DriverMapping.this);
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
                *//*Intent login = new Intent(DriverMapping.this, UserRegistration.class);
                startActivity(login);*//*
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
//        try {
//            if (id == R.id.action_name) {
//
//                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//                View screenView = rootView.getRootView();
//                screenView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//                screenView.setDrawingCacheEnabled(false);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream .toByteArray();
//
//                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//                supporteditor.putString(MainActivity.ReferrenceURL,"Driver Mapping");
//                supporteditor.commit();
//                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//                supporteditorimg.putString(MainActivity.SupportImg,encodedimg);
//                supporteditorimg.commit();
//                Intent login = new Intent(DriverMapping.this, SupportTicket.class);
//                startActivity(login);
//                return true;
//
//                // Do something
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}