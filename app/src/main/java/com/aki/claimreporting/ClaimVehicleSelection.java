package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.CertificateActivation.reqidval;
import static com.aki.claimreporting.ClaimLocation.locationupdate;
import static com.aki.claimreporting.ClaimLocation.progressdialog;
import static com.aki.claimreporting.MainActivity.backViewcount;
import static com.aki.claimreporting.MainActivity.driverSideViewcount;
import static com.aki.claimreporting.MainActivity.frontViewcount;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.passengerSideViewcount;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.roofViewcount;
import static com.aki.claimreporting.MainActivity.stokenval;
import static com.aki.claimreporting.MainActivity.unauthorize;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaimVehicleSelection extends AppCompatActivity {

    public static boolean setNewVehicleSelected = false;
    public static ArrayList<String> sListpager;
    public static ArrayList<String> sMakeModel;
    public static int valuelistadpt;
    public static int listSize;
    public static ArrayList<VehicleselectClaimResponse> listOfVehiclesDetail;
    public static LinearLayout selectedVehicleLayout;
    public static TextView selectedVehicleModel;
    public static String lattestfinal;
    public static String longtestfinal;
    public static String smsphnnumbers;
    public static String drivercraid;
    public static CardFragmentPagerAdapter pagerAdapter;
    public static String insuranceid;
    public static String insurancename;
    public String membercraid;
    public String ambulanceoff, thirdpartyoff, towingoff, policeoff, claimtypeoff;
    public DatabaseHelper mydb;
    public String finalpushsmstext, finalpushsmssend, PINString;
    public boolean isambulance;
    public boolean ispolice;
    public boolean istowing;
    List<InsuranceComInfo> insurnamelist = new ArrayList<>();
    SharedPreferences sharedpreferences;
    CheckBox ambulanceEnabled, policeEnabled, towingEnabled;
    ViewPager viewPager;
    TextView sendsmsid, nextid;
    LinearLayout EmergencyServiceProviderList, noservice;
    LinearLayout sendsmslin, nextscreenlin;
    LinearLayout ambulancelin, towinglin, policelin;
    String memberCompanyName, insurerID, certifcateType, typeOfInsurance, typeofCover, engineNumber, chassisNumber, insuredPhoneNo, insuredMailId, passengersCount, tonnage, sumInsured, insuredPIN, certificateNo, insuredName, policyBeginDate, policyEndDate, policyno, make, model, yearofManufacture, registratioNo;


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public static String SHA1(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        } catch (Exception ignored) {

            ignored.printStackTrace();
            return null;
        }
    }

    public static String formatDate(String inputDate) throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        Date returnDate = format1.parse(inputDate);
        assert returnDate != null;
        return (format2.format(returnDate));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_vehicle_selection);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Claim Vehicle Selection");
        preventSizeChange(this, getSupportActionBar());
        listOfVehiclesDetail = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frontViewcount = 0;
        backViewcount = 0;
        driverSideViewcount = 0;
        passengerSideViewcount = 0;
        roofViewcount = 0;
        try {
            if (isNetworkConnected() == true) {

                getallvehicleinfo();
            } else {
                getnonetworkVehicleinfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        init();


    }

    public void getnonetworkVehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            mydb = new DatabaseHelper(ClaimVehicleSelection.this);
            Cursor curseattachtoken = mydb.getvehicledetails();
            int counttoken = curseattachtoken.getCount();
            if (counttoken >= 1) {
                while (curseattachtoken.moveToNext()) {
                    VehicleselectClaimResponse element = new VehicleselectClaimResponse(AESCrypt.decrypt(curseattachtoken.getString(1)), AESCrypt.decrypt(curseattachtoken.getString(2)), curseattachtoken.getString(3), curseattachtoken.getString(4), curseattachtoken.getString(5), curseattachtoken.getString(6), curseattachtoken.getString(7), curseattachtoken.getString(8), curseattachtoken.getString(9), curseattachtoken.getString(10), curseattachtoken.getString(14), curseattachtoken.getString(14));
                    listOfVehiclesDetail.add(element);

                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initListView();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //  progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);

        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            nextscreenlin = (LinearLayout) findViewById(R.id.nextscreenlin);

            sendsmslin = (LinearLayout) findViewById(R.id.sendsmslin);


            if (isNetworkConnected()) {
                nextscreenlin.setVisibility(View.VISIBLE);
                sendsmslin.setVisibility(View.GONE);
            } else {
                nextscreenlin.setVisibility(View.GONE);
                sendsmslin.setVisibility(View.VISIBLE);
            }

            viewPager = (ViewPager) findViewById(R.id.viewPager);
            nextid = (TextView) findViewById(R.id.nextid);
            sendsmsid = (TextView) findViewById(R.id.sendsmsid);
            ambulanceEnabled = findViewById(R.id.checkambulance);
            towingEnabled = findViewById(R.id.checktowing);
            policeEnabled = findViewById(R.id.checkpolice);
            EmergencyServiceProviderList = findViewById(R.id.ServiceProviders);

            noservice = findViewById(R.id.noservice);


            ambulancelin = findViewById(R.id.ambulancelin);
            towinglin = findViewById(R.id.towinglin);
            policelin = findViewById(R.id.policelin);

            MainActivity.ambulanceenabled = "No";
            MainActivity.policeinfoenabled = "No";
            MainActivity.towingagencyenabled = "No";

            SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
            String incidenttypeval = incitype.getString("typeidincident", "");
            if (Objects.equals(incidenttypeval, "B2EC755A-88EF-4F53-8911-C13688D226D3")) {
                // EmergencyServiceProviderList.setVisibility(View.GONE);


                if (ClaimLocation.pastdateornot == true) {
                    EmergencyServiceProviderList.setVisibility(View.GONE);
                    noservice.setVisibility(View.VISIBLE);

                } else {
                    EmergencyServiceProviderList.setVisibility(View.VISIBLE);
                    noservice.setVisibility(View.GONE);
                    ambulancelin.setVisibility(View.VISIBLE);
                    towinglin.setVisibility(View.GONE);
                    policelin.setVisibility(View.VISIBLE);
                }


            } else {

                if (ClaimLocation.pastdateornot == true) {
                    EmergencyServiceProviderList.setVisibility(View.GONE);
                    noservice.setVisibility(View.VISIBLE);

                } else {
                    EmergencyServiceProviderList.setVisibility(View.VISIBLE);
                    noservice.setVisibility(View.GONE);
                    ambulancelin.setVisibility(View.VISIBLE);
                    towinglin.setVisibility(View.VISIBLE);
                    policelin.setVisibility(View.VISIBLE);
                }


            }

            try {
                nextscreenlin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedVehicleLayout == null) {
                            Toast.makeText(ClaimVehicleSelection.this, "Please Select your Vehicle!", Toast.LENGTH_SHORT).show();
                        } else {
                            MainActivity.ambulanceenabled = (ambulanceEnabled.isChecked()) ? "Yes" : "No";
                            MainActivity.towingagencyenabled = (towingEnabled.isChecked()) ? "Yes" : "No";
                            MainActivity.policeinfoenabled = (policeEnabled.isChecked()) ? "Yes" : "No";


                            getclaimincidentid();
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            try {
                sendsmslin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MainActivity.ambulanceenabled = (ambulanceEnabled.isChecked()) ? "Yes" : "No";
                        MainActivity.towingagencyenabled = (towingEnabled.isChecked()) ? "Yes" : "No";
                        MainActivity.policeinfoenabled = (policeEnabled.isChecked()) ? "Yes" : "No";

                        if (MainActivity.ambulanceenabled == "Yes") {
                            ambulanceoff = "1";
                            isambulance = true;
                        } else {
                            ambulanceoff = "0";
                            isambulance = false;
                        }
                        if (MainActivity.towingagencyenabled == "Yes") {
                            towingoff = "1";
                            istowing = true;
                        } else {
                            towingoff = "0";
                            istowing = false;
                        }
                        if (MainActivity.policeinfoenabled == "Yes") {
                            policeoff = "1";
                            ispolice = true;
                        } else {
                            policeoff = "0";
                            ispolice = false;
                        }

                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                        String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                        if (incidenttypeval.equals("630CF0B1-C91C-48D5-BD09-2F23D6C3AAB8")) {
                            claimtypeoff = "1";
                            thirdpartyoff = "1";
                        } else if (incidenttypeval.equals("F82589E6-7344-47B2-A672-11013F538551")) {
                            claimtypeoff = "2";
                            thirdpartyoff = "0";
                        } else {
                            claimtypeoff = "3";
                            thirdpartyoff = "0";
                        }
                        mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                        Cursor curseattachuser = mydb.getUserdetails();
                        int countuser = curseattachuser.getCount();
                        if (countuser >= 1) {
                            while (curseattachuser.moveToNext()) {
                                try {
                                    membercraid = AESCrypt.decrypt(curseattachuser.getString(2));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        }
                        // String vehnewid = MainActivity.vehicleoffline;
                        SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                        String lattest = locationPref.getString(MainActivity.Latitude, null);
                        String longtest = locationPref.getString(MainActivity.Longitude, null);
                        lattestfinal = lattest;
                        longtestfinal = longtest;

                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmm", Locale.US);
                        String dateTime = sdf.format(Calendar.getInstance().getTime());
//                SharedPreferences driverpref = getSharedPreferences("DriverSelection",MODE_PRIVATE);
//                String drivercraidnew = driverpref.getString(Claim.driverusercraid,"");
//                if(drivercraidnew.equals(""))
//                {
//                    try {
//                        drivercraid =  AESCrypt.decrypt(membercraid);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                else
//                {
//                    drivercraid = drivercraidnew;
//                }
                        try {
                            drivercraid = membercraid;
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        SharedPreferences vecidpref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                        String vehnewid = vecidpref.getString("Vechilerefid", "");
                        String vehidnew = null;
                        try {
                            vehidnew = AESCrypt.decrypt(vehnewid).replace("-", "~");
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String mergefinal = null;
                        try {
                            //mergefinal = dateTime+ AESCrypt.decrypt(membercraid)+ "-" + drivercraid +"-"+ vehidnew + ":" +lattestfinal+ ":" +longtestfinal;
                            mergefinal = dateTime + membercraid + "#" + drivercraid + "#" + vehidnew + ":" + lattestfinal + ":" + longtestfinal;
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String PasswordKey = "WeLove9SA";
                        String shakey = null;
                        try {
                            // shakey = dateTime+"-"+lattestfinal+"-"+longtestfinal+"-"+vehidnew+"-"+ AESCrypt.decrypt(membercraid);
                            // shakey = dateTime+"#"+lattestfinal+"#"+longtestfinal+"#"+vehidnew+"#"+ AESCrypt.decrypt(membercraid);
                            shakey = dateTime + "#" + lattestfinal + "#" + longtestfinal + "#" + vehidnew + "#" + membercraid;
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String newval = SHA1(PasswordKey + shakey);

                        //  String newval = SHA1(mergefinal);
                        String firstthreeChars = newval.substring(0, 3);
                        String lastthreeChars = newval.substring(newval.length() - 3);
                        String checkdigit = firstthreeChars + lastthreeChars;
                        mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                        int randomPIN = (int) (Math.random() * 9000) + 1000;
                        PINString = String.valueOf(randomPIN);
                        finalpushsmstext = checkdigit + thirdpartyoff + ambulanceoff + policeoff + towingoff + claimtypeoff + mergefinal;
                        finalpushsmssend = "Do not change the text " + finalpushsmstext + " " + PINString;
                        if (mydb.getclaimofflineiddetails().getCount() != 0) {
                            mydb.deleteclaimofflineiddata();
                        }
                        boolean Isinserted = mydb.insertclaimofflineid(finalpushsmstext);
                        if (Isinserted) {
                            boolean test = Isinserted;
                            Log.i(null, "Insertion Done");
                        } else {
                            boolean test = Isinserted;
                            Log.i(null, "Not Insertion Done");
                        }

                        alertsmsuser();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            sListpager = new ArrayList<String>();

            sListpager.add("KJN 789U");
            sListpager.add("JUY6 879J");
            sListpager.add("BGA 89JJ");
            sListpager.add("BNH 569A");
//
            sMakeModel = new ArrayList<>();

            sMakeModel.add("Toyota - Camry");
            sMakeModel.add("Mini - Cooper S");
            sMakeModel.add("BWM - A6");
            sMakeModel.add("Audi - Q3");

            valuelistadpt = sListpager.size() + 1;
            listSize = sListpager.size();

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void alertsmsuser() {
        android.app.AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(ClaimVehicleSelection.this);
        alertDialog2.setTitle("Offline Claim Support...");
        alertDialog2.setMessage("You don't have Internet on your mobile. So, please click the 'SEND' option so that the CRA app will send an SMS to the support team to get instant help immediately.");
        //alertDialog2.setIcon(R.drawable.delete);
        alertDialog2.setPositiveButton("SEND",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                            Cursor curseattachsms = mydb.getsendsmsdetails();
                            int countsms = curseattachsms.getCount();
                            if (countsms >= 1) {
                                while (curseattachsms.moveToNext()) {
                                    smsphnnumbers = AESCrypt.decrypt(curseattachsms.getString(2));
                                }
                            }

                            Uri sms_uri = Uri.parse("smsto:" + smsphnnumbers);
                            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                            sms_intent.putExtra("sms_body", finalpushsmssend);
                            startActivity(sms_intent);

                            nextscreenlin.setVisibility(View.VISIBLE);
                            sendsmslin.setVisibility(View.GONE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        alertDialog2.show();


    }

    public void getallvehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        postURL = getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
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
                                .url(postURL)
                                //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(ClaimVehicleSelection.this, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // progressdialog.dismiss();
                                        unauthorize(ClaimVehicleSelection.this);
                                        return;
                                    }
                                });
                            } else {
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                try {
                                    String reqidval = staticJsonObj.getString("reqID");
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();

                                            //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
                                            // startActivity(step2);
                                        }
                                    });
                                    JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");

                                    if (vehicleResponseList.length() == 0) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initListView();
                                            }
                                        });

                                    } else {

                                        for (int i = 0; i < vehicleResponseList.length(); i++) {
                                            JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                            //  if (vehicleObj.getString("isSubmitted").contains("false")) {
                                            //Toast.makeText(ClaimVehicleSelection.this, "Your vehicle is not Submitted Yet, Please submit the vehicle and try later.", Toast.LENGTH_SHORT).show();
                                            //   } else {
                                            Log.i("VehicleResponse", vehicleResponseList.toString());
                                            VehicleselectClaimResponse element = new VehicleselectClaimResponse(AESCrypt.decrypt(vehicleObj.getString("registrationNo")), AESCrypt.decrypt(vehicleObj.getString("certificateNo")), vehicleObj.getString("insurerName"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("id"), vehicleObj.getString("coverageType"), vehicleObj.getString("vINNumber"), vehicleObj.getString("vehicleRefID"), vehicleObj.getString("insurerID"));
                                            listOfVehiclesDetail.add(element);

                                            //  }

                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initListView();
                                            }
                                        });
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

//                                                mAdapter = new VehicleClaimAdapter(ClaimVehicleSelection.this,regnumlist,ClaimVehicleSelection.this);
//                                                ListView list = (ListView) findViewById(R.id.listclaimvechall);
//                                                list.setAdapter(mAdapter);
//                                                // setListViewHeightBasedOnItems(list);
//                                                list.setTextFilterEnabled(true);
//                                                list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//                                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                    @Override
//                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                                                        //mAdapter.setSelectedIndex(position);
//                                                        mAdapter.notifyDataSetChanged();
////                                                        VehicleselectClaimResponse responsenew = regnumlist.get(position);
////                                                        String regnovaltest = responsenew.getRegistrationNo();
////                                                        String regnovaltest1 = regnovaltest;
////                                                        SharedPreferences certpref = getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
////                                                        SharedPreferences.Editor certeditor = certpref.edit();
////                                                        certeditor.putString(ClaimRegFragment.CertificateID, responsenew.getCertificateNo());
////                                                        certeditor.putString(ClaimRegFragment.Vechilerefid, responsenew.getVehicleRefID());
////                                                        certeditor.commit();
////                                                        driverselection(mcontext, activity);
//                                                    }
//                                                });

                                            }
                                        });
                                    }


                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            unauthorize(ClaimVehicleSelection.this);
                                        }
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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
                            runOnUiThread(progressdialog::dismiss);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //progressdialog.dismiss();
                }
            });
            ex.getStackTrace();
            //MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getnetworkVehicleinfo",ex.toString(),ex.toString());
            //mCrashlytics.recordException(ex);
        }

    }

    public void getInsuranceCompanyApi(String LicencePlateNo) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }


                    Thread thread = new Thread(() -> {
                        try {

                            postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllinsurer";
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
                                    .url(postURL)
                                    .header("Authorization", "Bearer " + stokenval)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse;

                            try {
                                runOnUiThread(() ->
                                        progressdialog = ProgressDialog.show(ClaimVehicleSelection.this, getString(R.string.loading), getString(R.string.please), true));
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(ClaimVehicleSelection.this);
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
                                        insurnamelist.clear();
                                        JSONArray insuranceResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllinsurer");
                                        for (int i = 0; i < insuranceResponseList.length(); i++) {
                                            JSONObject insurObj = insuranceResponseList.getJSONObject(i);
                                            InsuranceComInfo element = new InsuranceComInfo(
                                                    insurObj.getString("insurerID"),
                                                    insurObj.getInt("dMVICMemberCompanyID"),
                                                    insurObj.getInt("iMIDSMemberCompanyID"),
                                                    insurObj.getString("insurerName")
                                            );
                                            insurnamelist.add(element);
                                        }
                                        runOnUiThread(() -> getVehicleInfoApi(LicencePlateNo));
                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            unauthorize(ClaimVehicleSelection.this);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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

                            } catch (final IOException e) {
                                runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(() ->
                                        Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                            }

                        } catch (Exception e) {
                            runOnUiThread(progressdialog::dismiss);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimVehicleSelection.this);
                    dialog.setMessage("GPS locations is not enabled.Please enable it");
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void getVehicleInfoApi(String LicencePlateNo) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    postURL = getString(R.string.uaturl) + "/app/Integration/GetCertificateByRegistrationNo";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("LicencePlate", LicencePlateNo);
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(postURL)
                            .header("Authorization", "Bearer " + stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            runOnUiThread(() -> {
                                progressdialog.dismiss();
                                unauthorize(ClaimVehicleSelection.this);
                            });
                        } else {
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {
                                try {
                                    staticJsonObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                    try {
                                        certificateNo = staticJsonObj.optString("certificateNo");
                                        if (certificateNo.isEmpty() || Objects.equals(certificateNo, "null")) {
                                            certificateNo = "-";
                                        }
                                    } catch (Exception e) {
                                        certificateNo = "-";
                                        e.printStackTrace();
                                    }
                                    try {
                                        insuredName = staticJsonObj.optString("certificateNo");
                                        if (insuredName.isEmpty() || Objects.equals(insuredName, "null")) {
                                            insuredName = "-";
                                        }
                                    } catch (Exception e) {
                                        insuredName = "-";
                                        e.printStackTrace();
                                    }
                                    try {
                                        insuredName = staticJsonObj.optString("insuredName");
                                        if (insuredName.isEmpty() || Objects.equals(insuredName, "null")) {
                                            insuredName = "-";
                                        }
                                    } catch (Exception e) {
                                        insuredName = "-";
                                        e.printStackTrace();
                                    }
                                    try {
                                        policyBeginDate = staticJsonObj.optString("policyBeginDate");
                                        if (policyBeginDate.isEmpty() || Objects.equals(policyBeginDate, "null")) {
                                            policyBeginDate = "-";
                                        } else {
                                            policyBeginDate = formatDate(policyBeginDate);
                                        }
                                    } catch (Exception e) {
                                        policyBeginDate = "-";
                                        e.printStackTrace();
                                    }
                                    try {
                                        policyEndDate = staticJsonObj.optString("policyEndDate");
                                        if (policyEndDate.isEmpty() || Objects.equals(policyEndDate, "null")) {
                                            policyEndDate = "-";
                                        } else {
                                            policyEndDate = formatDate(policyEndDate);
                                        }
                                    } catch (Exception e) {
                                        policyEndDate = "-";
                                        e.printStackTrace();
                                    }
                                    try {
                                        policyno = staticJsonObj.optString("policyno");
                                        if (policyno.isEmpty() || Objects.equals(policyno, "null")) {
                                            policyno = "-";
                                        }
                                    } catch (Exception e) {
                                        policyno = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        make = staticJsonObj.optString("make");
                                        if (make.isEmpty() || Objects.equals(make, "null")) {
                                            make = "-";
                                        }
                                    } catch (Exception e) {
                                        make = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        model = staticJsonObj.optString("model");
                                        if (model.isEmpty() || Objects.equals(model, "null")) {
                                            model = "-";
                                        }
                                    } catch (Exception e) {
                                        model = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        yearofManufacture = staticJsonObj.optString("yearofManufacture");
                                        if (yearofManufacture.isEmpty() || Objects.equals(yearofManufacture, "null")) {
                                            yearofManufacture = "-";
                                        }
                                    } catch (Exception e) {
                                        yearofManufacture = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        registratioNo = staticJsonObj.optString("registratioNo");
                                        if (registratioNo.isEmpty() || Objects.equals(registratioNo, "null")) {
                                            registratioNo = "-";
                                        }
                                    } catch (Exception e) {
                                        registratioNo = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        insuredPIN = staticJsonObj.optString("insuredPIN");
                                        if (insuredPIN.isEmpty() || Objects.equals(insuredPIN, "null")) {
                                            insuredPIN = "-";
                                        }
                                    } catch (Exception e) {
                                        insuredPIN = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        sumInsured = staticJsonObj.optString("sumInsured");
                                        if (sumInsured.isEmpty() || Objects.equals(sumInsured, "null") || Objects.equals(sumInsured, "0")) {
                                            sumInsured = "-";
                                        }
                                    } catch (Exception e) {
                                        sumInsured = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        tonnage = staticJsonObj.optString("tonnage");
                                        if (tonnage.isEmpty() || Objects.equals(tonnage, "null") || Objects.equals(tonnage, "0")) {
                                            tonnage = "-";
                                        }
                                    } catch (Exception e) {
                                        tonnage = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        passengersCount = staticJsonObj.optString("passengersCount");
                                        if (passengersCount.isEmpty() || Objects.equals(passengersCount, "null") || Objects.equals(passengersCount, "0")) {
                                            passengersCount = "-";
                                        }
                                    } catch (Exception e) {
                                        passengersCount = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        insuredMailId = staticJsonObj.optString("insuredEmailID");
                                        if (insuredMailId.isEmpty() || Objects.equals(insuredMailId, "null")) {
                                            insuredMailId = "-";
                                        }
                                    } catch (Exception e) {
                                        insuredMailId = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        insuredPhoneNo = staticJsonObj.optString("insuredPhoneNumber");
                                        if (insuredPhoneNo.isEmpty() || Objects.equals(insuredPhoneNo, "null")) {
                                            insuredPhoneNo = "-";
                                        }
                                    } catch (Exception e) {
                                        insuredPhoneNo = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        chassisNumber = staticJsonObj.optString("chassisNumber");
                                        if (chassisNumber.isEmpty() || Objects.equals(chassisNumber, "null")) {
                                            chassisNumber = "-";
                                        }
                                    } catch (Exception e) {
                                        chassisNumber = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        registratioNo = staticJsonObj.optString("registratioNo");
                                        if (registratioNo.isEmpty() || Objects.equals(registratioNo, "null")) {
                                            registratioNo = "-";
                                        }
                                    } catch (Exception e) {
                                        registratioNo = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        engineNumber = staticJsonObj.optString("engineNumber");
                                        if (engineNumber.isEmpty() || Objects.equals(engineNumber, "null") || Objects.equals(engineNumber, "0")) {
                                            engineNumber = "-";
                                        }
                                    } catch (Exception e) {
                                        engineNumber = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        typeofCover = staticJsonObj.optString("typeofCover");
                                        if (typeofCover.isEmpty() || Objects.equals(typeofCover, "null")) {
                                            typeofCover = "-";
                                        }
                                    } catch (Exception e) {
                                        typeofCover = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        typeOfInsurance = staticJsonObj.optString("typeOfInsurance");
                                        if (typeOfInsurance.isEmpty() || Objects.equals(typeOfInsurance, "null")) {
                                            typeOfInsurance = "-";
                                        }
                                    } catch (Exception e) {
                                        typeOfInsurance = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        certifcateType = staticJsonObj.optString("certifcateType");
                                        if (certifcateType.isEmpty() || Objects.equals(certifcateType, "null")) {
                                            certifcateType = "-";
                                        }
                                    } catch (Exception e) {
                                        certifcateType = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        memberCompanyName = staticJsonObj.optString("memberCompanyName");
                                        if (memberCompanyName.isEmpty() || Objects.equals(memberCompanyName, "null")) {
                                            memberCompanyName = "-";
                                        }
                                    } catch (Exception e) {
                                        memberCompanyName = "-";
                                        e.printStackTrace();
                                    }

                                    try {
                                        int cmpId = staticJsonObj.getInt("memberCompanyID");
                                        for (int i = 0; i < insurnamelist.size(); i++) {
                                            System.out.println(insurnamelist.get(i));
                                            if (insurnamelist.get(i).getdMVICMemberCompanyID() == cmpId) {
                                                insurerID = insurnamelist.get(i).getInsurerID();
                                            }
                                        }
                                    } catch (Exception e) {
                                        insurerID = "-";
                                        e.printStackTrace();
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                runOnUiThread(() -> {
                                    addVehiclesToTheUserApi(insurerID, typeOfInsurance, typeofCover, engineNumber, chassisNumber, insuredPhoneNo, insuredMailId, passengersCount, tonnage, sumInsured, insuredPIN, certificateNo, insuredName, policyBeginDate, policyEndDate, policyno, make, model, yearofManufacture, registratioNo, memberCompanyName, certifcateType);
                                });
                            } else if (staticJsonObj.getInt("rcode") == 2) {

                                try {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        try {
                                            String errorText = "The entered registration number is not an active certificate. Please enter the valid registration number";
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                            alert.show();
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
//                                try {
//                                    runOnUiThread(progressdialog::dismiss);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                                    mCrashlytics.recordException(e);
//                                }
//                                try {
//                                    String errorText = "The entered registration number is not an active certificate";
//                                    AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
//                                    alert.setCancelable(false);
//                                    alert.setMessage(errorText);
//                                    alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
//                                    alert.show();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                                    mCrashlytics.recordException(e);
//                                }
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(ClaimVehicleSelection.this);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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
                        runOnUiThread(progressdialog::dismiss);
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        runOnUiThread(() ->
                                Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                    }
                });
                thread.start();
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            runOnUiThread(() -> {

                progressdialog.dismiss();
            });
            ex.getStackTrace();
            //MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getnetworkVehicleinfo",ex.toString(),ex.toString());
            //mCrashlytics.recordException(ex);
        }

    }

    public void addVehiclesToTheUserApi(String insurerID, String typeOfInsurance, String typeofCover, String engineNumber, String chassisNumber, String insuredPhoneNo, String insuredMailId, String passengersCount, String tonnage, String sumInsured, String insuredPIN, String certificateNo, String insuredName, String policyBeginDate, String policyEndDate, String policyno, String make, String model, String yearofManufacture, String registratioNo, String memberCompanyName, String certifcateType) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    postURL = getString(R.string.uaturl) + "/app/Vehicle/AddVehicle";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("ownVehicleID", "a0986d14-2021-4d2b-b16e-6d2592259f34");
                    Details.addProperty("authorizedID", "8752fd3e-e34a-4c04-b224-458a26f719b2");
                    Details.addProperty("certificateNo", certificateNo);
                    Details.addProperty("policyNo", policyno);
                    //TODO Ask Arun..
                    Details.addProperty("certificateType", certifcateType);
                    Details.addProperty("coverageType", typeofCover);
                    Details.addProperty("policyBeginDate", policyBeginDate);
                    Details.addProperty("policyEndDate", policyEndDate);
                    Details.addProperty("registrationNo", registratioNo);
                    Details.addProperty("vINNumber", chassisNumber);
                    Details.addProperty("make", make);
                    Details.addProperty("model", model);
                    Details.addProperty("yearOfMfg", yearofManufacture);
                    Details.addProperty("insurerID", insurerID);
                    Details.addProperty("insuredName", insuredName);
                    Details.addProperty("insuredNationalID", "");
                    Details.addProperty("insuredPIN", insuredPIN);
                    Details.addProperty("sumInsured", sumInsured);
                    Details.addProperty("tonnage", tonnage);
                    Details.addProperty("passengersCount", passengersCount);
                    Details.addProperty("insuredMailId", insuredMailId);
                    Details.addProperty("insuredPhoneNo", insuredPhoneNo);
                    Details.addProperty("insurerName", memberCompanyName);
                    Details.addProperty("engineNumber", engineNumber);
                    Details.addProperty("speedoMeterReading", "");
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(postURL)
                            .header("Authorization", "Bearer " + stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            runOnUiThread(() -> {
                                progressdialog.dismiss();
                                unauthorize(ClaimVehicleSelection.this);
                            });
                        } else {
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(() -> {
                                    //Todo next Api Call
                                    listOfVehiclesDetail.clear();
                                    getUpdatedAllVehiclesInfo();
                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(ClaimVehicleSelection.this);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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
                        runOnUiThread(progressdialog::dismiss);
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        runOnUiThread(() ->
                                Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                    }
                });
                thread.start();
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            runOnUiThread(() -> progressdialog.dismiss());
            e.getStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void getUpdatedAllVehiclesInfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    postURL = getString(R.string.uaturl) + "/app/Vehicle/GetAllVehicle";
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
                            .url(postURL)
                            .header("Authorization", "Bearer " + stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {

                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            runOnUiThread(() -> {
                                progressdialog.dismiss();
                                unauthorize(ClaimVehicleSelection.this);
                            });
                        } else {
                            assert staticResponse.body() != null;
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            try {
                                String reqidval = staticJsonObj.getString("reqID");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            if (staticJsonObj.getInt("rcode") == 1) {
                                JSONArray vehicleResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVehicle");

                                if (vehicleResponseList.length() == 0) {
                                    runOnUiThread(() -> {
                                        initListView();
                                    });

                                } else {

                                    for (int i = 0; i < vehicleResponseList.length(); i++) {
                                        JSONObject vehicleObj = vehicleResponseList.getJSONObject(i);
                                        //   if (vehicleObj.getString("isSubmitted").contains("false")) {
                                        //Toast.makeText(ClaimVehicleSelection.this, "Your vehicle is not Submitted Yet, Please submit the vehicle and try later.", Toast.LENGTH_SHORT).show();
                                        /// } else {
                                        Log.i("VehicleResponse", vehicleResponseList.toString());
                                        VehicleselectClaimResponse element = new VehicleselectClaimResponse(AESCrypt.decrypt(vehicleObj.getString("registrationNo")), AESCrypt.decrypt(vehicleObj.getString("certificateNo")), vehicleObj.getString("insurerName"), vehicleObj.getString("make"), vehicleObj.getString("model"), vehicleObj.getString("yearOfMfg"), vehicleObj.getString("policyBeginDate"), vehicleObj.getString("id"), vehicleObj.getString("coverageType"), vehicleObj.getString("vINNumber"), vehicleObj.getString("vehicleRefID"), vehicleObj.getString("insurerID"));
                                        listOfVehiclesDetail.add(element);
                                        //  }

                                    }

                                    runOnUiThread(() -> {
                                        setNewVehicleSelected = true;
                                        initListView();
                                    });

                                }

                                runOnUiThread(() -> progressdialog.dismiss());
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(ClaimVehicleSelection.this);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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
                        runOnUiThread(progressdialog::dismiss);
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        runOnUiThread(() -> Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                    }
                });
                thread.start();
            }
        } catch (Exception e) {
            runOnUiThread(() -> progressdialog.dismiss());
            e.getStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    void clearData() {
        MainActivity.damagecountnew = 0;
        CarView.carviewcircle1 = "0";
        CarView.carviewcircle2 = "0";
        CarView.carviewcircle3 = "0";
        CarView.carviewcircle4 = "0";
        CarView.carviewcircle5 = "0";
        CarView.carviewcircle6 = "0";
        CarView.carviewcircle7 = "0";
        CarView.carviewcircle8 = "0";
        CarView.carviewcircle9 = "0";
        CarView.carviewcircle10 = "0";
        CarView.carviewcircle11 = "0";
        CarView.carviewcircle12 = "0";
        CarView.carviewcircle13 = "0";
        CarView.carviewcircle14 = "0";
        CarView.carviewcircle15 = "0";
        CarView280.carviewcircle1 = "0";
        CarView280.carviewcircle2 = "0";
        CarView280.carviewcircle3 = "0";
        CarView280.carviewcircle4 = "0";
        CarView280.carviewcircle5 = "0";
        CarView280.carviewcircle6 = "0";
        CarView280.carviewcircle7 = "0";
        CarView280.carviewcircle8 = "0";
        CarView280.carviewcircle9 = "0";
        CarView280.carviewcircle10 = "0";
        CarView280.carviewcircle11 = "0";
        CarView280.carviewcircle12 = "0";
        CarView280.carviewcircle13 = "0";
        CarView280.carviewcircle14 = "0";
        CarView280.carviewcircle15 = "0";
        CarView400.carviewcircle1 = "0";
        CarView400.carviewcircle2 = "0";
        CarView400.carviewcircle3 = "0";
        CarView400.carviewcircle4 = "0";
        CarView400.carviewcircle5 = "0";
        CarView400.carviewcircle6 = "0";
        CarView400.carviewcircle7 = "0";
        CarView400.carviewcircle8 = "0";
        CarView400.carviewcircle9 = "0";
        CarView400.carviewcircle10 = "0";
        CarView400.carviewcircle11 = "0";
        CarView400.carviewcircle12 = "0";
        CarView400.carviewcircle13 = "0";
        CarView400.carviewcircle14 = "0";
        CarView400.carviewcircle15 = "0";

        mydb = new DatabaseHelper(this);
        Cursor curseattachimage = mydb.getlocalimageattachment();
        int countimage = curseattachimage.getCount();

        if (countimage > 0) {
            mydb.deletealllocalimage();
        }
        Cursor cursethirdattachimage = mydb.getthirdlocalimages();
        int countthirdimage = cursethirdattachimage.getCount();

        if (countthirdimage > 0) {
            mydb.deletethirdlocalimage();
        }
        Cursor cursethirdpartdetails = mydb.getthirdpartydetails();
        int countthirddetail = cursethirdpartdetails.getCount();

        if (countthirddetail > 0) {
            mydb.deletethirdpartydetails();
        }

        Cursor cursestolenpartdetails = mydb.getClaimImgmore();
        int countstolendetail = cursestolenpartdetails.getCount();

        if (countstolendetail > 0) {
            mydb.deleteClaimImgmor();
        }


        SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("visimagefilepath", "");
        editor.apply();

        SharedPreferences sharedPreference1 = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreference1.edit();
        editor1.putString("visaudiofilepath", "");
        editor1.apply();

        SharedPreferences sharedPreference2 = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreference2.edit();
        editor2.putString("videofilepath", "");
        editor2.apply();


        SharedPreferences sharedPreference3 = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3 = sharedPreference3.edit();
        editor3.putString("videofilepathlocation", "");
        editor3.apply();
    }

    public void getclaimincidentid() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (isNetworkConnected() == true) {
            try {
                if (isNetworkConnected() == true) {

                    if (checkGPSStatus() == true) {

                        mydb = new DatabaseHelper(ClaimVehicleSelection.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        progressdialog = new ProgressDialog(ClaimVehicleSelection.this);
                        String encryptedSHA = "";
                        String sourceStr = MainActivity.InsertMobileParameters();
                        try {
                            encryptedSHA = AESCrypt.encrypt(sourceStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread thread = new Thread(new Runnable() {
                            SharedPreferences locPref = getSharedPreferences("LocationCurrent", MODE_PRIVATE);

                            public void run() {
                                SharedPreferences vecidpref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                                String vechid = vecidpref.getString("Vechilerefid", "");
                                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                String incidenttypeval = incitype.getString("typeidincident", "");
                                postURL = getString(R.string.uaturl) + "/app/Claim/CRARegistration";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");

                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                JsonObject Details = new JsonObject();

                                try {
                                    if (ClaimLocation.locationupdate.isEmpty()) {
                                        Details.addProperty("incLocation", locPref.getString("Address1", ""));
                                    } else {
                                        Details.addProperty("incLocation", locationupdate);
                                    }
                                    Details.addProperty("VehicleId", AESCrypt.decrypt(vechid));
                                    Details.addProperty("incidentDatetime", ClaimLocation.incidentselecteddate);
                                    Details.addProperty("driverUserId", "");
                                    Details.addProperty("claimTypeID", incidenttypeval);
                                    Details.addProperty("isEmergencyContact", (MainActivity.ambulanceenabled.equals("Yes") || MainActivity.policeinfoenabled.equals("Yes") || MainActivity.towingagencyenabled.equals("Yes")));
                                    Details.addProperty("isCallAmbulance", (MainActivity.ambulanceenabled.equals("Yes")));
                                    Details.addProperty("isCallPolice", (MainActivity.policeinfoenabled.equals("Yes")));
                                    Details.addProperty("isCallTowing", (MainActivity.towingagencyenabled.equals("Yes")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(postURL)
                                        .header("Authorization", "Bearer " + stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .post(body)
                                        .build();
                                Response staticResponse;

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(ClaimVehicleSelection.this, "Loading", "Please wait...", true);
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
                                                unauthorize(ClaimVehicleSelection.this);
                                            }
                                        });
                                    } else {
                                        try {
                                            assert staticResponse.body() != null;
                                            String staticRes = staticResponse.body().string();
                                            Log.i(null, staticRes);
                                            final JSONObject staticJsonObj = new JSONObject(staticRes);

                                            try {
                                                reqidval = staticJsonObj.getString("reqID");
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                            if (staticJsonObj.getInt("rcode") == 1) {
                                                String craid = staticJsonObj.getJSONObject("rObj").getString("incidentUniqueCode");
                                                sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor craeeditor = sharedpreferences.edit();
                                                craeeditor.putString("CraIdval", craid);
                                                craeeditor.apply();

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressdialog.dismiss();
                                                        if (ambulanceEnabled.isChecked() || towingEnabled.isChecked() || policeEnabled.isChecked()) {
                                                            Intent redirect = new Intent(ClaimVehicleSelection.this, ServiceProvider.class);
                                                            startActivity(redirect);
                                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                                        } else {
                                                            SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
                                                            if (incidentsharedpreferences.getString(ClaimType.typeidincident, "") == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
                                                                startActivity(new Intent(ClaimVehicleSelection.this, ClaimStolenTheft.class));
                                                            } else {
                                                                try {
                                                                    clearData();
                                                                    Intent car = new Intent(ClaimVehicleSelection.this, CaptureDamagedParts.class);
                                                                    startActivity(car);
//                                                                    DisplayMetrics dm = getResources().getDisplayMetrics();
//                                                                    int densityDpi = dm.densityDpi;
//                                                                    if (densityDpi >= 320 && densityDpi <= 390) {
//                                                                        Intent car = new Intent(ClaimVehicleSelection.this, CarView.class);
//                                                                        startActivity(car);
//                                                                    }
//                                                                    if (densityDpi >= 280 && densityDpi <= 300) {
//                                                                        Intent car = new Intent(ClaimVehicleSelection.this, CarView.class);
//                                                                        startActivity(car);
//                                                                    }
//                                                                    if (densityDpi >= 310 && densityDpi <= 395) {
//                                                                        Intent car = new Intent(ClaimVehicleSelection.this, CarView280.class);
//                                                                        startActivity(car);
//                                                                    }
//                                                                    if (densityDpi >= 400 && densityDpi <= 520) {
//                                                                        Intent car = new Intent(ClaimVehicleSelection.this, CarView400.class);
//                                                                        startActivity(car);
//                                                                    } else {
//                                                                        Intent car = new Intent(ClaimVehicleSelection.this, CarView400.class);
//                                                                        startActivity(car);
//                                                                    }
                                                                    //startActivity(new Intent(ClaimVehicleSelection.this, ThirdParty.class));
                                                                } catch (Exception ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressdialog.dismiss();
                                                        unauthorize(ClaimVehicleSelection.this);
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
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVehicleSelection.this);
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
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
//                                            runOnUiThread(new Runnable() {
//                                                public void run() {
//                                                    progressdialog.dismiss();
//                                                    try {
//                                                        Toast.makeText(ClaimVehicleSelection.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException ex) {
//                                                        ex.printStackTrace();
//                                                        //MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//                                                    }
//                                                    return;
//                                                }
//                                            });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }

                                } catch (final Exception ex) {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
//                                MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//                                mCrashlytics.recordException(ex);
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(ClaimVehicleSelection.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        thread.start();

                    } else {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimVehicleSelection.this);
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
                progressdialog.dismiss();
                ex.getStackTrace();
//            MainActivity.MobileErrorLog(reqidval,"ClaimRegFragment-getclaimincidentid",ex.toString(),ex.toString());
//            mCrashlytics.recordException(ex);
                // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

            }
        } else {
            try {
                Intent car = new Intent(ClaimVehicleSelection.this, CaptureDamagedParts.class);
                startActivity(car);
//                DisplayMetrics dm = getResources().getDisplayMetrics();
//                int densityDpi = dm.densityDpi;
//                if (densityDpi >= 320 && densityDpi <= 390) {
//                    Intent car = new Intent(ClaimVehicleSelection.this, CarView.class);
//                    startActivity(car);
//                }
//                if (densityDpi >= 280 && densityDpi <= 300) {
//                    Intent car = new Intent(ClaimVehicleSelection.this, CarView.class);
//                    startActivity(car);
//                }
//                if (densityDpi >= 310 && densityDpi <= 395) {
//                    Intent car = new Intent(ClaimVehicleSelection.this, CarView280.class);
//                    startActivity(car);
//                }
//                if (densityDpi >= 400 && densityDpi <= 520) {
//                    Intent car = new Intent(ClaimVehicleSelection.this, CarView400.class);
//                    startActivity(car);
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


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

    private void initListView() {
        Collections.reverse(listOfVehiclesDetail);
        valuelistadpt = listOfVehiclesDetail.size() + 1;
        ViewPager viewPager = findViewById(R.id.viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        pagerAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, this), ClaimVehicleSelection.this);
        ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(viewPager, pagerAdapter);
        fragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(false, fragmentCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);

        if (setNewVehicleSelected) {
            viewPager.setCurrentItem(1, true);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimVehicleSelection.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mydb = new DatabaseHelper(ClaimVehicleSelection.this);
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
                Intent login = new Intent(ClaimVehicleSelection.this, Dashboard.class);
                startActivity(login);
            }
        });
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}